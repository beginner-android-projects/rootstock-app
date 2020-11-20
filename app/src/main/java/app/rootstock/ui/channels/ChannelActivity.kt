package app.rootstock.ui.channels

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import app.rootstock.R
import app.rootstock.data.network.ReLogInObservable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main_workspace.*
import javax.inject.Inject

@AndroidEntryPoint
class ChannelActivity: AppCompatActivity() {

    private val viewModel: ChannelViewModel by viewModels()

    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_workspace)

        toolbar = findViewById(R.id.home_toolbar)
        setSupportActionBar(toolbar)
        home_toolbar.navigationIcon?.setTint(Color.WHITE)
        setObservers()

    }

    private fun setObservers() {

    }

}