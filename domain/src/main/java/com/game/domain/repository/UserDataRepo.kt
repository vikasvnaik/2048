package com.game.domain.repository

interface UserDataRepo {
    var userName: String?
    var highScore: Long?
    var score: Long?
    var firstRun: Boolean?
}