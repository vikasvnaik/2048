package com.game.domain.manager

import com.game.domain.repository.UserDataRepo


class UserPrefDataManager(private val userDataRepo: UserDataRepo) : UserDataRepo {

    override var userName: String?
        get() = userDataRepo.userName
        set(value) {
            userDataRepo.userName = value
        }

    override var score: Int?
        get() = userDataRepo.score
        set(value) {
            userDataRepo.score = value
        }

    override var highScore: Int?
        get() = userDataRepo.highScore
        set(value) {
            userDataRepo.highScore = value
        }
}