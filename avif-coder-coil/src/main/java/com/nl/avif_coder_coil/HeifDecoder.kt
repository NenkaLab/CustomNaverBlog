package com.nl.avif_coder_coil

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage
import coil3.decode.DecodeResult
import coil3.decode.Decoder
import coil3.decode.ImageSource
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import coil3.request.allowRgb565
import coil3.request.bitmapConfig
import coil3.size.Scale
import coil3.size.Size
import coil3.size.pxOrElse
import com.radzivon.bartoshyk.avif.coder.HeifCoder
import com.radzivon.bartoshyk.avif.coder.PreferredColorConfig
import com.radzivon.bartoshyk.avif.coder.ScaleMode
import kotlinx.coroutines.runInterruptible
import okio.ByteString.Companion.encodeUtf8

class HeifDecoder(
    context: Context?,
    private val source: ImageSource,
    private val options: Options,
    private val imageLoader: ImageLoader
) : Decoder {

    private val coder = HeifCoder(context)

    @OptIn(ExperimentalCoilApi::class)
    @SuppressLint("ObsoleteSdkInt")
    override suspend fun decode(): DecodeResult = runInterruptible {
        // ColorSpace is preferred to be ignored due to lib is trying to handle all color profile by itself
        val sourceData = source.source().readByteArray()

        var mPreferredColorConfig: PreferredColorConfig = when (options.bitmapConfig) {
            Bitmap.Config.ALPHA_8 -> PreferredColorConfig.RGBA_8888
            Bitmap.Config.RGB_565 -> if (options.allowRgb565) PreferredColorConfig.RGB_565 else PreferredColorConfig.DEFAULT
            Bitmap.Config.ARGB_8888 -> PreferredColorConfig.RGBA_8888
            else -> PreferredColorConfig.DEFAULT
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && options.bitmapConfig == Bitmap.Config.RGBA_F16) {
            mPreferredColorConfig = PreferredColorConfig.RGBA_F16
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && options.bitmapConfig == Bitmap.Config.HARDWARE) {
            mPreferredColorConfig = PreferredColorConfig.HARDWARE
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && options.bitmapConfig == Bitmap.Config.RGBA_1010102) {
            mPreferredColorConfig = PreferredColorConfig.RGBA_1010102
        }

        if (options.size == Size.ORIGINAL) {
            val originalImage =
                coder.decode(
                    sourceData,
                    preferredColorConfig = mPreferredColorConfig
                )
            return@runInterruptible DecodeResult(
                image = originalImage.asImage(true),
                isSampled = false
            )
        }

        val dstWidth = options.size.width.pxOrElse { 0 }
        val dstHeight = options.size.height.pxOrElse { 0 }
        val scaleMode = when (options.scale) {
            Scale.FILL -> ScaleMode.FILL
            Scale.FIT -> ScaleMode.FIT
        }

        val originalImage =
            coder.decodeSampled(
                sourceData,
                dstWidth,
                dstHeight,
                preferredColorConfig = mPreferredColorConfig,
                scaleMode,
            )
        return@runInterruptible DecodeResult(
            image = originalImage.asImage(true),
            isSampled = true
        )
    }

    class Factory(private val context: Context? = null) : Decoder.Factory {
        override fun create(
            result: SourceFetchResult,
            options: Options,
            imageLoader: ImageLoader
        ): Decoder? {
            return if (AVAILABLE_BRANDS.any {
                    result.source.source().rangeEquals(4, it)
                }) HeifDecoder(context, result.source, options, imageLoader) else null
        }

        @Suppress("SpellCheckingInspection")
        companion object {
            private val MIF = "ftypmif1".encodeUtf8()
            private val MSF = "ftypmsf1".encodeUtf8()
            private val HEIC = "ftypheic".encodeUtf8()
            private val HEIX = "ftypheix".encodeUtf8()
            private val HEVC = "ftyphevc".encodeUtf8()
            private val HEVX = "ftyphevx".encodeUtf8()
            private val AVIF = "ftypavif".encodeUtf8()
            private val AVIS = "ftypavis".encodeUtf8()

            private val AVAILABLE_BRANDS = listOf(MIF, MSF, HEIC, HEIX, HEVC, HEVX, AVIF, AVIS)
        }
    }
}