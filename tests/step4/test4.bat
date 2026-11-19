@echo off
set "ROOT_DIR=%~dp0..\.."
call "%ROOT_DIR%\run.bat" --package libcurl --repo "https://dl-cdn.alpinelinux.org/alpine/latest-stable/main/x86_64/" --depth 2 --reverse true
pause
