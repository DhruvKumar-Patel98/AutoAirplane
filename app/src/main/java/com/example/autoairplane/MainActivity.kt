package com.example.autoairplane


import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.webkit.WebStorage
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var dateText:TextInputEditText
    lateinit var sharedPreferences:SharedPreferences
    lateinit var durationTime:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dateText = findViewById<TextInputEditText>(R.id.date_text)
        val dateTimeButton = findViewById<Button>(R.id.date_button)
        val resetTimeButton=findViewById<Button>(R.id.resetDate_button)
        durationTime = findViewById<TextInputEditText>(R.id.duration)
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


        val dateBuilder2 = MaterialDatePicker.Builder.datePicker()
        val datePicker2 = dateBuilder2.build()

        dateTimeButton.setOnClickListener {
            datePicker.show(supportFragmentManager, "date_tag")
        }

        datePicker.addOnPositiveButtonClickListener {

            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .build()
            // update selected date in text field
            selectedStartDate=datePicker.headerText
            // show time picker
            timePicker.show(supportFragmentManager, "time_tag")

            timePicker.addOnPositiveButtonClickListener {
                // update selected time in text field
                startHour = timePicker.hour.toString().padStart(2, '0')
                startMinute = timePicker.minute.toString().padStart(2, '0')

                //MMM dd, yyyy
                val inputFormat=SimpleDateFormat("MMM d, yyyy", Locale.US)
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val date = inputFormat.parse(selectedStartDate)
                val startDate = dateFormat.format(date)

                startTime=startDate + " $startHour:$startMinute"
                dateText.setText(startTime)

                sharedPreferences.edit().putString("startTime",startTime).apply()
                datePicker2.show(supportFragmentManager,"date_tag2")
            }
            timePicker.addOnNegativeButtonClickListener{
                val currentDateTime = dateTimeFormat.format(Date())
                dateText.setText(currentDateTime)
            }

        }

        datePicker2.addOnPositiveButtonClickListener {

            val timePicker2 = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .build()
            // update selected date in text field
            selectedEndDate=datePicker2.headerText
            // show time picker
            timePicker2.show(supportFragmentManager, "time_tag2")
            timePicker2.addOnPositiveButtonClickListener {
                // update selected time in text field
                endHour = timePicker2.hour.toString().padStart(2, '0')
                endMinute = timePicker2.minute.toString().padStart(2, '0')

                //MMM dd, yyyy
                val inputFormat=SimpleDateFormat("MMM d, yyyy", Locale.US)
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val date = inputFormat.parse(selectedEndDate)
                val endDate = dateFormat.format(date)

                endTime=endDate+" "+endHour+":"+endMinute
                dateText.setText(startTime+"\u2192"+endTime)

                sharedPreferences.edit().putString("endTime", endTime).apply()
                val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
                val st = LocalDateTime.parse(startTime, formatter)
                val et = LocalDateTime.parse(endTime, formatter)

                val duration=Duration.between(st,et)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    durationTime.setText(duration.toHoursPart().toString()+"H "+duration.toMinutesPart().toString()+"M")
                }
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
            durationTime.setText("")
        }
    }

    override fun onResume() {
        super.onResume()

        // Retrieve the saved date and time and set it in the text box
        val savedStartDateTime = sharedPreferences.getString("startTime", null)
        val savedEndDateTime = sharedPreferences.getString("endTime", null)
        if (savedStartDateTime != null&&savedEndDateTime!=null) {
            dateText.setText(savedStartDateTime+"→"+savedEndDateTime)
            val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
            val st = LocalDateTime.parse(savedStartDateTime, formatter)
            val et = LocalDateTime.parse(savedEndDateTime, formatter)

            val duration=Duration.between(st,et)
            durationTime.setText(duration.toHours().toString()+"H "+duration.toMinutes()+"M")
        }
        else{
            val dateTimeFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            val currentDateTime = dateTimeFormat.format(Date())

            dateText.setText(currentDateTime)
        }
    }
}