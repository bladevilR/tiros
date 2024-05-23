@echo off
rem tiros_manage 根路径，必须设置
set rootpath=E:\DEV_CENTER\nannar\5I023_TIROS\sourcecode\1.trunk\tiros_manage
rem 打印的中文不乱码，默认是gbk，改为utf8
chcp 65001
rem 服务器IP
set server=\\192.168.1.10\tiros
rem 服务器密码
set username=administrator
set password=nannar@2021
set fromPath=%rootpath%\jeecg-boot-module-system\target\tiros_manage
set toPath=%server%\tiros_manage
set appName=tiros_manage.jar
set copyLib=jeecg-boot*.jar

echo "更新源码..."
cd /d %rootpath%
svn update

echo "打包编译..."
call mvn clean package -Dmaven.test.skip=true

cd /d %fromPath%

echo "上传文件到服务器..."
net use %server% "%password%" /user:"%username%"
xcopy %fromPath%\%appName% %toPath% /y
xcopy %fromPath%\app.bat %toPath% /y
xcopy %fromPath%\lib\%copyLib% %toPath%\lib /y

echo "重启服务"
sc %toPath%\app.bat restart
echo 运行完成，按任意键结束
pause
exist