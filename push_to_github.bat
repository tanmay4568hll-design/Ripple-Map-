@echo off
title Push RippleMap to GitHub
echo ========================================================
echo   Pushing RippleMap repository to GitHub...
echo   Remote: https://github.com/tanmay4568hll-design/Ripple-Map-
echo ========================================================
echo.
git push -u origin main
echo.
if %ERRORLEVEL% EQU 0 (
    echo ========================================================
    echo   SUCCESS: Code pushed to GitHub successfully!
    echo   View your repository at:
    echo   https://github.com/tanmay4568hll-design/Ripple-Map-
    echo ========================================================
) else (
    echo ========================================================
    echo   PUSH FAILED: If prompted for password, you need to use
    echo   a GitHub Personal Access Token (PAT) instead of your
    echo   regular password.
    echo.
    echo   Generate token at: https://github.com/settings/tokens
    echo ========================================================
)
echo.
pause
