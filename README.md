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
FROM confluentinc/cp-kafka-connect:latest

COPY ./path-to-your-jar/auth0-kafka-connector.jar /usr/share/java/kafka-connect-jdbc/

ENV CLASSPATH=/usr/share/java/kafka-connect-jdbc/* 
```

This Dockerfile uses the latest Kafka Connect image provided by Confluent and copies the connector jar into the appropriate directory.

### Docker Compose

You can use Docker Compose to easily manage your services. Create a `docker-compose.yml` file:

```yaml
version: '3'
services:
  kafka-connect:
    build: .
    ports:
      - 8083:8083
```
This will build the Docker image and start a container with the Kafka Connect service exposed on port 8083.

To start the connector, simply run:

```bash
docker-compose up -d

