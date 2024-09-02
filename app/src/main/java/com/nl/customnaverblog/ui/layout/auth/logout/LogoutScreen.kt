package com.nl.customnaverblog.ui.layout.auth.logout

import android.webkit.CookieManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nl.customnaverblog.Urls

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogoutScreen(
    onLogoutSuccess: () -> Unit,
    onLogoutCancel: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "로그아웃")
                },
                navigationIcon = {
                    IconButton(onClick = onLogoutCancel) {
                        Icon(imageVector = Icons.Rounded.ArrowBackIosNew, contentDescription = null)
                    }
                }
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Text(
                        text = "로그아웃"
                    )
                    Text(
                        text = "로그아웃 하시겠습니까?"
                    )
                    Button(
                        onClick = {
                            val cookieManager = CookieManager.getInstance()

                            val cookie = cookieManager.getCookie("https://m.blog.naver.com/")
                                .split(";").map { it.split("=") }

                            val domains =listOf(
                                "https://m.blog.naver.com/",
                                "https://section.blog.naver.com/",
                                "https://m.naver.com/"
                            )

                            for (c in cookie) {
                                for (d in domains) {
                                    cookieManager.setCookie(
                                        d,
                                        "${c[0].trim()}=; expires=Thu, 01 Jan 1970 00:00:00 GMT"
                                    )
                                }
                            }

                            cookieManager.flush()

                            onLogoutSuccess()
                        }
                    ) {
                        Text(text = "로그아웃 하기")
                    }
                }
            }
        }
    )

    BackHandler(onBack = onLogoutCancel)
}