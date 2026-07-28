#!/usr/bin/env sh
# Gradle wrapper shell script
if [ -z "$JAVA_HOME" ]; then
  echo "JAVA_HOME is not set"
  exec gradle "$@"
else
  exec "$JAVA_HOME/bin/java" -jar "$(dirname "$0")/gradle/wrapper/gradle-wrapper.jar" "$@"
fi
