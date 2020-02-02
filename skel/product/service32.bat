@echo off
rem **************************************************************************
rem Batch file to create a Windows Service for the VOLANO Chat Server.
rem Run the batch file with no parameters for help.
rem
rem After installing and starting the service, see the file "output.log" for
rem startup informational messages and "error.log" for startup error messages.
rem See the Event Viewer Application Log for the JavaService program messages.
rem **************************************************************************
setlocal

rem **************************************************************************
rem Set JVM_DLL to the path of the jvm.dll file on your system.
rem Set MSC_DLL to the corresponding Microsoft C Library used by Java.
rem Set SERVICE to JavaService.exe (32-bit) or JavaService64.exe (64-bit).
rem **************************************************************************

rem 32-bit HotSpot Client VM, 32-bit Microsoft C Library, 32-bit JavaService
set JVM_DLL="C:\Program Files\Java\jre8\bin\client\jvm.dll"
set MSC_DLL="C:\Program Files\Java\jre8\bin\msvcr100.dll"
set SERVICE=JavaService.exe

rem **************************************************************************
rem Set the maximum Java heap and stack sizes.
rem **************************************************************************
set JVM_OPTIONS=-Xmx256m -Xss256k

if "%1" == "install" goto install
if "%1" == "remove" goto remove
goto usage

rem **************************************************************************
rem Install the service.
rem **************************************************************************
:install
set CP=.
for %%f in (lib\*.jar) do call cpappend.bat %%f
set OPTIONS=%JVM_OPTIONS% -Dinstall.root=. -Dcatalina.home=. -Djava.class.path=%CP% -Djava.security.manager -Djava.security.policy=conf/policy.txt

if not exist %JVM_DLL% (
    echo Java Library not found: %JVM_DLL%
) else if not exist %MSC_DLL% (
    echo C Library not found: %MSC_DLL%
) else (
    echo Java Library = %JVM_DLL%
    echo C Library = %MSC_DLL%
    echo JavaService = %SERVICE%
    echo Options = %OPTIONS%
    echo Copying over the C Library ...
    copy %MSC_DLL%
    echo Installing the VOLANO chat service ...
    %SERVICE% -install VolanoChat %JVM_DLL% %OPTIONS% -start COM.volano.Main -out output.log -err error.log -current "%CD%"
)
goto end

rem **************************************************************************
rem Remove the service.
rem **************************************************************************
:remove
echo Removing the VOLANO chat service ...
%SERVICE% -uninstall VolanoChat
goto end

rem **************************************************************************
rem Print usage information.
rem **************************************************************************
:usage
echo Usage: service [option]
echo where option is one of:
echo   install  to install the VOLANO chat service
echo   remove   to remove the VOLANO chat service
goto end

:end
endlocal
