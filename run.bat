@echo off
chcp 65001 >nul
if not exist "%JAVA_HOME%\bin\javac.exe" set "JAVA_HOME=C:\Program Files\JetBrains\PyCharm Community Edition 2024.2.3\jbr"
call "%~dp0gradlew.bat" run
