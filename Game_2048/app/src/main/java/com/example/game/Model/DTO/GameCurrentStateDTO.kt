package com.example.game.Model.DTO

import java.util.LinkedList


data class GameCurrentStateDTO(
    var board: LinkedList<Int>,
    var score: Int,
    var best: Int
)
