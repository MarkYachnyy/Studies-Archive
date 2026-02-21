@echo off

set CD=%~dp0

set JAVA_HOME=C:\Users\managers\.jdks\corretto-1.8.0_342

set JAVA=java
if not "%JAVA_HOME%"=="" (
  set JAVA="%JAVA_HOME%\bin\%JAVA%"
)

set M2_HOME=%USERPROFILE%\.m2
set CP=%CD%\out\production\Task9
set CP=%CP%;%M2_HOME%\repository\com\intellij\forms_rt\7.0.3\forms_rt-7.0.3.jar
set CP=%CP%;%M2_HOME%\repository\asm\asm-commons\3.0\asm-commons-3.0.jar
set CP=%CP%;%M2_HOME%\repository\asm\asm-tree\3.0\asm-tree-3.0.jar
set CP=%CP%;%M2_HOME%\repository\asm\asm\3.0\asm-3.0.jar
set CP=%CP%;%M2_HOME%\repository\com\jgoodies\forms\1.1-preview\forms-1.1-preview.jar
set CP=%CP%;%M2_HOME%\repository\jdom\jdom\1.0\jdom-1.0.jar
set CP=%CP%;%M2_HOME%\repository\commons-cli\commons-cli\1.4\commons-cli-1.4.jar

%JAVA% -classpath "%CP%" ru.cs.vsu.yachnyy_m_a.Main %*