package com.dicoding.skinalyze.ui.userprofile

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dicoding.skinalyze.AlarmReceiver
import com.dicoding.skinalyze.R
import java.util.*

class SettingFragment : Fragment() {

    private lateinit var morningTimeTextView: TextView
    private lateinit var nightTimeTextView: TextView

    // Request Notification Permission
    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Notification permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    // Request Exact Alarm Permission
    private val requestExactAlarmPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Exact alarm permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Exact alarm permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        // Inisialisasi TextView
        morningTimeTextView = view.findViewById(R.id.txt_morning_time)
        nightTimeTextView = view.findViewById(R.id.txt_night_time)

        // Mengatur reminder pagi
        view.findViewById<View>(R.id.card_morning_reminder).setOnClickListener {
            showTimePicker(isMorning = true) { hour, minute ->
                val time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                morningTimeTextView.text = time
                setReminder(hour, minute, isMorning = true)
            }
        }

        // Mengatur reminder malam
        view.findViewById<View>(R.id.card_night_reminder).setOnClickListener {
            showTimePicker(isMorning = false) { hour, minute ->
                val time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                nightTimeTextView.text = time
                setReminder(hour, minute, isMorning = false)
            }
        }

        // Permintaan izin untuk POST_NOTIFICATIONS jika belum diberikan
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!isPermissionGranted(android.Manifest.permission.POST_NOTIFICATIONS)) {
                requestNotificationPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Permintaan izin untuk SCHEDULE_EXACT_ALARM jika belum diberikan
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!isPermissionGranted(android.Manifest.permission.SCHEDULE_EXACT_ALARM)) {
                requestExactAlarmPermission.launch(android.Manifest.permission.SCHEDULE_EXACT_ALARM)
            }
        }

        return view
    }

    private fun showTimePicker(isMorning: Boolean, onTimeSet: (hour: Int, minute: Int) -> Unit) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            if (isMorning) {
                // Validasi untuk pengingat pagi (06:00–11:59)
                if (selectedHour in 6..11) {
                    onTimeSet(selectedHour, selectedMinute)
                } else {
                    showInvalidTimePopup(
                        "Invalid Morning Time",
                        "We do not recommend doing skincare outside of the morning hours. Please select a time between 06:00–11:59 for your morning skincare reminder."
                    )
                }
            } else {
                // Validasi untuk pengingat malam (20:00–23:59)
                if (selectedHour in 20..23 || (selectedHour == 0 && selectedMinute == 0)) {
                    onTimeSet(selectedHour, selectedMinute)
                } else {
                    showInvalidTimePopup(
                        "Invalid Evening Time",
                        "We do not recommend doing skincare outside of the evening hours. Please select a time between 20:00–23:59 for your evening skincare reminder."
                    )
                }
            }
        }, hour, minute, true).show()
    }

    private fun showInvalidTimePopup(title: String, message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Got It") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun setReminder(hour: Int, minute: Int, isMorning: Boolean) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Jika waktu yang dipilih sudah terlewati untuk hari ini, jadwalkan untuk hari berikutnya
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("isMorning", isMorning) // Identifikasi apakah ini pengingat pagi/malam
        }

        val requestCode = if (isMorning) 1 else 2 // ID unik untuk pagi/malam
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY, // Interval 24 jam
                    pendingIntent
                )
            } else {
                Toast.makeText(requireContext(), "Permission to schedule exact alarms is denied", Toast.LENGTH_SHORT).show()
            }
        } else {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }

        Toast.makeText(
            requireContext(),
            if (isMorning) "Morning reminder set for ${hour}:${minute}" else "Night reminder set for ${hour}:${minute}",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
    }
}
