package app.rootstock.views

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import app.rootstock.databinding.DialogLogoutBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LogOutDialogFragment(private val logOut: (() -> Unit)) : AppCompatDialogFragment() {

    private lateinit var binding: DialogLogoutBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext()).create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogLogoutBinding.inflate(layoutInflater, container, true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.apply {

            lifecycleOwner = viewLifecycleOwner
            executePendingBindings()
            logout.setOnClickListener { dismiss(); logOut.invoke() }
            cancel.setOnClickListener { dismiss() }
        }
        if (showsDialog) {
            (requireDialog() as AlertDialog).setView(binding.root)
        }
    }


}
