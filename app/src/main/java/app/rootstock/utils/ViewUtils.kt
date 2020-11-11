package app.rootstock.utils

import android.content.Context
import android.util.DisplayMetrics

/**
 * Converts dp to pixels
 */
fun Context.convertDpToPx(dp: Float): Float {
    return (dp * (this.resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
}