package pl.edu.ur.dc131419.manageandsave.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable

@Composable
fun ViewModeToggle(
    viewMode: ViewMode,
    onChange: (ViewMode) -> Unit
) {
    SingleChoiceSegmentedButtonRow {
        SegmentedButton(
            selected = viewMode == ViewMode.LIST,
            onClick = { onChange(ViewMode.LIST) },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
            icon = { Icon(Icons.Filled.List, contentDescription = "Widok listy") }
        ) { }

        SegmentedButton(
            selected = viewMode == ViewMode.GRID,
            onClick = { onChange(ViewMode.GRID) },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
            icon = { Icon(Icons.Filled.GridView, contentDescription = "Widok kafelków") }
        ) { }
    }
}
