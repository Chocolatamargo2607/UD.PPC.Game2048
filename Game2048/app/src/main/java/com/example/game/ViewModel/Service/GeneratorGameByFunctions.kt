package com.example.game.ViewModel.Service

import android.content.Context
import android.widget.GridView
import com.example.game.Model.DTO.GameState
import com.example.game.View.Components.BoardView
import com.example.game.ViewModel.Interface.InterfaceGeneratorGame
import java.util.LinkedList

class GeneratorGameByFunctions(
    override val columns: Int,
    best:Int = 0,
) : InterfaceGeneratorGame
{
    var gameState: GameState;
    lateinit var boardView: BoardView
    init {
        val board =this.generatorInitialValues(this.columns.times(this.columns))
        this.gameState = GameState(
            board = LinkedList(board),
            score = 0,
            best = best
        )

    }
    override fun generatorBoard(context: Context): GridView {
        boardView = BoardView(
            context,
            this.columns,
            gameState.board
        )
        return boardView.grid
    }
    fun updateBoard(context: Context):GridView{
        boardView.content = gameState.board
        return boardView.updateContentGird(context)
    }
    fun generatorInitialValues(size:Int):List<Short>{
        val range:IntRange = 0..<size
        val index1:Int = range.random()
        var index2:Int
        do {
            index2 = range.random()
        }while (index1 == index2)
        val items: List<Short> = List(size,) {
            if (it == index1 || it == index2) listOf(2,4).random().toShort() else 0
        }
        return items
    }

}