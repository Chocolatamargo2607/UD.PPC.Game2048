package com.example.game.View.Components

import android.app.AlertDialog
import android.content.Context

class FinallyGameAlertDialog(context: Context, event:()->Unit) {
    private val builder:AlertDialog.Builder = AlertDialog.Builder(context)
    val dialog: AlertDialog = builder.create()
    init {
        builder.setTitle("GAME OVER!!")
        builder.setPositiveButton("New Game") { dialog, _ ->
            event()
            dialog.dismiss()
        }
    }
}