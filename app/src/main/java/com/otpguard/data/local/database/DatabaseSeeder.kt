package com.otpguard.data.local.database

import com.otpguard.data.local.entity.AppConfigEntity
import com.otpguard.data.local.entity.InputValidationRuleEntity
import com.otpguard.data.local.entity.MonitoredAppEntity
import com.otpguard.data.local.entity.RegexRuleEntity
import com.otpguard.data.local.entity.WarningTemplateEntity
import com.otpguard.util.Channels
import com.otpguard.util.ConfigKeys
import com.otpguard.util.TemplateScopes

object DatabaseSeeder {

    fun getDefaultMonitoredApps(): List<MonitoredAppEntity> = listOf(
        MonitoredAppEntity(
            packageName = "com.google.android.apps.messaging",
            displayName = "Android Messages",
            channel = Channels.SMS,
            isDefault = true
        ),
        MonitoredAppEntity(
            packageName = "com.samsung.android.messaging",
            displayName = "Samsung Messages",
            channel = Channels.SMS,
            isDefault = true
        ),
        MonitoredAppEntity(
            packageName = "com.whatsapp",
            displayName = "WhatsApp",
            channel = Channels.WHATSAPP,
            isDefault = true
        ),
        MonitoredAppEntity(
            packageName = "com.whatsapp.w4b",
            displayName = "WhatsApp Business",
            channel = Channels.WHATSAPP,
            isDefault = true
        ),
        MonitoredAppEntity(
            packageName = "org.telegram.messenger",
            displayName = "Telegram",
            channel = Channels.OTHER,
            isDefault = true
        ),
        MonitoredAppEntity(
            packageName = "org.thunderdog.chalern",
            displayName = "Telegram X",
            channel = Channels.OTHER,
            isDefault = true
        ),
        MonitoredAppEntity(
            packageName = "org.thoughtcrime.securesms",
            displayName = "Signal",
            channel = Channels.OTHER,
            isDefault = true
        ),
    )

    fun getDefaultRegexRules(): List<RegexRuleEntity> = listOf(
        RegexRuleEntity(
            name = "WhatsApp Code",
            pattern = """(?i)your\s+whatsapp(?:\s+business)?\s+code[:\s]+\d{3}[-\s]?\d{3}""",
            description = "Detects WhatsApp and WhatsApp Business verification codes",
            isDefault = true,
            priority = 10
        ),
        RegexRuleEntity(
            name = "Generic 'is your code'",
            pattern = """(?i)(?<!\d)\d{4,8}(?!\d)\s+is\s+your\s+(?:\w+\s+)?(?:verification|security|login|auth(?:entication)?)\s+code""",
            description = "Detects patterns like '284938 is your verification code'",
            isDefault = true,
            priority = 20
        ),
        RegexRuleEntity(
            name = "Generic 'code is'",
            pattern = """(?i)(?:verification|security|login|auth(?:entication)?)\s+code\s*(?:is|:)\s*\d{4,8}""",
            description = "Detects patterns like 'Your verification code is 482910'",
            isDefault = true,
            priority = 30
        ),
        RegexRuleEntity(
            name = "OTP Keyword + Digits",
            pattern = """(?i)\bOTP\b[\s:]+(?:\w+\s+)?(?<!\d)\d{4,8}(?!\d)""",
            description = "Detects patterns like 'Your OTP: 482901' or 'OTP is 384729'",
            isDefault = true,
            priority = 40
        ),
        RegexRuleEntity(
            name = "Google-style Code",
            pattern = """(?i)\b[A-Z]-\d{4,6}\b""",
            description = "Detects Google-style codes like 'G-482910'",
            isDefault = true,
            priority = 50
        ),
        RegexRuleEntity(
            name = "Telegram Code",
            pattern = """(?i)(?:login|telegram)\s+code[:\s]+\d{4,6}""",
            description = "Detects Telegram login codes",
            isDefault = true,
            priority = 60
        ),
        RegexRuleEntity(
            name = "Dash-separated Code",
            pattern = """(?i)(?:(?:code|pin|otp)[:\s]+\d{3,4}[-\s]\d{3,4}|\d{3,4}[-\s]\d{3,4}\s+(?:.*?\s)?(?:code|pin|otp))""",
            description = "Detects dash-separated codes near keywords like '482-910 as your code'",
            isDefault = true,
            priority = 70
        ),
        RegexRuleEntity(
            name = "Broad Fallback",
            pattern = """(?i)(?:code|passcode|pin|otp)[\s:]*\d{4,8}\b""",
            description = "Broad fallback: digits near code-related keywords",
            isDefault = true,
            priority = 80
        ),
    )

