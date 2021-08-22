package com.game.di


import com.game.BuildConfig
import com.game.data.repository.UserDateRepoImpl
import com.game.domain.manager.UserPrefDataManager
import com.game.domain.repository.UserDataRepo
import com.game.extensions.P
import org.koin.dsl.module
import timber.log.Timber


fun dependency() = listOf(vm, repository, manager, useCases,singleInstance)
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
        UserDateRepoImpl(get()) as UserDataRepo
    }
}

val singleInstance = module {
    single { P.customPrefs(get(), BuildConfig.APPLICATION_ID) }
}
