/* Licensed under MIT 2026. */
package org.fuchss.restredis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import org.fuchss.restredis.client.Client;
import org.fuchss.restredis.client.ClientConfiguration;
import org.fuchss.restredis.server.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Integration tests that verify client-server-Redis interaction using Testcontainers.
 */
@Testcontainers
class ServerClientIntegrationTest {

    @SuppressWarnings("resource")
    @Container
    private static final GenericContainer<?> REDIS =
            new GenericContainer<>(DockerImageName.parse("redis:latest")).withExposedPorts(6379);

    @TempDir
    private static Path tempDir;

    private static Thread serverThread;
    private static Client client;
    private static String baseUrl;

    @BeforeAll
    static void startServer() throws Exception {
        int httpPort = TestUtilities.findFreePort();
        Path configFile = tempDir.resolve("server_config.json");
        TestUtilities.writeServerConfig(configFile, REDIS.getHost(), REDIS.getMappedPort(6379), httpPort);

        serverThread = new Thread(
                () -> {
                    try {
                        Server.main(new String[] {configFile.toString()});
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                },
                "rest-redis-test-server");
        serverThread.setDaemon(true);
        serverThread.start();

        baseUrl = "http://localhost:" + httpPort;
        TestUtilities.waitForServerReady(baseUrl);

        client = new Client(new ClientConfiguration(baseUrl, null, null));
    }

    @AfterAll
    static void stopServer() throws InterruptedException {
        if (client != null) {
            client.close();
        }
        if (serverThread != null) {
            serverThread.interrupt();
            serverThread.join(5000);
        }
    }

    @Test
    void pingWorks() {
        assertTrue(client.ping());
    }

    @Test
    void isBridgeAvailable() {
        assertTrue(client.isBridgeAvailable());
    }

    @Test
    void canCheckExistsAndFetch() {
        String key = TestUtilities.uniqueKey("exists");
        String field = "field";
        String value = "value";

        assertFalse(client.exists(key));
        long hsetResult = client.hset(key, field, value);
        assertTrue(hsetResult >= 0);
        assertTrue(client.exists(key));
        assertEquals(value, client.hget(key, field));
    }

    @Test
    void canUpdateExistingField() {
        String key = TestUtilities.uniqueKey("update");
        String field = "field";
        String value1 = "value-1";
        String value2 = "value-2";

        long created = client.hset(key, field, value1);
        long updated = client.hset(key, field, value2);

        assertTrue(created >= 0);
        assertTrue(updated >= 0);
        assertEquals(value2, client.hget(key, field));
    }
}
