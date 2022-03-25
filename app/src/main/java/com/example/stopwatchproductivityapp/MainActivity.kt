package org.hyperskill.stopwatch

import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat

const val CHANNEL_ID = "org.hyperskill"
const val NOTIFICATION_ID = 393939

class MainActivity : AppCompatActivity() {
    private val colorList: Array<Int> = arrayOf(Color.GRAY, Color.BLUE, Color.GREEN, Color.RED, Color.CYAN, Color.MAGENTA)
    private val handler = Handler(Looper.getMainLooper())

    var currentColor = Color.GRAY
    var isTimerRunning = false
    var timer = 0
    var upperLimit = Int.MAX_VALUE

    private fun setColor(progressBar: ProgressBar) {
        var result: Int
        do {
            val pickedColor = colorList[(colorList.indices).random()]
            result = pickedColor
        } while (pickedColor == currentColor)
        progressBar.indeterminateTintList = ColorStateList.valueOf(result)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val startButton = findViewById<Button>(R.id.startButton)
        val resetButton = findViewById<Button>(R.id.resetButton)
        val settingsButton = findViewById<Button>(R.id.settingsButton)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val contentView = LayoutInflater.from(this).inflate(R.layout.activity_alert_dialog, null, false)

        val dialog = AlertDialog.Builder(this)
                .setView(contentView)
                .setPositiveButton("OK") { dialogInterface: DialogInterface, i: Int ->
                    try {
                        val number = contentView.findViewById<EditText>(R.id.upperLimitEditText).text.toString().toInt()
                        upperLimit = number
                    } catch (_: Exception) {

                    }
                    dialogInterface.dismiss()
                }
                .setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
                .create()

        val channel = NotificationChannel(CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_HIGH).apply {
            description = "Time exceeded"
        }
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.abc_vector_test)
                .setContentTitle("Notification")
                .setContentText("Time exceeded")
                .setPriority(NotificationCompat.PRIORITY_HIGH)

        val updateTimer: Runnable by lazy {
            object : Runnable {
                override fun run() {
                    val textView = findViewById<TextView>(R.id.textView)
                    setColor(progressBar)
                    timer += 1
                    val minutes = timer / 60
                    val seconds = timer % 60
                    "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}".also {
                        textView.text = it
                        if (timer >= upperLimit) {
                            textView.setTextColor(Color.RED)
                            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
                        }
                    }
                    handler.postDelayed(this, 1000)
                }
            }
        }

        val finishTimer = Runnable {
            val textView = findViewById<TextView>(R.id.textView)
            textView.text = getString(R.string.timer_default_value)
            textView.setTextColor(Color.BLACK)
            upperLimit = Int.MAX_VALUE
        }

        startButton.setOnClickListener {
            if (!isTimerRunning) {
                setColor(progressBar)
                progressBar.visibility = View.VISIBLE
                settingsButton.isEnabled = false
                handler.postDelayed(updateTimer, 1000)
                isTimerRunning = true
            }
        }

        resetButton.setOnClickListener {
            if (isTimerRunning) {
                handler.removeCallbacks(updateTimer)
                progressBar.visibility = View.INVISIBLE
                settingsButton.isEnabled = true
                isTimerRunning = false
                timer = 0
                handler.post(finishTimer)
            }
        }

        settingsButton.setOnClickListener {
            dialog.show()
        }
    }
}
