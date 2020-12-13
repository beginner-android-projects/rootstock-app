package app.rootstock.ui.channels

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import app.rootstock.R
import app.rootstock.data.channel.Channel
import app.rootstock.data.network.ReLogInObservable
import app.rootstock.data.network.ReLogInObserver
import app.rootstock.databinding.ActivityChannelBinding
import app.rootstock.ui.main.WorkspaceActivity.Companion.BUNDLE_CHANNEL_EXTRA
import app.rootstock.ui.main.WorkspaceActivity.Companion.BUNDLE_WORKSPACE_EXTRA
import app.rootstock.ui.messages.MessagesViewModel
import app.rootstock.ui.signup.RegisterActivity
import app.rootstock.utils.hideSoftKeyboard
import app.rootstock.views.ChannelEditDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
@ExperimentalCoroutinesApi
class ChannelActivity : AppCompatActivity(), ReLogInObserver {

    private val messagesViewModel: MessagesViewModel by viewModels()

    private val favouritesViewModel: ChannelFavouritesViewModel by viewModels()

    private lateinit var binding: ActivityChannelBinding

    private var channel: Channel? = null

    lateinit var toolbar: Toolbar

    private val toggleListener = View.OnClickListener {
        channel?.channelId?.let { id ->
            favouritesViewModel.toggle(id).invokeOnCompletion {
                checkIcon()
            }
        }
    }

    private val moreListener = View.OnClickListener {
        messagesViewModel.channel.value?.let { c ->
            val dialog = ChannelEditDialogFragment(c, ::updateChannel)
            dialog.show(supportFragmentManager, DIALOG_CHANNEL_EDIT)
        }
    }

    private fun updateChannel(channel: Channel) {
        messagesViewModel.setChannel(channel)
        messagesViewModel.modifyChannel()
    }

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_channel)
        binding.viewmodel = messagesViewModel
        binding.lifecycleOwner = this

        channel = intent?.getSerializableExtra(BUNDLE_CHANNEL_EXTRA) as? Channel
        channel?.let { messagesViewModel.setChannel(it) }

        binding.favourites.setOnClickListener(toggleListener)
        binding.more.setOnClickListener(moreListener)
        setToolbar()
        checkIcon()

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

    private fun checkIcon() {
        lifecycleScope.launch {
            channel?.channelId?.let {
                val isFavourite = favouritesViewModel.isFavourite(it)
                val resource = if (isFavourite) R.drawable.ic_arrow_down else
                    R.drawable.ic_favourite
                binding.favourites.setImageResource(resource)
            }
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
        val modified = messagesViewModel.modifiedChannel.value
        data.putExtra(BUNDLE_WORKSPACE_EXTRA, modified)
        setResult(RESULT_OK, data)
        finish()
    }

    companion object {
        const val DIALOG_CHANNEL_EDIT = "DIALOG_CHANNEL_EDIT"
    }


    @Inject
    lateinit var reLogInObservable: ReLogInObservable

    override fun onStart() {
        super.onStart()
        reLogInObservable.addObserver(this)
    }

    override fun onStop() {
        super.onStop()
        reLogInObservable.removeObserver(this)
    }

    override fun submit() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finishAfterTransition()
    }
}