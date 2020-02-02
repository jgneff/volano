@echo off
rem **************************************************************************
rem Sample Windows batch file for starting the VOLANO Chat Server.
rem Leave the Command Prompt window open after running this batch file.
rem Press Ctrl-C in the Command Prompt window to stop the server.
rem **************************************************************************
setlocal

rem **************************************************************************
rem Set JAVA to the path of the java.exe program on your system.
rem Java 8 Update 5 Server JRE installed in "C:\jdk1.8.0_05" requires:
rem     set JAVA="C:\jdk1.8.0_05\bin\java.exe"
rem **************************************************************************
set JAVA="C:\Windows\System32\java.exe"
set OPTIONS=-Xmx256m -Xss256k

set CP=.
for %%f in (lib\*.jar) do call cpappend.bat %%f
if not exist %JAVA% (
    echo Unable to find Java executable %JAVA%
) else (
    %JAVA% %OPTIONS% -version
    %JAVA% %OPTIONS% -Dinstall.root=. -Dcatalina.home=. -Djava.security.manager -Djava.security.policy=conf/policy.txt -cp %CP% COM.volano.Main %1
)
endlocal
