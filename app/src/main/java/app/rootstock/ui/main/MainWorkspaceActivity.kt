package app.rootstock.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.rootstock.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainWorkspaceActivity : AppCompatActivity() {

//    private  val viewModel: HomeActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_workspace)


    }
}