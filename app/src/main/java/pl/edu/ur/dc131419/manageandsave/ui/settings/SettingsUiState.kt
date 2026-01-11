    package pl.edu.ur.dc131419.manageandsave.ui.settings

    import pl.edu.ur.dc131419.manageandsave.data.preferences.ThemeMode

    data class SettingsUiState(
        val vibrateEnabled: Boolean = true,
        val themeMode: ThemeMode = ThemeMode.SYSTEM
    )


