package com.example.game.View



import android.content.Context
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.game.View.Profiles.TextViewAdapter

// Clase que crea el GridView
class Board(
    context: Context,
    private val columns: Int,
    var content: List<Int>
) {
    val grid:GridView;
    private val gap:Int = 4;
    init {
        val dimension = context.resources.displayMetrics.widthPixels.times(.8).toInt();
        val padding = 15;
        val items = adapterListValuesToListTextViews(context,this.content)
        val adapterList = TextViewAdapter(context,items);
        this.grid = GridView(context,).apply {
            numColumns = columns;
            layoutParams = ViewGroup.LayoutParams(dimension, dimension);
            horizontalSpacing = gap;
            verticalSpacing = gap;
            clipToPadding = false
            adapter = adapterList;
            background = ContextCompat.getDrawable(context, com.example.game.R.drawable.border);
            columnWidth = GridView.AUTO_FIT;
            setPadding(padding,padding,padding,padding);
        }
    }
    fun updateContentGird(context: Context):GridView{
        val items = adapterListValuesToListTextViews(context,this.content)
        this.grid.adapter = TextViewAdapter(context,items)
        return this.grid
    }
    private fun adapterListValuesToListTextViews(context: Context, values:List<Int>):List<TextView>{
        val items: List<TextView> = List(values.size,) {
            BoardElement(
                context,
                this.columns,
                values[it]
            ).textView
        }
        return items
    }
}