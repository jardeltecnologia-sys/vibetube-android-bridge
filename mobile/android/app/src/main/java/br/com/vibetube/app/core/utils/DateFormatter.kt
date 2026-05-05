package br.com.vibetube.app.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Formata datas ISO-8601 do Blogger ("2024-01-15T20:00:00.000-03:00") para
 * uma string amigável tipo "há 2 dias" ou "15 jan 2024".
 */
object DateFormatter {

    private val isoFormats = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'"
    )

    fun toRelative(iso: String?): String {
        if (iso.isNullOrBlank()) return ""
        val date = parse(iso) ?: return ""
        val now = System.currentTimeMillis()
        val diff = now - date.time
        return when {
            diff < 60_000L -> "agora"
            diff < 3_600_000L -> "${diff / 60_000L} min"
            diff < 86_400_000L -> "${diff / 3_600_000L} h"
            diff < 7L * 86_400_000L -> "${diff / 86_400_000L} d"
            else -> shortDate(date)
        }
    }

    fun shortDate(date: Date): String {
        val fmt = SimpleDateFormat("d MMM yyyy", Locale("pt", "BR"))
        return fmt.format(date)
    }

    private fun parse(iso: String): Date? {
        for (pattern in isoFormats) {
            try {
                val fmt = SimpleDateFormat(pattern, Locale.US)
                fmt.timeZone = TimeZone.getTimeZone("UTC")
                return fmt.parse(iso)
            } catch (_: Exception) {
                continue
            }
        }
        return null
    }
}
