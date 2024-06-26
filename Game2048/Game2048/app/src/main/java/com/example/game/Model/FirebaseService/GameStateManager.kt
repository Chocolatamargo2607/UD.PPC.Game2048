package com.example.game.Model.FirebaseService
import android.content.Context
import com.example.game.Model.DTO.GameCurrentStateDTO
import com.example.game.ViewModel.Events.MainActivityInterfaceController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class GameStateManager {
    private val firestore = FirebaseFirestore.getInstance()
    private val gson = Gson()
    private val collectionPath = "gameStates" // Colección en Firestore

    fun saveGameState(context: Context, gameState: GameCurrentStateDTO) {
        val uniqueID = UniqueIDManager.getUniqueID(context)
        val gameStateMap = gson.toJson(gameState).let { gson.fromJson(it, Map::class.java) }
        firestore.collection(collectionPath).document(uniqueID)
            .set(gameStateMap)
            .addOnSuccessListener {
                // Estado del juego guardado exitosamente
            }
            .addOnFailureListener { e ->
                // Error al guardar el estado del juego
                e.printStackTrace()
            }
    }

    fun loadGameState(context: Context, userId: String, callback: (GameCurrentStateDTO?) -> Unit) {
        val uniqueID = UniqueIDManager.getUniqueID(context)
        firestore.collection(collectionPath).document(uniqueID)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val gameStateJson = gson.toJson(document.data)
                    val gameState = gson.fromJson(gameStateJson, GameCurrentStateDTO::class.java)
                    callback(gameState)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                // Error al cargar el estado del juego
                e.printStackTrace()
                callback(null)
            }
    }
}
