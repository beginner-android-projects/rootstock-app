package app.rootstock.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.rootstock.databinding.FragmentSignupBinding
import app.rootstock.ui.signup.SignUpViewModel

class LogInFragment: Fragment() {

    private lateinit var binding: FragmentSignupBinding

    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}