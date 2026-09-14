# RepSet Tracker

A simple black/red gym counter built for one-thumb use.

## Features
- Equal-size **+ REP**, **+ SET**, and **COMPLETE SET** buttons
- Button order: **+ REP → COMPLETE SET → + SET**
- **+ REP** is bright red, **+ SET** is dark red, and **COMPLETE SET** is dark with a red outline
- **COMPLETE SET** adds one set and resets reps to 0
- **− REP** and **− SET** correction buttons
- Reset confirmation so a workout is not wiped accidentally
- Black/red dark theme
- Haptic tap feedback in the Android APK
- Keeps the Android screen awake while the app is open
- Saves current sets/reps automatically
- No account, ads, or in-app install button

## Option 1 — Build the Android APK on GitHub
1. Create a GitHub repository and upload everything in this project.
2. Make sure the default branch is `main`.
3. Open **Actions** → **Build Android APK**.
4. Choose **Run workflow** (a push to `main` also triggers it).
5. Open the completed workflow run and download **RepSet-Tracker-APK**.
6. Unzip the artifact and install `app-debug.apk` on Android.

Android may ask you to allow installs from your browser/files app because the debug APK is not from Google Play.

## Option 2 — Chrome install from the three-dot menu
The repository also includes a PWA version: `index.html`, `manifest.json`, `sw.js`, and the app icons.

After the repo is published with GitHub Pages, open its Pages URL in Chrome. Chrome can offer **⋮ → Install app** or **Add to Home screen**, depending on the Chrome/Android version. There is intentionally **no install button on the tracker screen**.

To publish with the simple GitHub Pages branch method:
1. Open the repo's **Settings** → **Pages**.
2. Under **Build and deployment**, choose **Deploy from a branch**.
3. Select branch `main` and folder `/ (root)`.
4. Save and open the GitHub Pages URL after deployment completes.

## Android package
`com.kingdomunderground.repsettracker`
