package app.rootstock.adapters

import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import java.lang.Exception


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

@BindingAdapter("lastMessage")
fun bindingChannelLastMessage(textView: TextView, lastMessage: String?) {
    if (lastMessage == null) {
        textView.text = "No memo yet"
    } else textView.text = lastMessage
}

@BindingAdapter("drawableInt")
fun bindDrawableInt(imageView: ImageView, id: Int) {
    imageView.setImageResource(id)
}


@BindingAdapter("colorFilter")
fun bindColorFilter(imageView: ImageView, color: String) {
    try {
        val c = Color.parseColor(color)
        imageView.setColorFilter(c)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
@BindingAdapter("backgroundColor")
fun bindColorFilter(view: View, color: String) {
    try {
        val c = Color.parseColor(color)
        view.setBackgroundColor(c)
    } catch (e: Exception) {
        e.printStackTrace()
    }
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