@echo off
javac -d bin\production -classpath bin\production -sourcepath src -bootclasspath "%JAVA_HOME%\jre\lib\rt.jar" -source 1.6 -target 1.6 src\sk\pa3kc\*.java
cd bin\production
jar -fx ..\..\lib\MyLibrary.jar
cd ..
jar -cfve HTTPServer.jar sk.pa3kc.Program -C production .
cd ..
