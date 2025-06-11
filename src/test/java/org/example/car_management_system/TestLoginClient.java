package org.example.car_management_system;


import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TestLoginClient {

    public static void main(String[] args) throws Exception {
        String apiUrl = "http://localhost:8080/api/v1/auth/login"; // Đảm bảo server đang chạy
        String jsonRequest = """
                {
                    "username": "leminhphuc2",
                    "password": "12345"
                }
                """;

        for (int i = 1; i <= 100; i++) {
            sendRequest(apiUrl, jsonRequest, i);
            Thread.sleep(150);
        }
    }

    private static void sendRequest(String apiUrl, String json, int index) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            System.out.println("[" + index + "] Response Code: " + responseCode);
        } catch (Exception e) {
            System.err.println("[" + index + "] Request failed: " + e.getMessage());
        }
    }
}
