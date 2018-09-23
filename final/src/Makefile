all:
	javac */*.java
	jar cfe client.jar client.RunClient */*.class
	jar cfe server.jar server.RunServer */*.class

clean:
	rm -rf */*.class
	rm -rf *.class
	rm -rf *.jar

