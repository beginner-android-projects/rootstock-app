package app.rootstock.views

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import app.rootstock.databinding.DialogDeleteAccountBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class DeleteAccountDialogFragment(
    private val action: ((email: String) -> Unit),
    private val email: String
) :
    AppCompatDialogFragment() {

    private lateinit var binding: DialogDeleteAccountBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext()).create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDeleteAccountBinding.inflate(layoutInflater, container, true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            executePendingBindings()
        }

        binding.deleteButton.setOnClickListener {
            if (binding.email.editText?.text.toString() == email) {
                dismiss(); action.invoke(binding.email.editText?.text.toString())
            } else {
                binding.email.error = "Invalid email"
            }
        }
        binding.cancel.setOnClickListener { dismiss() }

        if (showsDialog) {
            (requireDialog() as AlertDialog).setView(binding.root)
        }
    }


}

