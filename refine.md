# Complete Remaining Markdown Fixes

The previous changes addressed syntax highlighting, headings, and callouts, but missed several core extensions and list layout bugs:

1. **Add Missing Markwon Plugins (`build.gradle.kts`):**
   - Add `implementation("io.noties.markwon:ext-strikethrough:4.6.2")`
      - Add `implementation("io.noties.markwon:html:4.6.2")`
         - Register `StrikethroughPlugin.create()` and `HtmlPlugin.create()` inside the Markwon builder in `MarkdownViewerScreen.kt`.

         2. **Fix Task List Checkbox Spacing:**
            - If using `TaskListPlugin`, configure the task list drawable/span or add a trailing space/padding so `[x] Text` does not glue the checkbox directly against the first letter (`☑Text` -> `☑ Text`).

            3. **Verify Underscore Parsing:**
               - Verify why `__bold__` and `_italic_` failed to render. Check if any text-sanitization or regex replacement is escaping or stripping `_` characters before passing the string to Markwon.
               
