# file-processor

![Screen 1](doc/medidoc.png?raw=true "Diagram")

A spring boot applications that reads files from an MQTT topic and saves them to 
an SQL database after processing. 

The application will also serve the processed files
over MQTT as a response to command objects. These operations are done through specific topics.

Built using Gradle, JDK 21 and Spring Boot.

## How to run

Use docker-compose.yaml to run the application.

If making changes to the source, Spring Boot will deploy the dependent applications
using spring-compose.yaml.

This application is part of the larger Medidoc project. The Medidoc project is an assignment
completed as part of studies in the Computer Science Master of Science program at IUBH.

Ivan Šarić - 2023
