package app.rootstock.ui.signup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.rootstock.R
import app.rootstock.databinding.ActivitySignupBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.databinding.DataBindingUtil.setContentView


@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView<ActivitySignupBinding>(this, R.layout.activity_signup)

    }

}