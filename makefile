javac -classpath lib/jade.jar -d classes src/agents/*.java src/constants/*.java
java -cp lib/jade.jar:classes jade.Boot -gui -host localhost -port 7654

