package com.nl.customnaverblog.hilt.repo

import com.nl.customnaverblog.hilt.data.User

interface UserStatusRepository {

    suspend fun isLogin(): Boolean
    suspend fun getUser(): User.User

}