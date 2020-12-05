package app.rootstock.ui.channels

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import app.rootstock.R
import app.rootstock.data.channel.Channel
import app.rootstock.databinding.ActivityChannelBinding
import app.rootstock.ui.main.WorkspaceActivity.Companion.BUNDLE_CHANNEL_EXTRA
import app.rootstock.ui.main.WorkspaceActivity.Companion.BUNDLE_WORKSPACE_EXTRA
import app.rootstock.ui.messages.MessagesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@AndroidEntryPoint
@ExperimentalCoroutinesApi
class ChannelActivity : AppCompatActivity() {

    private val viewModel: MessagesViewModel by viewModels()

    private lateinit var binding: ActivityChannelBinding

    private var channel: Channel? = null

    lateinit var toolbar: Toolbar

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        channel = intent?.getSerializableExtra(BUNDLE_CHANNEL_EXTRA) as? Channel
        binding = DataBindingUtil.setContentView(this, R.layout.activity_channel)
        binding.channel = channel
        binding.lifecycleOwner = this

        setToolbar()
        channel?.let { viewModel.setChannel(it) }

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
        val data = Intent()
        val modified = viewModel.modifiedChannel.value
        data.putExtra(BUNDLE_WORKSPACE_EXTRA, modified)
        setResult(RESULT_OK, data)
        finish()
    }

}