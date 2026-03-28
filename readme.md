# OTP Cautioner

**OTP Cautioner** (also known as "OTP Guard") is a lightweight, offline Android application that passively monitors incoming notifications from messaging apps (e.g. SMS) for OTP/verification code patterns. Upon detection, it immediately surfaces a prominent warning notification to alert the user against sharing the code with anyone.

## 📦 Usage

### 1. Install
From the Github releases page, install the APK on an Android device (above Android 8.0). See ([Android Studio Help Page](https://developer.android.com/studio/run/emulator-install-add-files))

### 2. Grant Notification Access
* Open the app on your Android phone.
* On the **Enable Notification Access** screen, tap the **Open Notification Settings** button
* On the next screen, tap "yes" to the question "Allow notification access for OTP Guard?"
* Tap the **I've enabled it - Check again** button

### 3. Allow app to notify you
* Enable notifications under your Android phone's configuration for this application. See ([Google Help Page](https://support.google.com/android/answer/9079661?hl=en)), although it is most commonly in  Settings app > Notifications > App Notifications.

## What is missing?
The following are known features that are missing or known bugs:
* Does not lock the phone and sound the alarm loudly (intentional as there will be false positives since we do not know when you ACTUALLY did request for an OTP)

---
# Technical Details (Generated)

## 🔒 Security & Privacy Aims

### Privacy
1.  **No OTP Storage:** The actual OTP value is never stored. Only metadata (timestamp, app, rule) is logged.
2.  **Offline Operation:** No network permissions are requested. All data is local.
3.  **No Data Export:** No analytics, telemetry, or cloud sync.

### Integrity
*   **Hash Chain:** The `detection_event` table uses a hash chain (`row_hash`) to enable offline tamper detection. Users can verify log integrity via the "Verify Log Integrity" action.
*   **Regex Safeguards:** Regex evaluation is time-boxed (200ms timeout) to prevent denial-of-service via complex patterns.
*   **Input Validation:** All user inputs are validated at the GUI layer against rules stored in `input_validation_rule` before persistence.

## 🏗 Architecture

OTP Guard is a **single-user, offline application**. There is no backend server and  no network connectivity needed. All data resides on-device in a local **Room (SQLite)** database.

### Core Mechanism
The app leverages Android's `NotificationListenerService` API. Once the user grants **Notification Access**, the app receives callbacks for every notification posted system-wide, allowing it to scan text content without requesting dangerous permissions like `READ_SMS`.

### Tech Stack
*   **Language:** Kotlin
*   **UI:** Jetpack Compose / Material 3
*   **Database:** Room (SQLite) with WAL mode
*   **Background:** `NotificationListenerService` + `WorkManager` (for log pruning)
*   **Min SDK:** API 26 (Android 8.0)

## 🗄 Database Schema

The application data is structured around six core entities.

### 1. `monitored_app`
Stores the messaging apps whose notifications are scanned.
*   **Key Change:** Added `channel` column (`whatsapp`, `sms`, `other`) for template resolution.
*   **Constraints:** `package_name` must match Android package regex; `channel` is constrained to specific values.

### 2. `regex_rule`
Stores regex patterns used to detect OTP content.
*   **Constraints:** Patterns are length-checked (max 500 chars); `priority` determines evaluation order.
*   **Safety:** Patterns are tested for catastrophic backtracking before saving.

### 3. `warning_template`
**Replaces v1.0's `notification_template`.** Supports scoped templates: global default, per-channel, and per-app.
*   **Scope Logic:** `scope` can be `global`, `channel`, or `app`.
*   **Resolution:** App-specific → Channel-specific → Global Default.
*   **Constraint:** Unique constraint on `(scope, scope_reference_id)` ensures one template per scope level.

### 4. `detection_event`
Audit log of every OTP detection and service state change. **No PII or OTP values are stored.**
*   **Key Change:** Added `event_type` (`detection`, `service_toggle), `timeout`, `new_service_state`, and `row_hash`.
*   **Hash Chain:** `row_hash` implements a sequential hash chain (`SHA-256`) for tamper evidence.
*   **Lifecycle:** INSERT-only. Rows are pruned nightly based on `log_retention_days`.

### 5. `app_config`
Key-value store for user preferences.
*   **v1.1 Keys:** Added `log_retention_days` and `regex_timeout_ms`.
*   **Master Toggle:** `service_enabled` controls the monitoring service state.

### 6. `input_validation_rule` (New)
Stores validation metadata for each user-editable field.
*   **Purpose:** Provides a data-driven foundation for GUI validation.
*   **Structure:** Links `target_entity` and `target_field` to validation types (e.g., `regex_format`, `max_length`).

## ⚙️ Configuration

Default configuration values are stored in `app_config`. Key settings include:

| Key | Default | Description |
| :--- | :--- | :--- |
| `service_enabled` | `"true"` | Master on/off switch for monitoring. |
| `warning_sound_enabled` | `"true"` | Play sound with warning notification. |
| `log_retention_days` | `"90"` | Days to retain detection events. |
| `regex_timeout_ms` | `"200"` | Timeout for regex evaluation. |
| `collapse_window_ms` | `"5000"` | Deduplicate warnings within this window. |



## 📄 License

This code for OTP Cautioner is released under ([CC BY-NC-SA 4.0](https://creativecommons.org/licenses/by-nc-sa/4.0/legalcode.en))

---

## 📞 Support
Please see the following statements:

NO SUPPORT OBLIGATION
The developer is under no obligation to provide technical support, maintenance, updates, bug fixes, or customer service for this application. You acknowledge that you are not entitled to any ongoing assistance, troubleshooting, or application improvements. Use of this application constitutes acceptance that you are solely responsible for its operation on your device. No support should be expected in any way.

NO GUARANTEE OF CRIME PREVENTION
This application is provided for informational and/or deterrent purposes only. OTP Cautioner does not guarantee, warrant, or ensure the prevention of crime, theft, vandalism, or any illegal activity. The developer makes no representations regarding the effectiveness of this application in preventing, stopping, or deterring criminal acts. By using this application, you acknowledge and agree that criminal activity may still occur regardless of whether this application is active, installed, or functioning properly.

USE AT YOUR OWN RISK
This application is provided on an "AS IS" and "AS AVAILABLE" basis without any warranties of any kind, either express or implied, including but not limited to warranties of merchantability, fitness for a particular purpose, or non-infringement. You assume all risk associated with the use of this application, including but not limited to device malfunction, data loss, financial loss, physical harm, mental harm and/or loss of life.

LIMITATION OF LIABILITY
To the fullest extent permitted by applicable law, the developer of OTP Cautioner shall not be liable for any direct, indirect, incidental, special, consequential, or punitive damages arising out of or relating to your use of, or inability to use, this application. This includes, without limitation, damages for personal injury, property damage, theft, loss of data, financial loss, or any other damages resulting from crime or security breaches that occur while using or failing to use this application, even if advised of the possibility of such damages.

INDEMNIFICATION
You agree to indemnify, defend, and hold harmless the developer from any claims, damages, losses, liabilities, costs, or expenses (including attorney's fees) arising out of or relating to your use of the application or any violation of these terms.

By downloading, installing, or using OTP Cautioner (or any derived work), you acknowledge that you have read, understood, and agreed to this disclaimer. If you do not agree, do not use this application.
