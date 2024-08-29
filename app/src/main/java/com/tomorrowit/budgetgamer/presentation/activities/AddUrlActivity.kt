package com.tomorrowit.budgetgamer.presentation.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tomorrowit.budgetgamer.BuildConfig
import com.tomorrowit.budgetgamer.R
import com.tomorrowit.budgetgamer.common.config.extensions.finishAnimation
import com.tomorrowit.budgetgamer.common.config.extensions.isGameUrl
import com.tomorrowit.budgetgamer.common.config.extensions.isUrl
import com.tomorrowit.budgetgamer.common.utils.ClipboardHelper
import com.tomorrowit.budgetgamer.common.utils.Logic
import com.tomorrowit.budgetgamer.common.utils.ViewHandler
import com.tomorrowit.budgetgamer.common.utils.ViewHelper
import com.tomorrowit.budgetgamer.databinding.ActivityAddUrlBinding
import com.tomorrowit.budgetgamer.domain.listeners.OnSingleClickListener
import com.tomorrowit.budgetgamer.presentation.base.BaseSlideActivity
import com.tomorrowit.budgetgamer.presentation.dialogs.InfoDialog
import com.tomorrowit.budgetgamer.presentation.dialogs.LoadingDialog
import com.tomorrowit.budgetgamer.presentation.viewmodels.AddUrlState
import com.tomorrowit.budgetgamer.presentation.viewmodels.AddUrlViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.InputStream
import javax.inject.Inject

@AndroidEntryPoint
class AddUrlActivity : BaseSlideActivity() {
    private lateinit var binding: ActivityAddUrlBinding

    private val viewModel: AddUrlViewModel by viewModels()

    companion object {
        const val ACTION_ID: String = "ACTION_ID"
        const val ADD_GAME: Int = 0
        const val ADD_ARTICLE: Int = 1
    }

    @Inject
    lateinit var clipboardHelper: ClipboardHelper

    private var userChoice: Int = ADD_GAME

    private val isGame: Boolean
        get() = userChoice == ADD_GAME

