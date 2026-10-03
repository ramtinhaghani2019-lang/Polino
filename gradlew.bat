@echo off
setlocal
set APP_HOME=%~dp0
set JAR=%APP_HOME%gradle\wrapper\gradle-wrapper.jar
if not exist "%JAR%" (
  echo Gradle wrapper JAR is missing; downloading official Gradle 9.5.0 wrapper...
  powershell -NoProfile -ExecutionPolicy Bypass -Command "$u='https://raw.githubusercontent.com/gradle/gradle/v9.5.0/gradle/wrapper/gradle-wrapper.jar'; Invoke-WebRequest -Uri $u -OutFile '%JAR%'"
  if errorlevel 1 exit /b 1
)
java -classpath "%JAR%" org.gradle.wrapper.GradleWrapperMain %*
endlocal
