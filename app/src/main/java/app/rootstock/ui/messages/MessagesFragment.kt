package app.rootstock.ui.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import app.rootstock.databinding.MessagesFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessagesFragment : Fragment() {

    private val viewModel: MessagesViewModel by activityViewModels()

    private lateinit var binding: MessagesFragmentBinding

//    lateinit var adapter: ChannelListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MessagesFragmentBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
//        adapter = ChannelListAdapter(
//            lifecycleOwner = this,
//            editDialog = ::openEditDialog,
//            openChannel = ::openChannel
//        )
//        binding.recyclerView.adapter = adapter
//
//        val itemDecorator =
//            SpacingItemDecoration(
//                endSpacing = requireContext().convertDpToPx(dp = 25f).toInt(),
//                startSpacing = requireContext().convertDpToPx(dp = CHANNEL_START_SPACING).toInt(),
//            )
//        binding.recyclerView.apply {
//            addItemDecoration(itemDecorator)
//            addOnScrollListener(object : RecyclerView.OnScrollListener() {
//                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                    super.onScrolled(recyclerView, dx, dy)
//                    viewModel.pageScrolled()
//                }
//            })
//        }
//
    }

    private fun setObservers() {
    }
}
