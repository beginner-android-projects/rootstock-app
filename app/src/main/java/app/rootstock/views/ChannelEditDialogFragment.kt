package app.rootstock.views

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import app.rootstock.R
import app.rootstock.adapters.PatternAdapter
import app.rootstock.data.channel.Channel
import app.rootstock.databinding.DialogChannelEditBinding
import app.rootstock.ui.channels.ColorsViewModel
import app.rootstock.ui.main.WorkspaceViewModel
import app.rootstock.utils.InternetUtil
import app.rootstock.utils.autoFitColumns
import app.rootstock.utils.convertDpToPx
import app.rootstock.utils.makeToast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


/**
 * Dialog that appears when editing channel.
 */
@AndroidEntryPoint
@ExperimentalCoroutinesApi
class ChannelEditDialogFragment(
    private val channel: Channel,
    private val changed: ((channel: Channel) -> Unit)? = null
) : AppCompatDialogFragment() {

    companion object {
        private const val spanCount = 4

    }

    private var currentColor: String? = null

    private lateinit var binding: DialogChannelEditBinding

    private val viewModel: WorkspaceViewModel by activityViewModels()

    private val editViewModel: ColorsViewModel by viewModels()

    private val adapterToSet = PatternAdapter(items = mutableListOf(), ::patternClicked)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext()).create()
    }

    // todo check not loaded patterns click

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogChannelEditBinding.inflate(layoutInflater, container, true)
        return binding.root
    }

    private fun patternClicked(position: Int?, image: String?) {
        if (position == null) return

        val isPicked =
            binding.colorsRv.findViewHolderForAdapterPosition(position)?.itemView?.findViewById<ChannelPickImageView>(
                R.id.color_item
            )?.isPicked

        // if color is picked by user, change line accordingly
        // otherwise, return to the initial color
        if (isPicked == false) changeImage(image)
        else changeImage(image)

        // unpick previously picked color
        if (adapterToSet.previousPickedPosition != null && position != adapterToSet.previousPickedPosition) {
            adapterToSet.previousPickedPosition?.let {
                binding.colorsRv.findViewHolderForAdapterPosition(it)?.itemView?.findViewById<ChannelPickImageView>(
                    R.id.color_item
                )?.unPick()
            }
        }
    }

    private fun changeImage(image: String?) {
        image ?: return
        currentColor = image
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.apply {
            channel = this@ChannelEditDialogFragment.channel
            lifecycleOwner = viewLifecycleOwner
            executePendingBindings()
        }
        binding.colorsRv.apply {
            adapter = adapterToSet
            autoFitColumns(50, spanCount)
            addItemDecoration(
                GridSpacingItemDecorator(
                    spanCount,
                    requireContext().convertDpToPx(10f).toInt(),
                    false
                )
            )
        }
        editViewModel.images.observe(this) {
            adapterToSet.updateList(it.urls)
        }

        if (showsDialog) {
            (requireDialog() as AlertDialog).setView(binding.root)
        }
        binding.save.setOnClickListener {
            if (!InternetUtil.isInternetOn()) {
                makeToast(getString(R.string.no_connection))
                return@setOnClickListener
            }
            val newName =
                view?.findViewById<EditText>(R.id.channel_edit_name_text)?.text?.toString()
                    ?: return@setOnClickListener
            val newChannel = Channel(
                name = newName,
                imageUrl = currentColor,
                channelId = channel.channelId,
                workspaceId = channel.workspaceId,
                lastMessage = channel.lastMessage,
                lastUpdate = channel.lastUpdate,
                backgroundColor = channel.backgroundColor
            )
            changed?.invoke(newChannel)
            viewModel.updateChannel(newChannel)
            dismiss()
        }
        binding.cancel.setOnClickListener { dismiss() }
    }

}
