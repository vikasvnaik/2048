package com.game.domain.manager

import com.game.domain.repository.UserDataRepo


class UserPrefDataManager(private val userDataRepo: UserDataRepo) : UserDataRepo {

    override var userName: String?
        get() = userDataRepo.userName
        set(value) {
            userDataRepo.userName = value
        }

    override var score: Long?
        get() = userDataRepo.score
        set(value) {
            userDataRepo.score = value
        }

    override var highScore: Long?
        get() = userDataRepo.highScore
        set(value) {
            userDataRepo.highScore = value
        }

    override var firstRun: Boolean?
        get() = userDataRepo.firstRun
        set(value) {
            userDataRepo.firstRun = value
        }
}