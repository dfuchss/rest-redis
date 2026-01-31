# REST-Redis

Expose basic Redis hash operations over HTTP with a lightweight Spark-based server and a Java client.

## Features

- HTTP bridge for Redis using Spark Java
- Java client with simple methods for `/`, `/ping`, `/exists`, `/hget`, `/hset`
- Integration tests with test containers

## Endpoints

- `GET /` — bridge availability
- `GET /ping` — Redis ping
- `POST /exists` — check if a key exists
- `POST /hget` — read a hash field
- `POST /hset` — write a hash field

## Configuration

Server configuration file example (`server_config.json`):

- `redis_host`: Redis host
- `redis_port`: Redis port
- `http_port`: HTTP port for the REST server

## Running the server

Run the `Server` main class with an optional config path argument (defaults to `server_config.json`).

## Using the client

Create a `Client` with a `ClientConfiguration`, then call `isBridgeAvailable()`, `ping()`, `exists()`, `hget()`, or `hset()`.

## Tests

Tests use test containers to start Redis. Ensure Docker is available before running the test suite.

## Docker

This project includes a Docker setup for easy deployment and testing. To build the Docker image, run the following command:

```bash
docker build -t ghcr.io/dfuchss/rest-redis .
```

To run the Docker container, use:

```bash
docker run -p 8080:8080 -v $(pwd)/server_config.json:/app/server_config.json ghcr.io/dfuchss/rest-redis
```

Make sure to adjust the port mapping and configuration file path as necessary for your environment.

### Configuration

The server is configured using a JSON configuration file. By default, the server looks for `server_config.json` in the working directory. You can also specify a custom config file path as a command-line argument.

Example `server_config.json`:

```json
{
  "redis_host": "redis",
  "redis_port": 6379,
  "http_port": 8080
}
```

See [ServerConfiguration.java](src/main/java/org/fuchss/restredis/server/ServerConfiguration.java) for details on configuration options.

### Docker Compose

You can also use Docker Compose to manage your application. Below is a sample `docker-compose.yml` file:

```yaml
services:
  rest-redis:
    image: ghcr.io/dfuchss/rest-redis
    ports:
      - "8080:8080"
    volumes:
      - ./server_config.json:/app/server_config.json:ro

  redis:
    image: redis:latest
    expose:
      - "6379"
    volumes:
      - ./redis-data:/data
```

This configuration sets up both the REST API and a Redis instance. The server configuration is mounted as a volume to allow easy customization.

## Payloads

The following payloads are supported by the REST API:

- **ExistsRequest**: Checks if a key exists in the Redis database. See [ExistsRequest.java](src/main/java/org/fuchss/restredis/dto/ExistsRequest.java).
- **HGetRequest**: Retrieves the value associated with a key from a hash. See [HGetRequest.java](src/main/java/org/fuchss/restredis/dto/HGetRequest.java).
- **HSetRequest**: Sets a value for a key in a hash. See [HSetRequest.java](src/main/java/org/fuchss/restredis/dto/HSetRequest.java).

