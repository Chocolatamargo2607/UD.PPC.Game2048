package com.example.game.View.Utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import com.example.game.R

object ShapeCreator {
    fun createBackground(
        context: Context,
        value: Int,
        baseColor: Int = ContextCompat.getColor(context, R.color.white)
    ): Drawable {
        val adjustedColor = adjustColor(baseColor, value)
        val shape = GradientDrawable()
        shape.setColor(adjustedColor)
        shape.setStroke(2, Color.WHITE)
        shape.cornerRadius = 8f
        return shape
    }
    private fun adjustColor(baseColor: Int, value: Int): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(baseColor, hsv)
        hsv[2] = hsv[2] * (1 - resolveValue(value) * 0.03f)
        return Color.HSVToColor(hsv)
    }
    private fun resolveValue(value:Int):Float{
        return when(value){
            in 2..16 -> value.times(0.5f)
            in 17..64 -> value.times(0.2f)
            in 64..512 -> value.times(0.05f)
            in 512..2048 -> value.times(0.02f)
            else-> value.toFloat()
        }
    }
}