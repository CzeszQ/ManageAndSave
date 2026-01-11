package pl.edu.ur.dc131419.manageandsave.ui.screens

import SettingsScreen
import androidx.compose.runtime.Composable
import pl.edu.ur.dc131419.manageandsave.ui.settings.SettingsViewModel

@Composable
fun SettingsRoute(
    vm: SettingsViewModel,
    onBack: () -> Unit
) {
    SettingsScreen(vm = vm, onBack = onBack)
}

