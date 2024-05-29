package com.example.game.ViewModel.Interface

import android.content.Context
import android.widget.GridView

interface InterfaceGeneratorGame{
    val columns: Int
    fun generatorBoard(context: Context):GridView

}