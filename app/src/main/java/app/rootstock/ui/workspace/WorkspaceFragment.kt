package app.rootstock.ui.workspace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import app.rootstock.adapters.WorkspacePagerAdapter
import app.rootstock.databinding.FragmentWorkspaceBinding
import app.rootstock.ui.main.WorkspaceEvent
import app.rootstock.ui.main.WorkspaceViewModel
import app.rootstock.utils.makeToast
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@AndroidEntryPoint
@ExperimentalCoroutinesApi
class WorkspaceFragment : Fragment() {

    private val args: WorkspaceFragmentArgs by navArgs()

    private val viewModel: WorkspaceViewModel by activityViewModels()

    private lateinit var binding: FragmentWorkspaceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWorkspaceBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setup adapter for viewpager with tablayout
        val adapter = WorkspacePagerAdapter(this)
        binding.pager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = when (position) {
                1 -> "channels"
                else -> "workspaces"
            }
        }.attach()
        viewModel.loadWorkspace(args.workspaceId)
        setObservers()

    }

    private fun setObservers() {
        viewModel.eventWorkspace.observe(viewLifecycleOwner) {
            when (val content = it.getContentIfNotHandled()) {
                is WorkspaceEvent.OpenWorkspace -> {
                    openWorkspace(content.workspaceId)
                }
                is WorkspaceEvent.NoUser -> {
                    makeToast("Please relogin to see workspaces")
                }
                is WorkspaceEvent.Error -> {
                    makeToast("Some errors with network...")

                }
                null -> {
                }
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (args.workspaceId != null)
                        findNavController().navigateUp()
                    else activity?.finish()
                }
            })
    }

    /**
     * navigates to workspace using workspaceId
     * If workspaceId is null, we are in a root workspace
     */
    private fun openWorkspace(workspaceId: String) {
        findNavController().navigate(
            WorkspaceFragmentDirections.actionWorkspaceFragmentSelf(
                workspaceId
            )
        )
    }


}