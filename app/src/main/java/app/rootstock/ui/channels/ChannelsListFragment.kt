package app.rootstock.ui.channels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import app.rootstock.adapters.ChannelListAdapter
import app.rootstock.databinding.FragmentChannelsListBinding
import app.rootstock.ui.main.WorkspaceViewModel
import app.rootstock.utils.convertDpToPx
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        adapter = ChannelListAdapter(
            lifecycleOwner = this,
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
            }
        }
    }


}
