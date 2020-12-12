package app.rootstock.ui.main

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import app.rootstock.R
import app.rootstock.data.channel.Channel
import app.rootstock.data.network.CreateOperation
import app.rootstock.data.network.ReLogInObservable
import app.rootstock.data.network.ReLogInObserver
import app.rootstock.databinding.ActivityMainWorkspaceBinding
import app.rootstock.ui.channels.FavouriteChannelsFragment
import app.rootstock.ui.settings.SettingsActivity
import app.rootstock.ui.signup.RegisterActivity
import app.rootstock.utils.convertDpToPx
import app.rootstock.views.ChannelCreateDialogFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main_workspace.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class WorkspaceActivity : AppCompatActivity(), ReLogInObserver {

    private val viewModel: WorkspaceViewModel by viewModels()

    @Inject
    lateinit var reLogInObservable: ReLogInObservable

    private lateinit var binding: ActivityMainWorkspaceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_workspace)

        setClickListeners()
        setToolbar()
        setObservers()
    }

    private fun setToolbar() {
        setSupportActionBar(binding.homeToolbar)
        binding.homeToolbar.navigationIcon?.setTint(Color.WHITE)
    }

    private fun setClickListeners() {
        binding.fab.apply {
            // make icon white
            setColorFilter(Color.WHITE)
            shapeAppearanceModel =
                shapeAppearanceModel.withCornerSize { BUTTON_ROUND_SIZE }
            // set listeners
            setOnClickListener { openAddItemDialog() }
        }

        binding.bottomAppBar.apply {
            setOnNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_home -> viewModel.navigateToRoot()
                    R.id.menu_settings -> navigateToSettings()
                }
                true
            }
        }
    }

    private fun createChannelOperation(op: CreateOperation<Channel?>) {
        when (op) {
            is CreateOperation.Success -> {
                op.obj?.let { viewModel.addChannel(it) }
            }
            is CreateOperation.Error -> {
                // todo toast/snackbar
            }
        }
    }

    private fun openAddItemDialog() {
        when (viewModel.pagerPosition.value) {
            1 -> {
                val dialog = viewModel.workspace.value?.workspaceId?.let {
                    ChannelCreateDialogFragment(
                        it, ::createChannelOperation
                    )
                }
                dialog?.show(
                    supportFragmentManager,
                    DIALOG_CHANNEL_CREATE
                )
            }
            else -> {
            }
        }
    }

    private fun navigateToSettings() {
        // todo change to nav
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun setObservers() {
        viewModel.workspace.observe(this) {
            if (it == null) return@observe
            home_toolbar.title = it.name
            if (viewModel.isAtRoot.value == false) binding.homeToolbar.navigationIcon =
                null else binding.homeToolbar.navigationIcon =
                ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_down, null)
        }
        viewModel.pagerPosition.observe(this) {
            if (it == null || it > 1) return@observe
            if (viewModel.hasSwiped)
                animateFab(it)
            else changeFabBackground(it)
        }

        viewModel.eventEdit.observe(this) {
            when (it.getContentIfNotHandled()) {
                EditEvent.EDIT_OPEN -> {
                    val vg = window.decorView.rootView as? ViewGroup ?: return@observe
                    applyDim(vg, DIM_AMOUNT)
                }
                EditEvent.EDIT_EXIT -> {
                    val vg = window.decorView.rootView as? ViewGroup ?: return@observe
                    clearDim(vg)
                }
                else -> {
                }
            }
        }
        viewModel.isAtRoot.observe(this) {
            if (it == true) {
                try {
                    supportFragmentManager.commit {
                        val fragment =
                            FavouriteChannelsFragment(
                                binding.backdropView,
                                viewModel.favouriteShowed
                            ) { viewModel.showFavourite() }
                        setReorderingAllowed(true)
                        add(R.id.favourites_container, fragment)
                    }
                } catch (e: Exception) {
                }
            }
        }
        viewModel.pagerScrolled.observe(this) {
            when (it.getContentIfNotHandled()) {
                PagerEvent.PAGER_SCROLLED -> {
                    backdrop_view.closeBackdrop()
                }
                else -> {
                }
            }
        }
    }

    private fun changeFabBackground(position: Int) {
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        val toCircle = position == 1
        val value = if (toCircle) BUTTON_ROUND_SIZE else BUTTON_ROUNDED_SQUARE_SIZE
        fab.apply {
            shapeAppearanceModel =
                shapeAppearanceModel.withCornerSize { (convertDpToPx(value)) }
        }
    }

    private fun applyDim(parent: ViewGroup, dimAmount: Float) {
        val dim: Drawable = ColorDrawable(Color.BLACK)
        dim.setBounds(0, 0, parent.width, parent.height)
        dim.alpha = (255 * dimAmount).toInt()
        val overlay = parent.overlay
        overlay.add(dim)
    }

    private fun clearDim(parent: ViewGroup) {
        val overlay = parent.overlay
        overlay.clear()

    }

    private fun animateFab(position: Int) {
        // if is currently on channels fragment, animate to circle
        val toCircle = position == 1
        val startEnd =
            if (toCircle) BUTTON_ROUNDED_SQUARE_SIZE to BUTTON_ROUND_SIZE else BUTTON_ROUND_SIZE to BUTTON_ROUNDED_SQUARE_SIZE
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        lifecycleScope.launch {
            ObjectAnimator.ofFloat(fab, "interpolation", startEnd.first, startEnd.second).apply {
                duration = ANIMATION_DURATION_FAB
                addUpdateListener { animator ->
                    fab.apply {
                        shapeAppearanceModel =
                            shapeAppearanceModel.withCornerSize { (convertDpToPx(animator.animatedValue as Float)) }
                    }
                }
            }.start()
        }
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


    companion object {
        const val ANIMATION_DURATION_FAB = 200L
        const val DIM_AMOUNT = 0.3f

        // 10f - round dps for square button
        // 30f - for circle button
        const val BUTTON_ROUNDED_SQUARE_SIZE = 10f
        const val BUTTON_ROUND_SIZE = 30f
        const val DIALOG_CHANNEL_CREATE = "DIALOG_CHANNEL_CREATE"
        const val REQUEST_CODE_CHANNEL_ACTIVITY = 100
        const val BUNDLE_WORKSPACE_EXTRA = "BUNDLE_WORKSPACE_EXTRA"
        const val BUNDLE_CHANNEL_EXTRA = "BUNDLE_WORKSPACE_EXTRA"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CHANNEL_ACTIVITY) {
                data?.getBooleanExtra(BUNDLE_WORKSPACE_EXTRA, false)?.let {
                    if (it) viewModel.loadWorkspace(viewModel.workspace.value?.workspaceId)
                }
            }
        }
    }
}
