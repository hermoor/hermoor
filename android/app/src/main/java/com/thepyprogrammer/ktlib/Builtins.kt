package com.thepyprogrammer.ktlib

import kotlin.math.roundToInt

fun Float.round(dp: Int = 0) = run {
    when {
        dp <= 0 -> this.roundToInt().toFloat()
        else -> {
            String.format("%.${dp}f", this).toFloat()
        }
    }
}

fun Double.round(dp: Int = 0) = run {
    when {
        dp <= 0 -> this.roundToInt().toDouble()
        else -> {
            String.format("%.${dp}f", this).toDouble()
        }
    }
}

fun Int.pad(zeros: Int) = "%0${zeros}d".format(this)