    private val loadingDialog: LoadingDialog by lazy {
        LoadingDialog(
            this@AddUrlActivity,
            getString(R.string.please_wait_dots),
            getString(R.string.sending_link)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUrlBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeState()

        userChoice = intent.extras?.getInt(ACTION_ID) ?: ADD_GAME

        if (userChoice == ADD_ARTICLE) {
            binding.addUrlActivityBar.title = getString(R.string.add_article)
            binding.addUrlActivityLayout.setHint(R.string.add_article_hint)
        } else {
            binding.addUrlActivityBar.title = getString(R.string.add_game)
            binding.addUrlActivityLayout.setHint(R.string.add_game_hint)
        }

        binding.addUrlActivityBar.setNavigationOnClickListener {
            finish()
        }
        checkSupport(Logic.getPhoneLanguageCode())

        binding.addUrlActivitySubmit.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                if (validateFields()) {
                    viewModel.addLink(isGame, binding.addUrlActivityEdittext.text.toString())
                }
            }
        })

        @SuppressLint("SetJavaScriptEnabled") // The WebView is local.
        binding.addUrlActivityInstructions.settings.javaScriptEnabled = true

        binding.addUrlActivityInstructions.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                injectCSS()
                super.onPageFinished(view, url)
            }
        }
    }

    private fun observeState() {
        viewModel.stateUrl.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                when (state) {
                    is AddUrlState.Init -> {}

                    is AddUrlState.IsLoading -> {
                        loadingDialog.show()
                    }

                    is AddUrlState.IsSuccess -> {
                        loadingDialog.dismiss()
                        InfoDialog(
                            this@AddUrlActivity,
                            getString(R.string.success),
                            state.message
                        ).apply {
                            setOnDismissListener {
                                finish()
                            }
                        }.show()
                    }

                    is AddUrlState.ShowWarning -> {
                        loadingDialog.dismiss()
                        InfoDialog(
                            this@AddUrlActivity,
                            getString(R.string.error),
                            state.message
                        ).apply {
                            setOnDismissListener {
                                finish()
                            }
                        }.show()
                    }

                    is AddUrlState.IsAllowed -> {
                        loadingDialog.dismiss()
                        InfoDialog(
                            this@AddUrlActivity,
                            getString(R.string.error),
                            state.message
                        ).show()
                    }

                    is AddUrlState.IsDenied -> {
                        loadingDialog.dismiss()
                        InfoDialog(
                            this@AddUrlActivity,
                            getString(R.string.error),
                            state.message
                        ).show()
                    }
                }

            }.launchIn(lifecycleScope)
    }

    private fun validateFields(): Boolean {
        if (binding.addUrlActivityEdittext.text.toString().isEmpty()) {
            InfoDialog(
                this@AddUrlActivity,
                getString(R.string.error),
                getString(R.string.empty_field)
            ).show()
            return false
        } else {
            if (userChoice == ADD_ARTICLE) {
                if (!binding.addUrlActivityEdittext.text.toString().isUrl()) {
                    InfoDialog(
                        this@AddUrlActivity,
                        getString(R.string.error),
                        getString(R.string.link_wrong_format)
                    ).show()
                    return false
                }
            } else {
                if (!binding.addUrlActivityEdittext.text.toString().isGameUrl()) {
                    InfoDialog(
                        this@AddUrlActivity,
                        getString(R.string.error),
                        getString(R.string.link_wrong_format)
                    ).show()
                    return false
                }
            }
        }
        return true
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            val clipboardText: String = clipboardHelper.copyFromClipboard()

            if (userChoice == ADD_ARTICLE) {
                if (clipboardText.isUrl() && binding.addUrlActivityEdittext.text.toString()
                        .isEmpty()
                ) {
                    binding.addUrlActivityEdittext.setText(clipboardText)
                }
            } else {
                if (clipboardText.isGameUrl() && binding.addUrlActivityEdittext.text.toString()
                        .isEmpty()
                ) {
                    binding.addUrlActivityEdittext.setText(clipboardText)
                }
            }
        }
    }

    // Inject CSS method: read style-dark.css from assets folder
    // Append stylesheet to document head
    private fun injectCSS() {
        try {
            val inputStream: InputStream = if (ViewHandler.isDarkModeOn(this@AddUrlActivity)) {
                assets.open("styles/style-dark.css")
            } else {
                assets.open("styles/style-light.css")
            }
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            inputStream.close()
            val encoded = Base64.encodeToString(buffer, Base64.NO_WRAP)
            binding.addUrlActivityInstructions.evaluateJavascript("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +  // Tell the browser to BASE64-decode the string into your script !!!
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()", ValueCallback<String?> {
                showWebView()
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkSupport(language: String) {
        val storage: FirebaseStorage = FirebaseStorage.getInstance(BuildConfig.FIREBASE_STORAGE_URL)

        val listRef: StorageReference = when (userChoice) {
            ADD_GAME -> storage.reference.child(language)
                .child("add_game_instructions.html")

            ADD_ARTICLE -> storage.reference.child(language)
                .child("add_article_instructions.html")

            else -> storage.reference.child(language).child("add_article_instructions.html")
        }
        listRef.downloadUrl.addOnSuccessListener { uri ->
            binding.addUrlActivityInstructions.loadUrl(
                uri.toString()
            )
        }
            .addOnFailureListener {
                if (language != "en") {
                    checkSupport("en")
                } else {
                    showError()
                }
            }
    }

    private fun showError() {
        //Todo: Show error layout here
        ViewHelper.hideView(binding.addUrlActivityInstructions)
        ViewHelper.showView(binding.addUrlActivityLoading)
    }

    private fun showWebView() {
        //Todo: Hide error here
        ViewHelper.showView(binding.addUrlActivityInstructions)
        ViewHelper.hideView(binding.addUrlActivityLoading)
    }

    override fun finish() {
        super.finish()
        finishAnimation()
    }
}