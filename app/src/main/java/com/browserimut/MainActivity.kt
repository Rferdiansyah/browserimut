package com.browserimut

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var urlEditText: EditText
    private lateinit var btnBack: ImageButton
    private lateinit var btnForward: ImageButton
    private lateinit var btnHome: ImageButton

    private val homeUrl = "https://duckduckgo.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        urlEditText = findViewById(R.id.urlEditText)
        btnBack = findViewById(R.id.btnBack)
        btnForward = findViewById(R.id.btnForward)
        btnHome = findViewById(R.id.btnHome)

        setupWebView()
        setupUrlBar()
        setupButtons()

        // Load default homepage
        webView.loadUrl(homeUrl)
    }

    private fun setupWebView() {
        webView.webViewClient = object : WebViewClient() {
            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                urlEditText.setText(url)
                updateNavigationButtons()
                super.doUpdateVisitedHistory(view, url, isReload)
            }
        }
        webView.webChromeClient = WebChromeClient()

        val settings: WebSettings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        // Enable force zoom
        settings.setSupportZoom(true)
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
    }

    private fun setupUrlBar() {
        urlEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_GO ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                // Hide keyboard
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                v.clearFocus()

                var url = v.text.toString()
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    if (url.contains(".") && !url.contains(" ")) {
                        url = "https://$url"
                    } else {
                        // Search
                        url = "https://duckduckgo.com/?q=${url.replace(" ", "+")}"
                    }
                }
                webView.loadUrl(url)
                true
            } else {
                false
            }
        }
    }

    private fun setupButtons() {
        btnBack.setOnClickListener {
            if (webView.canGoBack()) webView.goBack()
        }
        btnForward.setOnClickListener {
            if (webView.canGoForward()) webView.goForward()
        }
        btnHome.setOnClickListener {
            webView.loadUrl(homeUrl)
        }
    }

    private fun updateNavigationButtons() {
        if (webView.canGoBack()) {
            btnBack.isEnabled = true
            btnBack.setColorFilter(Color.parseColor("#FFFFFF"))
        } else {
            btnBack.isEnabled = false
            btnBack.setColorFilter(Color.parseColor("#888888"))
        }

        if (webView.canGoForward()) {
            btnForward.isEnabled = true
            btnForward.setColorFilter(Color.parseColor("#FFFFFF"))
        } else {
            btnForward.isEnabled = false
            btnForward.setColorFilter(Color.parseColor("#888888"))
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
