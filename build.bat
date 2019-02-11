@echo off
cd bin\production
jar -fx ..\..\lib\MyLibrary.jar
cd ..\..
javac -d bin\production -classpath bin\production -sourcepath src -bootclasspath "%JAVA_HOME%\jre\lib\rt.jar" -source 1.6 -target 1.6 src\sk\pa3kc\*.java src\sk\pa3kc\httpconstants\*.java
jar -cfve bin\HTTPServer.jar sk.pa3kc.Program -C bin\production .
