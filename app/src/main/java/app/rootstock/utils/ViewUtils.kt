package app.rootstock.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Converts dp to pixels
 */
fun Context.convertDpToPx(dp: Float): Float {
    return (dp * (this.resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
}


/**
 * @param columnWidthDp - in dp
 */
fun RecyclerView.autoFitColumns(columnWidthDp: Int, spanCountNum: Int) {
    val displayMetrics = this.context.resources.displayMetrics
    val noOfColumns =
        ((displayMetrics.widthPixels / displayMetrics.density) / columnWidthDp).toInt()
    this.layoutManager =
        GridLayoutManager(this.context, noOfColumns).apply { spanCount = spanCountNum }
}

fun Context.showKeyboard() {
    (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.apply {
        toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }
}