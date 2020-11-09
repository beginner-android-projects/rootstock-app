package app.rootstock.adapters

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputLayout
import retrofit2.http.Url


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
//
//
//@BindingAdapter(value = ["imageUrl", "placeholder"], requireAll = false)
//fun imageUrl(imageView: ImageView, imageUrl: Url?, placeholder: Drawable?) {
//    when (imageUrl) {
//        null -> {
//            Glide.with(imageView)
//                .load(placeholder)
//                .into(imageView)
//        }
//        else -> {
//            Glide.with(imageView)
//                .load(imageUrl)
//                .apply(RequestOptions().placeholder(placeholder))
//                .into(imageView)
//        }
//    }
//}