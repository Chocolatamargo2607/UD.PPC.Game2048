package com.example.game.ViewModel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.game.Model.DTO.GameCurrentStateDTO
import com.example.game.ViewModel.Events.GameInterfaceGeneratorBoard

@SuppressLint("ClickableViewAccessibility")
class MainViewModel:ViewModel() {
    private val _observableGameCurrentStateDTO: MutableLiveData<GameCurrentStateDTO> = MutableLiveData()
    val observableGameCurrentStateDTO: LiveData<GameCurrentStateDTO> get() = _observableGameCurrentStateDTO

    val gridView:GameInterfaceGeneratorBoard = GameInterfaceGeneratorBoard(4);
    init {
        _observableGameCurrentStateDTO.value = gridView.gameCurrentStateDTO
    }
    fun updateGameState(newGameCurrentStateDTO: GameCurrentStateDTO){
        _observableGameCurrentStateDTO.value = newGameCurrentStateDTO
    }
    override fun onCleared() {
        super.onCleared()
    }
}