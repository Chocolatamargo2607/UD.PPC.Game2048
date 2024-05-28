package com.example.game.ViewModel.Service

import android.annotation.SuppressLint
import android.widget.Button
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.example.game.R
import com.example.game.ViewModel.MainViewModel


// MainActivityUIHandler.kt
class MainActivityUIHandler(
    private val activity: AppCompatActivity,
    private val mainViewModel: MainViewModel
) {
    private lateinit var gridView: GridView
    private lateinit var gameEventHandler: GameEventHandler

    @SuppressLint("ClickableViewAccessibility")
    fun setupUI() {
        val containerLinearLayout: LinearLayout = activity.findViewById(R.id.container_board)
        val buttonNewGame: Button = activity.findViewById(R.id.btn_newGame)
        val bestText: TextView = activity.findViewById(R.id.input_text_best)
        val scoreText: TextView = activity.findViewById(R.id.input_text_score)

        gridView = mainViewModel.gridView.generatorBoard(activity)
        gameEventHandler = GameEventHandler(mainViewModel, EventToMovement(), activity)

        gridView.setOnTouchListener { _, event -> gameEventHandler.eventGrid(event) }
        containerLinearLayout.addView(gridView)

        buttonNewGame.setOnClickListener {
            gameEventHandler.eventNewGame()
        }

        mainViewModel.observableGameState.observe(activity, Observer {
            mainViewModel.gridView.gameState = it
            gridView = mainViewModel.gridView.updateBoard(activity)
            bestText.text = it.best.toString()
            scoreText.text = it.score.toString()
        })

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(activity.findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun enableEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)
    }
}

