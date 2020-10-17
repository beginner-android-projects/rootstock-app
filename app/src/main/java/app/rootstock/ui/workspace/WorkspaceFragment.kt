package app.rootstock.ui.workspace

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.rootstock.ui.main.MainWorkspaceViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkspaceFragment : Fragment() {

    private val viewModel: MainWorkspaceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.user.observe(this, {
            Log.d("123", it.toString())
        })

    }

}