package com.christianlatona.android.draganddraw

import android.graphics.PointF
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize // kotlinx
class Box(private val start: PointF): Parcelable {

    var end: PointF = start

    var rotation: Float = 0f

    val left: Float
        get() = start.x.coerceAtMost(end.x) // Math.min(start.x, end.x)

    val right: Float
        get() = start.x.coerceAtLeast(end.x) // Math.max(start.x, end.x)

    val top: Float
        get() = start.y.coerceAtMost(end.y) // Math.min(start.y, end.y)

    val bottom: Float
        get() = start.y.coerceAtLeast(end.y) // Math.max(start.y, end.y)
}