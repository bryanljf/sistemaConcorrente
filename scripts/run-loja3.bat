@echo off
cd /d %~dp0..
java -cp bin loja.LojaMain 3 6003 localhost 5000
