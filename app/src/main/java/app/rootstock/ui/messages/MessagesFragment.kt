package app.rootstock.ui.messages

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.PagingData
import app.rootstock.adapters.MessageAdapter
import app.rootstock.data.messages.Message
import app.rootstock.data.messages.MessageRepository.Companion.NETWORK_PAGE_SIZE
import app.rootstock.databinding.MessagesFragmentBinding
import app.rootstock.utils.convertDpToPx
import app.rootstock.utils.showKeyboard
import app.rootstock.views.MessagesLoadStateAdapter
import app.rootstock.views.SpacingItemDecorationReversed
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_account_start.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
@OptIn(ExperimentalPagingApi::class)
class MessagesFragment : Fragment() {

    private val viewModel: MessagesViewModel by activityViewModels()

    private lateinit var binding: MessagesFragmentBinding

    private var searchJob: Job? = null

    private lateinit var adapter: MessageAdapter

    private var created = false

    private var isEditing = false

    private fun search(channelId: Long, refresh: Boolean = false) {
        // Make sure we cancel the previous job before creating a new one
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.searchRepo(channelId, refresh).collectLatest {
                try {
                    adapter.submitData(it)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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
        viewModel.channel.observe(viewLifecycleOwner) {
            it ?: return@observe
            search(channelId = it.channelId)
            initSearch()
        }

        val itemDecorator =
            SpacingItemDecorationReversed(requireContext().convertDpToPx(SPACING).toInt())
        binding.list.apply {
            addItemDecoration(itemDecorator)
        }

        binding.send.setOnClickListener {
            sendMessage()
        }
        viewModel.messageEvent.observe(viewLifecycleOwner) { event ->
            when (event.getContentIfNotHandled()) {
                MessageEvent.ERROR -> {
                }
                MessageEvent.CREATED -> {
                }
                else -> {
                }
            }
        }


    }

    private fun refreshList() {
        adapter.lastItemPosition = 1
        if (adapter.lastItemPosition < NETWORK_PAGE_SIZE) {
            binding.list.scrollToPosition(0); return
        }
        viewModel.channel.value?.let {
            lifecycleScope.launch {
                adapter.submitData(PagingData.empty())
                search(channelId = it.channelId, refresh = true)
            }
        }
    }

    private fun sendMessage() {
        val message = binding.content.text.toString()
        if (message.isBlank()) return
        binding.content.text?.clear()
        binding.content.clearFocus()
        if (isEditing) {
            isEditing = false
            messageEditingId?.let {
                viewModel.editMessage(it, message)
            }
            messageEditingId = null
        } else {
            created = true
            // todo set in a variable so in case of an error saved copy will be displayed
            viewModel.sendMessage(message)
        }
    }

    private var messageEditingId: Long? = null

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.channel.value?.channelId?.let { outState.putLong(LAST_SEARCH_QUERY, it) }
    }

    private fun deleteMessage(id: Long, anchor: View) {
        viewModel.deleteMessage(id)
    }


    private fun editMessage(message: Message) {
        isEditing = true
        messageEditingId = message.messageId
        binding.content.setText(message.content)
        binding.content.requestFocus()
        requireContext().showKeyboard()
        binding.content.setSelection(binding.content.length())
    }

    private fun initAdapter() {
        adapter = MessageAdapter(viewLifecycleOwner, ::deleteMessage, ::editMessage)
        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = MessagesLoadStateAdapter { adapter.retry() },
            footer = MessagesLoadStateAdapter { adapter.retry() }
        )
        adapter.addLoadStateListener { loadState ->
            // Only show the list if refresh succeeds.
//            binding.list.isVisible = loadState.source.refresh is LoadState.NotLoading
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
        lifecycleScope.launch {
            adapter.dataRefreshFlow.collect {
                // if message has been created, scroll to the bottom
                if (created) {
                    created = false
                    refreshList()
                }
            }
        }
    }


    companion object {
        private const val LAST_SEARCH_QUERY: String = "last_search_query"
        private const val SPACING: Float = 20f

    }
}

