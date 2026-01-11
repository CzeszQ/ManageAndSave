package pl.edu.ur.dc131419.manageandsave.data.preferences


import android.content.SharedPreferences
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class ThemeMode { SYSTEM, LIGHT, DARK }

class SettingsRepository @Inject constructor(
    private val prefs: SharedPreferences
){

    private val _vibrateEnabled = MutableStateFlow(prefs.getBoolean(KEY_VIBRATE_ENABLED, true))
    val vibrateEnabled: StateFlow<Boolean> = _vibrateEnabled

    private val _themeMode = MutableStateFlow(readThemeMode())
    val themeMode: StateFlow<ThemeMode> = _themeMode

    private val listener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            when (key) {
                KEY_VIBRATE_ENABLED -> _vibrateEnabled.value =
                    prefs.getBoolean(KEY_VIBRATE_ENABLED, true)
                KEY_THEME_MODE -> _themeMode.value = readThemeMode()
            }
        }

    init { prefs.registerOnSharedPreferenceChangeListener(listener) }

    fun setVibrateEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_VIBRATE_ENABLED, enabled).apply()
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
    }

    private fun readThemeMode(): ThemeMode {
        val value = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        return runCatching { ThemeMode.valueOf(value) }.getOrDefault(ThemeMode.SYSTEM)
    }

    companion object {
        private const val KEY_VIBRATE_ENABLED = "vibrate_enabled"
        private const val KEY_THEME_MODE = "theme_mode"
    }
}
