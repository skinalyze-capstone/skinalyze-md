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

    // Request Notification Permission
    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            showPermissionResultToast("Notification permission", isGranted)
        }

    // Request Exact Alarm Permission
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

        // Initialize Views
        initializeViews(view)

        // Initialize Switch and SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("ReminderSettings", Context.MODE_PRIVATE)
        setupSwitches(view, sharedPref)

        // Request Permissions
        requestNecessaryPermissions()

        return view
    }

    private fun initializeViews(view: View) {
        morningTimeTextView = view.findViewById(R.id.txt_morning_time)
        nightTimeTextView = view.findViewById(R.id.txt_night_time)
    }

    private fun setupSwitches(view: View, sharedPref: SharedPreferences) {
        val morningSwitch = view.findViewById<SwitchCompat>(R.id.switch_morning_reminder)
        val nightSwitch = view.findViewById<SwitchCompat>(R.id.switch_night_reminder)
        val darkModeSwitch = view.findViewById<SwitchCompat>(R.id.switch_dark_mode)

        // Load switch states from SharedPreferences
        morningSwitch.isChecked = sharedPref.getBoolean("morningReminder", false)
        nightSwitch.isChecked = sharedPref.getBoolean("nightReminder", false)

        setupDarkModeSwitch(darkModeSwitch)

        // Listener for morning switch
        morningSwitch.setOnCheckedChangeListener { _, isChecked ->
            handleMorningSwitch(isChecked, sharedPref)
        }

        // Listener for night switch
        nightSwitch.setOnCheckedChangeListener { _, isChecked ->
            handleNightSwitch(isChecked, sharedPref)
        }

        // Set reminder time pickers
        setupTimePickers(view)
    }

    private fun setupDarkModeSwitch(darkModeSwitch: SwitchCompat) {
        val sharedPref = requireContext().getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        val isDarkModeEnabled = sharedPref.getBoolean("DARK_MODE", false)

        // Set initial switch state
        darkModeSwitch.isChecked = isDarkModeEnabled

        // Set AppCompatDelegate according to preference
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkModeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        // Listener for dark mode switch
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
        // Set up morning reminder time picker
        view.findViewById<View>(R.id.card_morning_reminder).setOnClickListener {
            showTimePicker(isMorning = true) { hour, minute ->
                val time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                morningTimeTextView.text = time
                setReminder(hour, minute, isMorning = true)
            }
        }

        // Set up night reminder time picker
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
        if (isChecked) {
            val time = morningTimeTextView.text.toString().split(":")
            val hour = time.getOrNull(0)?.toIntOrNull() ?: 6
            val minute = time.getOrNull(1)?.toIntOrNull() ?: 0
            setReminder(hour, minute, isMorning = true)
        } else {
            cancelReminder(isMorning = true)
        }
        sharedPref.edit().putBoolean("morningReminder", isChecked).apply()
    }

    private fun handleNightSwitch(isChecked: Boolean, sharedPref: SharedPreferences) {
        if (isChecked) {
            val time = nightTimeTextView.text.toString().split(":")
            val hour = time.getOrNull(0)?.toIntOrNull() ?: 20
            val minute = time.getOrNull(1)?.toIntOrNull() ?: 0
            setReminder(hour, minute, isMorning = false)
        } else {
            cancelReminder(isMorning = false)
        }
        sharedPref.edit().putBoolean("nightReminder", isChecked).apply()
    }

    private fun showTimePicker(isMorning: Boolean, onTimeSet: (hour: Int, minute: Int) -> Unit) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            if (isMorning) {
                if (selectedHour in 6..9) {
                    onTimeSet(selectedHour, selectedMinute)
                } else {
                    showInvalidTimePopup(
                        "Invalid Morning Time",
                        "We don't recommend morning skincare at that time. Please select a time between 06:00 and 09:59 for your morning skincare reminder."
                    )
                }
            } else {
                if (selectedHour in 20..23 || (selectedHour == 0 && selectedMinute == 0)) {
                    onTimeSet(selectedHour, selectedMinute)
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
            }
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
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
