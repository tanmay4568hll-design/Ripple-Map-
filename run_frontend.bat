@echo off
title RippleMap - Starting Frontend Server
echo ===================================================
echo   RippleMap Dependency Radar - Launching Web App
echo ===================================================
echo Opening in default web browser...
start http://127.0.0.1:5173
echo Starting local web server on port 5173...
python -m http.server 5173 --bind 127.0.0.1
if %ERRORLEVEL% NEQ 0 (
    echo Python server failed. Opening index.html directly...
    start index.html
)
pause
