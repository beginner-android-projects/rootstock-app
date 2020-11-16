package app.rootstock.ui.channels

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import app.rootstock.R
import app.rootstock.adapters.ChannelListAdapter
import app.rootstock.data.channel.Channel
import app.rootstock.databinding.FragmentChannelsListBinding
import app.rootstock.ui.main.WorkspaceViewModel
import app.rootstock.utils.convertDpToPx
import app.rootstock.views.ChannelEditDialogFragment
import app.rootstock.views.SpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ChannelsListFragment : Fragment() {

    private val viewModel: WorkspaceViewModel by activityViewModels()

    private lateinit var binding: FragmentChannelsListBinding

    lateinit var adapter: ChannelListAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChannelsListBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    private fun openEditDialog(anchor: View, channel: Channel) {
        viewModel.editChannelStart()
        showPopup(anchor, channel)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        adapter = ChannelListAdapter(
            lifecycleOwner = this,
            editDialog = ::openEditDialog
        )
        binding.recyclerView.adapter = adapter

        val itemDecorator =
            SpacingItemDecoration(
                endSpacing = requireContext().convertDpToPx(dp = 25f).toInt(),
                startSpacing = requireContext().convertDpToPx(dp = 15f).toInt(),
            )
        binding.recyclerView.addItemDecoration(itemDecorator)

    }

    private fun setObservers() {
        viewModel.channels.observe(viewLifecycleOwner) {
            if (it != null && ::adapter.isInitialized) {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun showPopup(anchor: View, channel: Channel) {
        val popUpView = layoutInflater.inflate(R.layout.popup_edit_channel, null)
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val popupWindow = PopupWindow(popUpView, width, height, true)
        var yoff = requireContext().convertDpToPx(60f).toInt()
        val location = IntArray(2)
        anchor.getLocationOnScreen(location)
        // difference between screen size and anchor location on y axis
        val ydiff = Resources.getSystem().displayMetrics.heightPixels - location[1]
        // if popup is going to be close to bottom nav bar, force yoff in opposite direction
        if (ydiff.toFloat() / Resources.getSystem().displayMetrics.heightPixels < 0.35f) yoff *= -3
        popupWindow.showAsDropDown(anchor, 0, yoff)
        popupWindow.setOnDismissListener { viewModel.editChannelStop() }

        popUpView.findViewById<View>(R.id.edit)?.setOnClickListener {
            popupWindow.dismiss()
            val dialog = ChannelEditDialogFragment(channel)
            dialog.show(requireActivity().supportFragmentManager, DIALOG_CHANNEL_EDIT)
        }
    }

    companion object {
        private const val DIALOG_CHANNEL_EDIT = "dialog_schedule_hints"

    }

}
