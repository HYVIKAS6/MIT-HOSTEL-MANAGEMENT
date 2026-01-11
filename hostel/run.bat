@echo off
echo Compiling...
dir src\*.java /s /b > sources.txt
javac @sources.txt
del sources.txt
echo Running...
java -cp src Main