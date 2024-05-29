package com.example.game.ViewModel.Events

import android.app.AlertDialog
import android.content.Context
import android.view.MotionEvent
import com.example.game.Model.DTO.GameCurrentStateDTO
import com.example.game.ViewModel.Profiles.MovementDirection
import java.util.LinkedList
import kotlin.math.sqrt

class MovementEvent {

    private var lastX:Float = 0f;
    private var lastY:Float = 0f;
    private val calculateToArea: (Number) -> Int = { size -> sqrt(size.toDouble()).toInt() }
    private val calculateIndexRow:(area:Int,row:Int) -> Int = {area,row-> area.times(row-1)}
    private val calculateIndexCol:(area:Int,row:Int,col:Int) -> Int = {
        area,row,col-> area.times(row-1)+(col-1)
    }
    // Var para modificar valor de ficha ganadora
    private val verifyWinBoxInBoard: (List<Int>) -> Unit = { board ->
        if (board.find { value -> value == 2048 } != null) {
            throw Error("You Won!")
        }
    }
    fun movement(
        event: MotionEvent,
        gameCurrentStateDTO: GameCurrentStateDTO,
        context: Context,
        alertEvent:()->Unit
    ):GameCurrentStateDTO{

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                this.lastX = event.x;
                this.lastY = event.y;
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                val movementDirection: MovementDirection? = resolveOrientationEvent(
                    event.x - this.lastX,
                    event.y - this.lastY
                )
                this.lastY = 0f;
                this.lastX = 0f;
                if(movementDirection is MovementDirection){
                    try {
                        val result:Pair<Number,List<Int>> = this.executeMovement(
                            movementDirection,
                            gameCurrentStateDTO.score,
                            gameCurrentStateDTO.board
                        )
                        gameCurrentStateDTO.board =  result.second as LinkedList<Int>
                        gameCurrentStateDTO.score = result.first.toInt()
                        gameCurrentStateDTO.best = if(gameCurrentStateDTO.score>gameCurrentStateDTO.best)gameCurrentStateDTO.score
                        else
                            gameCurrentStateDTO.best
                        verifyWinBoxInBoard(gameCurrentStateDTO.board)
                    }catch (e:Error){
                        val message:String = if (e.message==null) "Error" else e.message!!
                        showAlert(context,
                            message,
                            "New Game",
                            alertEvent
                        )
                    }
                }
            }
        }
        return gameCurrentStateDTO
    }
    private fun showAlert(context:Context,title:String,buttonMessage:String,event:()->Unit){
        AlertDialog.Builder(context)
            .setTitle(title)
            .setPositiveButton(buttonMessage){ _, _ ->event()}
            .show();
    }
    private fun executeMovement(
        direction: MovementDirection,
        score: Int,
        board: List<Int>
    ): Pair<Number,List<Int>>{
        val area:Int = calculateToArea(board.size)
        verifyGameOverInBoard(board,area)
        val resultMovement:Pair<Number, LinkedList<Int>> = horizontalAndVerticalMovement(
            direction,
            score,
            board,
            area
        )
        val newBoard = if(!equalsBoards(board,resultMovement.second))
            generatorRandomValue(resultMovement.second)
        else
            resultMovement.second

        return Pair(resultMovement.first,newBoard)
    }
    private fun resolveOrientationEvent(deltaX: Float, deltaY: Float): MovementDirection? {
        return when {
            deltaX > 0 && Math.abs(deltaX) > Math.abs(deltaY) -> MovementDirection.RIGHT
            deltaX < 0 && Math.abs(deltaX) > Math.abs(deltaY) -> MovementDirection.LEFT
            deltaY > 0 && Math.abs(deltaY) > Math.abs(deltaX) -> MovementDirection.DOWN
            deltaY < 0 && Math.abs(deltaY) > Math.abs(deltaX) -> MovementDirection.UP
            else -> null
        }
    }
    private fun equalsBoards(board: List<Int>, newBoard: List<Int>): Boolean {
        for (i in board.indices) {
            if (board[i] != newBoard[i]) {
                return false
            }
        }
        return true
    }
    private fun verifyBoxEmptyInBoard(board: LinkedList<Int>):Boolean{
        return board.find { it == 0 } is Int
    }
    private fun verifyGameOverInBoard(board: List<Int>,area: Int = calculateToArea(board.size)){
        for (i in MovementDirection.entries){
            if (
                !equalsBoards(
                    board,
                    horizontalAndVerticalMovement(i,0,board,area).second
                )
            ){
                return
            }
        }
        throw Error("Game Over!")
    }

    private fun generatorRandomValue(board: LinkedList<Int>):LinkedList<Int>{
        if (!verifyBoxEmptyInBoard(board))
            throw Error("Game Over!")
        val range = board.indices
        val valueRandom:Int = listOf(2,4).random()
        var  i = true
        while (i){
            val index = range.random()
            if(board[index] == 0){
                board[index] =  valueRandom
                i = false
            }
        }
        return board
    }

    private fun horizontalAndVerticalMovement(
        direction: MovementDirection,
        scoreSum:Int,
        board: List<Int>,
        area:Int = calculateToArea(board.size)
    ):Pair<Number,LinkedList<Int>> {
        var newScoreSum = scoreSum;
        var newBoard: MutableList<Int> = board.toMutableList()
        val getElementToBoard = if(
            direction == MovementDirection.RIGHT ||
            direction == MovementDirection.LEFT
        ){
            ::getRowToBoard
        }else {
            ::getColumnBoard
        }
        for (element in 1..area){
            val resultElement:Pair<List<Int>,List<Int>> = getElementToBoard(element,board,area)
            val elementValues: MutableList<Int> = resultElement.first.map { it }.toMutableList()
            if (direction == MovementDirection.RIGHT ||direction == MovementDirection.DOWN){
                elementValues.reverse()
            }
            val result:Pair<Int, MutableList<Int>> = shift(elementValues)
            val newElement = if (direction == MovementDirection.RIGHT ||direction == MovementDirection.DOWN){
                result.second.reverse()
                result.second
            }else
                result.second
            newBoard = replaceElement(newBoard,newElement,resultElement.second)
            newScoreSum += result.first
        }
        return Pair(newScoreSum,LinkedList(newBoard))
    }
    private fun shift(
        element:MutableList<Int>,
    ):Pair<Int,MutableList<Int>>{
        var i = 0
        var score = 0
        val size = element.size;
        while (i < size){
            var j = i + 1
            while (j < size){
                if (element[i]!=0 && element[i] == element[j]){
                    element[i] += element[j]
                    score += element[i]
                    element[j] = 0
                    i++
                }else if(element[i] == 0 && element[j] != 0){
                    element[i] = element[j]
                    element[j] = 0
                }else if(element[j] != 0){
                    i++
                    if(element[i] == 0)
                        j--
                }
                j++
            }
            i++
        }
        return  Pair(score,element)
    }
    private fun replaceElement(
        board: MutableList<Int>,
        element: MutableList<Int>,
        indices:List<Int>
    ):MutableList<Int>{
        var i = 0
        while (i<element.size){
            board[indices[i]] = element[i]
            i++
        }
        return board
    }
    private fun getColumnBoard(
        colum:Int,
        board: List<Int>,
        area:Int = calculateToArea(board.size)
    ): Pair<List<Int>,List<Int>>{
        val col:LinkedList<Int> = LinkedList()
        val indexsCol: LinkedList<Int> = LinkedList()
        for(i in 1..area){
            val indexElementInCol = calculateIndexCol(area,i,colum)
            val valueBox = board[indexElementInCol]
            indexsCol.add(indexElementInCol)
            col.add(valueBox)
        }
        return Pair(col,indexsCol)
    }
    private fun getRowToBoard(
        row:Int,
        board: List<Int>,
        area:Int = calculateToArea(board.size)
    ): Pair<List<Int>,MutableList<Int>>{
        val indexInit = calculateIndexRow(area,row)
        val values = board.subList(indexInit,indexInit+area)
        val index = (indexInit..<indexInit + area).toMutableList()
        return Pair(values,index)
    }
}