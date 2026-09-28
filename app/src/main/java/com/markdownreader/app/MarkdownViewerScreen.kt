package com.markdownreader.app

import androidx.compose.material.icons.filled.Palette
import com.markdownreader.app.ui.theme.ReadingTheme
import android.text.method.LinkMovementMethod
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.zIndex
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
import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
    currentTheme: ReadingTheme,
    onThemeChange: (ReadingTheme) -> Unit,
    onOpenFile: () -> Unit
) {
    val context = LocalContext.current
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
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

    val markwon = remember(context, textColor, backgroundColor) {
        val textSizePx = 16f * context.resources.displayMetrics.scaledDensity
        Markwon.builder(context)
            .usePlugin(io.noties.markwon.inlineparser.MarkwonInlineParserPlugin.create())
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
                builder.theme().blockBackgroundProvider { android.graphics.drawable.ColorDrawable(backgroundColor) }
                builder.theme().inlineBackgroundProvider { android.graphics.drawable.ColorDrawable(backgroundColor) }
                builder.errorHandler { _, _ -> null }
            })
            .usePlugin(object : io.noties.markwon.AbstractMarkwonPlugin() {
                override fun configureTheme(builder: io.noties.markwon.core.MarkwonTheme.Builder) {
                    val density = context.resources.displayMetrics.density
                    val alpha12Text = androidx.core.graphics.ColorUtils.setAlphaComponent(textColor, 31) // ~12%
                    val alpha5Text = androidx.core.graphics.ColorUtils.setAlphaComponent(textColor, 13)  // ~5%
                    builder
                        .headingBreakHeight(0)
                        .thematicBreakHeight((1 * density).toInt())
                        .thematicBreakColor(alpha12Text)
                        .headingTextSizeMultipliers(floatArrayOf(1.5f, 1.3f, 1.15f, 1.0f, 0.9f, 0.8f))
                        .blockMargin((14 * density).toInt())
                        .codeBackgroundColor(alpha5Text)
                        .codeBlockBackgroundColor(alpha5Text)
                        .codeTextSize((15 * context.resources.displayMetrics.scaledDensity).toInt())
                }
            })
            .build()
    }

    var parsedMarkdown by remember { mutableStateOf<android.text.Spanned?>(null) }

    LaunchedEffect(content, markwon) {
        withContext(Dispatchers.IO) {
            val processed = preprocessMarkdown(content)
            parsedMarkdown = markwon.toMarkdown(processed)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            AnimatedVisibility(
                visible = isUiVisible,
                enter = slideInVertically(initialOffsetY = { -it }),
                exit = slideOutVertically(targetOffsetY = { -it }),
                modifier = Modifier.zIndex(2f)
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = fileName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                            fontSize = 18.sp
                        )
                    },
                    actions = {
                        IconButton(onClick = {
                            val nextTheme = when (currentTheme) {
                                ReadingTheme.LIGHT -> ReadingTheme.SEPIA
                                ReadingTheme.SEPIA -> ReadingTheme.DARK
                                ReadingTheme.DARK -> ReadingTheme.LIGHT
                                ReadingTheme.SYSTEM -> ReadingTheme.SEPIA
                            }
                            onThemeChange(nextTheme)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Toggle reading theme"
                            )
                        }
                        IconButton(onClick = onOpenFile) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = "Open file"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        actionIconContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    ) { innerPadding ->
        val density = LocalDensity.current
        
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            
            // Fullscreen content
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    androidx.core.widget.NestedScrollView(ctx).apply {
                        isNestedScrollingEnabled = true
                        clipToPadding = false
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
                                textSize = 17f
                                setLineSpacing(0f, 1.35f)
                                val padHoriz = (22 * ctx.resources.displayMetrics.density).toInt()
                                val padVert = (16 * ctx.resources.displayMetrics.density).toInt()
                                setPadding(padHoriz, padVert, padHoriz, padVert * 2)
                                setOnClickListener(clickListener)
                            }
                        )
                    }
                },
                update = { scrollView ->
                    val textView = scrollView.getChildAt(0) as TextView
                    
                    // Apply inner padding to allow content to slide behind toolbar
                    val topPaddingPx = with(density) { innerPadding.calculateTopPadding().toPx().toInt() }
                    val bottomPaddingPx = with(density) { innerPadding.calculateBottomPadding().toPx().toInt() }
                    scrollView.setPadding(0, topPaddingPx, 0, bottomPaddingPx)
                    
                    // Only update if text color or background changed (or first time)
                    if (textView.currentTextColor != textColor) {
                        textView.setTextColor(textColor)
                        textView.setLinkTextColor(linkColor)
                        scrollView.setBackgroundColor(backgroundColor)
                    }
                    
                    // Only set markdown if it changed
                    if (textView.tag != parsedMarkdown) {
                        textView.tag = parsedMarkdown
                        parsedMarkdown?.let { 
                            markwon.setParsedMarkdown(textView, it)
                        }
                    }
                }
            )
        }
    }
}
