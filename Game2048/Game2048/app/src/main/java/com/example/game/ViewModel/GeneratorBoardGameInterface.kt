package com.example.game.ViewModel

import android.content.Context
import android.widget.GridView

interface GeneratorBoardGameInterface{
    val columns: Int
    fun generatorBoard(context: Context):GridView

}