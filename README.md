# Childphone

## Getting started
1. Install Android Studio Giraffe or newer with Android SDK 34 and build tools.
2. Open the project via **File ▸ Open** and select the repository root.
3. Let Gradle sync; the first sync will download dependencies using the Gradle wrapper.

## Running the app
- Use an emulator or device with API level 26+.
- Grant the in-app camera permission when asked to exercise the CameraX sandbox tab.
- No real phone calls, contacts, or system settings are modified by the app. Everything is simulated within the sandbox.

## Gradle wrapper JAR policy
The hosting environment rejects raw binary files, so the canonical `gradle/wrapper/gradle-wrapper.jar` is not committed.

Instead a text-based `gradle-wrapper.jar.base64` is stored in the same directory. Both `gradlew` (Unix) and `gradlew.bat` (Windows) first attempt to decode this stub; if it is missing they fall back to downloading the official Gradle distribution and extracting the wrapper JAR. Ensure that one of the following tools is available:

- Unix: `base64` or `python3` to decode the stub, plus `curl`/`wget` and `unzip`/`python3` for the download fallback.
- Windows: `certutil` or PowerShell to decode, and `curl` or PowerShell to download/extract if needed.

After the first run the decoded JAR lives under `gradle/wrapper/` locally and Gradle behaves just like a standard project.

If you prefer manual control you can also pre-create the JAR via `gradle wrapper --gradle-version 8.2.1` using a locally installed Gradle distribution; the auto-restore logic detects the file and skips decoding/downloading.
