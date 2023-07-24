# Kotlin image downloader

## Test task that demonstrates concurrent image downloading into database

This project aims to develop an application in Kotlin, using Spring and Docker. The application downloads images from the service [LoremFlickr](https://loremflickr.com) with randomized image sizes, ranging from 10 to 5000. These images are then saved to a PostgreSQL database.

## Database

The PostgreSQL database holds two tables:
- `FILES`: Holds downloaded file information and contents (ID, URL, SIZE, CONTENT_TYPE, CONTENT).
- `SUMMARY`: Materialized view that keeps summary statistics of uploaded files (FILES_COUNT, FILES_SIZE) in async non-blocking manner by background update process. 

## Stack

- Spring
- Docker
- PostgreSQL
- Kotlin
- Coroutines

## Setup & Run

Ensure you have all the requirements installed (Java 17 SDK). Clone this repository, navigate into the cloned directory, then follow these steps:

1. Build the Docker image: `gradle bootBuildImage`
2. Make sure that the image is built `Successfully built image 'docker.io/library/app:0.0.1-SNAPSHOT'`. VPN might yield this result: ` net/http: TLS handshake timeout
   [creator]     ERROR: failed to build: exit status 1` 
3. Run `postgres` via docker-compose service and execute `create database images`, ensure that database has been created, otherwise service won't start
4. Run `migration` via docker-compose service
5. Run `service` container via docker-compose service

The app should now be running and performing tasks according to its programmed behavior.

## Logging

Logs are generated during the application's operation to provide insights into its process. Since there's not much of an info can be given while application is running events of saving file into database are the only ones that are logged.

## License

[MIT](LICENSE)
