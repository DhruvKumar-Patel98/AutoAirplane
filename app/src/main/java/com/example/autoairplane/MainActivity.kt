package com.example.autoairplane


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.webkit.WebStorage
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
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
        lateinit var endHour:String
        lateinit var endMinute:String
        lateinit var selectedStartDate:String
        lateinit var selectedEndDate:String
        lateinit var startTime:String
        lateinit var endTime:String
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

        val dateBuilder2 = MaterialDatePicker.Builder.datePicker()
        val datePicker2 = dateBuilder2.build()

        val timePicker2 = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(12)
            .setMinute(0)
            .build()

        dateTimeButton.setOnClickListener {
            datePicker.show(supportFragmentManager, "date_tag")
        }

        datePicker.addOnPositiveButtonClickListener {
            // update selected date in text field
            selectedStartDate=datePicker.headerText
            // show time picker
            timePicker.show(supportFragmentManager, "time_tag")

            timePicker.addOnPositiveButtonClickListener {
                // update selected time in text field
                startHour = timePicker.hour.toString().padStart(2, '0')
                startMinute = timePicker.minute.toString().padStart(2, '0')
                startTime=selectedStartDate + "  $startHour:$startMinute "
                dateText.setText(startTime)

                sharedPreferences.edit().putString("startTime",startTime).apply()
                datePicker2.show(supportFragmentManager,"date_tag2")
            }
        }
        timePicker.addOnNegativeButtonClickListener{
            val currentDateTime = dateTimeFormat.format(Date())
            dateText.setText(currentDateTime)
        }

        datePicker2.addOnPositiveButtonClickListener {
            // update selected date in text field
            selectedEndDate=datePicker2.headerText
            // show time picker
            timePicker2.show(supportFragmentManager, "time_tag2")
            timePicker2.addOnPositiveButtonClickListener {
                // update selected time in text field
                endHour = timePicker2.hour.toString().padStart(2, '0')
                endMinute = timePicker2.minute.toString().padStart(2, '0')
                endTime=selectedEndDate+" "+endHour+":"+endMinute
                dateText.setText(startTime+"\u2192"+endTime)

                sharedPreferences.edit().putString("endTime", endTime).apply()
            }
            timePicker2.addOnCancelListener{
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
        val savedStartDateTime = sharedPreferences.getString("startTime", null)
        val savedEndDateTime = sharedPreferences.getString("endTime", null)
        if (savedStartDateTime != null&&savedEndDateTime!=null) {
            dateText.setText(savedStartDateTime+"→"+savedEndDateTime)
        }
        else{
            val dateTimeFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            val currentDateTime = dateTimeFormat.format(Date())

            dateText.setText(currentDateTime)
        }
    }
}