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
import app.rootstock.databinding.DialogChannelDeleteBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ChannelDeleteDialogFragment(
    private val channel: Channel,
    private val delete: ((channelId: Long) -> Unit)
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
            channel = this@ChannelDeleteDialogFragment.channel
            lifecycleOwner = viewLifecycleOwner
            executePendingBindings()

            message.text = getString(
                R.string.delete_channel_body,
                this@ChannelDeleteDialogFragment.channel.name
            )
            delete.setOnClickListener { delete(this@ChannelDeleteDialogFragment.channel.channelId); dismiss() }
            cancel.setOnClickListener { dismiss() }
        }
        if (showsDialog) {
            (requireDialog() as AlertDialog).setView(binding.root)
        }
    }


}
