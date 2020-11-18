package app.rootstock.views

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.viewModels
import app.rootstock.R
import app.rootstock.adapters.ColorListAdapter
import app.rootstock.data.channel.Channel
import app.rootstock.data.channel.ChannelConstants.channelPossibleColors
import app.rootstock.data.channel.ChannelConstants.defaultChannelColor
import app.rootstock.data.network.CreateOperation
import app.rootstock.databinding.DialogChannelCreateBinding
import app.rootstock.ui.channels.ChannelCreateViewModel
import app.rootstock.utils.autoFitColumns
import app.rootstock.utils.convertDpToPx
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.lang.RuntimeException


@AndroidEntryPoint
@ExperimentalCoroutinesApi
class ChannelCreateDialogFragment(
    private val workspaceId: String,
    private val channelResult: ((CreateOperation<Channel?>) -> Unit)
) : AppCompatDialogFragment() {

    companion object {
        private const val spanCount = 4
    }

    private lateinit var binding: DialogChannelCreateBinding

    private val viewModel: ChannelCreateViewModel by viewModels()

    private val adapterToSet = ColorListAdapter(items = channelPossibleColors, ::onColorClicked)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext()).create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogChannelCreateBinding.inflate(layoutInflater, container, true)
        return binding.root
    }

    private fun onColorClicked(position: Int?) {
        if (position == null) return
        val isPicked =
            binding.colorsRv.findViewHolderForAdapterPosition(position)?.itemView?.findViewById<ChannelPickImageView>(
                R.id.color_item
            )?.isPicked

        // if not picked by user, change to default color because there is no channel yet created
        if (isPicked == false) viewModel.channel.value?.color?.let {
            changeColor(
                defaultChannelColor
            )
        }
        else changeColor(channelPossibleColors[position])

        // unpick previously picked color
        if (adapterToSet.previousPickedPosition != null && position != adapterToSet.previousPickedPosition) {
            adapterToSet.previousPickedPosition?.let {
                binding.colorsRv.findViewHolderForAdapterPosition(it)?.itemView?.findViewById<ChannelPickImageView>(
                    R.id.color_item
                )?.unPick()
            }
        }
    }

    private fun changeColor(color: String) {
        val colorToSet: Int = try {
            Color.parseColor(color)
        } catch (e: RuntimeException) {
            e.printStackTrace()
            Color.parseColor(defaultChannelColor)
        }
        binding.colorLine.setColorFilter(colorToSet)
        viewModel.channel.value?.color = color
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