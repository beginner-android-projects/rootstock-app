package app.rootstock.utils

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Converts dp to pixels
 */
fun Context.convertDpToPx(dp: Float): Float {
    return (dp * (this.resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
}


/**
 * @param columnWidth - in dp
 */
fun RecyclerView.autoFitColumns(columnWidth: Int, spanCountNum: Int) {
    val displayMetrics = this.context.resources.displayMetrics
    val noOfColumns =
        ((displayMetrics.widthPixels / displayMetrics.density) / columnWidth).toInt()
    this.layoutManager = GridLayoutManager(this.context, noOfColumns).apply { spanCount = spanCountNum }
}
