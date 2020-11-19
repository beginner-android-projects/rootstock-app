package app.rootstock.views

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import app.rootstock.R
import app.rootstock.data.channel.Channel
import app.rootstock.data.workspace.Workspace
import app.rootstock.databinding.DialogChannelDeleteBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

enum class DeleteDialogType {
    CHANNEL, WORKSPACE
}

/**
 * Dialog Fragment for deleting an entity with @param id and @param name
 * Used for deleting [Channel] and [Workspace]
 */
class DeleteDialogFragment<T>(
    private val name: String,
    private val id: T,
    private val deleteType: DeleteDialogType,
    private val delete: ((id: T) -> Unit),
) : AppCompatDialogFragment() {

    private lateinit var binding: DialogChannelDeleteBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext()).create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogChannelDeleteBinding.inflate(layoutInflater, container, true)
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.apply {
            type = deleteType.name.toLowerCase(Locale.ROOT)
            lifecycleOwner = viewLifecycleOwner
            executePendingBindings()
            message.text = getString(
                R.string.delete_channel_body, name
            )
            delete.setOnClickListener { delete(id); dismiss() }
            cancel.setOnClickListener { dismiss() }
        }
        if (showsDialog) {
            (requireDialog() as AlertDialog).setView(binding.root)
        }
    }


}
