package app.rootstock.ui.workspace

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import app.rootstock.adapters.WorkspaceEventHandler
import app.rootstock.adapters.WorkspaceListAdapter
import app.rootstock.databinding.FragmentWorkspaceListBinding
import app.rootstock.ui.main.WorkspaceEvent
import app.rootstock.ui.main.WorkspaceViewModel
import app.rootstock.utils.autoFitColumns
import app.rootstock.utils.convertDpToPx
import app.rootstock.utils.makeToast
import app.rootstock.views.GridSpacingItemDecorator
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
            workspaceEventHandler = viewModel
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
                }
            }
        }
    }

    companion object{
        const val WORKSPACE_SPAN_COUNT = 2
        const val WORKSPACE_COLUMN_WIDTH_DP = 100
    }


}