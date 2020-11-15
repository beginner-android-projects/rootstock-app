package app.rootstock.ui.settings

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import app.rootstock.R
import app.rootstock.adapters.SettingsListAdapter
import app.rootstock.databinding.ActivitySettingsBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModels()

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        setRv()
        setObservers()
        setToolbar()

    }

    private fun setRv() {
        val items = listOf(
            SettingsItem(
                drawable = R.drawable.ic_baseline_work_24,
                title = getString(R.string.privacy_policy),
                actionHandler = privacyItemClick
            ),
            SettingsItem(
                drawable = R.drawable.ic_baseline_work_24,
                title = getString(R.string.settings_sign_out),
                actionHandler = signOutItemClick
            ),
            SettingsItem(
                drawable = R.drawable.ic_delete_24,
                title = getString(R.string.settings_delete_account),
                actionHandler = deleteItemClick
            ),
        )

        val adapterToSet = SettingsListAdapter(items = items, lifecycleOwner = this)
        binding.rv.apply {
            adapter = adapterToSet
        }
    }

    private val privacyItemClick = object : SettingsItemClick {
        override fun invoke() {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(URL_PRIVACY_POLICY))
            startActivity(intent)
        }
    }

    private val signOutItemClick = object : SettingsItemClick {
        override fun invoke() {
            showSignOutDialog()
        }
    }

    private fun showSignOutDialog() {
        //
    }

    private val deleteItemClick = object : SettingsItemClick {
        override fun invoke() {
            showDeleteDialog()
        }
    }

    private fun showDeleteDialog() {
        //
    }

    private fun setToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.navigationIcon?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setObservers() {
        viewModel.userData.observe(this) {
        }
    }

    companion object {
        // todo real site link
        const val URL_PRIVACY_POLICY = "https://google.com"
    }

}

class SettingsItem(
    @DrawableRes val drawable: Int,
    val title: String,
    val actionHandler: SettingsItemClick
)