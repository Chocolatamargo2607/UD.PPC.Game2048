package com.example.game.ViewModel.Events

import android.content.Context
import android.view.MotionEvent
import com.example.game.Model.DTO.GameCurrentStateDTO
import com.example.game.ViewModel.MainViewModel
import java.util.LinkedList

class GameEventController(

    private val mainViewModel: MainViewModel,
    private val movementEvent: MovementEvent,
    private val context: Context
) {
    fun eventGrid(event: MotionEvent): Boolean {
        var gameCurrentStateDTO: GameCurrentStateDTO = mainViewModel.observableGameCurrentStateDTO.value ?: return false
        gameCurrentStateDTO = movementEvent.movementInBoard(
            event,
            gameCurrentStateDTO,
            context,
            ::eventNewGame
        )
        mainViewModel.updateGameState(gameCurrentStateDTO)
        return true
    }

    fun eventNewGame(): Boolean {
        val gameCurrentStateDTO: GameCurrentStateDTO = mainViewModel.observableGameCurrentStateDTO.value ?: return false
        gameCurrentStateDTO.board = LinkedList(mainViewModel.gridView.generatorInitialValues(gameCurrentStateDTO.board.size))
        gameCurrentStateDTO.score = 0
        mainViewModel.updateGameState(gameCurrentStateDTO)
        return true
    }
}