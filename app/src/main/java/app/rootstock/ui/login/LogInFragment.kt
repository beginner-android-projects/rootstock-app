package app.rootstock.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.rootstock.R
import app.rootstock.databinding.FragmentLoginBinding
import app.rootstock.databinding.FragmentSignupBinding
import app.rootstock.ui.main.WorkspaceActivity
import app.rootstock.ui.signup.EventUserSignUp
import app.rootstock.ui.signup.SignUpViewModel
import app.rootstock.utils.makeToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogInFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private val viewModel: LogInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false).apply {
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
        viewModel.logInStatus.observe(viewLifecycleOwner) {
            when (it.peekContent()) {
                EventUserLogIn.SUCCESS -> startMainWorkspaceActivity()
                EventUserLogIn.INVALID_DATA -> makeToast(
                    getString(R.string.invalid_email_or_password),
                    false
                )
                EventUserLogIn.FAILED -> makeToast(
                    getString(R.string.login_failed),
                    false
                )
                EventUserLogIn.LOADING -> {
                }
            }
        }

    }

    override fun onStop() {
        super.onStop()
        viewModel.stopLogIn()
    }

    private fun startMainWorkspaceActivity() {
        val intent = Intent(requireContext(), WorkspaceActivity::class.java)
        startActivity(intent)
        requireActivity().finishAfterTransition()
    }
}
