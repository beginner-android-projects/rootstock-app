package app.rootstock.ui.channels

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.navigation.navArgs
import app.rootstock.R
import app.rootstock.databinding.ActivityChannelBinding
import app.rootstock.ui.channels.ChannelActivityArgs.Companion.fromBundle
import app.rootstock.ui.messages.MessagesViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChannelActivity : AppCompatActivity() {

    private val viewModel: MessagesViewModel by viewModels()

    private lateinit var binding: ActivityChannelBinding

    private val args: ChannelActivityArgs by navArgs()

    private val channel by lazy {
        fromBundle(args.toBundle()).channel
    }

    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_channel)
        binding.channel = channel
        binding.lifecycleOwner = this

        setToolbar()
        setObservers()
        channel?.let { viewModel.setChannel(it) }

    }

    private fun setToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        toolbar.navigationIcon?.setTint(Color.WHITE)
    }

    private fun setObservers() {

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.channel_menu, menu)
        return true
    }
}