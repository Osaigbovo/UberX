package com.osai.uberx.utils

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.animation.LinearInterpolator
import com.google.android.gms.maps.model.GroundOverlay
import com.osai.uberx.R

fun GroundOverlay.startOverlayAnimation() {
    val animatorSet = AnimatorSet()
    val vAnimator = ValueAnimator.ofInt(0, 100)
    vAnimator.repeatCount = ValueAnimator.INFINITE
    vAnimator.repeatMode = ValueAnimator.RESTART
    vAnimator.interpolator = LinearInterpolator()
    vAnimator.addUpdateListener { valueAnimator ->
        val `val` = valueAnimator.animatedValue as Int
        setDimensions(`val`.toFloat())
    }
    val tAnimator = ValueAnimator.ofFloat(0f, 1f)
    tAnimator.repeatCount = ValueAnimator.INFINITE
    tAnimator.repeatMode = ValueAnimator.RESTART
    tAnimator.interpolator = LinearInterpolator()
    tAnimator.addUpdateListener { valueAnimator ->
        val `val` = valueAnimator.animatedValue as Float
        transparency = `val`
    }
    animatorSet.duration = 3000
    animatorSet.playTogether(vAnimator, tAnimator)
    animatorSet.start()
}

fun Drawable.drawableToBitmap(): Bitmap? {
    var bitmap: Bitmap? = null
    if (this is BitmapDrawable) {
        val bitmapDrawable = this
        if (bitmapDrawable.bitmap != null) {
            return bitmapDrawable.bitmap
        }
    }
    bitmap = if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    } else {
        Bitmap.createBitmap(
            intrinsicWidth,
            intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
    }
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}

fun getCarBitmap(context: Context): Bitmap {
    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_car)
    return Bitmap.createScaledBitmap(bitmap, 50, 100, false)
}
