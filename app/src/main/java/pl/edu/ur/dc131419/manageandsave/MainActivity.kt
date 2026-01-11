package pl.edu.ur.dc131419.manageandsave

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import pl.edu.ur.dc131419.manageandsave.ui.theme.ManageAndSaveTheme
import pl.edu.ur.dc131419.manageandsave.ui.screens.BudgetScreen
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import pl.edu.ur.dc131419.manageandsave.data.preferences.ThemeMode
import pl.edu.ur.dc131419.manageandsave.ui.dashboard.DashboardViewModel
import pl.edu.ur.dc131419.manageandsave.ui.settings.SettingsViewModel


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val ui by settingsViewModel.uiState.collectAsState()

            val darkTheme = when (ui.themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            ManageAndSaveTheme(darkTheme = darkTheme) {
                BudgetScreen(
                    viewModel = dashboardViewModel,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}
