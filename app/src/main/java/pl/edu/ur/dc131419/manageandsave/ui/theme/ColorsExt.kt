package pl.edu.ur.dc131419.manageandsave.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

@Composable
fun tailwindBgToColor(tw: String): Color {
    // Wykrycie czy aktualny Theme jest ciemny na podstawie background z ColorScheme
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    return if (!isDark) {
        when (tw) {
            "bg-green-500" -> Color(0xFF22C55E)
            "bg-blue-500" -> Color(0xFF3B82F6)
            "bg-purple-500" -> Color(0xFFA855F7)
            "bg-orange-500" -> Color(0xFFF97316)
            "bg-pink-500" -> Color(0xFFEC4899)
            "bg-red-500" -> Color(0xFFEF4444)
            else -> Color(0xFF22C55E)
        }
    } else {
        // ciemniejsze odcienie (przykład – możesz dobrać inne)
        when (tw) {
            "bg-green-500" -> Color(0xFF16A34A) // green-600
            "bg-blue-500" -> Color(0xFF2563EB)  // blue-600
            "bg-purple-500" -> Color(0xFF7C3AED) // purple-600
            "bg-orange-500" -> Color(0xFFEA580C) // orange-600
            "bg-pink-500" -> Color(0xFFDB2777) // pink-600
            "bg-red-500" -> Color(0xFFDC2626) // red-600
            else -> Color(0xFF16A34A)
        }
    }
}
