all:
	javac */*.java
	jar cfe cliente.jar client.RunClient */*.class
	jar cfe servidor.jar server.RunServer */*.class

clean:
	rm -rf */*.class
	rm -rf *.class
	rm -rf *.jar

