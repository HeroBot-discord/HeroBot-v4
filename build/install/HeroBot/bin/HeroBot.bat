@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  HeroBot startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and HERO_BOT_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\HeroBot-1.0-SNAPSHOT.jar;%APP_HOME%\lib\JDA-4.1.1_148.jar;%APP_HOME%\lib\reflections-0.9.12.jar;%APP_HOME%\lib\bucket4j-core-4.10.0.jar;%APP_HOME%\lib\redisson-3.12.5.jar;%APP_HOME%\lib\jackson-dataformat-yaml-2.10.3.jar;%APP_HOME%\lib\snakeyaml-1.26.jar;%APP_HOME%\lib\gson-2.8.6.jar;%APP_HOME%\lib\slf4j-simple-1.7.30.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\annotations-16.0.1.jar;%APP_HOME%\lib\slf4j-api-1.7.30.jar;%APP_HOME%\lib\nv-websocket-client-2.9.jar;%APP_HOME%\lib\okhttp-3.13.0.jar;%APP_HOME%\lib\opus-java-1.0.4.pom;%APP_HOME%\lib\commons-collections4-4.1.jar;%APP_HOME%\lib\trove4j-3.0.3.jar;%APP_HOME%\lib\jackson-databind-2.10.3.jar;%APP_HOME%\lib\fst-2.57.jar;%APP_HOME%\lib\javassist-3.26.0-GA.jar;%APP_HOME%\lib\netty-resolver-dns-4.1.48.Final.jar;%APP_HOME%\lib\netty-handler-4.1.48.Final.jar;%APP_HOME%\lib\netty-codec-dns-4.1.48.Final.jar;%APP_HOME%\lib\netty-codec-4.1.48.Final.jar;%APP_HOME%\lib\netty-transport-4.1.48.Final.jar;%APP_HOME%\lib\netty-buffer-4.1.48.Final.jar;%APP_HOME%\lib\netty-resolver-4.1.48.Final.jar;%APP_HOME%\lib\netty-common-4.1.48.Final.jar;%APP_HOME%\lib\cache-api-1.0.0.jar;%APP_HOME%\lib\reactor-core-3.3.4.RELEASE.jar;%APP_HOME%\lib\rxjava-2.2.19.jar;%APP_HOME%\lib\jackson-core-2.10.3.jar;%APP_HOME%\lib\byte-buddy-1.10.7.jar;%APP_HOME%\lib\jodd-bean-5.0.13.jar;%APP_HOME%\lib\okio-1.17.2.jar;%APP_HOME%\lib\opus-java-api-1.0.4.jar;%APP_HOME%\lib\opus-java-natives-1.0.4.jar;%APP_HOME%\lib\jackson-annotations-2.10.3.jar;%APP_HOME%\lib\reactive-streams-1.0.3.jar;%APP_HOME%\lib\objenesis-2.5.1.jar;%APP_HOME%\lib\jodd-core-5.0.13.jar;%APP_HOME%\lib\jna-4.4.0.jar

@rem Execute HeroBot
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %HERO_BOT_OPTS%  -classpath "%CLASSPATH%" fr.matthieu.herobot.Main %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable HERO_BOT_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%HERO_BOT_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
