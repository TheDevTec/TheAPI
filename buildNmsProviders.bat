@echo off
call gradlew -b "NmsProvider - 1.8.R3/build.gradle" clean build
call gradlew -b "NmsProvider - 1.12.R1/build.gradle" clean build
call gradlew -b "NmsProvider - 1.16.R3/build.gradle" clean build
call gradlew -b "NmsProvider - 1.17.1/build.gradle" clean build
call gradlew -b "NmsProvider - 1.19.4/build.gradle" clean build
call gradlew -b "NmsProvider - 1.20/build.gradle" clean build
call gradlew -b "NmsProvider - 1.20.2/build.gradle" clean build
call gradlew -b "NmsProvider - 1.20.4/build.gradle" clean build
call gradlew -b "NmsProvider - 1.20.6/build.gradle" clean build
call gradlew -b "NmsProvider - 1.21.5/build.gradle" clean build
call gradlew -b "NmsProvider - 1.21.6/build.gradle" clean build
pause
