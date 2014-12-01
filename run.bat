@ECHO OFF

REM - This batch file is used to run the binary files in the
REM - bin directory.

REM - Author: Alex Dale
REM - Date: October 20, 2014

CLS

ECHO Running java %1
java -classpath bin; %1