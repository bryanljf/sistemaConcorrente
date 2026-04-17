@echo off
cd /d %~dp0..
java -cp bin loja.LojaMain 2 6002 localhost 5000
