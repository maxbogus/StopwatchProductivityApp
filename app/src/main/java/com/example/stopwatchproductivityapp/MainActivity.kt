package org.hyperskill.stopwatch

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())
    var timer = 0
    var isTimerRunning = false
    private val updateTimer: Runnable = object : Runnable {
        override fun run() {
            val textView = findViewById<TextView>(R.id.textView)
            timer += 1
            val minutes = timer / 60
            val seconds = timer % 60
            "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}".also { textView.text = it }
            handler.postDelayed(this, 1000)
        }
    }

    private val finishTimer: Runnable = Runnable {
        val textView = findViewById<TextView>(R.id.textView)
        textView.text = getString(R.string.timer_default_value)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val startButton = findViewById<Button>(R.id.startButton)
        val resetButton = findViewById<Button>(R.id.resetButton)

        startButton.setOnClickListener {
            if (!isTimerRunning) {
                handler.postDelayed(updateTimer, 100)
                isTimerRunning = true
            }
        }
        resetButton.setOnClickListener {
            if (isTimerRunning) {
                handler.removeCallbacks(updateTimer)
                isTimerRunning = false
                timer = 0
                handler.post(finishTimer)
            }
        }
    }
}
