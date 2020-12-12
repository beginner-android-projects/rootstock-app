package app.rootstock.ui.channels

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import app.rootstock.R
import app.rootstock.adapters.ChannelListAdapter
import app.rootstock.data.channel.Channel
import app.rootstock.databinding.FragmentChannelsListBinding
import app.rootstock.ui.main.WorkspaceActivity
import app.rootstock.ui.main.WorkspaceActivity.Companion.BUNDLE_CHANNEL_EXTRA
import app.rootstock.ui.main.WorkspaceActivity.Companion.REQUEST_CODE_CHANNEL_ACTIVITY
import app.rootstock.ui.main.WorkspaceViewModel
import app.rootstock.utils.convertDpToPx
import app.rootstock.views.*
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
    ): View {
        binding = FragmentChannelsListBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    private fun openEditDialog(anchor: View, channel: Channel, card: View) {
        viewModel.editDialogOpened()
        showEditPopup(anchor, channel, card)
    }

    private fun openChannel(channel: Channel) {
        val intent = Intent(requireActivity(), ChannelActivity::class.java)
        intent.putExtra(BUNDLE_CHANNEL_EXTRA, channel)
        requireActivity().startActivityForResult(intent, REQUEST_CODE_CHANNEL_ACTIVITY)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ChannelListAdapter(
            lifecycleOwner = this,
            editDialog = ::openEditDialog,
            openChannel = ::openChannel
        )
        binding.recyclerView.adapter = adapter

        val itemDecorator =
            SpacingItemDecoration(
                endSpacing = requireContext().convertDpToPx(dp = CHANNEL_END_SPACING).toInt(),
                startSpacing = requireContext().convertDpToPx(dp = CHANNEL_START_SPACING).toInt(),
            )
        binding.recyclerView.apply {
            addItemDecoration(itemDecorator)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    viewModel.pageScrolled()
                }
            })
        }
        setObservers()
    }


    private fun setObservers() {
        viewModel.channels.observe(viewLifecycleOwner) {
            if (it != null && ::adapter.isInitialized) {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()

            }
        }

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                binding.recyclerView.scrollToPosition(0)
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                binding.recyclerView.scrollToPosition(0)
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                binding.recyclerView.scrollToPosition(0)
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                binding.recyclerView.scrollToPosition(0)
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                binding.recyclerView.scrollToPosition(0)
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                binding.recyclerView.scrollToPosition(0)
            }
        })
    }

    private fun showEditPopup(anchor: View, channel: Channel, card: View) {
        val popUpView = layoutInflater.inflate(R.layout.popup_channel_menu, null)
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
        popUpView.findViewById<View>(R.id.delete)?.setOnClickListener {
            popupWindow.dismiss()
            val content = getString(
                R.string.delete_channel_body, channel.name
            )
            val deleteObj = DeleteObj(
                content = content,
                delete = ::deleteChannel,
                id = channel.channelId,
                deleteType = ItemType.CHANNEL,
                bold = Pair(32, content.length - 1)
            )
            val dialog = DeleteDialogFragment(deleteObj)

            dialog.show(requireActivity().supportFragmentManager, DIALOG_CHANNEL_DELETE)
        }
    }

    private fun deleteChannel(channelId: Long) {
        viewModel.deleteChannel(channelId)
    }

    companion object {
        private const val DIALOG_CHANNEL_EDIT = "dialog_channel_edit"
        private const val DIALOG_CHANNEL_DELETE = "dialog_channel_delete"
        private const val CHANNEL_START_SPACING = 15f
        private const val CHANNEL_END_SPACING = 25f

    }

}
