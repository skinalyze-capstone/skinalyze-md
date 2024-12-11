package com.dicoding.skinalyze.ui.userprofile

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dicoding.skinalyze.AlarmReceiver
import com.dicoding.skinalyze.R
import java.util.Calendar
import java.util.Locale

class SettingFragment : Fragment() {

    private lateinit var morningTimeTextView: TextView
    private lateinit var nightTimeTextView: TextView
    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            showPermissionResultToast("Notification permission", isGranted)
        }
    private val requestExactAlarmPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            showPermissionResultToast("Exact alarm permission", isGranted)
        }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        initializeViews(view)

        val sharedPref = requireContext().getSharedPreferences("ReminderSettings", Context.MODE_PRIVATE)
        setupSwitches(view, sharedPref)

        requestNecessaryPermissions()
        return view
    }

    private fun initializeViews(view: View) {
        morningTimeTextView = view.findViewById(R.id.txt_morning_time)
        nightTimeTextView = view.findViewById(R.id.txt_night_time)
    }

    @SuppressLint("SetTextI18n")
    private fun setupSwitches(view: View, sharedPref: SharedPreferences) {
        val morningSwitch = view.findViewById<SwitchCompat>(R.id.switch_morning_reminder)
        val nightSwitch = view.findViewById<SwitchCompat>(R.id.switch_night_reminder)
        val darkModeSwitch = view.findViewById<SwitchCompat>(R.id.switch_dark_mode)
        val isMorningReminder = sharedPref.getBoolean("morningReminder", false)
        val isNightReminder = sharedPref.getBoolean("nightReminder", false)

        morningSwitch.isChecked = sharedPref.getBoolean("morningReminder", false)
        nightSwitch.isChecked = sharedPref.getBoolean("nightReminder", false)
        setupDarkModeSwitch(darkModeSwitch)

        if (isMorningReminder) {
            val morningTime = sharedPref.getString("morningTime", "07:00") ?: "07:00"
            morningTimeTextView.text = morningTime
        } else {
            morningTimeTextView.text = sharedPref.getString("morningTime", "07:00")
        }

        if (isNightReminder) {
            val nightTime = sharedPref.getString("nightTime", "20:00") ?: "20:00"
            nightTimeTextView.text = nightTime
        } else {
            nightTimeTextView.text = sharedPref.getString("nightTime", "20:00")
        }

        morningSwitch.setOnCheckedChangeListener { _, isChecked ->
            handleMorningSwitch(isChecked, sharedPref)
        }

        nightSwitch.setOnCheckedChangeListener { _, isChecked ->
            handleNightSwitch(isChecked, sharedPref)
        }

        setupTimePickers(view)
    }

    private fun setupDarkModeSwitch(darkModeSwitch: SwitchCompat) {
        val sharedPref = requireContext().getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        val isDarkModeEnabled = sharedPref.getBoolean("DARK_MODE", false)
        darkModeSwitch.isChecked = isDarkModeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkModeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            saveDarkModePreference(isChecked)
        }
    }

    private fun saveDarkModePreference(isDarkMode: Boolean) {
        val sharedPreferences = requireContext().getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("DARK_MODE", isDarkMode).apply()
    }

    private fun setupTimePickers(view: View) {
        view.findViewById<View>(R.id.card_morning_reminder).setOnClickListener {
            showTimePicker(isMorning = true) { hour, minute ->
                val time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                morningTimeTextView.text = time
                setReminder(hour, minute, isMorning = true)
            }
        }

        view.findViewById<View>(R.id.card_night_reminder).setOnClickListener {
            showTimePicker(isMorning = false) { hour, minute ->
                val time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                nightTimeTextView.text = time
                setReminder(hour, minute, isMorning = false)
            }
        }
    }

    private fun requestNecessaryPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!isPermissionGranted(android.Manifest.permission.POST_NOTIFICATIONS)) {
                requestNotificationPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!isPermissionGranted(android.Manifest.permission.SCHEDULE_EXACT_ALARM)) {
                requestExactAlarmPermission.launch(android.Manifest.permission.SCHEDULE_EXACT_ALARM)
            }
        }
    }

    private fun handleMorningSwitch(isChecked: Boolean, sharedPref: SharedPreferences) {
        sharedPref.edit().putBoolean("morningReminder", isChecked).apply()
        if (isChecked) {
            val time = morningTimeTextView.text.toString().split(":")
            val hour = time.getOrNull(0)?.toIntOrNull() ?: 6
            val minute = time.getOrNull(1)?.toIntOrNull() ?: 0
            setReminder(hour, minute, isMorning = true)
            Toast.makeText(context, "Morning reminder on", Toast.LENGTH_SHORT).show()
        } else {
            cancelReminder(isMorning = true)
            Toast.makeText(context, "Morning reminder off", Toast.LENGTH_SHORT).show()
        }
        sharedPref.edit().putBoolean("morningReminder", isChecked).apply()
    }

    private fun handleNightSwitch(isChecked: Boolean, sharedPref: SharedPreferences) {
        sharedPref.edit().putBoolean("nightReminder", isChecked).apply()
        if (isChecked) {
            val time = nightTimeTextView.text.toString().split(":")
            val hour = time.getOrNull(0)?.toIntOrNull() ?: 20
            val minute = time.getOrNull(1)?.toIntOrNull() ?: 0
            setReminder(hour, minute, isMorning = false)
            Toast.makeText(context, "Night reminder on", Toast.LENGTH_SHORT).show()
        } else {
            cancelReminder(isMorning = false)
            Toast.makeText(context, "Night reminder off", Toast.LENGTH_SHORT).show()
        }
        sharedPref.edit().putBoolean("nightReminder", isChecked).apply()
    }

    private fun showTimePicker(isMorning: Boolean, onTimeSet: (hour: Int, minute: Int) -> Unit) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            val sharedPref = requireContext().getSharedPreferences("ReminderSettings", Context.MODE_PRIVATE)
            val time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
            if (isMorning) {
                if (selectedHour in 6..9) {
                    onTimeSet(selectedHour, selectedMinute)
                    sharedPref.edit().putString("morningTime", time).apply()
                    morningTimeTextView.text = time
                } else {
                    showInvalidTimePopup(
                        "Invalid Morning Time",
                        "We don't recommend morning skincare at that time. Please select a time between 06:00 and 09:59 for your morning skincare reminder."
                    )
                }
            } else {
                if (selectedHour in 20..23 || (selectedHour == 0 && selectedMinute == 0)) {
                    onTimeSet(selectedHour, selectedMinute)
                    sharedPref.edit().putString("nightTime", time).apply()
                    nightTimeTextView.text = time
                } else {
                    showInvalidTimePopup(
                        "Invalid Evening Time",
                        "We don't recommend night skincare at that time. Please select a time between 20:00–23:59 for your night skincare reminder."
                    )
                }
            }
        }, hour, minute, true).show()
    }

    private fun showInvalidTimePopup(title: String, message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Got It") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    @SuppressLint("DefaultLocale")
    private fun setReminder(hour: Int, minute: Int, isMorning: Boolean) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("isMorning", isMorning)
        }

        val requestCode = if (isMorning) 1 else 2
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            } else {
                Toast.makeText(
                    context,
                    "Permission to set exact alarms is required.",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
        Toast.makeText(
            context,
            "Reminder set for ${String.format("%02d:%02d", hour, minute)}.",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun cancelReminder(isMorning: Boolean) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("isMorning", isMorning)
        }
        val requestCode = if (isMorning) 1 else 2
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
        Log.d("CancelReminder", "Alarm canceled for requestCode: $requestCode")
    }

    private fun showPermissionResultToast(permission: String, isGranted: Boolean) {
        if (isGranted) {
            Toast.makeText(context, "$permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "$permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
    }
}
