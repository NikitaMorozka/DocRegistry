package com.nikitamorozov.docregistry.infrastructure.tools;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

public class DocumentGeneratorApp {

    public static void main(String[] args) throws Exception {
        int n = 1000;
        String baseUrl = "http://localhost:8080";

        if (n <= 0) {
            System.err.println("count must be > 0");
            System.exit(1);
        }

        System.out.printf("Creating %d documents via API at %s%n", n, baseUrl);
        long start = System.currentTimeMillis();

        AtomicInteger created = new AtomicInteger();
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            for (int i = 1; i <= n; i++) {
                final int index = i;
                String json = """
                        {"author":"generator","title":"Generated document %d"}
                        """.formatted(index);
                HttpPost post = new HttpPost(baseUrl + "/api/documents");
                post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
                try {
                    client.execute(post, response -> {
                        int code = response.getCode();
                        if (code >= 200 && code < 300) {
                            int c = created.incrementAndGet();
                            if (c % 100 == 0 || c == n) {
                                System.out.printf("Progress: %d/%d%n", c, n);
                            }
                        } else {
                            System.err.printf("Failed to create document %d: HTTP %d%n", index, code);
                        }
                        return null;
                    });
                } catch (IOException e) {
                    System.err.printf("Error creating document %d: %s%n", index, e.getMessage());
                }
            }
        }

        long elapsed = System.currentTimeMillis() - start;
        System.out.printf("Created %d/%d documents in %d ms%n", created.get(), n, elapsed);
    }
}