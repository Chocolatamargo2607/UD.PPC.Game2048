package com.example.game.ViewModel.Events

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
import com.example.game.Model.FirebaseService.GameStateManager
import com.example.game.Model.FirebaseService.UniqueIDManager
import com.example.game.R
import com.example.game.ViewModel.MainViewModel

class MainActivityInterfaceController(
    private val activity: AppCompatActivity,
    private val mainViewModel: MainViewModel
) {
    private lateinit var gridView: GridView
    private lateinit var gameEventController: GameEventController

    @SuppressLint("ClickableViewAccessibility")
    fun setupUI() {
        val userId = UniqueIDManager.getUniqueID(activity)
        val containerLinearLayout: LinearLayout = activity.findViewById(R.id.container_board)
        val buttonNewGame: Button = activity.findViewById(R.id.btn_newGame)
        val buttonSaveGame: Button = activity.findViewById(R.id.btn_saveGame)
        val bestText: TextView = activity.findViewById(R.id.input_text_best)
        val scoreText: TextView = activity.findViewById(R.id.input_text_score)

        gridView = mainViewModel.gridView.generatorBoard(activity)
        gameEventController = GameEventController(mainViewModel, MovementEvent(), activity)

        gridView.setOnTouchListener { _, event -> gameEventController.eventGrid(event) }
        containerLinearLayout.addView(gridView)

        val gameStateManager = GameStateManager()
        gameStateManager.loadGameState(activity, userId) { savedGameState -> // Pasar `activity` como `Context`
            if (savedGameState != null) {
                mainViewModel.updateGameState(savedGameState)
            }
        }
        buttonNewGame.setOnClickListener {
            gameEventController.eventNewGame()
        }

        buttonSaveGame.setOnClickListener {
            val currentGameState = mainViewModel.observableGameCurrentStateDTO.value
            if (currentGameState != null) {
                gameStateManager.saveGameState(activity, currentGameState) // Pasar `activity` como `Context`
            }
        }
        mainViewModel.observableGameCurrentStateDTO.observe(activity, Observer {
            mainViewModel.gridView.gameCurrentStateDTO = it
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
