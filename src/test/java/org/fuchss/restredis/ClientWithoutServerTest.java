/* Licensed under MIT 2026. */
package org.fuchss.restredis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import kong.unirest.core.Unirest;
import org.fuchss.restredis.client.Client;
import org.fuchss.restredis.client.ClientConfiguration;
import org.fuchss.restredis.dto.ExistsRequest;
import org.fuchss.restredis.dto.HGetRequest;
import org.fuchss.restredis.dto.HSetRequest;
import org.fuchss.restredis.server.Server;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ClientWithoutServerTest {

    @TempDir
    private Path tempDir;

    @Test
    void clientFailsWhenRedisUnavailableButServerRunning() throws Exception {
        int httpPort = TestUtilities.findFreePort();
        int redisPort = TestUtilities.findFreePort();
        Path configFile = tempDir.resolve("server_config.json");
        TestUtilities.writeServerConfig(configFile, "localhost", redisPort, httpPort);

        Thread serverThread = new Thread(
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

        String baseUrl = "http://localhost:" + httpPort;
        TestUtilities.waitForServerReady(baseUrl);

        try {
            assertEquals(200, Unirest.get(baseUrl + "/").asString().getStatus());
            assertEquals(503, Unirest.get(baseUrl + "/ping").asString().getStatus());
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
            assertThrows(IllegalStateException.class, () -> new Client(new ClientConfiguration(baseUrl, null, null)));
        } finally {
            Unirest.shutDown();
            serverThread.interrupt();
            serverThread.join(5000);
        }
    }
}
