@echo off
set "ROOT_DIR=%~dp0..\.."
call "%ROOT_DIR%\run.bat" --package A --repo "%ROOT_DIR%\tests\step3\test_repo.txt" --test true --depth 2