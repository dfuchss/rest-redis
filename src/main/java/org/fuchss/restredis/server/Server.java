/* Licensed under MIT 2026. */
package org.fuchss.restredis.server;

import static spark.Spark.awaitInitialization;
import static spark.Spark.get;
import static spark.Spark.initExceptionHandler;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.stop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import org.fuchss.restredis.dto.ExistsRequest;
import org.fuchss.restredis.dto.HGetRequest;
import org.fuchss.restredis.dto.HSetRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.RedisClient;
import spark.Request;
import spark.Response;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) throws InterruptedException {
        File configFile = args.length != 1 ? new File("server_config.json") : new File(args[0]);
        ServerConfiguration configuration = ServerConfiguration.loadFromFile(configFile);

        port(configuration.httpPort());

        initExceptionHandler(e -> logger.error(e.getMessage(), e));

        RedisClient redisClient = null;
        try {
            redisClient =
                    RedisClient.create("redis://%s:%d".formatted(configuration.redisHost(), configuration.redisPort()));
            initializeRoutes(redisClient);
            awaitInitialization();
            logger.info(
                    "REST-Redis server started on port {}, connected to Redis at {}:{}",
                    configuration.httpPort(),
                    configuration.redisHost(),
                    configuration.redisPort());
            // Wait for JVM shutdown and block thread
            Thread.currentThread().join();
        } finally {
            if (redisClient != null) {
                redisClient.close();
            }
            stop();
        }
    }

    private static void initializeRoutes(RedisClient redisClient) {
        get("/ping", (req, res) -> handlePing(res, redisClient));
        post("/exists", (req, res) -> handleExistsRequest(req, res, redisClient));
        post("/hget", (req, res) -> handleHget(req, res, redisClient));
        post("/hset", (req, res) -> handleHset(req, res, redisClient));
    }

    private static String handlePing(Response res, RedisClient redisClient) {
        try {
            redisClient.ping();
            return "PONG";
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.status(503);
            return "Redis server not reachable";
        }
    }

    private static String handleExistsRequest(Request req, Response res, RedisClient redisClient) {
        try {
            ExistsRequest existsRequest = MAPPER.readValue(req.body(), ExistsRequest.class);
            return String.valueOf(redisClient.exists(existsRequest.key()));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.status(400);
            return "Bad Request";
        }
    }

    private static String handleHget(Request req, Response res, RedisClient redisClient) {
        try {
            HGetRequest hGetRequest = MAPPER.readValue(req.body(), HGetRequest.class);
            return redisClient.hget(hGetRequest.key(), hGetRequest.field());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.status(400);
            return "Bad Request";
        }
    }

    private static long handleHset(Request req, Response res, RedisClient redisClient) throws JsonProcessingException {
        try {
            HSetRequest hSetRequest = MAPPER.readValue(req.body(), HSetRequest.class);
            return redisClient.hset(hSetRequest.key(), hSetRequest.field(), hSetRequest.value());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.status(400);
            return -1;
        }
    }
}
