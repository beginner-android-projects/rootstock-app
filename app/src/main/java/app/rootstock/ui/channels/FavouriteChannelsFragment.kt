package app.rootstock.ui.channels

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import app.rootstock.R
import app.rootstock.adapters.ChannelFavouritesAdapter
import app.rootstock.data.channel.Channel
import app.rootstock.databinding.FragmentFavouritesBinding
import app.rootstock.ui.main.WorkspaceActivity
import app.rootstock.utils.autoFitColumns
import app.rootstock.utils.convertDpToPx
import app.rootstock.views.Backdrop
import app.rootstock.views.GridSpacingItemDecoratorWithCustomCenter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class FavouriteChannelsFragment constructor(private val backdrop: Backdrop) : Fragment() {

    private val viewModel: ChannelFavouritesViewModel by viewModels()

    private lateinit var binding: FragmentFavouritesBinding

    lateinit var adapter: ChannelFavouritesAdapter

    // current size of favourite channels in view
    private var currentSize: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    @ExperimentalCoroutinesApi
    private fun openChannel(channel: Channel) {
        val intent = Intent(requireActivity(), ChannelActivity::class.java)
        intent.putExtra(WorkspaceActivity.BUNDLE_CHANNEL_EXTRA, channel)
        startActivityForResult(intent, WorkspaceActivity.REQUEST_CODE_CHANNEL_ACTIVITY)
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ChannelFavouritesAdapter(viewLifecycleOwner, ::openChannel)

        binding.recyclerView.apply {
            adapter = this@FavouriteChannelsFragment.adapter
            autoFitColumns(
                CHANNELS_COLUMN_WIDTH_DP,
                CHANNELS_SPAN_COUNT
            )
            // inner and bottom-element padding are same
            addItemDecoration(
                GridSpacingItemDecoratorWithCustomCenter(
                    spanCount = CHANNELS_SPAN_COUNT,
                    spacing = requireContext().convertDpToPx(5f).toInt(),
                    centerSpacing = requireContext().convertDpToPx(5f).toInt(),
                    bottomSpacing = requireContext().convertDpToPx(10f).toInt()
                )
            )
            val pad = requireContext().convertDpToPx(20f).toInt()
            setPadding(
                pad,
                paddingTop,
                pad,
                pad
            )
        }

        viewModel.favourites.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            if (currentSize != null && currentSize != it.size)
                backdrop.closeBackdrop()
            currentSize = it.size
            if (it.isNullOrEmpty()) {
                binding.favouritesTitle.text = getString(R.string.no_favourite_channels)
                binding.favouritesStar.visibility = View.GONE
            } else {
                if (binding.favouritesStar.visibility == View.GONE) binding.favouritesStar.visibility =
                    View.VISIBLE
                if (binding.favouritesTitle.text == getString(R.string.no_favourite_channels))
                    binding.favouritesTitle.text = getString(R.string.favourite_channels)
            }
        }
    }

    companion object {
        const val CHANNELS_SPAN_COUNT = 2
        const val CHANNELS_COLUMN_WIDTH_DP = 100
    }


}