package app.rootstock.ui.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import app.rootstock.adapters.MessageAdapter
import app.rootstock.databinding.MessagesFragmentBinding
import app.rootstock.utils.convertDpToPx
import app.rootstock.views.MessagesLoadStateAdapter
import app.rootstock.views.SpacingItemDecorationReversed
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@AndroidEntryPoint
@ExperimentalPagingApi
class MessagesFragment : Fragment() {

    private val viewModel: MessagesViewModel by activityViewModels()

    private lateinit var binding: MessagesFragmentBinding

    private var searchJob: Job? = null

    private lateinit var adapter: MessageAdapter

    private fun search(channelId: Long) {
        // Make sure we cancel the previous job before creating a new one
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.searchRepo(channelId).collectLatest {
                adapter.submitData(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MessagesFragmentBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        viewModel._channel.observe(viewLifecycleOwner) {
            search(channelId = it.channelId)
            initSearch()
        }

        val itemDecorator =
            SpacingItemDecorationReversed(requireContext().convertDpToPx(20f).toInt())
        binding.list.apply {
            addItemDecoration(itemDecorator)
        }

        binding.send.setOnClickListener { }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.channel.value?.channelId?.let { outState.putLong(LAST_SEARCH_QUERY, it) }
    }

    private fun initAdapter() {
        adapter = MessageAdapter(viewLifecycleOwner)
        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = MessagesLoadStateAdapter { adapter.retry() },
            footer = MessagesLoadStateAdapter { adapter.retry() }
        )
        adapter.addLoadStateListener { loadState ->
            // Only show the list if refresh succeeds.
            binding.list.isVisible = loadState.source.refresh is LoadState.NotLoading
            // Show loading spinner during initial load or refresh.
//            binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
            // Show the retry state if initial load or refresh fails.
//            binding.retryButton.isVisible = loadState.source.refresh is LoadState.Error

            // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                Toast.makeText(
                    requireContext(),
                    "\uD83D\uDE28 Wooops",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun initSearch() {
        // Scroll to top when the list is refreshed from network.
        lifecycleScope.launch {
            adapter.loadStateFlow
                // Only emit when REFRESH LoadState for RemoteMediator changes.
                .distinctUntilChangedBy { it.refresh }
                // Only react to cases where Remote REFRESH completes i.e., NotLoading.
                .filter { it.refresh is LoadState.NotLoading }
                .collect { binding.list.scrollToPosition(0) }
        }
    }

    companion object {
        private const val LAST_SEARCH_QUERY: String = "last_search_query"
    }
}

