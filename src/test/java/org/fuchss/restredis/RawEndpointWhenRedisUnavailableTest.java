/* Licensed under MIT 2026. */
package org.fuchss.restredis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import kong.unirest.core.Unirest;
import org.fuchss.restredis.dto.ExistsRequest;
import org.fuchss.restredis.dto.HGetRequest;
import org.fuchss.restredis.dto.HSetRequest;
import org.fuchss.restredis.server.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Raw endpoint behavior when Redis is unavailable.
 */
class RawEndpointWhenRedisUnavailableTest {

    @TempDir
    private Path tempDir;

    private Thread serverThread;
    private String baseUrl;

    @BeforeEach
    void startServer() throws Exception {
        int httpPort = TestUtilities.findFreePort();
        int redisPort = TestUtilities.findFreePort();
        Path configFile = tempDir.resolve("server_config.json");
        TestUtilities.writeServerConfig(configFile, "localhost", redisPort, httpPort);

        serverThread = new Thread(
                () -> {
                    try {
                        Server.main(new String[] {configFile.toString()});
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                },
                "rest-redis-test-server-no-redis");
        serverThread.setDaemon(true);
        serverThread.start();

        baseUrl = "http://localhost:" + httpPort;
        TestUtilities.waitForServerReady(baseUrl);
    }

    @AfterEach
    void stopServer() throws InterruptedException {
        Unirest.shutDown();
        if (serverThread != null) {
            serverThread.interrupt();
            serverThread.join(5000);
        }
    }

    @Test
    void bridgeEndpointIsAvailable() {
        assertEquals(200, Unirest.get(baseUrl + "/").asString().getStatus());
    }

    @Test
    void pingReportsRedisUnavailable() {
        assertEquals(503, Unirest.get(baseUrl + "/ping").asString().getStatus());
    }

    @Test
    void redisEndpointsReturnBadRequest() {
        assertEquals(
                400,
                Unirest.post(baseUrl + "/exists")
                        .body(new ExistsRequest("missing"))
                        .asString()
                        .getStatus());
        assertEquals(
                400,
                Unirest.post(baseUrl + "/hget")
                        .body(new HGetRequest("missing", "field"))
                        .asString()
                        .getStatus());
        assertEquals(
                400,
                Unirest.post(baseUrl + "/hset")
                        .body(new HSetRequest("missing", "field", "value"))
                        .asString()
                        .getStatus());
    }
}
