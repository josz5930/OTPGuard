package com.otpguard.domain.usecase

import com.otpguard.data.local.entity.MonitoredAppEntity
import com.otpguard.data.local.entity.WarningTemplateEntity
import com.otpguard.domain.repository.WarningTemplateRepository
import com.otpguard.util.Channels
import com.otpguard.util.TemplatePlaceholders
import com.otpguard.util.TemplateScopes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResolveTemplateUseCase @Inject constructor(
    private val warningTemplateRepository: WarningTemplateRepository
) {
    suspend fun execute(app: MonitoredAppEntity): WarningTemplateEntity {
        val appTemplate = warningTemplateRepository.getAppTemplate(app.id.toString())
        if (appTemplate != null) {
            return appTemplate
        }

        val channelTemplate = warningTemplateRepository.getChannelTemplate(app.channel)
        if (channelTemplate != null) {
            return channelTemplate
        }

        return warningTemplateRepository.getGlobalDefault() ?: createFallbackTemplate()
    }

    fun renderTemplate(
        template: WarningTemplateEntity,
        appName: String,
        channel: String,
        timestamp: Long = System.currentTimeMillis()
    ): RenderedTemplate {
        val channelLabel = when (channel) {
            Channels.WHATSAPP -> "WhatsApp"
            Channels.SMS -> "SMS"
            else -> "Other Notifications"
        }

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val formattedTime = timeFormat.format(Date(timestamp))

        val placeholders = mapOf(
            TemplatePlaceholders.APP_NAME to appName,
            TemplatePlaceholders.CHANNEL to channelLabel,
            TemplatePlaceholders.TIMESTAMP to formattedTime
        )

        val renderedTitle = replacePlaceholders(template.title, placeholders)
        val renderedBody = replacePlaceholders(template.body, placeholders)

        val finalTitle = if (renderedTitle.length > 200) {
            renderedTitle.take(197) + "…"
        } else {
            renderedTitle
        }

        val finalBody = if (renderedBody.length > 1000) {
            renderedBody.take(997) + "…"
        } else {
            renderedBody
        }

        return RenderedTemplate(
            title = finalTitle,
            body = finalBody
        )
    }

    private fun replacePlaceholders(text: String, placeholders: Map<String, String>): String {
        var result = text
        placeholders.forEach { (placeholder, value) ->
            val sanitizedValue = sanitizeValue(value)
            result = result.replace(placeholder, sanitizedValue)
        }

        result = result.replace(Regex("\\{\\{[\\w]+\\}\\}"), "")

        return result
    }

    private fun sanitizeValue(value: String): String {
        return value
            .filter { it >= ' ' || it == '\t' || it == '\n' }
            .take(100)
            .replace("{{", "&#123;&#123;")
            .replace("}}", "&#125;&#125;")
    }

    private fun createFallbackTemplate(): WarningTemplateEntity {
        return WarningTemplateEntity(
            name = "Fallback",
            title = "\u26A0\uFE0F BE CAREFUL \u2014 OTP Detected",
            body = "A verification code was just received. NEVER share this code with anyone \u2014 no legitimate service will ask for it.",
            scope = TemplateScopes.GLOBAL,
            scopeReferenceId = null,
            isDefault = true
        )
    }
}

data class RenderedTemplate(
    val title: String,
    val body: String
)
