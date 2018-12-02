@echo off

if not "%2" == "" goto parameters_ok

echo Usage: %0 memory_snapshot_file target_hprof_file
pause
exit

:parameters_ok

set EXE=%~dp0\..\jre\bin\java.exe

if "%ProgramFiles(x86)%" == "" goto start_command

set EXE=%~dp0\..\jre64\bin\java.exe

:start_command

"%EXE%" -cp "%~dp0\..\lib\yjp.jar" com.yourkit.Main -snapshot2hprof %1 %2
pause
