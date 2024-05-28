package com.example.game.ViewModel.Service

import android.content.Context
import android.view.MotionEvent
import com.example.game.Model.DTO.GameState
import com.example.game.ViewModel.MainViewModel

class GameEventHandler(
    private val mainViewModel: MainViewModel,
    private val eventToMovement: EventToMovement,
    private val context: Context
) {
    fun eventGrid(event: MotionEvent): Boolean {
        var currentGameState: GameState = mainViewModel.observableGameState.value ?: return false
        currentGameState = eventToMovement.eventMove(
            event,
            currentGameState,
            context,
            ::eventNewGame
        )
        mainViewModel.updateGameState(currentGameState)
        return true
    }

    fun eventNewGame(): Boolean {
        val currentGameState: GameState = mainViewModel.observableGameState.value ?: return false
        currentGameState.board = mainViewModel.gridView.generatorInitialValues(currentGameState.board.size)
        currentGameState.score = 0
        mainViewModel.updateGameState(currentGameState)
        return true
    }
}


