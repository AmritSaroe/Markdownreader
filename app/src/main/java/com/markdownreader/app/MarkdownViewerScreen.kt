package com.markdownreader.app

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun Color.toHex(): String =
    String.format("#%06X", 0xFFFFFF and toArgb())

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkdownViewerScreen(
    fileName: String,
    content: String,
    onOpenFile: () -> Unit
) {
    val context = LocalContext.current

    // Theme colors
    val background = MaterialTheme.colorScheme.background
    val bgArgb = background.toArgb()
    val textColorHex = MaterialTheme.colorScheme.onBackground.toHex()
    val bgColorHex = background.toHex()
    val linkColorHex = MaterialTheme.colorScheme.primary.toHex()
    val codeBgHex = MaterialTheme.colorScheme.surfaceVariant.toHex()
    val borderColorHex = MaterialTheme.colorScheme.outlineVariant.toHex()

    // Keep latest content accessible from the JS interface (created once in factory)
    val currentContent by rememberUpdatedState(content)

    // Immersive mode
    var isUiVisible by remember { mutableStateOf(false) }
    val view = LocalView.current
    val window = context.findActivity()?.window
    val insetsController = remember(window, view) {
        window?.let { WindowCompat.getInsetsController(it, view) }
    }

    LaunchedEffect(isUiVisible) {
        if (isUiVisible) {
            insetsController?.show(WindowInsetsCompat.Type.systemBars())
        } else {
            insetsController?.hide(WindowInsetsCompat.Type.systemBars())
            insetsController?.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            insetsController?.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    // Track when the WebView template + CDN scripts have fully loaded
    var pageReady by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        setBackgroundColor(bgArgb)

                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true

                        addJavascriptInterface(object {
                            @JavascriptInterface
                            fun getContent(): String = currentContent

                            @JavascriptInterface
                            fun onContentTapped() {
                                post { isUiVisible = !isUiVisible }
                            }

                            @JavascriptInterface
                            fun onPageReady() {
                                post { pageReady = true }
                            }
                        }, "Android")

                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                request?.url?.let { url ->
                                    try {
                                        context.startActivity(Intent(Intent.ACTION_VIEW, url))
                                    } catch (e: Exception) {
                                        // Ignore if no app can handle the URL
                                    }
                                }
                                return true
                            }
                        }

                        loadUrl("file:///android_asset/markdown_template.html")
                    }
                },
                update = { webView ->
                    webView.setBackgroundColor(bgArgb)
                    if (pageReady) {
                        webView.evaluateJavascript(
                            "setThemeColors('$textColorHex','$bgColorHex','$linkColorHex'," +
                                "'$codeBgHex','$borderColorHex');" +
                                "renderMarkdown(Android.getContent());",
                            null
                        )
                    }
                }
            )

            // Overlay TopAppBar
            AnimatedVisibility(
                visible = isUiVisible,
                enter = slideInVertically(initialOffsetY = { -it }),
                exit = slideOutVertically(targetOffsetY = { -it }),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = fileName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    actions = {
                        IconButton(onClick = onOpenFile) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = "Open file"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}
