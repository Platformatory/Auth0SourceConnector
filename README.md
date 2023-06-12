# Auth0-Kafka Source Connector

The Auth0-Kafka Source Connector is a simple and effective solution to transport your user data from Auth0 to Kafka. This connector reads data from Auth0 and sends it to a specified Kafka topic, allowing you to process and analyze your data in a much more flexible and scalable manner. 

## Schema

The following is an example of the user data schema that the connector uses:

```json
[
  {
    "user_id": "auth0|507f1f77bcf86cd799439020",
    "email": "john.doe@gmail.com",
    "email_verified": false,
    "username": "johndoe",
    "phone_number": "+199999999999999",
    "phone_verified": false,
    "created_at": "",
    "updated_at": "",
    "identities": [
      {
        "connection": "Initial-Connection",
        "user_id": "507f1f77bcf86cd799439020",
        "provider": "auth0",
        "isSocial": false
      }
    ],
    "app_metadata": {},
    "user_metadata": {},
    "picture": "",
    "name": "",
    "nickname": "",
    "multifactor": [
      ""
    ],
    "last_ip": "",
    "last_login": "",
    "logins_count": 0,
    "blocked": false,
    "given_name": "",
    "family_name": ""
  }
]
```

## Installation

The latest release of the Auth0-Kafka Source Connector can be found on the [project's GitHub page](https://github.com/your-github-username/project-name/releases). Here you'll find a JAR file which contains the necessary classes and dependencies for the connector.

### Docker Usage

For containerized environments, you can use Docker to build an image and run the connector.

#### Dockerfile

Create a Dockerfile in your project root:

```Dockerfile
# Use the Kafka Connect base image
FROM confluentinc/cp-kafka-connect:latest

# Set the working directory
WORKDIR /usr/share/java/kafka-connect-jars/

# Download and add the JAR file from GitHub into the Docker image
RUN wget https://github.com/itsviveksinghania/Auth0SourceConnector/releases/download/v1.0.3/my-project.jar
```

This Dockerfile uses the latest Kafka Connect image provided by Confluent and copies the connector jar into the appropriate directory.


### To start the connector, simply run:

```bash
curl -X POST -H "Content-Type: application/json" --data '{
    "name": "auth0-source-connector",
    "config": {
        "connector.class": "com.platformatory.source.connector.MySourceConnector",
        "tasks.max": "1",
        "topic": "test1",
        "api.endpoint": "users",
        "domain": "dev-82fa48v2xh17ro8r.us.auth0.com",
        "client.id": "R0dgVe210qJEjR9kNPGIzUAnI3iuv2Yx",
        "client.secret": "7CKl2vjXio-_atfjR5glSbMSfEaWp3N4ghSdx2cr6bFnJpYW5pBx7icg6xuTMrGJ"
    }
}' http://localhost:8083/connectors
```
