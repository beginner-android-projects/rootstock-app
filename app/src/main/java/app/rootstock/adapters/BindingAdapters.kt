package app.rootstock.adapters

import android.view.View
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout


@BindingAdapter("error")
fun bindingError(view: TextInputLayout, valid: Boolean) {
    if (view.editText?.text.isNullOrEmpty()) return
    if (valid) {
        if (!view.error.isNullOrEmpty()) view.error = null
    } else {
        view.error = "Invalid data"
    }
}

@BindingAdapter("loading")
fun bindingLoading(view: View, loading: Boolean) {
    if (!loading) {
        if (view.visibility == View.VISIBLE) view.visibility = View.GONE
        return
    }
    view.visibility = View.VISIBLE
}

@BindingAdapter("overlay_visible")
fun bindingLoadingOverlay(view: View, loading: Boolean) {
    if (!loading) {
        if (view.visibility == View.VISIBLE) view.visibility = View.INVISIBLE
        return
    }
    view.visibility = View.VISIBLE
}
