package app.rootstock.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import app.rootstock.R
import app.rootstock.data.network.ReLogInObservable
import app.rootstock.data.network.ReLogInObserver
import app.rootstock.ui.signup.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@AndroidEntryPoint
class WorkspaceActivity : AppCompatActivity(), ReLogInObserver {

    @ExperimentalCoroutinesApi
    private val viewModel: WorkspaceViewModel by viewModels()

    @Inject
    lateinit var reLogInObservable: ReLogInObservable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_workspace)
    }

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
