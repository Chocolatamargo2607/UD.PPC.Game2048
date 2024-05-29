package com.example.game.View

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.game.R
import com.example.game.View.Profiles.ShapeCreator

// Clase que genera los TextView y los componentes del GridView
class BoardElement(
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
            in 2..16 -> R.color.base1
            in 17..64 -> R.color.base2
            in 64..512 -> R.color.base3
            in 512..2048 -> R.color.base4
            else-> R.color.white
        }
        return  ContextCompat.getColor(context,color)
    }
}