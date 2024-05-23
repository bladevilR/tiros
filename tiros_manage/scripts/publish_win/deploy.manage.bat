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
set copyLib=jeecg-boot*.jar
set managePath=%rootpath%\jeecg-boot-module-system\target\tiros_manage
set toManagePath=%server%\tiros_manage
set manageJar=tiros_manage.jar


echo "更新源码..."
cd /d %rootpath%
svn update

echo "打包编译..."
call mvn clean package -Dmaven.test.skip=true

echo "开始发布tiros_manage..."
cd /d %managePath%

echo "上传文件到服务器..."
net use %server% "%password%" /user:"%username%"
xcopy %managePath%\%manageJar% %toManagePath% /y
xcopy %managePath%\app.bat %toManagePath% /y
xcopy %managePath%\lib\%copyLib% %toManagePath%\lib /y

echo "重启服务"
sc %toManagePath%\app.bat restart
echo "tiros_manage 发布完成"

echo 运行完成，按任意键结束
pause
exist