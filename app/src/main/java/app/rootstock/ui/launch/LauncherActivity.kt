package app.rootstock.ui.launch

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import app.rootstock.ui.main.WorkspaceActivity
import app.rootstock.ui.signup.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint


// todo: remove cleartext traffic from manifest on prod

@AndroidEntryPoint
class LauncherActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: LaunchViewModel by viewModels()

        viewModel.launchDestination.observe(this) {
            when (it.getContentIfNotHandled()) {
                LaunchDestination.WORKSPACE_ACTIVITY -> startWorkspaceActivity()
                LaunchDestination.SIGN_UP_ACTIVITY -> startSignInActivity()
                null -> startSignInActivity()
            }
        }
    }
    private fun startWorkspaceActivity() {
        val intent = Intent(this, WorkspaceActivity::class.java)
        startActivity(intent)
        finishAfterTransition()
    }

    private fun startSignInActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finishAfterTransition()
    }
}