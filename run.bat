@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-21"
set "PATH=%JAVA_HOME%\bin;%PATH%"
call gradle run --quiet --args="%*"
pause
exit /b %errorlevel%