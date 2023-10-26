//package com.example.btcontroll;
//
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
//public class Intellicheck {
//    public static void start(String[] args) throws Exception {
//        OkHttpClient client = new OkHttpClient();
//
//        MediaType mediaType = MediaType.parse("application/json");
//        RequestBody body = RequestBody.create(mediaType, "{\"private_data\":{\"document_type\":\"ID\",\"ttl\":\"2\",\"signals\":[\"< signals array of strings array of signals to run for this transaction that is verified against subscribed list>\"]}}");
//        Request request = new Request.Builder()
//                .url("https://example.com/api/v1/start")
//                .post(body)
//                .addHeader("accept", "application/json")
//                .addHeader("signature", "<calculated_signature>")
//                .addHeader("customer-id", "<customer_id>")
//                .addHeader("content-type", "application/json")
//                .build();
//
//        Response response = client.newCall(request).execute();
//    }
//
//    public static void front(String[] args) throws Exception {
//        OkHttpClient client = new OkHttpClient();
//
//        MediaType mediaType = MediaType.parse("application/json");
//        RequestBody body = RequestBody.create(mediaType, "{\"private_data\":{\"transaction_id\":\"<transaction_id>\",\"front\":\"<front_base64>\",\"is_error\":false}}");
//        Request request = new Request.Builder()
//                .url("https://example.com/api/v1/submit-front")
//                .post(body)
//                .addHeader("accept", "application/json")
//                .addHeader("signature", "<calculated_signature>")
//                .addHeader("customer-id", "<customer_id>")
//                .addHeader("content-type", "application/json")
//                .build();
//
//        Response response = client.newCall(request).execute();
//    }
//
//    public static void back(String[] args) throws Exception {
//        OkHttpClient client = new OkHttpClient();
//
//        MediaType mediaType = MediaType.parse("application/json");
//        RequestBody body = RequestBody.create(mediaType, "{\"private_data\":{\"transaction_id\":\"<transaction_id>\",\"back\":\"<backbase64>\",\"is_error\":false}}");
//        Request request = new Request.Builder()
//                .url("https://example.com/api/v1/submit-back")
//                .post(body)
//                .addHeader("accept", "application/json")
//                .addHeader("signature", "<calculated_signature>")
//                .addHeader("customer-id", "<customer_id>")
//                .addHeader("content-type", "application/json")
//                .build();
//
//        Response response = client.newCall(request).execute();
//    }
//
//    public static void barcode(String[] args) throws Exception {
//        OkHttpClient client = new OkHttpClient();
//
//        MediaType mediaType = MediaType.parse("application/json");
//        RequestBody body = RequestBody.create(mediaType, "{\"private_data\":{\"transaction_id\":\"<transaction_id>\",\"barcode\":\"<barcodebase64>\",\"is_error\":false}}");
//        Request request = new Request.Builder()
//                .url("https://example.com/api/v1/submit-barcode")
//                .post(body)
//                .addHeader("accept", "application/json")
//                .addHeader("signature", "<calculated_signature>")
//                .addHeader("customer-id", "<customer_id>")
//                .addHeader("content-type", "application/json")
//                .build();
//
//        Response response = client.newCall(request).execute();
//    }
//
//    public static void selfie(String[] args) throws Exception {
//        OkHttpClient client = new OkHttpClient();
//
//        MediaType mediaType = MediaType.parse("application/json");
//        RequestBody body = RequestBody.create(mediaType, "{\"private_data\":{\"transaction_id\":\"<transaction_id>\"}}");
//        Request request = new Request.Builder()
//                .url("https://example.com/api/v1/start-selfie")
//                .post(body)
//                .addHeader("accept", "application/json")
//                .addHeader("signature", "<calculated_signature>")
//                .addHeader("customer-id", "<customer_id>")
//                .addHeader("content-type", "application/json")
//                .build();
//
//        Response response = client.newCall(request).execute();
//    }
//
//    public static void end(String[] args) throws Exception {
//        OkHttpClient client = new OkHttpClient();
//
//        MediaType mediaType = MediaType.parse("application/json");
//        RequestBody body = RequestBody.create(mediaType, "{\"private_data\":{\"transaction_id\":\"<transaction_id>\"}}");
//        Request request = new Request.Builder()
//                .url("https://example.com/api/v1/end")
//                .post(body)
//                .addHeader("accept", "application/json")
//                .addHeader("signature", "<calculated_signature>")
//                .addHeader("customer-id", "<customer_id>")
//                .addHeader("content-type", "application/json")
//                .build();
//
//        Response response = client.newCall(request).execute();
//    }
//}
