@echo off
set port=8080
set jarName=tiros_manage.jar
set opration=%1
set pid=<nul

if "%opration%" == "start" GOTO :fun_start

if "%opration%" == "stop" GOTO :fun_stop

if "%opration%" == "restart" GOTO :fun_restart

if "%opration%" == "status" GOTO :fun_status

echo 无效命令
GOTO :end

:fun_start
for /f "tokens=1-5" %%i in ('netstat -ano^|findstr ":%port%"') do ( set pid=%%m )
if DEFINED pid GOTO :isexist
start tiros_manage -jar -Dfile.encoding=utf-8 -Dloader.path=resources,lib %jarName% --server.port=%port%
echo %jarName% 开始运行...
GOTO :end

:fun_stop
echo %jarName% 开始停止...
for /f "tokens=1-5" %%i in ('netstat -ano^|findstr ":%port%"') do ( set pid=%%m )
if DEFINED pid taskkill /pid %pid% /f
echo %jarName% 已停止
GOTO :end

:fun_restart
echo %jarName% 开始重启...
for /f "tokens=1-5" %%i in ('netstat -ano^|findstr ":%port%"') do ( set pid=%%m )
if DEFINED pid taskkill /pid %pid% /f
start tiros_manage -jar -Dfile.encoding=utf-8 -Dloader.path=resources,lib %jarName% --server.port=%port%
echo %jarName% 完成重启...
GOTO :end

:fun_status
for /f "tokens=1-5" %%i in ('netstat -ano^|findstr ":%port%"') do ( set pid=%%m )
if DEFINED pid echo %jarName% 正在运行...
if not DEFINED pid echo %jarName% 没有运行...
GOTO :end

:isexist
echo %jarName% 已经在运行中，不能重复启动!!!

:end
exit /b