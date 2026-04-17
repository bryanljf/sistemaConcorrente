@echo off
cd /d %~dp0..
if not exist bin mkdir bin
javac -d bin -sourcepath src src\fabrica\*.java src\loja\*.java src\cliente\*.java
if %errorlevel%==0 (
    echo Compilacao concluida com sucesso.
) else (
    echo Falha na compilacao.
)
