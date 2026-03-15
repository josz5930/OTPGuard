package com.otpguard.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.otpguard.data.local.dao.*
import com.otpguard.data.local.entity.*
import com.otpguard.util.Channels
import com.otpguard.util.TemplateScopes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest

@Database(
    entities = [
        MonitoredAppEntity::class,
        RegexRuleEntity::class,
        DetectionEventEntity::class,
        AppConfigEntity::class,
        WarningTemplateEntity::class,
        InputValidationRuleEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class OtpGuardDatabase : RoomDatabase() {
    abstract fun monitoredAppDao(): MonitoredAppDao
    abstract fun regexRuleDao(): RegexRuleDao
    abstract fun detectionEventDao(): DetectionEventDao
    abstract fun appConfigDao(): AppConfigDao
    abstract fun warningTemplateDao(): WarningTemplateDao
    abstract fun inputValidationRuleDao(): InputValidationRuleDao

    companion object {
        @Volatile
        private var INSTANCE: OtpGuardDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Add channel column to monitored_app
                db.execSQL("ALTER TABLE monitored_app ADD COLUMN channel TEXT NOT NULL DEFAULT 'other'")

                // 2. Backfill channel for default apps
                db.execSQL("UPDATE monitored_app SET channel = 'whatsapp' WHERE package_name IN ('com.whatsapp', 'com.whatsapp.w4b')")
                db.execSQL("UPDATE monitored_app SET channel = 'sms' WHERE package_name IN ('com.google.android.apps.messaging', 'com.samsung.android.messaging')")

                // 3. Create warning_template table (replaces notification_template)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS warning_template (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        title TEXT NOT NULL,
                        body TEXT NOT NULL,
                        scope TEXT NOT NULL,
                        scope_reference_id TEXT,
                        is_default INTEGER NOT NULL DEFAULT 0,
                        created_at INTEGER NOT NULL,
                        updated_at INTEGER NOT NULL
                    )
                """)
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_template_scope ON warning_template (scope, scope_reference_id)")

                // 4. Migrate existing notification_template to warning_template
                db.execSQL("""
                    INSERT INTO warning_template (name, title, body, scope, scope_reference_id, is_default, created_at, updated_at)
                    SELECT name, title, body, 'global', NULL, 1, created_at, ${System.currentTimeMillis() / 1000}
                    FROM notification_template WHERE is_active = 1 LIMIT 1
                """)

                // 5. Drop old notification_template table
                db.execSQL("DROP TABLE IF EXISTS notification_template")

                // 6. Update detection_event table
                db.execSQL("ALTER TABLE detection_event ADD COLUMN event_type TEXT NOT NULL DEFAULT 'detection'")
                db.execSQL("ALTER TABLE detection_event ADD COLUMN timeout INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE detection_event ADD COLUMN new_service_state TEXT")
                db.execSQL("ALTER TABLE detection_event ADD COLUMN template_id INTEGER")
                db.execSQL("ALTER TABLE detection_event ADD COLUMN row_hash TEXT")

                // 7. Make app_id and rule_id nullable (recreate table)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS detection_event_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        event_type TEXT NOT NULL DEFAULT 'detection',
                        app_id INTEGER,
                        rule_id INTEGER,
                        detected_at INTEGER NOT NULL,
                        notification_key TEXT,
                        warning_posted INTEGER NOT NULL DEFAULT 1,
                        timeout INTEGER NOT NULL DEFAULT 0,
                        new_service_state TEXT,
                        template_id INTEGER,
                        row_hash TEXT,
                        FOREIGN KEY(app_id) REFERENCES monitored_app(id) ON DELETE SET NULL,
                        FOREIGN KEY(rule_id) REFERENCES regex_rule(id) ON DELETE SET NULL,
                        FOREIGN KEY(template_id) REFERENCES warning_template(id) ON DELETE SET NULL
                    )
                """)
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_event_app_id ON detection_event_new (app_id)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_event_rule_id ON detection_event_new (rule_id)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_event_template_id ON detection_event_new (template_id)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_event_detected_at ON detection_event_new (detected_at)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_event_type ON detection_event_new (event_type)")

                db.execSQL("""
                    INSERT INTO detection_event_new (id, app_id, rule_id, detected_at, notification_key, warning_posted)
                    SELECT id, app_id, rule_id, detected_at, notification_key, warning_posted FROM detection_event
                """)
                db.execSQL("DROP TABLE detection_event")
                db.execSQL("ALTER TABLE detection_event_new RENAME TO detection_event")

                // 8. Create input_validation_rule table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS input_validation_rule (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        target_entity TEXT NOT NULL,
                        target_field TEXT NOT NULL,
                        validation_type TEXT NOT NULL,
                        validation_param TEXT,
                        error_message TEXT NOT NULL,
                        is_enabled INTEGER NOT NULL DEFAULT 1,
                        created_at INTEGER NOT NULL
                    )
                """)
                db.execSQL("""
                    CREATE UNIQUE INDEX IF NOT EXISTS idx_validation_target 
                    ON input_validation_rule (target_entity, target_field, validation_type)
                """)

                // 9. Add new config keys
                val now = System.currentTimeMillis() / 1000
                db.execSQL("""
                    INSERT OR IGNORE INTO app_config (key, value, updated_at)
                    VALUES 
                        ('log_retention_days', '90', $now),
                        ('regex_timeout_ms', '200', $now)
                """)
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): OtpGuardDatabase {
            return INSTANCE ?: synchronized(this) {
                if (INSTANCE != null) {
                    INSTANCE!!
                } else {
                    // Use a wrapper to capture the instance reference
                    val instanceRef = mutableListOf<OtpGuardDatabase>()
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        OtpGuardDatabase::class.java,
                        "otpguard_database"
                    )
                        .addMigrations(MIGRATION_1_2)
                        .addCallback(object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                // Seed in background - instanceRef will be populated by then
                                scope.launch(Dispatchers.IO) {
                                    instanceRef.firstOrNull()?.let { seedDatabase(it) }
                                }
                            }
                        })
                        .build()
                    instanceRef.add(instance)
                    INSTANCE = instance
                    instance
                }
            }
        }

        private suspend fun seedDatabase(db: OtpGuardDatabase) {
            db.monitoredAppDao().insertAll(DatabaseSeeder.getDefaultMonitoredApps())
            db.regexRuleDao().insertAll(DatabaseSeeder.getDefaultRegexRules())
            DatabaseSeeder.getDefaultConfigs().forEach { config ->
                db.appConfigDao().set(config)
            }
            db.warningTemplateDao().insert(DatabaseSeeder.getDefaultWarningTemplate())
            db.inputValidationRuleDao().insertAll(DatabaseSeeder.getDefaultInputValidationRules())

            // Compute initial hashes for detection events (empty initially)
            updateAllRowHashes(db)
        }

        suspend fun updateAllRowHashes(db: OtpGuardDatabase) {
            val events = db.detectionEventDao().getAllEventsForHashing()
            var previousHash = "GENESIS"

            events.forEach { event ->
                val input = buildString {
                    append(event.id)
                    append("|")
                    append(event.eventType)
                    append("|")
                    append(event.appId?.toString() ?: "NULL")
                    append("|")
                    append(event.ruleId?.toString() ?: "NULL")
                    append("|")
                    append(event.detectedAt)
                    append("|")
                    append(previousHash)
                }

                val hash = sha256(input)
                db.detectionEventDao().updateRowHash(event.id, hash)
                previousHash = hash
            }
        }

        private fun sha256(input: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
            return bytes.joinToString("") { "%02x".format(it) }
        }
    }
}
