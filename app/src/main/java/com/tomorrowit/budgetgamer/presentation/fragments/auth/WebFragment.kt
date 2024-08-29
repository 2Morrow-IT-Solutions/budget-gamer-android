package com.tomorrowit.budgetgamer.presentation.fragments.auth

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tomorrowit.budgetgamer.R
import com.tomorrowit.budgetgamer.common.utils.Logic
import com.tomorrowit.budgetgamer.common.utils.ViewHandler
import com.tomorrowit.budgetgamer.common.utils.ViewHelper
import com.tomorrowit.budgetgamer.databinding.FragmentWebBinding
import com.tomorrowit.budgetgamer.domain.listeners.OnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import java.io.InputStream
import javax.inject.Inject

@AndroidEntryPoint
class WebFragment : Fragment() {
    private var page: Int = TERMS_AND_CONDITIONS
    private var social: Boolean = false

    private lateinit var binding: FragmentWebBinding

    @Inject
    lateinit var storage: FirebaseStorage

    companion object {
        const val TERMS_AND_CONDITIONS: Int = 1
        const val GDPR: Int = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentWebBinding.inflate(inflater, container, false)
        .apply { binding = this }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (this.arguments != null) {
            page = this.arguments?.getInt("page") ?: TERMS_AND_CONDITIONS
            social = this.arguments?.getBoolean("social") ?: false
        }

        // Enable Javascript
        binding.webFragmentWebview.getSettings().setJavaScriptEnabled(true)

        binding.webFragmentWebview.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                injectCSS()
                super.onPageFinished(view, url)
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val i = Intent(Intent.ACTION_VIEW, (request?.url))
                requireActivity().startActivity(i)
                return true
            }
        })

        binding.webFragmentBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        if (page == GDPR) {
            binding.webFragmentBar.title = getString(R.string.gdpr)
            binding.webFragmentAccept.setOnClickListener(object : OnSingleClickListener() {
                override fun onSingleClick(v: View?) {
                    if (social) {
                        findNavController().navigate(R.id.action_gdprFragment_to_socialFragment)
                    } else {
                        findNavController().navigate(R.id.action_gdprFragment_to_registerFragment)
                    }
                }
            })
            checkSupport(Logic.getPhoneLanguageCode())
        } else {
            binding.webFragmentBar.title = getString(R.string.terms)
            binding.webFragmentAccept.setOnClickListener(object : OnSingleClickListener() {
                override fun onSingleClick(v: View?) {
                    if (social) {
                        findNavController().navigate(R.id.action_termsFragment_to_gdprFragment_social)
                    } else {
                        findNavController().navigate(R.id.action_termsFragment_to_gdprFragment)
                    }
                }
            })
            checkSupport(Logic.getPhoneLanguageCode())
        }

    }

    // Inject CSS method: read style-dark.css from assets folder
    // Append stylesheet to document head
    private fun injectCSS() {
        try {
            val inputStream: InputStream
            inputStream = if (ViewHandler.isDarkModeOn(this@WebFragment.requireActivity())) {
                this@WebFragment.requireActivity().assets.open("styles/style-dark.css")
            } else {
                this@WebFragment.requireActivity().assets.open("styles/style-light.css")
            }
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            inputStream.close()
            val encoded = Base64.encodeToString(buffer, Base64.NO_WRAP)
            binding.webFragmentWebview.evaluateJavascript("javascript:(function() {" +
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
        val listRef: StorageReference = when (page) {
            GDPR -> storage.reference.child(language).child("gdpr.html")
            TERMS_AND_CONDITIONS -> storage.reference.child(language).child("terms.html")
            else -> storage.reference.child(language).child("terms.html")
        }

        listRef.downloadUrl.addOnSuccessListener { uri -> binding.webFragmentWebview.loadUrl(uri.toString()) }
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
        ViewHelper.hideView(binding.webFragmentWebview)
        ViewHelper.showView(binding.webFragmentLoading)
    }

    private fun showWebView() {
        //Todo: Hide error here
        ViewHelper.showView(binding.webFragmentWebview)
        ViewHelper.hideView(binding.webFragmentLoading)
        ViewHelper.showView(binding.webFragmentAccept)
    }
}