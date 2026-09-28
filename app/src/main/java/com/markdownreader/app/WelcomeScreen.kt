package com.markdownreader.app

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import androidx.compose.material.icons.filled.Palette
import com.markdownreader.app.ui.theme.ReadingTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    currentTheme: ReadingTheme,
    onThemeChange: (ReadingTheme) -> Unit,
    onOpenFile: () -> Unit,
    errorMessage: String? = null
) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Markdown Reader") },
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
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(120.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Read with ease",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Open a .md file from your file manager or tap below to browse.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            ExtendedFloatingActionButton(
                onClick = onOpenFile,
                icon = { Icon(Icons.Default.FolderOpen, contentDescription = null) },
                text = { Text("Open File") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
