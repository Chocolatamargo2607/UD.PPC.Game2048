package com.example.game.ViewModel.Service

import android.app.AlertDialog
import android.content.Context
import android.view.MotionEvent
import com.example.game.Model.DTO.GameState
import com.example.game.Utils.MovementDirection
import com.example.game.ViewModel.Exceptions.ErrorExceptionFinallyGame
import java.util.LinkedList
import kotlin.math.sqrt

class EventToMovement {
    private var lastX:Float = 0f;
    private var lastY:Float = 0f;
    private val calculateToArea: (Number) -> Int = { size -> sqrt(size.toDouble()).toInt() }
    private val calculateIndexRow:(area:Int,row:Int) -> Int = {area,row-> area.times(row-1)}
    private val calculateIndexCol:(area:Int,row:Int,col:Int) -> Int = {area,row,col-> area.times(row-1)+(col-1)}
    private val verifyWinBoxInBoard: (List<Short>) -> Unit = { board ->
        if (board.find { value -> value.toInt() == 2048 } != null) {
            throw ErrorExceptionFinallyGame("Congratulations you are the winner")
        }
    }
    fun eventMove(event: MotionEvent,gameState: GameState,context: Context,alertEvent:()->Unit):GameState{
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                this.lastX = event.x;
                this.lastY = event.y;
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                val movementDirection:MovementDirection? = resolveOrientationEvent(
                    event.x - this.lastX,
                    event.y - this.lastY
                )
                this.lastY = 0f;
                this.lastX = 0f;
                if(movementDirection is MovementDirection){
                    try {
                        val result:Pair<Number,List<Short>> = this.executeMovement(
                            movementDirection,
                            gameState.score,
                            gameState.board
                        )
                        gameState.board =  result.second
                        gameState.score = result.first.toInt()
                        gameState.best = if(gameState.score>gameState.best)gameState.score
                        else
                            gameState.best
                        verifyWinBoxInBoard(gameState.board)
                    }catch (e:ErrorExceptionFinallyGame){
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
        return gameState
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
        board: List<Short>
    ): Pair<Number,List<Short>>{
        val area:Int = calculateToArea(board.size)
        verifyGameOverInBoard(board,area)
        val resultMovement:Pair<Number, LinkedList<Short>> = horizontalAndVerticalMovement(
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
    private fun equalsBoards(board: List<Short>, newBoard: List<Short>): Boolean {
        for (i in board.indices) {
            if (board[i] != newBoard[i]) {
                return false
            }
        }
        return true
    }
    private fun verifyBoxEmptyInBoard(board: LinkedList<Short>):Boolean{
        val element = 0.toShort()
        return board.find { it == element } is Short
    }
    private fun verifyGameOverInBoard(board: List<Short>,area: Int = calculateToArea(board.size)){
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
        //Si en ninguna de las direcciones en la cual se simulo un movimiento existio un cambio es
        //GAME OVER
        throw ErrorExceptionFinallyGame("GAME OVER!!")
    }

    private fun generatorRandomValue(board: LinkedList<Short>):LinkedList<Short>{
        if (!verifyBoxEmptyInBoard(board))
            throw ErrorExceptionFinallyGame("GAME OVER!!")
        val range = board.indices
        val valueRandom:Short = listOf(2,4).random().toShort()
        var  i = true
        while (i){
            val index = range.random()
            if(board[index] == 0.toShort()){
                board[index] =  valueRandom
                i = false
            }
        }
        return board
    }

    private fun horizontalAndVerticalMovement(
        direction: MovementDirection,
        scoreSum:Int,
        board: List<Short>,
        area:Int = calculateToArea(board.size)
    ):Pair<Number,LinkedList<Short>> {
        var newScoreSum = scoreSum;
        var newBoardShort: MutableList<Short> = board.toMutableList()
        //es la funcion que se encarga de retornar los valores y indices
        val getElementToBoard = if(
            direction == MovementDirection.RIGHT ||
            direction == MovementDirection.LEFT
        ){
            // los :: se usan para retornar una referencia de algo en este caso de la funcion
            ::getRowToBoard
        }else {
            ::getColumnBoard
        }
        //Element puede ser o referirse a fila o columnas depende la horientacion del moviemiento
        for (element in 1..area){
            //resultElement es un objeto que contiene la lista de valores y la lista de indices
            val resultElement:Pair<List<Short>,List<Int>> = getElementToBoard(element,board,area)
            val elementValues: MutableList<Int> = resultElement.first.map { it.toInt() }.toMutableList()
            //Deacuerdo al movimiento que se hace se debe "normalizar" la entrada de al metodo shift
            if (direction == MovementDirection.RIGHT ||direction == MovementDirection.DOWN){
                elementValues.reverse()
            }
            //shift retorna el score de los movimientos y el element ya con el movimiento efectuado
            val result:Pair<Int, MutableList<Int>> = shift(elementValues)
            val newElement = if (direction == MovementDirection.RIGHT ||direction == MovementDirection.DOWN){
                result.second.reverse()
                result.second
            }else
                result.second
            newBoardShort = replaceElement(newBoardShort,newElement,resultElement.second)
            newScoreSum += result.first
        }
        return Pair(newScoreSum,LinkedList(newBoardShort))
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
        board: MutableList<Short>,
        element: MutableList<Int>,
        indices:List<Int>
    ):MutableList<Short>{
        var i = 0
        while (i<element.size){
            board[indices[i]] = element[i].toShort()
            i++
        }
        return board
    }
    private fun getColumnBoard(
        colum:Int,
        board: List<Short>,
        area:Int = calculateToArea(board.size)
    ): Pair<List<Short>,List<Int>>{
        val col:LinkedList<Short> = LinkedList()
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
        board: List<Short>,
        area:Int = calculateToArea(board.size)
    ): Pair<List<Short>,MutableList<Int>>{
        val indexInit = calculateIndexRow(area,row)
        val values = board.subList(indexInit,indexInit+area)
        val index = (indexInit..<indexInit + area).toMutableList()
        return Pair(values,index)
    }
}