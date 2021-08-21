package com.game.ui.di


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.game.data.repository.UserDateRepoImpl
import com.game.domain.manager.UserPrefDataManager
import com.game.domain.repository.UserDataRepo
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


fun dependency() = listOf(vm, repository, manager, useCases)
val vm = module {
}
val useCases = module {
}
val manager = module {
    // UserDateRepoImpl
    single { UserPrefDataManager(get()) }
}
val repository = module {
    // AuthApi
    single {
        UserDateRepoImpl(get(), get()) as UserDataRepo
    }
}
