package me.misha2win.scracesmpplugin.resourcepack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.bukkit.Bukkit;

import com.sun.net.httpserver.HttpServer;

public class ResourcePackServer {

    private HttpServer server;
    private byte[] resourcePack;
    private byte[] resourcePackSha1;

    public void start(int port) throws IOException {
        try (InputStream in = getClass().getResourceAsStream("/resourcepack.zip")) {
            if (in == null) {
                throw new IOException("resourcepack.zip not found");
            }

            resourcePack = in.readAllBytes();

            MessageDigest digest;
            try {
                digest = MessageDigest.getInstance("SHA-1");
                resourcePackSha1 = digest.digest(resourcePack);
            } catch (NoSuchAlgorithmException e) {
                Bukkit.getLogger().severe("SHA-1 algorithm not found");
                e.printStackTrace();
            }
        }

        server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/resourcepack.zip", exchange -> {
            if (!exchange.getRequestMethod().equals("GET")) {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
                return;
            }

            exchange.getResponseHeaders().set("Content-Type", "application/zip");
            exchange.sendResponseHeaders(200, resourcePack.length);

            try (OutputStream out = exchange.getResponseBody()) {
                out.write(resourcePack);
            }
        });

        server.start();

        Bukkit.getLogger().info("[ScarceLife] Started ResourcePackServer on port " + port);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    public byte[] getResourcePackSha1() {
        return resourcePackSha1;
    }

}
