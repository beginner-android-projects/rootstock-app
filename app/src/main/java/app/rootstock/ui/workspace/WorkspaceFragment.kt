package app.rootstock.ui.workspace

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import app.rootstock.databinding.FragmentLoginBinding
import app.rootstock.databinding.FragmentMainWorkspaceBinding
import app.rootstock.ui.main.MainWorkspaceEvent
import app.rootstock.ui.main.MainWorkspaceViewModel
import app.rootstock.utils.makeToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class WorkspaceFragment : Fragment() {

    private val viewModel: MainWorkspaceViewModel by viewModels()

    private lateinit var binding: FragmentMainWorkspaceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainWorkspaceBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }


    override fun onStart() {
        super.onStart()
        setObservers()
    }

    private fun setObservers() {
        viewModel.eventWorkspace.observe(viewLifecycleOwner) {
            when (it.getContentIfNotHandled()) {
                MainWorkspaceEvent.NO_USER -> {
                    makeToast("Please relogin to see workspaces")
                }
                MainWorkspaceEvent.ERROR -> {
                    makeToast("Some errors with network...")
                }
                null -> {
                }
            }
        }
        viewModel.workspace.observe(viewLifecycleOwner) {
        }
    }


}