package com.example.game.Model.FirebaseService

import android.content.Context
import com.example.game.ViewModel.Events.MainActivityInterfaceController
import android.content.SharedPreferences
import java.util.UUID

object UniqueIDManager {
    private const val PREFS_NAME = "game_prefs"
    private const val KEY_UNIQUE_ID = "unique_id"

    fun getUniqueID(context: Context): String {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        var uniqueID = sharedPreferences.getString(KEY_UNIQUE_ID, null)
        if (uniqueID == null) {
            uniqueID = UUID.randomUUID().toString()
            val editor = sharedPreferences.edit()
            editor.putString(KEY_UNIQUE_ID, uniqueID)
            editor.apply()
        }
        return uniqueID!!
    }
}
