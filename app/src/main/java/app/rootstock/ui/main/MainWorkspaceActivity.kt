package app.rootstock.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import app.rootstock.R
import app.rootstock.ui.signin.SignUpActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainWorkspaceActivity : AppCompatActivity() {

    private val viewModel: MainWorkspaceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!viewModel.isSignedIn()) {
            startSignInActivity()
            return
        }
        setContentView(R.layout.activity_main_workspace)


    }

    private fun startSignInActivity() {
        val intent = Intent(this, SignUpActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        startActivity(intent)
        finish()
    }
}