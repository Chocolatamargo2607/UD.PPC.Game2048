package com.example.game.View.Components

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.game.R
import com.example.game.View.Utils.ShapeCreator

class ElementBoardView(
    contex:Context,
    columns:Int=4,
    value:Int=0
){
    val textView:TextView;
    init {
        val backgroundElement: Drawable = ShapeCreator.createBackground(
            contex,
            value,
            resolveBaseColor(contex,value)
        )
        val dimension = contex.resources.displayMetrics.widthPixels.times(.75).div(columns).toInt()
        this.textView = TextView(contex).apply {
            layoutParams = LinearLayout.LayoutParams(dimension,dimension)
            setTextColor(ContextCompat.getColor(contex,R.color.white))
            gravity = Gravity.CENTER
            background = backgroundElement
            text = value.toString()
        }
    }
    private fun resolveBaseColor(context: Context,value:Int):Int{
        val color: Int = when(value){
            in 2..16 -> R.color.sand
            in 17..64 -> R.color.orange_300
            in 64..512 -> R.color.red_300
            in 512..2048 -> R.color.blue_300
            else-> R.color.white
        }
        return  ContextCompat.getColor(context,color)
    }
}