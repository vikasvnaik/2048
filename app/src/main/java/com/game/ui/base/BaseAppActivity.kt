package com.game.ui.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.game.databinding.ActivityBaseBinding
import com.game.domain.manager.UserPrefDataManager
import org.koin.android.ext.android.inject


abstract class BaseAppCompatActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityBaseBinding.inflate(layoutInflater).apply {
            containerView.addView(layout())
        }
    }


    val bundle by lazy {
        intent.extras ?: Bundle()
    }

    val userDataManager by inject<UserPrefDataManager>()

    /***
     *  Add UI View
     */
    abstract fun layout(): View

    /**
     *  Observe LiveData/Flow
     */
    open fun observeLiveData() {

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //binding.root.hideKeyboard()
        observeLiveData()

    }

    override fun onPause() {
        super.onPause()
        //binding.root.hideKeyboard()
    }


    override fun onBackPressed() {

    }
}