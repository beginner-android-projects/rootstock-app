package app.rootstock.ui.signup

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import app.rootstock.R
import app.rootstock.databinding.FragmentSignupBinding
import app.rootstock.ui.main.WorkspaceActivity
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

        val txtAlready = view?.findViewById<TextView>(R.id.already_have_account)
        txtAlready?.let { setUpTextAlready(it) }

        val txtPrivacy = view?.findViewById<TextView>(R.id.privay_policy)
        txtPrivacy?.let { setUpTextPrivacy(it) }

    }

    private fun setUpTextPrivacy(txtPrivacy: TextView) {
        txtPrivacy.setOnClickListener {
            // todo: open web privacy policy
        }
        // in case of translations??
        try {
            val spannable = SpannableString(txtPrivacy.text ?: getString(R.string.sign_up_privacy))
            spannable.setSpan(
                ForegroundColorSpan(requireContext().getColor(R.color.primary)),
                28,
                42,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
            txtPrivacy.text = spannable
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    private fun setUpTextAlready(txtAlready: TextView) {
        txtAlready.setOnClickListener {
            val action = SignUpFragmentDirections.actionSigninFragmentToLoginFragment()
            findNavController().navigate(action)
        }
        // in case of translations??
        try {
            val spannable = SpannableString(txtAlready.text ?: getString(R.string.already_account))
            spannable.setSpan(
                ForegroundColorSpan(requireContext().getColor(R.color.primary)),
                25,
                31,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
            txtAlready.text = spannable
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    private fun setObservers() {
        viewModel.signUpStatus.observe(viewLifecycleOwner) {
            when (it.getContentIfNotHandled()) {
                EventUserSignUp.SUCCESS -> startMainWorkspaceActivity()
                EventUserSignUp.USER_EXISTS -> makeToast(
                    getString(R.string.invalid_email),
                    long = true
                )
                EventUserSignUp.INVALID_DATA -> makeToast(getString(R.string.invalid), false)
                EventUserSignUp.FAILED -> makeToast(
                    getString(R.string.signup_failed),
                    false
                )
                EventUserSignUp.LOADING -> {
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopSignUp()
    }

    private fun startMainWorkspaceActivity() {
        val intent = Intent(requireContext(), WorkspaceActivity::class.java)
        startActivity(intent)
        requireActivity().finishAfterTransition()
    }
}

