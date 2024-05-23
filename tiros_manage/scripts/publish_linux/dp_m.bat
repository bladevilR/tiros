@echo off
set curpath=%cd%
rem 打印的中文不乱码，默认是gbk，改为utf8
chcp 65001
rem 服务器IP
set ip=%1
rem 服务器密码
set password=Root@123
if "%ip%" == "" (
 echo "请输入参数服务器IP"
 pause
 exit
) else (
 echo "开始发布Manage到：%ip% ..."
)

set fromRoot=E:\DEV_CENTER\nannar\5I023_TIROS\sourcecode\1.trunk\tiros_manage
set appPath=%fromRoot%\jeecg-boot-module-system
set appName=tiros_manage
set copyLib=jeecg-boot*.jar

set toRoot=/usr/local/tiros
set toAppPath=%toRoot%/%appName%

set hour_ten=%time:~0,1%
if "%hour_ten%" == " " (
    set bakName=tiros_manage_%date:~0,4%%date:~5,2%%date:~8,2%0%time:~1,1%%time:~3,2%.zip
) else (
    set bakName=tiros_manage_%date:~0,4%%date:~5,2%%date:~8,2%%time:~0,2%%time:~3,2%.zip
)
rem set bakName=tiros_manage_%date:~0,4%%date:~5,2%%date:~8,2%%time:~0,2%%time:~3,2%%time:~6,2%.zip

echo "更新源码..."
cd /d %fromRoot%
svn update

echo "打包编译..."
call mvn clean package -U -Dmaven.test.skip=true

cd /d %appPath%

if not exist .\target\publish (
  md .\target\publish
) else (
  rmdir .\target\publish
  md .\target\publish
)

md .\target\publish\lib

echo "拷贝要发布的文件..."
copy .\target\%appName%\%appName%.jar .\target\publish
copy .\target\%appName%\lib\%copyLib% .\target\publish\lib
copy .\target\%appName%\*.sh .\target\publish
rem xcopy .\target\resources .\target\publish\

echo "开始备份文件..."
%curpath%\putty\plink -v -batch -pw %password% root@%ip% "cd %toRoot%;source /etc/profile;zip -r %bakName% %appName%"
echo "备份程序完成"

echo "开始上传文件..."
%curpath%\putty\pscp -P 22 -pw %password% -r .\target\publish\ root@%ip%:%toAppPath%
echo "上传完成"
echo "开始启动(重启)程序..."
%curpath%\putty\plink -v -batch -pw %password% root@%ip% "cd %toAppPath%;source /etc/profile;./app.sh restart"
echo "发布完成，按任意键结束"
cd /d %curpath%
pause
exist