package pl.edu.ur.dc131419.manageandsave.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import pl.edu.ur.dc131419.manageandsave.data.preferences.SettingsRepository
import pl.edu.ur.dc131419.manageandsave.data.preferences.ThemeMode

@HiltViewModel
class SettingsViewModel @javax.inject.Inject constructor(
    private val repo: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> =
        combine(repo.vibrateEnabled, repo.themeMode) { vibrate, theme ->
            SettingsUiState(vibrateEnabled = vibrate, themeMode = theme)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState()
        )

    fun setThemeMode(mode: ThemeMode) = repo.setThemeMode(mode)
    fun setVibrateEnabled(enabled: Boolean) = repo.setVibrateEnabled(enabled)
}
