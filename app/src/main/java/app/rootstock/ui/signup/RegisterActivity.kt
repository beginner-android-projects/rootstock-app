package app.rootstock.ui.signup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.rootstock.R
import app.rootstock.databinding.ActivityAccountBinding
import androidx.databinding.DataBindingUtil.setContentView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView<ActivityAccountBinding>(this, R.layout.activity_account)

    }

}