package com.markdownreader.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.markdownreader.app.ui.theme.MarkdownReaderTheme
import java.io.BufferedReader
import java.io.InputStreamReader

import android.content.Context
import com.markdownreader.app.ui.theme.ReadingTheme

class MainActivity : ComponentActivity() {

    private var currentContent by mutableStateOf<String?>(null)
    private var currentFileName by mutableStateOf("Marko")
    private var errorMessage by mutableStateOf<String?>(null)
    private var currentTheme by mutableStateOf(ReadingTheme.SYSTEM)

    private val openDocumentLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
            }
            handleUri(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            val attributes = window.attributes
            attributes.layoutInDisplayCutoutMode = android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = attributes
        }
        
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val savedTheme = prefs.getString("theme", ReadingTheme.SYSTEM.name) ?: ReadingTheme.SYSTEM.name
        currentTheme = try { ReadingTheme.valueOf(savedTheme) } catch (e: Exception) { ReadingTheme.SYSTEM }
        
        handleIntent(intent)

        setContent {
            MarkdownReaderTheme(
                readingTheme = currentTheme,
                dynamicColor = false // Disable dynamic color to strictly use our optimized reading colors
            ) {
                val content = currentContent
                if (content != null) {
                    MarkdownViewerScreen(
                        fileName = currentFileName,
                        content = content,
                        currentTheme = currentTheme,
                        onThemeChange = { newTheme ->
                            currentTheme = newTheme
                            getSharedPreferences("settings", Context.MODE_PRIVATE)
                                .edit().putString("theme", newTheme.name).apply()
                        },
                        onOpenFile = { openFilePicker() }
                    )
                } else {
                    WelcomeScreen(
                        currentTheme = currentTheme,
                        onThemeChange = { newTheme ->
                            currentTheme = newTheme
                            getSharedPreferences("settings", Context.MODE_PRIVATE)
                                .edit().putString("theme", newTheme.name).apply()
                        },
                        onOpenFile = { openFilePicker() },
                        errorMessage = errorMessage
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun openFilePicker() {
        openDocumentLauncher.launch(
            arrayOf(
                "text/markdown",
                "text/x-markdown",
                "text/plain",
                "application/octet-stream",
                "*/*"
            )
        )
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) return

        when (intent.action) {
            Intent.ACTION_VIEW -> {
                intent.data?.let { handleUri(it) }
            }
            Intent.ACTION_SEND -> {
                if (intent.type == "text/plain") {
                    val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                    if (sharedText != null) {
                        currentFileName = "Shared Text.md"
                        currentContent = sharedText
                        errorMessage = null
                        return
                    }
                }
                @Suppress("DEPRECATION")
                val uri = intent.getParcelableExtra<android.os.Parcelable>(Intent.EXTRA_STREAM) as? Uri
                if (uri != null) {
                    handleUri(uri)
                }
            }
        }
    }

    private fun handleUri(uri: Uri) {
        currentFileName = getFileName(uri)
        val content = readFileContent(uri)
        if (content != null) {
            currentContent = content
            errorMessage = null
        } else {
            errorMessage = "Could not read the file."
        }
    }

    private fun getFileName(uri: Uri): String {
        // Try to get display name from content resolver
        try {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) {
                        val name = cursor.getString(nameIndex)
                        if (!name.isNullOrBlank()) return name
                    }
                }
            }
        } catch (_: Exception) { }

        // Fallback: extract from URI path
        return uri.lastPathSegment?.substringAfterLast('/') ?: "Unknown.md"
    }

    private fun readFileContent(uri: Uri): String? {
        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).readText()
            }
        } catch (e: Exception) {
            null
        }
    }
}
