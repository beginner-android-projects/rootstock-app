package app.rootstock.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import app.rootstock.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkspaceActivity : AppCompatActivity() {

    private val viewModel: MainWorkspaceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_workspace)

    }

}