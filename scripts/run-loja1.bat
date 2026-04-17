@echo off
cd /d %~dp0..
java -cp bin loja.LojaMain 1 6001 localhost 5000
