package com.nl.customnaverblog.ui.layout

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nl.customnaverblog.ui.layout.auth.check.CheckScreen
import com.nl.customnaverblog.ui.layout.auth.login.LoginScreen
import com.nl.customnaverblog.ui.layout.feed.FeedScreen
import com.nl.customnaverblog.ui.layout.feed.FeedViewModel
import com.nl.customnaverblog.ui.layout.post.PostScreen

@Composable
fun BlogNavigator(
    navController: NavHostController,
//    feedViewModel: FeedViewModel,
//    feedLazyScrollState: LazyListState,
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        authViewModel.isLogin {
            if (it) {
                navController.navigate("feed") {
                    popUpTo("check_login") {
                        inclusive = true
                    }
                }
            } else {
                navController.navigate("login") {
                    popUpTo("check_login") {
                        inclusive = true
                    }
                }
            }
        }
    }

    NavHost(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        navController = navController,
        startDestination = "check_login",
        enterTransition = {
            slideInHorizontally(
                tween(250)
            ) {
                it / 2
            } + fadeIn(tween(200))
        },
        exitTransition = {
            slideOutHorizontally(tween(250)) {
                it / 2
            } + fadeOut(tween(200))
        },
        popEnterTransition = {
            scaleIn(tween(250), 0.85f) + fadeIn(tween(200))
        },
        popExitTransition = {
            scaleOut(tween(250), 0.85f) + fadeOut(tween(200))
        }
    ) {
        composable(
            route = "check_login"
        ) {
            CheckScreen()
        }
        composable(
            route = "login"
        ) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("feed") {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                },
                onLoginFail = {
                },
                onCanceled = {
                }
            )
        }
        composable(
            route = "feed"
        ) {
            val feedViewModel: FeedViewModel = hiltViewModel()
            val feedLazyScrollState = rememberLazyListState()

            FeedScreen(
                viewModel = feedViewModel,
                lazyState = feedLazyScrollState,
                onBlogClick = { userId ->
                    navController.navigate("post/$userId")
                },
                onCommentClick = { userId, logNo ->

                },
                onPostClick = { userId, logNo ->
                    navController.navigate("post/$userId/$logNo")
                }
            )
        }
        composable(
            route = "post/{userId}/{logNo}",
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.StringType
                },
                navArgument("logNo") {
                    type = NavType.StringType
                },
            )
        ) { nav ->
            val args = nav.arguments
            val userId = args?.getString("userId")
            val logNo = args?.getString("logNo")

            if (userId == null || logNo == null) {
                navController.popBackStack()
                return@composable
            }

            PostScreen(
                userId = userId,
                logNo = logNo,
                onDismiss = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = "comment/{userId}/{logNo}",
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.StringType
                },
                navArgument("logNo") {
                    type = NavType.StringType
                },
            )
        ) {
        }
        composable(
            route = "blog/{userId}",
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.StringType
                },
            )
        ) {

        }
        composable(
            route = "search"
        ) {

        }
        composable(
            route = "setting"
        ) {

        }
    }
}