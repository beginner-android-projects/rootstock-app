package app.rootstock.ui.channels

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import app.rootstock.R
import app.rootstock.data.channel.Channel
import app.rootstock.databinding.ActivityChannelBinding
import app.rootstock.ui.main.WorkspaceActivity.Companion.BUNDLE_CHANNEL_EXTRA
import app.rootstock.ui.main.WorkspaceActivity.Companion.BUNDLE_WORKSPACE_EXTRA
import app.rootstock.ui.messages.MessagesViewModel
import app.rootstock.utils.hideSoftKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.lang.Exception


@AndroidEntryPoint
@ExperimentalCoroutinesApi
class ChannelActivity : AppCompatActivity() {

    private val viewModel: MessagesViewModel by viewModels()

    private val favouritesViewModel: ChannelFavouritesViewModel by viewModels()

    private lateinit var binding: ActivityChannelBinding

    private var channel: Channel? = null

    lateinit var toolbar: Toolbar

    private val toggleListener = View.OnClickListener {
        channel?.channelId?.let { id ->
            favouritesViewModel.toggle(id).invokeOnCompletion {
                lifecycleScope.launch {
                    checkIcon()
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_channel)

        channel = intent?.getSerializableExtra(BUNDLE_CHANNEL_EXTRA) as? Channel
        channel?.let {
            binding.channel = it
            viewModel.setChannel(it)
        }
        binding.lifecycleOwner = this

        binding.favourites.setOnClickListener(toggleListener)
        setToolbar()

        lifecycleScope.launch {
            checkIcon()
        }

        setObservers()
    }

    private fun setObservers() {
        favouritesViewModel.event.observe(this) {
            when (it.getContentIfNotHandled()) {
                FavouritesEvent.MAXIMUM_REACHED -> {
                    Toast.makeText(
                        this,
                        getString(R.string.favourite_channels_limit_exceeded),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                null -> {
                }
            }

        }
    }

    private suspend fun checkIcon() {
        channel?.channelId?.let {
            val isFavourite = favouritesViewModel.isFavourite(it)
            Log.d("123", "$isFavourite")
            val resource = if (isFavourite) R.drawable.ic_arrow_down else
                R.drawable.ic_favourite
            binding.favourites.setImageResource(resource)
        }
    }

    private fun setToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            closeActivity()
        }
        toolbar.navigationIcon?.setTint(Color.BLACK)
    }

    override fun onBackPressed() {
        closeActivity()
    }

    private fun closeActivity() {
        try {
            hideSoftKeyboard()
        } catch (e: Exception) {
        }
        val data = Intent()
        val modified = viewModel.modifiedChannel.value
        data.putExtra(BUNDLE_WORKSPACE_EXTRA, modified)
        setResult(RESULT_OK, data)
        finish()
    }

}