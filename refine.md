# Bug Fix: Resolve Toolbar & Document Header Overlap and Inset Issues

Looking at the current screen, the top action bar (containing the filename, theme toggle, and folder picker) is rendering directly over the Markdown document's H1 title. The top bar is also colliding with the top edge of the display.

---

### 1. Fix Layout Overlap

#### If Using Jetpack Compose:
- Locate the main screen's `Scaffold`.
- Ensure the lambda's `innerPadding` / `paddingValues` is passed to the content:
  ```kotlin
    Scaffold(
    	      topBar = { TopAppBar(...) },
    	            contentWindowInsets = WindowInsets.statusBars
    	              ) { innerPadding ->
    	                    Box(modifier = Modifier
    	                              .fillMaxSize()
    	                                        .padding(innerPadding) // <-- Fix: ensure content starts BELOW the topBar
    	                                                  .consumeWindowInsets(innerPadding)
    	                                                        ) {
    	                                                        	          // Markdown View / Content
    	                                                        	                }
    	                                                        	                  }
    	                                                        	                  
    	                                                        }}
    )
