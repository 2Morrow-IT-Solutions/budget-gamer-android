package com.tomorrowit.budgetgamer.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tomorrowit.budgetgamer.R
import com.tomorrowit.budgetgamer.common.config.extensions.finishAnimation
import com.tomorrowit.budgetgamer.common.utils.Logic
import com.tomorrowit.budgetgamer.common.utils.ViewHandler
import com.tomorrowit.budgetgamer.common.utils.ViewHelper
import com.tomorrowit.budgetgamer.databinding.ActivityHtmlBinding
import com.tomorrowit.budgetgamer.presentation.base.BaseSlideActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.InputStream
import javax.inject.Inject

@AndroidEntryPoint
class HtmlActivity : BaseSlideActivity() {
    private var value = 0;

    companion object {
        const val PAGE_CODE: String = "PAGE_CODE"
        const val ABOUT_APP: Int = 0
        const val GDPR: Int = 1
        const val TERMS_AND_CONDITIONS: Int = 2
    }

    private lateinit var binding: ActivityHtmlBinding

    @Inject
    lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHtmlBinding.inflate(layoutInflater)

        setContentView(binding.root)

        value = intent.getIntExtra(PAGE_CODE, 0)

        when (value) {
            ABOUT_APP -> prepareAboutApp()
            GDPR -> prepareGDPR()
            TERMS_AND_CONDITIONS -> prepareTerms()
        }

        // Enable Javascript
        binding.htmlActivityWebview.getSettings().setJavaScriptEnabled(true)

        binding.htmlActivityWebview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                injectCSS()
                super.onPageFinished(view, url)
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val i = Intent(Intent.ACTION_VIEW, (request?.url))
                this@HtmlActivity.startActivity(i)
                return true
            }
        }

        setViews()
    }

    // Inject CSS method: read style-dark.css from assets folder
    // Append stylesheet to document head
    private fun injectCSS() {
        try {
            val inputStream: InputStream = if (ViewHandler.isDarkModeOn(this@HtmlActivity)) {
                assets.open("styles/style-dark.css")
            } else {
                assets.open("styles/style-light.css")
            }
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            inputStream.close()
            val encoded = Base64.encodeToString(buffer, Base64.NO_WRAP)
            binding.htmlActivityWebview.evaluateJavascript(
                "javascript:(function() {" +
                        "var parent = document.getElementsByTagName('head').item(0);" +
                        "var style = document.createElement('style');" +
                        "style.type = 'text/css';" +  // Tell the browser to BASE64-decode the string into your script !!!
                        "style.innerHTML = window.atob('" + encoded + "');" +
                        "parent.appendChild(style)" +
                        "})()"
            ) {
                showWebView()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun prepareAboutApp() {
        binding.htmlActivityBar.title = getString(R.string.about_app)
        checkSupport(Logic.getPhoneLanguageCode())
    }

    private fun prepareGDPR() {
        binding.htmlActivityBar.title = getString(R.string.gdpr)
        checkSupport(Logic.getPhoneLanguageCode())
    }

    private fun prepareTerms() {
        binding.htmlActivityBar.title = getString(R.string.terms)
        checkSupport(Logic.getPhoneLanguageCode())
    }

    private fun checkSupport(language: String) {
        val listRef: StorageReference = when (value) {
            ABOUT_APP -> storage.reference.child(language).child("about_app.html")
            GDPR -> storage.reference.child(language).child("gdpr.html")
            TERMS_AND_CONDITIONS -> storage.reference.child(language).child("terms.html")
            else -> storage.reference.child("pages").child(language).child("about_app.html")
        }
        listRef.downloadUrl.addOnSuccessListener { uri -> binding.htmlActivityWebview.loadUrl(uri.toString()) }
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
        ViewHelper.hideView(binding.htmlActivityWebview)
        ViewHelper.showView(binding.htmlActivityLoading)
    }

    private fun showWebView() {
        //Todo: Hide error here
        ViewHelper.showView(binding.htmlActivityWebview)
        ViewHelper.hideView(binding.htmlActivityLoading)
    }

    override fun finish() {
        super.finish()
        finishAnimation()
    }

    private fun setViews() {
        binding.htmlActivityBar.setNavigationOnClickListener {
            finish()
        }
    }
}