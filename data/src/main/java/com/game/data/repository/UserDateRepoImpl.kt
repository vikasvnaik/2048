package com.game.data.repository

import android.content.SharedPreferences
import com.game.domain.extensions.get
import com.game.domain.extensions.set
import com.game.domain.repository.UserDataRepo
import kotlinx.serialization.json.Json

class UserDateRepoImpl(private val sharedPreferences: SharedPreferences) :
    UserDataRepo {

    override var userName: String?
        get() {
            return sharedPreferences["userName", ""]
        }
        set(value) {
            sharedPreferences["userName"] = value
        }

    override var score: Long?
        get() {
            return sharedPreferences["score", 0]
        }
        set(value) {
            sharedPreferences["score"] = value
        }

    override var highScore: Long?
        get() {
            return sharedPreferences["highScore", 0]
        }
        set(value) {
            sharedPreferences["highScore"] = value
        }

    override var firstRun: Boolean?
        get() {
            return sharedPreferences["firstRun", true]
        }
        set(value) {
            sharedPreferences["firstRun"] = value
        }
}