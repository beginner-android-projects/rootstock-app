package app.rootstock.ui.main

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import app.rootstock.R
import app.rootstock.data.channel.Channel
import app.rootstock.data.network.CreateOperation
import app.rootstock.data.network.ReLogInObservable
import app.rootstock.data.network.ReLogInObserver
import app.rootstock.ui.settings.SettingsActivity
import app.rootstock.ui.signup.RegisterActivity
import app.rootstock.utils.convertDpToPx
import app.rootstock.views.ChannelCreateDialogFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main_workspace.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class WorkspaceActivity : AppCompatActivity(), ReLogInObserver {

    private val viewModel: WorkspaceViewModel by viewModels()

    @Inject
    lateinit var reLogInObservable: ReLogInObservable

    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_workspace)

        findViewById<FloatingActionButton>(R.id.fab)?.apply {
            // make icon white
            setColorFilter(Color.WHITE)
            shapeAppearanceModel =
                shapeAppearanceModel.withCornerSize { 30f }
            // set listeners
            setOnClickListener { openAddItemDialog() }
        }

        findViewById<BottomNavigationView>(R.id.bottom_app_bar)?.apply {
            setOnNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_home -> viewModel.navigateToRoot()
                    R.id.menu_settings -> navigateToSettings()
                }
                true
            }
        }
        toolbar = findViewById(R.id.home_toolbar)
        setSupportActionBar(toolbar)
        home_toolbar.navigationIcon?.setTint(Color.WHITE)
        setObservers()

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
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun setObservers() {
        viewModel.workspace.observe(this) {
            if (it == null) return@observe
            home_toolbar.title = it.name
            if (viewModel.isAtRoot == false) toolbar.navigationIcon =
                null else toolbar.navigationIcon =
                ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_down, null)
        }
        viewModel.pagerPosition.observe(this) {
            if (it == null) return@observe
            animateFab(it)
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

    private fun applyDim(@NonNull parent: ViewGroup, dimAmount: Float) {
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
        if (position > 1) return
        val toCircle = position == 1
        // 10f - round dps for square button
        // 30f - for circle button
        val startEnd = if (toCircle) 10f to 30f else 30f to 10f
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
        const val DIALOG_CHANNEL_CREATE = "DIALOG_CHANNEL_CREATE"
    }


}
