@echo off
echo Dang thuc hien ket noi cong 3000 giua May tinh va Dien thoai...
adb reverse tcp:3000 tcp:3000
if %ERRORLEVEL% EQU 0 (
    echo [OK] Da ket noi thanh cong! Bay gio ban co the chay App tren may that.
) else (
    echo [LOI] Khong tim thay thiet bi Android. Vui long cam cap USB va bat 'USB Debugging'.
)
pause
