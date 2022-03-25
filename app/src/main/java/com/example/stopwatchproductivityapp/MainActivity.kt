package org.hyperskill.stopwatch

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())
    var timer = 0
    var isTimerRunning = false
    val colorList: Array<Int> = arrayOf(Color.GRAY, Color.BLACK, Color.BLUE, Color.GREEN, Color.RED)
    var currentColor = Color.GRAY
    var upperLimit = Int.MAX_VALUE
    private val updateTimer: Runnable = object : Runnable {
        override fun run() {
            val textView = findViewById<TextView>(R.id.textView)
            val progressBar = findViewById<ProgressBar>(R.id.progressBar)
            var result: Int
            do {
                val pickedColor = colorList[(colorList.indices).random()]
                result = pickedColor
            } while (pickedColor == currentColor)
            progressBar.indeterminateTintList = ColorStateList.valueOf(result)
            timer += 1
            val minutes = timer / 60
            val seconds = timer % 60
            "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}".also {
                textView.text = it
                if (timer >= upperLimit) {
                    textView.setTextColor(Color.RED)
                }
            }
            handler.postDelayed(this, 1000)
        }
    }

    private val finishTimer: Runnable = Runnable {
        val textView = findViewById<TextView>(R.id.textView)
        textView.text = getString(R.string.timer_default_value)
        textView.setTextColor(Color.BLACK)
        upperLimit = Int.MAX_VALUE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val startButton = findViewById<Button>(R.id.startButton)
        val resetButton = findViewById<Button>(R.id.resetButton)
        val settingsButton = findViewById<Button>(R.id.settingsButton)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val contentView = LayoutInflater.from(this).inflate(R.layout.activity_alert_dialog, null, false)

        startButton.setOnClickListener {
            if (!isTimerRunning) {
                progressBar.indeterminateTintList = ColorStateList.valueOf(currentColor)
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

        settingsButton.setOnClickListener {
            dialog.show()
        }
    }
}
