# Workflow Adjustment: Termux & GitHub Actions CI

Do not run `./gradlew` locally. This project compiles exclusively via GitHub Actions CI from a Termux development environment.

---

### Instructions:
1. **Check Wrapper in Git:**
   - Run `git status` or `git log -1 -- gradlew` to verify whether `gradlew` and `gradle/wrapper/gradle-wrapper.properties` exist in git tracking or were accidentally removed.
      - If `gradlew` exists in git, restore it (`git checkout HEAD -- gradlew gradle/`).
         - If it was never added, keep our code changes ready and do not attempt to invoke a local build.
         2. **Review Code Modifications:**
            - Verify that all changes in `build.gradle.kts` (Prism4j, syntax highlighting, kapt/plugins) and `MarkdownViewerScreen.kt` (headings, callout visitor) are cleanly formatted with no syntax errors.
            3. **Commit Changes:**
               - Commit the applied bug fixes with a clear message:
                    `git commit -am "fix: add syntax highlighting, gfm callouts, and heading scale"`
                    4. **Push to Trigger CI:**
                       - Push the commit to trigger the GitHub Actions build workflow.
                       
