package com.example.ultra.shopping.checkout.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.WebKit.WKWebpagePreferences
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PaystackWebView(
    authorizationUrl: String,
    reference: String,
    onSuccess: (reference: String) -> Unit,
    onCanceled: () -> Unit,
    onError: (message: String) -> Unit,
    modifier: Modifier
) {
    val config = WKWebViewConfiguration()
    val webpagePreferences = WKWebpagePreferences()
    webpagePreferences.allowsContentJavaScript = true

    config.defaultWebpagePreferences = webpagePreferences

    val webView = remember { WKWebView(frame = platform.CoreGraphics.CGRectMake(0.0, 0.0, 0.0, 0.0), configuration = config) }

    val navDelegate = remember {
        object : NSObject(), WKNavigationDelegateProtocol {
            override fun webView(
                webView: WKWebView,
                decidePolicyForNavigationAction: platform.WebKit.WKNavigationAction,
                decisionHandler: (platform.WebKit.WKNavigationActionPolicy) -> Unit
            ) {
                val url = webView.URL?.absoluteString ?: ""
                val handled = handleIosUrl(url, reference, onSuccess, onCanceled, onError)
                decisionHandler(
                    if (handled) platform.WebKit.WKNavigationActionPolicy.WKNavigationActionPolicyCancel
                    else platform.WebKit.WKNavigationActionPolicy.WKNavigationActionPolicyAllow
                )
            }
        }
    }

    UIKitView(
        modifier = modifier,
        factory = {
            webView.apply {
                navigationDelegate = navDelegate
                loadRequest(platform.Foundation.NSURLRequest(platform.Foundation.NSURL(string = authorizationUrl)))
            }
        }
    )
}

private fun handleIosUrl(
    url: String,
    reference: String,
    onSuccess: (String) -> Unit,
    onCanceled: () -> Unit,
    onError: (String) -> Unit
): Boolean {
    if (url == "https://standard.paystack.co/close") {
        onSuccess(reference)
        return true
    }

    if (url.contains("/cancel") || url.contains("cancel_action")) {
        onCanceled()
        return true
    }

    if (url.contains(reference) || url.contains("trxref=") || url.contains("reference=")) {
        onSuccess(reference)
        return true
    }

    return false
}
