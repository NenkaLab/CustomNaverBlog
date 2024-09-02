package com.nl.customnaverblog

import android.app.Application
import android.graphics.Bitmap
import android.os.Build
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.addLastModifiedToFileCacheKey
import coil3.bitmapFactoryMaxParallelism
import coil3.decode.BitmapFactoryDecoder
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.memory.MemoryCache
import coil3.network.ktor.KtorNetworkFetcherFactory
import coil3.request.CachePolicy
import coil3.request.allowHardware
import coil3.request.allowRgb565
import coil3.request.bitmapConfig
import coil3.request.crossfade
import coil3.size.Precision
import coil3.svg.SvgDecoder
import coil3.util.DebugLogger
import coil3.video.MediaDataSourceFetcher
import coil3.video.VideoFrameDecoder
import com.nl.avif_coder_coil.HeifDecoder
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MainApplication : Application(), SingletonImageLoader.Factory {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(applicationContext)
            .components {
                add(KtorNetworkFetcherFactory())
                add(HeifDecoder.Factory(applicationContext))
                add(SvgDecoder.Factory())
                add(VideoFrameDecoder.Factory())
                add(BitmapFactoryDecoder.Factory())
                add(MediaDataSourceFetcher.Factory())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(AnimatedImageDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .addLastModifiedToFileCacheKey(true)
            .allowHardware(true)
            .allowRgb565(false)
            .bitmapConfig(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Bitmap.Config.RGBA_1010102
                else Bitmap.Config.ARGB_8888
            )
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .diskCache(
                DiskCache.Builder()
                    .maxSizePercent(0.3)
                    .directory(applicationContext.cacheDir.resolve("image_cache"))
                    .build()
            )
            .memoryCache(
                MemoryCache.Builder()
                    .maxSizePercent(applicationContext, 0.3)
                    .weakReferencesEnabled(true)
                    .build()
            )
            .bitmapFactoryMaxParallelism(2)
            .precision(Precision.EXACT)
            .apply { if (BuildConfig.DEBUG) logger(DebugLogger()) }
            .build()
    }

}