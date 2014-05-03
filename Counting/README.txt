Contact: chuanlihao@yahoo.com
Date: 2014-05-03

For this project, it's a Java project created by Eclipse.

About the file format: 
  All the files are edited in UNIX, only '\n' is appended at the end of each line.
  So in Windows system, you may choose an appropriate editer except Notepad, in
  order to view the code.

Libraries used here:
  1. JavaSE-1.6
  2. JUnit 4
  3. Mockito-1.9.5 https://code.google.com/p/mockito/downloads/detail?name=mockito-all-1.9.5.jar

How to run the project:
  1. To run the counting simulation, just run the project as a Java Application, and
     select class CounterSimulator as the driver class.
  2. To run unit tests, run the project as JUnit Test.  You need to configure the
     Mockito library correctly, refer above section for more info on it.  Otherwise
     you can only run part of the test cases.

How to view the code:
  You may view the code as the following order:
  Converter --> BaseConverter --> BaseConcreteConverter --> LiteralCheckingConverter
    --> MulripleConverter --> CompositeMulripleConverter --> CountingSimulator

For any other questions, please contact chuanlihao@yahoo.com directly.
