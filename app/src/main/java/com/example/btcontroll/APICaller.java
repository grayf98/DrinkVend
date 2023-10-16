package com.example.btcontroll;

import okhttp3.*;
import org.json.JSONObject;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class APICaller {

    private static final String CUSTOMER_ID = "<INSERT CUSTOMER ID HERE>";
    private static final String API_KEY = "<INSERT API KEY HERE>";
    private static final String BASE_URL = "<BASE URL HERE>";
    private static final String ENDPOINT = "/api/v1/start";

    public static void main(String[] args) throws Exception {
        JSONObject publicData = new JSONObject();
        JSONObject privateData = new JSONObject()
                .put("signals", new String[]{"idcheck"})
                .put("document_type", "na_dl");

        JSONObject payload = new JSONObject()
                .put("public_data", publicData)
                .put("private_data", privateData);

        String base64Payload = Base64.getEncoder().encodeToString(payload.toString().getBytes(StandardCharsets.UTF_8));
        String apiSignature = "sha256=" + hmacSha256(API_KEY, base64Payload);

        RequestBody body = RequestBody.create(base64Payload, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(BASE_URL + ENDPOINT)
                .addHeader("Content-type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("customer-id", CUSTOMER_ID)
                .addHeader("signature", apiSignature)
                .post(body)
                .build();

        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String headerSignature = response.header("signature");
                String signature = "sha256=" + hmacSha256(API_KEY, response.body().string());

                if (headerSignature != null && headerSignature.equals(signature)) {
                    System.out.println("Valid signature");
                } else {
                    System.out.println("Invalid return signature");
                    return;
                }

                String decodedJson = new String(Base64.getDecoder().decode(response.body().string()));
                System.out.println("Response: " + decodedJson);
            } else {
                throw new RuntimeException("Request failed: " + response.message());
            }
        }
    }

    private static String hmacSha256(String secretKey, String data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] digest = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();

        for (byte b : digest) {
            hexString.append(String.format("%02x", b));
        }

        return hexString.toString();
    }
}
