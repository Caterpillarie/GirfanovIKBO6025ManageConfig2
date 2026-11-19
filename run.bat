@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-21"
set "PATH=%JAVA_HOME%\bin;%PATH%"
cd /d "%~dp0."
gradle -q run --args="%*"
pause
exit /b %errorlevel%