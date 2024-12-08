@echo off
call gradlew -b "NmsProvider - 1.7.R4/build.gradle" clean build
call gradlew -b "NmsProvider - 1.8.R3/build.gradle" clean build
call gradlew -b "NmsProvider - 1.12.R1/build.gradle" clean build
call gradlew -b "NmsProvider - 1.14.R1/build.gradle" clean build
call gradlew -b "NmsProvider - 1.16.R3/build.gradle" clean build
call gradlew -b "NmsProvider - 1.17.R1/build.gradle" clean build
call gradlew -b "NmsProvider - 1.18.R1/build.gradle" clean build
call gradlew -b "NmsProvider - 1.18.R2/build.gradle" clean build
call gradlew -b "NmsProvider - 1.19.R1/build.gradle" clean build
call gradlew -b "NmsProvider - 1.19.R2/build.gradle" clean build
call gradlew -b "NmsProvider - 1.19.R3/build.gradle" clean build
call gradlew -b "NmsProvider - 1.20.R1/build.gradle" clean build
call gradlew -b "NmsProvider - 1.20.R2/build.gradle" clean build
call gradlew -b "NmsProvider - 1.20.R3/build.gradle" clean build
call gradlew -b "NmsProvider - 1.20.R4/build.gradle" clean build
call gradlew -b "NmsProvider - 1.21.R1/build.gradle" clean build
call gradlew -b "NmsProvider - 1.20.6/build.gradle" clean build
pause
