package app.rootstock.adapters

import androidx.annotation.StringRes
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
