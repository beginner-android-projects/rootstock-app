package app.rootstock.ui.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.rootstock.R
import app.rootstock.databinding.FragmentSignupBinding
import app.rootstock.utils.makeToast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignupBinding

    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewmodel = viewModel

        setObservers()
    }

    private fun setObservers() {
        viewModel.signUpStatus.observe(viewLifecycleOwner) {
            when (it.peekContent()) {
                EventUserSignUp.SUCCESS -> makeToast("success", true)
                EventUserSignUp.USER_EXISTS -> makeToast(
                    getString(R.string.invalid_email),
                    long = true
                )
                EventUserSignUp.INVALID_DATA -> makeToast(getString(R.string.invalid_data), true)
                EventUserSignUp.FAILED -> makeToast(
                    getString(R.string.signup_failed),
                    true
                )
                else -> {
                }
            }
        }
    }
}
