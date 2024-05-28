package com.example.game
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.game.ViewModel.MainViewModel
import com.example.game.ViewModel.Service.MainActivityUIHandler

// MainActivity.kt
class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var uiHandler: MainActivityUIHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        uiHandler = MainActivityUIHandler(this, mainViewModel)

        val startButton: Button = findViewById(R.id.button)
        startButton.setOnClickListener {
            startGame()
        }
    }

    private fun startGame() {
        setContentView(R.layout.activity_main)
        uiHandler.setupUI()
    }
}

