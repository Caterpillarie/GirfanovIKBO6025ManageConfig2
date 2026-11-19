@echo off
set "ROOT_DIR=%~dp0..\.."
call "%ROOT_DIR%\run.bat" --package D --repo "%ROOT_DIR%\tests\step4\test_repo.txt" --test true --depth 4 --reverse true
pause