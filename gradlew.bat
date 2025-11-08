@ECHO OFF
set DEFAULT_JVM_OPTS="-Xmx64m -Xms64m"
set DIR=%~dp0
set APP_HOME=%DIR%
set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar

if NOT "%JAVA_HOME%"=="" goto findJavaFromJavaHome

set JAVACMD=java
goto execute

:findJavaFromJavaHome
set JAVACMD=%JAVA_HOME%\bin\java.exe

:execute
"%JAVACMD%" %DEFAULT_JVM_OPTS% -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
