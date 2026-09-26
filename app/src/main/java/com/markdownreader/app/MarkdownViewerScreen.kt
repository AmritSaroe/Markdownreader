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
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin
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

    val markwon = remember(context, textColor) {
        val textSizePx = 16f * context.resources.displayMetrics.scaledDensity
        Markwon.builder(context)
            .usePlugin(TablePlugin.create(context))
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(TaskListPlugin.create(context))
            .usePlugin(HtmlPlugin.create())
            .usePlugin(LinkifyPlugin.create())
            .usePlugin(SoftBreakAddsNewLinePlugin.create())
            .usePlugin(MarkwonInlineParserPlugin.create())
            .usePlugin(JLatexMathPlugin.create(textSizePx) { builder ->
                builder.inlinesEnabled(true)
                builder.blocksEnabled(true)
                builder.theme().textColor(textColor)
                builder.errorHandler { _, _ -> null }
            })
            .build()
    }

    Scaffold(
        topBar = {
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
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            factory = { ctx ->
                ScrollView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setBackgroundColor(backgroundColor)

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
                            setPadding(pad, pad, pad, pad)
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
    }
}
