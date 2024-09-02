package com.nl.customnaverblog.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.nl.customnaverblog.ProvideChromeCustomTabs
import com.nl.customnaverblog.ui.layout.BlogNavigator
import com.nl.customnaverblog.ui.layout.feed.FeedViewModel
import com.nl.customnaverblog.ui.theme.CustomNaverBlogTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            CustomNaverBlogTheme {
                ProvideChromeCustomTabs {
                    BlogNavigator(
                        navController,
                    )
                }
            }
        }
    }

}