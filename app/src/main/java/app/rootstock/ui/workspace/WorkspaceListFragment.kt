package app.rootstock.ui.workspace

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import app.rootstock.adapters.WorkspaceEventHandler
import app.rootstock.adapters.WorkspaceListAdapter
import app.rootstock.databinding.FragmentWorkspaceListBinding
import app.rootstock.ui.main.WorkspaceEvent
import app.rootstock.ui.main.WorkspaceViewModel
import app.rootstock.utils.makeToast
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
            workspaces = viewModel.workspacesChildren,
            workspaceEventHandler = viewModel
        )
        binding.recyclerView.adapter = adapter
        setObservers()
    }

    private fun setObservers() {
        viewModel.workspacesChildren.observe(viewLifecycleOwner) {
            if (it != null){
                if (::adapter.isInitialized) {
                    adapter.submitList(it)
                }
            }
        }
    }


}