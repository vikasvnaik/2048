package com.game.ui.main

import android.os.Bundle
import com.game.a2048.ui.base.BaseAppCompatActivity
import com.game.databinding.ActivityMainBinding
import com.game.extensions.viewBinding

class MainActivity : BaseAppCompatActivity(){
    private val binding by viewBinding(ActivityMainBinding::inflate)
    override fun layout() = binding.root
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}