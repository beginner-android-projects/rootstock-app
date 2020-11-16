package app.rootstock.views

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.activityViewModels
import app.rootstock.R
import app.rootstock.adapters.ColorListAdapter
import app.rootstock.data.channel.Channel
import app.rootstock.databinding.DialogChannelEditBinding
import app.rootstock.ui.main.WorkspaceViewModel
import app.rootstock.utils.autoFitColumns
import app.rootstock.utils.convertDpToPx
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.lang.RuntimeException


/**
 * Dialog that appears when editing channel.
 */
@AndroidEntryPoint
@ExperimentalCoroutinesApi
class ChannelEditDialogFragment(private val channel: Channel) : AppCompatDialogFragment() {

    companion object {
        private val channelPossibleColors = listOf(
            "#f0ff0f",
            "#f3fa0f",
            "#b0ff0a",
            "#c01f0a",
            "#c01f0a",
            "#c01f0a",
            "#c01f0a",
            "#c01f0a",
        )
        private const val spanCount = 4

    }

    private var currentColor: String? = null

    private lateinit var binding: DialogChannelEditBinding

    private val viewModel: WorkspaceViewModel by activityViewModels()

    private val adapterToSet = ColorListAdapter(items = channelPossibleColors, ::onColorClicked)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext()).create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogChannelEditBinding.inflate(layoutInflater, container, true)
        return binding.root
    }

    private fun onColorClicked(view: View, position: Int?) {
        if (position == null) return
        val isPicked =
            binding.colorsRv.findViewHolderForAdapterPosition(position)?.itemView?.findViewById<ChannelPickImageView>(
                R.id.color_item
            )?.isPicked

        if (isPicked == false) changeColor(default = true, channel.backgroundColor)
        else changeColor(default = false, channelPossibleColors[position])

        // unpick previously picked color
        if (adapterToSet.previousPickedPosition != null && position != adapterToSet.previousPickedPosition) {
            adapterToSet.previousPickedPosition?.let {
                binding.colorsRv.findViewHolderForAdapterPosition(it)?.itemView?.findViewById<ChannelPickImageView>(
                    R.id.color_item
                )?.unPick()
            }
        }
    }

    private fun changeColor(default: Boolean = true, color: String) {
        val colorToSet: Int = try {
            Color.parseColor(color)
        } catch (e: RuntimeException) {
            e.printStackTrace()
            Color.parseColor(channelPossibleColors[0])
        }
        if (default) binding.colorLine.setColorFilter(colorToSet)
        else binding.colorLine.setColorFilter(colorToSet)
        currentColor = color
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

        if (showsDialog) {
            (requireDialog() as AlertDialog).setView(binding.root)
        }
        binding.save.setOnClickListener {
            val newChannel = channel.copy()
            newChannel.apply {
                name = view?.findViewById<EditText>(R.id.channel_edit_name_text)?.text.toString()
                currentColor?.let {
                    backgroundColor = it
                }
            }
            viewModel.updateChannel(newChannel)
            dismiss()
        }
        binding.cancel.setOnClickListener { dismiss() }
    }

}
