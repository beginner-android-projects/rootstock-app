package app.rootstock.ui.workspace

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import app.rootstock.R
import app.rootstock.adapters.WorkspacePagerAdapter
import app.rootstock.databinding.FragmentWorkspaceBinding
import app.rootstock.ui.favourite.FavouriteFragment
import app.rootstock.ui.main.WorkspaceEvent
import app.rootstock.ui.main.WorkspaceViewModel
import app.rootstock.utils.makeToast
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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

    private val changeFab = object : ViewPager2.OnPageChangeCallback() {
        var currentPosition: Int? = 0
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            if (currentPosition != position) {
                currentPosition = position
                viewModel.animateFab(position)
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setup adapter for viewpager with tablayout
        val adapter = WorkspacePagerAdapter(this)

        binding.pager.registerOnPageChangeCallback(changeFab)

        binding.pager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = when (position) {
                1 -> "channels"
                else -> "workspaces"
            }
        }.attach()
        viewModel.loadWorkspace(args.workspaceId)
        setObservers()
        setUpFavourites(args.workspaceId)

    }

    private fun setUpFavourites(workspaceId: String?) {
        if (workspaceId != null) return
        lifecycleScope.launch {
            val parent = view?.findViewById<View>(R.id.favourites_placeholder)
            val favouriteFragment = FavouriteFragment()
            if (parent != null) {
                parentFragmentManager.beginTransaction()
                    .add(R.id.favourites_placeholder, favouriteFragment).commit()
            }
        }
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
                is WorkspaceEvent.NavigateToRoot -> {
                    navigateToRoot()
                }
                null -> {
                }
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!isAtRoot())
                        findNavController().navigateUp()
                    else activity?.finish()
                }
            })
    }

    private fun isAtRoot() = args.workspaceId == null

    private fun navigateToRoot() {
        if (!isAtRoot())
            findNavController().navigate(R.id.workspace_fragment)
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