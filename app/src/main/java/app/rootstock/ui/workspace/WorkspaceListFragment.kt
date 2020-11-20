package app.rootstock.ui.workspace

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import app.rootstock.R
import app.rootstock.adapters.WorkspaceListAdapter
import app.rootstock.data.workspace.WorkspaceI
import app.rootstock.databinding.FragmentWorkspaceListBinding
import app.rootstock.ui.main.WorkspaceViewModel
import app.rootstock.utils.autoFitColumns
import app.rootstock.utils.convertDpToPx
import app.rootstock.views.DeleteDialogFragment
import app.rootstock.views.ItemType
import app.rootstock.views.GridSpacingItemDecoratorWithCustomCenter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@AndroidEntryPoint
@ExperimentalCoroutinesApi
class WorkspaceListFragment : Fragment() {

    private val viewModel: WorkspaceViewModel by activityViewModels()

    private lateinit var binding: FragmentWorkspaceListBinding

    lateinit var adapter: WorkspaceListAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWorkspaceListBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = WorkspaceListAdapter(
            lifecycleOwner = this,
            workspaceEventHandler = viewModel,
            ::openEditDialog
        )
        binding.recyclerView.adapter = adapter
        binding.recyclerView.autoFitColumns(WORKSPACE_COLUMN_WIDTH_DP, WORKSPACE_SPAN_COUNT)
        binding.recyclerView.addItemDecoration(
            GridSpacingItemDecoratorWithCustomCenter(
                spanCount = WORKSPACE_SPAN_COUNT,
                spacing = requireContext().convertDpToPx(20f).toInt(),
                centerSpacing = requireContext().convertDpToPx(10f).toInt(),
                bottomSpacing = requireContext().convertDpToPx(30f).toInt()
            )
        )
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                viewModel.pageScrolled()
            }
        })
        setObservers()
    }

    private fun setObservers() {
        viewModel.workspacesChildren.observe(viewLifecycleOwner) {
            if (it != null) {
                if (::adapter.isInitialized) {
                    adapter.submitList(it)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun openEditDialog(anchor: View, workspace: WorkspaceI) {
        viewModel.editDialogOpened()
        showEditPopup(anchor, workspace)
    }

    private fun showEditPopup(anchor: View, workspace: WorkspaceI) {
        val popUpView = layoutInflater.inflate(R.layout.popup_edit, null)
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val popupWindow = PopupWindow(popUpView, width, height, true)
        var yoff = requireContext().convertDpToPx(40f).toInt()
        val location = IntArray(2)
        anchor.getLocationOnScreen(location)

        // difference between screen size and anchor location on y axis
        val ydiff = Resources.getSystem().displayMetrics.heightPixels - location[1]
        // if popup is going to be close to bottom nav bar, force yoff in opposite direction
        if (ydiff.toFloat() / Resources.getSystem().displayMetrics.heightPixels < 0.25f) yoff *= -3

        popupWindow.showAsDropDown(anchor, yoff, yoff)
        popupWindow.setOnDismissListener { viewModel.editChannelStop() }

        popUpView.findViewById<TextView>(R.id.edit_text)?.text = getString(R.string.edit_workspace)
        popUpView.findViewById<TextView>(R.id.delete_text)?.text =
            getString(R.string.delete_workspace)

        popUpView.findViewById<View>(R.id.edit)?.setOnClickListener {
            popupWindow.dismiss()
//            val dialog = ChannelEditDialogFragment(channel)
//            dialog.show(requireActivity().supportFragmentManager,
//                ChannelsListFragment.DIALOG_CHANNEL_EDIT
//            )
        }
        popUpView.findViewById<View>(R.id.delete)?.setOnClickListener {
            popupWindow.dismiss()
            val dialog = DeleteDialogFragment(
                name = workspace.name,
                id = workspace.workspaceId,
                delete = ::delete,
                deleteType = ItemType.WORKSPACE
            )
            dialog.show(
                requireActivity().supportFragmentManager,
                DIALOG_WORKSPACE_DELETE
            )
        }
    }

    private fun delete(wsId: String) {
        viewModel.deleteWorkspace(wsId)
    }

    companion object {
        const val WORKSPACE_SPAN_COUNT = 2
        const val WORKSPACE_COLUMN_WIDTH_DP = 100
        const val DIALOG_WORKSPACE_DELETE = "DIALOG_WORKSPACE_DELETE"
    }


}