package com.example.ultra.shopping.checkout.presentation.screen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
actual fun PaystackWebView(
    authorizationUrl: String,
    reference: String,
    onSuccess: (reference: String) -> Unit,
    onCanceled: () -> Unit,
    onError: (message: String) -> Unit,
    modifier: Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true

                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        url?.let { handleUrl(it, reference, onSuccess, onCanceled, onError, view) }
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val url = request?.url?.toString() ?: return false
                        return handleUrl(url, reference, onSuccess, onCanceled, onError, view)
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        super.onReceivedError(view, request, error)
                        if (request?.isForMainFrame == true) {
                            onError(error?.description?.toString() ?: "Network error")
                        }
                    }
                }

                loadUrl(authorizationUrl)
            }
        }
    )
}

/**
 * Returns true if the URL was handled (caller should NOT navigate further).
 */
private fun handleUrl(
    url: String,
    reference: String,
    onSuccess: (String) -> Unit,
    onCanceled: () -> Unit,
    onError: (String) -> Unit,
    webView: WebView?
): Boolean {
    // 3DS / card authentication complete — page tries to close itself
    if (url == "https://standard.paystack.co/close") {
        onSuccess(reference)
        webView?.stopLoading()
        return true
    }

    // Paystack cancellation redirect (common pattern)
    if (url.contains("/cancel") || url.contains("cancel_action")) {
        onCanceled()
        webView?.stopLoading()
        return true
    }

    // Successful payment redirect — contains reference in query params
    if (url.contains(reference) || url.contains("trxref=") || url.contains("reference=")) {
        onSuccess(reference)
        webView?.stopLoading()
        return true
    }

    return false
}
