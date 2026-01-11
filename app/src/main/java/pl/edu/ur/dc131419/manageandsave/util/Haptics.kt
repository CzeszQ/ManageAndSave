package pl.edu.ur.dc131419.manageandsave.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

class Haptics(private val context: Context) {

    fun tick() {
        val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= 31) {
            val vm = context.getSystemService(android.os.VibratorManager::class.java)
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (!vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(80)
        }
    }
}
