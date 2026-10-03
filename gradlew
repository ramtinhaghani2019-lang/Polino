#!/bin/sh
set -e
APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
if [ ! -f "$JAR" ]; then
  echo "Gradle wrapper JAR is missing; downloading official Gradle 9.5.0 wrapper..."
  URL="https://raw.githubusercontent.com/gradle/gradle/v9.5.0/gradle/wrapper/gradle-wrapper.jar"
  if command -v curl >/dev/null 2>&1; then
    curl -fL "$URL" -o "$JAR"
  elif command -v wget >/dev/null 2>&1; then
    wget -O "$JAR" "$URL"
  else
    echo "Install curl/wget or open the project in Android Studio and regenerate the Gradle wrapper." >&2
    exit 1
  fi
fi
exec java -classpath "$JAR" org.gradle.wrapper.GradleWrapperMain "$@"
