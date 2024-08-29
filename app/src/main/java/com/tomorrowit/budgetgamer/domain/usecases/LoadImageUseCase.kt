package com.tomorrowit.budgetgamer.domain.usecases

import android.content.Context
import android.widget.ImageView
import coil.ImageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Scale
import com.tomorrowit.budgetgamer.R

class LoadImageUseCase(
    private val context: Context,
    private val imageLoader: ImageLoader
) {
    companion object {
        const val NORMAL_IMAGE = -1
        const val GAME_IMAGE = 0
        const val GAME_DETAILS = 1
        const val PROFILE_IMAGE = 2
    }

    operator fun invoke(
        imageView: ImageView,
        url: String,
        imageType: Int = NORMAL_IMAGE
    ) {
        when (imageType) {
            GAME_IMAGE -> {
                loadGameImage(imageView, url)
            }

            GAME_DETAILS -> {
                loadGameDetailsImage(imageView, url)
            }

            PROFILE_IMAGE -> {
                loadProfilePicture(imageView, url)
            }

            else -> {
                this.loadNormalImage(imageView, url)
            }
        }
    }

    private fun loadNormalImage(imageView: ImageView, url: String) {
        val request = ImageRequest.Builder(context)
            .data(url)
            .target(imageView)
            .crossfade(true)
            .scale(Scale.FILL)
            .placeholder(R.color.colorBackgroundSecondary)
            .error(R.drawable.ic_close)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build()
        imageLoader.enqueue(request)
    }


    private fun loadProfilePicture(imageView: ImageView, url: String) {
        val request = ImageRequest.Builder(context)
            .data(url)
            .target(imageView)
            .crossfade(true)
            .scale(Scale.FILL)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build()
        imageLoader.enqueue(request)
    }

    private fun loadGameImage(imageView: ImageView, url: String) {
        val request = ImageRequest.Builder(context)
            .data(url)
            .target(imageView)
            .crossfade(true)
            .placeholder(R.color.colorBackgroundSecondary)
            .error(R.drawable.ic_close)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build()
        imageLoader.enqueue(request)
    }

    private fun loadGameDetailsImage(imageView: ImageView, url: String) {
        val request = ImageRequest.Builder(context)
            .data(url)
            .target(
                onStart = {
                    imageView.setImageResource(R.color.colorBackgroundSecondary)
                },
                onSuccess = { result ->
                    val imageWidth = result.intrinsicWidth
                    val imageHeight = result.intrinsicHeight

                    val imageViewWidth = imageView.width
                    val calculatedHeight = (imageViewWidth * imageHeight / imageWidth).toInt()

                    imageView.layoutParams.height = calculatedHeight
                    imageView.requestLayout()

                    imageView.setImageDrawable(result)
                },
                onError = {
                    imageView.setImageResource(R.drawable.ic_close)
                }
            )
            .crossfade(true)
            .scale(Scale.FILL)
            .placeholder(R.color.colorBackgroundSecondary)
            .error(R.drawable.ic_close)
            .build()
        imageLoader.enqueue(request)
    }
}