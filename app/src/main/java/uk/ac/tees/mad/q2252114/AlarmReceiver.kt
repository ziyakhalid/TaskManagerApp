// AlarmReceiver.kt
package uk.ac.tees.mad.q2252114

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "YourApp:WakeLock"
            )

            wakeLock.acquire()

            // Handle the intent

            // Release the wake lock
            wakeLock.release()
        }
    }
}
