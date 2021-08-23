package com.game.ui.main

import android.os.Bundle
import com.game.ui.base.BaseAppCompatActivity
import com.game.databinding.ActivityMainBinding
import com.game.extensions.viewBinding
import com.game.ui.views.GameView

class MainActivity : BaseAppCompatActivity(){

    private val binding by viewBinding(ActivityMainBinding::inflate)
    override fun layout() = binding.root
    private var view: GameView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = GameView(this, userDataManager)
        binding.flGame.addView(view)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}