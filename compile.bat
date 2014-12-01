@ECHO OFF

REM - This is used to compile all the source code found in
REM - the src directory.  The compiled files are created
REM - in the bin directory.

REM - Author: Alex Dale
REM - Date: October 20, 2014

CLS

ECHO Deleting old bin\*.class files
del bin\*.class

ECHO Comiling all src\*.java
javac -sourcepath src -classpath bin; src\*.java -d bin