    fun getDefaultConfigs(): List<AppConfigEntity> = listOf(
        AppConfigEntity(key = ConfigKeys.WARNING_SOUND_ENABLED, value = "true"),
        AppConfigEntity(key = ConfigKeys.WARNING_VIBRATE_ENABLED, value = "true"),
        AppConfigEntity(key = ConfigKeys.WARNING_AUTO_DISMISS_SECONDS, value = "60"),
        AppConfigEntity(key = ConfigKeys.COLLAPSE_WINDOW_MS, value = "5000"),
        AppConfigEntity(key = ConfigKeys.SERVICE_ENABLED, value = "true"),
        AppConfigEntity(key = ConfigKeys.NOTIFICATION_ACCESS_GRANTED, value = "false"),
        AppConfigEntity(key = ConfigKeys.LOG_RETENTION_DAYS, value = "90"),
        AppConfigEntity(key = ConfigKeys.REGEX_TIMEOUT_MS, value = "200"),
    )

    fun getDefaultWarningTemplate(): WarningTemplateEntity = WarningTemplateEntity(
        name = "Global Default",
        title = "\u26A0\uFE0F BE CAREFUL \u2014 OTP Detected",
        body = "A verification code was just received. NEVER share this code with anyone \u2014 no legitimate service will ask for it.",
        scope = TemplateScopes.GLOBAL,
        scopeReferenceId = null,
        isDefault = true
    )

    fun getDefaultInputValidationRules(): List<InputValidationRuleEntity> = listOf(
        // Monitored App validation rules
        InputValidationRuleEntity(
            targetEntity = "monitored_app",
            targetField = "package_name",
            validationType = "required",
            errorMessage = "Enter an Android package name."
        ),
        InputValidationRuleEntity(
            targetEntity = "monitored_app",
            targetField = "package_name",
            validationType = "max_length",
            validationParam = "255",
            errorMessage = "Package name must be 255 characters or fewer."
        ),
        InputValidationRuleEntity(
            targetEntity = "monitored_app",
            targetField = "package_name",
            validationType = "regex_format",
            validationParam = """^[a-zA-Z][a-zA-Z0-9_]*(\.[a-zA-Z][a-zA-Z0-9_]*){1,}$""",
            errorMessage = "Enter a valid Android package name (e.g., com.example.app)."
        ),
        InputValidationRuleEntity(
            targetEntity = "monitored_app",
            targetField = "package_name",
            validationType = "unique",
            errorMessage = "This package name already exists."
        ),
        InputValidationRuleEntity(
            targetEntity = "monitored_app",
            targetField = "display_name",
            validationType = "required",
            errorMessage = "Enter a display name."
        ),
        InputValidationRuleEntity(
            targetEntity = "monitored_app",
            targetField = "display_name",
            validationType = "max_length",
            validationParam = "100",
            errorMessage = "Display name must be 100 characters or fewer."
        ),
        InputValidationRuleEntity(
            targetEntity = "monitored_app",
            targetField = "channel",
            validationType = "enum",
            validationParam = "whatsapp,sms,other",
            errorMessage = "Select a valid channel."
        ),
        // Regex Rule validation rules
        InputValidationRuleEntity(
            targetEntity = "regex_rule",
            targetField = "name",
            validationType = "required",
            errorMessage = "Enter a rule name."
        ),
        InputValidationRuleEntity(
            targetEntity = "regex_rule",
            targetField = "name",
            validationType = "max_length",
            validationParam = "100",
            errorMessage = "Rule name must be between 1 and 100 characters."
        ),
        InputValidationRuleEntity(
            targetEntity = "regex_rule",
            targetField = "name",
            validationType = "unique",
            errorMessage = "A rule with this name already exists."
        ),
        InputValidationRuleEntity(
            targetEntity = "regex_rule",
            targetField = "pattern",
            validationType = "required",
            errorMessage = "Enter a regex pattern."
        ),
        InputValidationRuleEntity(
            targetEntity = "regex_rule",
            targetField = "pattern",
            validationType = "max_length",
            validationParam = "500",
            errorMessage = "Regex pattern must be 500 characters or fewer."
        ),
        InputValidationRuleEntity(
            targetEntity = "regex_rule",
            targetField = "description",
            validationType = "max_length",
            validationParam = "500",
            errorMessage = "Description must be 500 characters or fewer."
        ),
        InputValidationRuleEntity(
            targetEntity = "regex_rule",
            targetField = "priority",
            validationType = "range_min",
            validationParam = "1",
            errorMessage = "Priority must be at least 1."
        ),
        InputValidationRuleEntity(
            targetEntity = "regex_rule",
            targetField = "priority",
            validationType = "range_max",
            validationParam = "9999",
            errorMessage = "Priority must be 9999 or less."
        ),
        // Warning Template validation rules
        InputValidationRuleEntity(
            targetEntity = "warning_template",
            targetField = "title",
            validationType = "required",
            errorMessage = "Enter a notification title."
        ),
        InputValidationRuleEntity(
            targetEntity = "warning_template",
            targetField = "title",
            validationType = "max_length",
            validationParam = "100",
            errorMessage = "Title must be 100 characters or fewer."
        ),
        InputValidationRuleEntity(
            targetEntity = "warning_template",
            targetField = "body",
            validationType = "required",
            errorMessage = "Enter a notification body."
        ),
        InputValidationRuleEntity(
            targetEntity = "warning_template",
            targetField = "body",
            validationType = "max_length",
            validationParam = "500",
            errorMessage = "Body must be 500 characters or fewer."
        ),
    )
}
