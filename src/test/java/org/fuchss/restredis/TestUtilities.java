/* Licensed under MIT 2026. */
package org.fuchss.restredis;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.util.UUID;
import kong.unirest.core.Unirest;
import org.fuchss.restredis.server.ServerConfiguration;

/**
 * Shared helpers for integration tests.
 */
final class TestUtilities {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private TestUtilities() {}

    static int findFreePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    static void waitForServerReady(String baseUrl) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 30000;
        while (System.currentTimeMillis() < deadline) {
            if (isServerResponding(baseUrl)) {
                return;
            }
            Thread.sleep(150);
        }
        throw new IllegalStateException("REST-Redis server did not become ready in time");
    }

    static String uniqueKey(String prefix) {
        return prefix + ":" + UUID.randomUUID();
    }

    static void writeServerConfig(Path configFile, String redisHost, int redisPort, int httpPort) throws IOException {
        MAPPER.writeValue(configFile.toFile(), new ServerConfiguration(redisHost, redisPort, httpPort));
    }

    private static boolean isServerResponding(String baseUrl) {
        try {
            var response = Unirest.get(baseUrl + "/").asString();
            return response.getStatus() >= 200 && response.getStatus() < 600;
        } catch (Exception e) {
            return false;
        }
    }
}
