package com.example.autoairplane


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.webkit.WebStorage
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var dateText:TextInputEditText
    lateinit var sharedPreferences:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dateText = findViewById<TextInputEditText>(R.id.date_text)
        val dateTimeButton = findViewById<Button>(R.id.date_button)
        val resetTimeButton=findViewById<Button>(R.id.resetDate_button)
        val durationTime = findViewById<TextInputEditText>(R.id.duration)
        val doneButton = findViewById<Button>(R.id.Set_Timer)
        lateinit var startHour:String
        lateinit var startMinute:String
        lateinit var selectedDate:String
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        val dateTimeFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        val currentDateTime = dateTimeFormat.format(Date())

        dateText.setText(currentDateTime)
        val dateBuilder = MaterialDatePicker.Builder.datePicker()
        val datePicker = dateBuilder.build()

        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(12)
            .setMinute(0)
            .build()

        dateTimeButton.setOnClickListener {
            datePicker.show(supportFragmentManager, "date_tag")
        }

        datePicker.addOnPositiveButtonClickListener {
            // update selected date in text field
            selectedDate=datePicker.headerText
            // show time picker
            timePicker.show(supportFragmentManager, "time_tag")
            timePicker.addOnPositiveButtonClickListener {
                // update selected time in text field
                startHour = timePicker.hour.toString().padStart(2, '0')
                startMinute = timePicker.minute.toString().padStart(2, '0')
                dateText.setText(selectedDate + "  $startHour:$startMinute ")

                sharedPreferences.edit().putString("dateTime", dateText.text.toString()).apply()


            }
            timePicker.addOnCancelListener{
                val savedDateTime = sharedPreferences.getString("dateTime", null)
                if (savedDateTime != null) {
                    dateText.setText(savedDateTime)
                }
                else{
                   // val dateTimeFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                    val currentDateTime = dateTimeFormat.format(Date())

                    dateText.setText(currentDateTime)
                }
            }
        }

        resetTimeButton.setOnClickListener {
            val cacheDir = cacheDir
            if (cacheDir.isDirectory) {
                cacheDir.deleteRecursively()
            }
            WebStorage.getInstance().deleteAllData()
            // Clear SharedPreferences
            val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()
           // val dateTimeFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            val currentDateTime = dateTimeFormat.format(Date())
            dateText.setText(currentDateTime)
        }

    }

    override fun onResume() {
        super.onResume()

        // Retrieve the saved date and time and set it in the text box
        val savedDateTime = sharedPreferences.getString("dateTime", null)
        if (savedDateTime != null) {
            dateText.setText(savedDateTime)
        }
        else{
            val dateTimeFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            val currentDateTime = dateTimeFormat.format(Date())

            dateText.setText(currentDateTime)
        }
    }
}