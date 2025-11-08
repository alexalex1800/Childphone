@ECHO OFF

SETLOCAL

SET "APP_BASE_NAME=%~n0"
SET "APP_HOME=%~dp0"
IF "%APP_HOME:~-1%"=="\" SET "APP_HOME=%APP_HOME:~0,-1%"

SET "WRAPPER_DIR=%APP_HOME%\gradle\wrapper"
SET "WRAPPER_JAR=%WRAPPER_DIR%\gradle-wrapper.jar"
SET "WRAPPER_STUB=%WRAPPER_DIR%\gradle-wrapper.jar.base64"
SET "PROPERTIES_FILE=%WRAPPER_DIR%\gradle-wrapper.properties"

IF NOT EXIST "%WRAPPER_DIR%" (
    MKDIR "%WRAPPER_DIR%" || (
        ECHO ERROR: Unable to create wrapper directory "%WRAPPER_DIR%"
        EXIT /B 1
    )
)

IF NOT EXIST "%WRAPPER_JAR%" (
    IF EXIST "%WRAPPER_STUB%" (
        SET "WRAPPER_TEMP=%WRAPPER_JAR%.part"
        ECHO Gradle wrapper JAR missing. Restoring from base64 stub.
        WHERE certutil >NUL 2>&1
        IF %ERRORLEVEL% EQU 0 (
            certutil -f -decode "%WRAPPER_STUB%" "%WRAPPER_TEMP%" >NUL
        ) ELSE (
            WHERE powershell >NUL 2>&1
            IF %ERRORLEVEL% EQU 0 (
                powershell -NoProfile -Command "try { [IO.File]::WriteAllBytes('%WRAPPER_TEMP%', [Convert]::FromBase64String((Get-Content -Raw -LiteralPath '%WRAPPER_STUB%'))) } catch { exit 1 }"
            ) ELSE (
                ECHO ERROR: Please install certutil or PowerShell to decode the Gradle wrapper stub.
                EXIT /B 1
            )
        )
        IF NOT EXIST "%WRAPPER_TEMP%" (
            ECHO ERROR: Failed to decode Gradle wrapper stub.
            EXIT /B 1
        )
        MOVE /Y "%WRAPPER_TEMP%" "%WRAPPER_JAR%" >NUL
        IF %ERRORLEVEL% NEQ 0 (
            ECHO ERROR: Unable to move decoded wrapper into place.
            EXIT /B 1
        )
    ) ELSE (
        IF NOT EXIST "%PROPERTIES_FILE%" (
            ECHO ERROR: %PROPERTIES_FILE% is missing. Please regenerate the Gradle wrapper.
            EXIT /B 1
        )

        FOR /F "tokens=2 delims==" %%I IN ('FINDSTR /B "distributionUrl" "%PROPERTIES_FILE%"') DO SET "DISTRIBUTION_URL=%%I"
        SET "DISTRIBUTION_URL=%DISTRIBUTION_URL:https\://=https://%"
        FOR %%I IN (%DISTRIBUTION_URL%) DO SET "DISTRIBUTION_URL=%%~I"

        SET "WRAPPER_VERSION=%DISTRIBUTION_URL:*-=%"
        SET "WRAPPER_VERSION=%WRAPPER_VERSION:-bin.zip=%"
        SET "WRAPPER_VERSION=%WRAPPER_VERSION:-all.zip=%"

        SET "WRAPPER_DOWNLOAD=%DISTRIBUTION_URL%"
        SET "WRAPPER_TEMP_ZIP=%WRAPPER_DIR%\gradle-%WRAPPER_VERSION%.zip"
        SET "WRAPPER_TEMP=%WRAPPER_JAR%.part"

        ECHO Gradle wrapper JAR missing. Downloading distribution %WRAPPER_DOWNLOAD%

        WHERE curl >NUL 2>&1
        IF %ERRORLEVEL% EQU 0 (
            curl -fL "%WRAPPER_DOWNLOAD%" -o "%WRAPPER_TEMP_ZIP%"
        ) ELSE (
            WHERE powershell >NUL 2>&1
            IF %ERRORLEVEL% EQU 0 (
                powershell -NoProfile -Command "try { Invoke-WebRequest -Uri '%WRAPPER_DOWNLOAD%' -OutFile '%WRAPPER_TEMP_ZIP%' -UseBasicParsing } catch { exit 1 }"
            ) ELSE (
                ECHO ERROR: Please install curl or PowerShell to download the Gradle distribution.
                EXIT /B 1
            )
        )

        IF NOT EXIST "%WRAPPER_TEMP_ZIP%" (
            ECHO ERROR: Failed to download Gradle distribution.
            EXIT /B 1
        )

        powershell -NoProfile -Command "try { $zip = '%WRAPPER_TEMP_ZIP%'; $jar = '%WRAPPER_TEMP%'; $version = '%WRAPPER_VERSION%'; Add-Type -AssemblyName System.IO.Compression.FileSystem; $archive = [System.IO.Compression.ZipFile]::OpenRead($zip); $entry = $archive.Entries | Where-Object { $_.FullName -eq \"gradle-$version/lib/gradle-wrapper-$version.jar\" }; if (-not $entry) { exit 1 }; $entry.Open().CopyTo([IO.File]::Create($jar)); $archive.Dispose() } catch { exit 1 }"
        IF %ERRORLEVEL% NEQ 0 (
            ECHO ERROR: Unable to extract wrapper from distribution.
            DEL /F /Q "%WRAPPER_TEMP_ZIP%" >NUL 2>&1
            EXIT /B 1
        )

        DEL /F /Q "%WRAPPER_TEMP_ZIP%" >NUL 2>&1

        MOVE /Y "%WRAPPER_TEMP%" "%WRAPPER_JAR%" >NUL
        IF %ERRORLEVEL% NEQ 0 (
            ECHO ERROR: Unable to move downloaded wrapper into place.
            EXIT /B 1
        )
    )
)

SET "CLASSPATH=%WRAPPER_JAR%"

SET "DEFAULT_JVM_OPTS=-Xmx64m -Xms64m"

IF NOT "%JAVA_HOME%"=="" GOTO findJavaFromJavaHome

SET "JAVA_EXE=java.exe"
%JAVA_EXE% -version >NUL 2>&1
IF %ERRORLEVEL% NEQ 0 GOTO fail
GOTO init

:findJavaFromJavaHome
SET "JAVA_HOME=%JAVA_HOME:"=%"
SET "JAVA_EXE=%JAVA_HOME%\bin\java.exe"

IF EXIST "%JAVA_EXE%" GOTO init

ECHO ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
GOTO fail

:init
SET CMD_LINE_ARGS=
:mainLoop
IF "%1"=="" GOTO execute
SET CMD_LINE_ARGS=%CMD_LINE_ARGS% "%1"
SHIFT
GOTO mainLoop

:execute
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %CMD_LINE_ARGS%
GOTO end

:fail
ECHO.
ECHO ERROR: Gradle could not start because a Java runtime was not found.
ECHO Please set the JAVA_HOME variable in your environment to match the location of your Java installation.

:end
ENDLOCAL
