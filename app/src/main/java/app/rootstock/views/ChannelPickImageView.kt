package app.rootstock.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import androidx.core.graphics.drawable.updateBounds
import app.rootstock.R

class ChannelPickImageView(context: Context, attributeSet: AttributeSet) :
    androidx.appcompat.widget.AppCompatImageView(context, attributeSet) {

    var isPicked = false

    override fun performClick(): Boolean {
        togglePicked()
        return super.performClick()
    }

    private fun togglePicked() {
        isPicked = !isPicked
        foreground = if (isPicked) {
            context.getDrawable(R.drawable.ic_check_24)
        } else {
            null
        }
    }

    init {
        foregroundGravity = Gravity.CENTER
    }

    fun unPick() {
        isPicked = false
        foreground = null
    }
}