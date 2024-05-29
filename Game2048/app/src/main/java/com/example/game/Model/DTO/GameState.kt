package com.example.game.Model.DTO


data class GameState(
    var board: List<Short>,
    var score: Int,
    var best: Int
)
