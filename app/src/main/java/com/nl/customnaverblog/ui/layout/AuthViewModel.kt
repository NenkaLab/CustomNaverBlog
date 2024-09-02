package com.nl.customnaverblog.ui.layout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nl.customnaverblog.hilt.repo.UserStatusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userStatus: UserStatusRepository
) : ViewModel() {

    fun isLogin(callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            callback(userStatus.isLogin())
        }
    }

    fun getUser() = runBlocking { userStatus.getUser() }

}