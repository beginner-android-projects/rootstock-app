package app.rootstock.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import app.rootstock.R
import app.rootstock.databinding.FragmentLoginBinding
import app.rootstock.ui.main.WorkspaceActivity
import app.rootstock.ui.signup.SignUpFragmentDirections
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

        val txtNoAccount = view?.findViewById<TextView>(R.id.no_account)
        txtNoAccount?.let { setUpTextNoAccount(it) }
    }

    val navigateToLogInFragment = {
        val action = LogInFragmentDirections.actionLoginFragmentToSigninFragment()
        findNavController().navigate(action)
    }

    private fun setUpTextNoAccount(txtNoAccount: TextView) {
        txtNoAccount.setOnClickListener {
            navigateToLogInFragment()
        }
        // in case of translations??
        try {
            val spannable = SpannableString(txtNoAccount.text ?: getString(R.string.no_account))
            spannable.setSpan(
                ForegroundColorSpan(requireContext().getColor(R.color.primary)),
                12,
                22,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
            txtNoAccount.text = spannable
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
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
                    getString(R.string.invalid_email),
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
