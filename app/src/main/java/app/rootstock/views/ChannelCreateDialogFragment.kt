package app.rootstock.views

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import app.rootstock.R
import app.rootstock.adapters.PatternAdapter
import app.rootstock.data.channel.Channel
import app.rootstock.data.channel.ChannelConstants.channelPossibleColors
import app.rootstock.data.network.CreateOperation
import app.rootstock.databinding.DialogChannelCreateBinding
import app.rootstock.ui.channels.ChannelCreateViewModel
import app.rootstock.ui.channels.ColorsViewModel
import app.rootstock.utils.autoFitColumns
import app.rootstock.utils.convertDpToPx
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay


@AndroidEntryPoint
@ExperimentalCoroutinesApi
class ChannelCreateDialogFragment(
    private val workspaceId: String,
    private val channelResult: ((CreateOperation<Channel?>) -> Unit)
) : AppCompatDialogFragment() {

    companion object {
        private const val spanCount = 4
    }

    private val editViewModel: ColorsViewModel by viewModels()

    private lateinit var binding: DialogChannelCreateBinding

    private val viewModel: ChannelCreateViewModel by viewModels()

    private val adapterToSet =
        PatternAdapter(items = channelPossibleColors.toMutableList(), ::patternClicked)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext()).create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogChannelCreateBinding.inflate(layoutInflater, container, true)
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
        viewModel.channel.value?.imageUrl = image
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.apply {
            viewmodel = this@ChannelCreateDialogFragment.viewModel
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
            viewModel.createChannel(workspaceId)
        }
        binding.cancel.setOnClickListener { dismiss() }

        viewModel.eventChannel.observe(viewLifecycleOwner) {
            if (it != null) {
                val op = it.getContentIfNotHandled() ?: return@observe
                channelResult(op)
                dismiss()
            }
        }
    }

}