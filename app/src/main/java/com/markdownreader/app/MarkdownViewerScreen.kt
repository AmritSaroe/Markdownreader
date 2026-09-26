package com.markdownreader.app

import android.text.method.LinkMovementMethod
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView
import io.noties.markwon.Markwon
import io.noties.markwon.SoftBreakAddsNewLinePlugin
import io.noties.markwon.ext.latex.JLatexMathPlugin
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.linkify.LinkifyPlugin

/**
 * Preprocesses markdown content to ensure MathJax, KaTeX, and LaTeX syntax
 * are standardized for the Markwon renderer:
 * - Converts MathJax block syntax `\[ ... \]` to `$$ ... $$`
 * - Converts MathJax inline syntax `\( ... \)` to `$ ... $`
 */
private fun preprocessMarkdown(input: String): String {
    // 1. Convert MathJax display blocks \[ ... \] to $$ ... $$
    var result = input.replace(Regex("""(?s)\\\[(.*?)\\\]""")) { match ->
        "\n$$\n" + match.groupValues[1].trim() + "\n$$\n"
    }
    // 2. Convert MathJax inline math \( ... \) to $ ... $
    result = result.replace(Regex("""\\\((.*?)\\\)""")) { match ->
        "$" + match.groupValues[1].trim() + "$"
    }
    return result
}

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkdownViewerScreen(
    fileName: String,
    content: String,
    onOpenFile: () -> Unit
) {
    val context = LocalContext.current
    val textColor = MaterialTheme.colorScheme.onBackground.toArgb()
    val linkColor = MaterialTheme.colorScheme.primary.toArgb()
    val backgroundColor = MaterialTheme.colorScheme.background.toArgb()

    var isUiVisible by remember { mutableStateOf(false) }
    
    val window = (context as Activity).window
    val view = androidx.compose.ui.platform.LocalView.current
    val insetsController = remember(window) { WindowCompat.getInsetsController(window, view) }

    // Toggle immersive mode based on UI visibility
    LaunchedEffect(isUiVisible) {
        if (isUiVisible) {
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        } else {
            insetsController.hide(WindowInsetsCompat.Type.systemBars())
            insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    // Ensure system bars are shown when leaving this screen
    DisposableEffect(Unit) {
        onDispose {
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    val markwon = remember(context, textColor) {
        val textSizePx = 16f * context.resources.displayMetrics.scaledDensity
        Markwon.builder(context)
            .usePlugin(TablePlugin.create(context))
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(TaskListPlugin.create(context))
            .usePlugin(HtmlPlugin.create())
            .usePlugin(LinkifyPlugin.create())
            .usePlugin(SoftBreakAddsNewLinePlugin.create())
            
            .usePlugin(JLatexMathPlugin.create(textSizePx) { builder ->
                builder.inlinesEnabled(true)
                builder.blocksEnabled(true)
                builder.theme().textColor(textColor)
                builder.errorHandler { _, _ -> null }
            })
            .build()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            
            // Fullscreen content
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    androidx.core.widget.NestedScrollView(ctx).apply {
                        isNestedScrollingEnabled = true
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        setBackgroundColor(backgroundColor)
                        
                        val clickListener = android.view.View.OnClickListener {
                            isUiVisible = !isUiVisible
                        }
                        setOnClickListener(clickListener)

                        addView(
                            TextView(ctx).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                                setTextIsSelectable(true)
                                movementMethod = LinkMovementMethod.getInstance()
                                setTextColor(textColor)
                                setLinkTextColor(linkColor)
                                textSize = 16f
                                setLineSpacing(8f, 1f)
                                val pad = (16 * ctx.resources.displayMetrics.density).toInt()
                                // Add extra top padding so text isn't stuck under the status bar when reading
                                setPadding(pad, pad * 3, pad, pad * 4)
                                setOnClickListener(clickListener)
                            }
                        )
                    }
                },
                update = { scrollView ->
                    val textView = scrollView.getChildAt(0) as TextView
                    textView.setTextColor(textColor)
                    textView.setLinkTextColor(linkColor)
                    scrollView.setBackgroundColor(backgroundColor)
                    val processed = preprocessMarkdown(content)
                    markwon.setMarkdown(textView, processed)
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
