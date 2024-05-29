package com.example.game.ViewModel.Events

import android.content.Context
import android.widget.GridView
import com.example.game.Model.DTO.GameCurrentStateDTO
import com.example.game.View.Board
import com.example.game.ViewModel.GeneratorBoardGameInterface
import java.util.LinkedList

class GameInterfaceGeneratorBoard(
    override val columns: Int,
    best:Int = 0,
) : GeneratorBoardGameInterface
{
    var gameCurrentStateDTO: GameCurrentStateDTO
    lateinit var board: Board
    init {
        val board =this.generatorInitialValues(this.columns.times(this.columns))
        this.gameCurrentStateDTO = GameCurrentStateDTO(
            board = LinkedList(board),
            score = 0,
            best = best
        )

    }
    override fun generatorBoard(context: Context): GridView {
        board = Board(
            context,
            this.columns,
            gameCurrentStateDTO.board
        )
        return board.grid
    }
    fun updateBoard(context: Context):GridView{
        board.content = gameCurrentStateDTO.board
        return board.updateContentGird(context)
    }
    fun generatorInitialValues(size:Int):List<Int>{
        val range:IntRange = 0..<size
        val index1:Int = range.random()
        var index2:Int
        do {
            index2 = range.random()
        }while (index1 == index2)
        val items: List<Int> = List(size,) {
            if (it == index1 || it == index2) listOf(2,4).random() else 0
        }
        return items
    }

}