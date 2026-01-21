# REST-Redis

Expose basic Redis hash operations over HTTP with a lightweight Spark-based server and a Java client.

## Features

- HTTP bridge for Redis using Spark Java
- Java client with simple methods for `/`, `/ping`, `/exists`, `/hget`, `/hset`
- JSON configuration for server and client
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

Client configuration file example (`client_config.json`):

- `rest_uri`: base URL for the REST server
- `username`: optional basic auth username
- `password`: optional basic auth password

## Running the server

Run the `Server` main class with an optional config path argument (defaults to `server_config.json`).

## Using the client

Create a `Client` with a `ClientConfiguration`, then call `isBridgeAvailable()`, `ping()`, `exists()`, `hget()`, or `hset()`.

## Tests

Tests use test containers to start Redis. Ensure Docker is available before running the test suite.
