package org.hyperskill.stopwatch

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.DrawableCompat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())
    var timer = 0
    var isTimerRunning = false
    val colorList: Array<Int> = arrayOf(Color.GRAY,Color.BLACK,Color.BLUE, Color.GREEN, Color.RED)
    var currentColor = Color.GRAY
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
                val progressBar = findViewById<ProgressBar>(R.id.progressBar)
                progressBar.indeterminateTintList = ColorStateList.valueOf(currentColor)
                progressBar.visibility = View.VISIBLE
                handler.postDelayed(updateTimer, 1000)
                isTimerRunning = true
            }
        }
        resetButton.setOnClickListener {
            if (isTimerRunning) {
                handler.removeCallbacks(updateTimer)
                val progressBar = findViewById<ProgressBar>(R.id.progressBar)
                progressBar.visibility = View.INVISIBLE
                isTimerRunning = false
                timer = 0
                handler.post(finishTimer)
            }
        }
    }
}
