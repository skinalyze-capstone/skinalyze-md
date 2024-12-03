package com.dicoding.skinalyze

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.dicoding.skinalyze.ui.userprofile.SettingFragment

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val isMorning = intent.getBooleanExtra("isMorning", false)  // Mengambil nilai extra
        val mainIntent = Intent(context, SettingFragment::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "reminder_channel"

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Reminder Notifications", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(if (isMorning) "Morning Skincare Reminder" else "Night Skincare Reminder")
            .setContentText(if (isMorning) "Don't forget your morning skincare routine before starting the day!" else "Finish your day with skincare routine. Keep your skin nourished while you sleep!")
            .setSmallIcon(R.drawable.logo_skinalyze)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(0, notification)
    }
}

