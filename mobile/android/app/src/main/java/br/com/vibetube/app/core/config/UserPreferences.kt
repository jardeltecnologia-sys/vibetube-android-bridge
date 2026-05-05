package br.com.vibetube.app.core.config

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "vibetube_prefs")

/**
 * Preferências simples — não confundir com FeatureFlagManager (que lê config JSON imutável).
 * Aqui ficam estados mutáveis em runtime: primeira execução, primeiro like, etc.
 */
class UserPreferences(private val context: Context) {

    private object Keys {
        val FIRST_RUN_DONE = booleanPreferencesKey("first_run_done")
        val LIKE_NOTICE_SHOWN = booleanPreferencesKey("like_notice_shown")
    }

    val firstRunDone: Flow<Boolean> = context.dataStore.data.map {
        it[Keys.FIRST_RUN_DONE] ?: false
    }

    suspend fun markFirstRunDone() {
        context.dataStore.edit { it[Keys.FIRST_RUN_DONE] = true }
    }

    val likeNoticeShown: Flow<Boolean> = context.dataStore.data.map {
        it[Keys.LIKE_NOTICE_SHOWN] ?: false
    }

    suspend fun markLikeNoticeShown() {
        context.dataStore.edit { it[Keys.LIKE_NOTICE_SHOWN] = true }
    }
}
