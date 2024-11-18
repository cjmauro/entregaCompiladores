@echo off
cd /d "%~dp0compilador\src"
java -jar Programa.jar
pause
cd /d "%~dp0compilador\src"

\masm32\bin\ml /c /Zd /coff hello.asm
if errorlevel 1 (
    echo Error en la compilación de hello.asm
    pause
    exit /b 1
)

\masm32\bin\Link /SUBSYSTEM:CONSOLE hello.obj /LIBPATH:\masm32\lib kernel32.lib masm32.lib
if errorlevel 1 (
    echo Error en el enlace de hello.obj
    pause
    exit /b 1
)
C:\masm32\proyectos\hello.exe
pause