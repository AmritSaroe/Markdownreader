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

class MainActivity : ComponentActivity() {

    private var currentContent by mutableStateOf<String?>(null)
    private var currentFileName by mutableStateOf("Markdown Reader")
    private var errorMessage by mutableStateOf<String?>(null)

    private val openDocumentLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            // Take persistable permission so we can re-read if needed
            try {
                contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
                // Not all providers support persistable permissions
            }
            handleUri(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleIntent(intent)

        setContent {
            MarkdownReaderTheme {
                val content = currentContent
                if (content != null) {
                    MarkdownViewerScreen(
                        fileName = currentFileName,
                        content = content,
                        onOpenFile = { openFilePicker() }
                    )
                } else {
                    WelcomeScreen(
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
