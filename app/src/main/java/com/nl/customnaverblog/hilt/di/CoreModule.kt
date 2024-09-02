package com.nl.customnaverblog.hilt.di

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.webkit.WebSettings
import com.nl.customnaverblog.Urls
import com.nl.customnaverblog.other.Client
import com.nl.customnaverblog.other.HardwareId
import com.nl.customnaverblog.other.JsonSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.URLProtocol
import java.util.UUID
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    @Singleton
    @Client(Urls.Type.BASE)
    fun providesKtorBlogBase(
        @ApplicationContext applicationContext: Context,
    ): HttpClient {
        return HttpClient(CIO) {
            install(UserAgent) {
                agent = WebSettings.getDefaultUserAgent(applicationContext)
            }
            defaultRequest {
                url {
                    host = Urls.Type.BASE.url
                    protocol = URLProtocol.HTTPS
                }
                header("Referer", "https://${Urls.Type.BASE.url}")
            }
        }
    }

    @Provides
    @Singleton
    @Client(Urls.Type.SECTION)
    fun providesKtorBlogSection(
        @ApplicationContext applicationContext: Context,
    ): HttpClient {
        return HttpClient(CIO) {
            install(UserAgent) {
                agent = WebSettings.getDefaultUserAgent(applicationContext)
            }
            defaultRequest {
                url {
                    host = Urls.Type.SECTION.url
                    protocol = URLProtocol.HTTPS
                }
                header("Referer", "https://${Urls.Type.SECTION.url}")
            }
        }
    }

    @Provides
    @Singleton
    @Client(Urls.Type.API)
    fun providesKtorBlogApi(
        @ApplicationContext applicationContext: Context,
    ): HttpClient {
        return HttpClient(CIO) {
            install(UserAgent) {
                agent = WebSettings.getDefaultUserAgent(applicationContext)
            }
            defaultRequest {
                url {
                    host = Urls.Type.API.url
                    protocol = URLProtocol.HTTPS
                }
                header("Referer", "https://${Urls.Type.BASE.url}")
            }
        }
    }

    @SuppressLint("HardwareIds")
    @Provides
    @Singleton
    @HardwareId
    fun providesHardwareId(
        @ApplicationContext applicationContext: Context
    ): String {
        return UUID.nameUUIDFromBytes(
            Settings.Secure.getString(
                applicationContext.contentResolver,
                Settings.Secure.ANDROID_ID
            ).toByteArray()
        ).toString()
    }

    @Provides
    @Singleton
    fun provideJsonSerializer(): JsonSerializer = JsonSerializer()

}