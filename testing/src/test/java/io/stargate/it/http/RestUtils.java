package io.stargate.it.http;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static org.assertj.core.api.Assertions.assertThat;

public class RestUtils {
    private static final Logger logger = LoggerFactory.getLogger(RestUtils.class);

    public static String get(String authToken, String path, int expectedStatusCode) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        Request request = new Request.Builder()
                .url(path)
                .get()
                .addHeader("X-Cassandra-Token", authToken)
                .build();

        Response response = client.newCall(request).execute();
        assertStatusCode(response, expectedStatusCode);

        ResponseBody body = response.body();
        assertThat(body).isNotNull();

        return body.string();
    }

    public static String post(String authToken, String path, String requestBody, int expectedStatusCode) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        Request request = new Request.Builder()
                .url(path)
                .post(RequestBody.create(MediaType.parse("application/json"), requestBody))
                .addHeader("X-Cassandra-Token", authToken)
                .build();

        Response response = client.newCall(request).execute();
        assertStatusCode(response, expectedStatusCode);

        ResponseBody body = response.body();
        assertThat(body).isNotNull();

        return body.string();
    }

    public static String put(String authToken, String path, String requestBody, int expectedStatusCode) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        Request request = new Request.Builder()
                .url(path)
                .put(RequestBody.create(MediaType.parse("application/json"), requestBody))
                .addHeader("X-Cassandra-Token", authToken)
                .build();

        Response response = client.newCall(request).execute();
        assertStatusCode(response, expectedStatusCode);

        ResponseBody body = response.body();
        assertThat(body).isNotNull();

        return body.string();
    }

    public static String patch(String authToken, String path, String requestBody, int expectedStatusCode) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        Request request = new Request.Builder()
                .url(path)
                .patch(RequestBody.create(MediaType.parse("application/json"), requestBody))
                .addHeader("X-Cassandra-Token", authToken)
                .build();

        Response response = client.newCall(request).execute();
        assertStatusCode(response, expectedStatusCode);

        ResponseBody body = response.body();
        assertThat(body).isNotNull();

        return body.string();
    }

    public static String delete(String authToken, String path, int expectedStatusCode) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        Request request = new Request.Builder()
                .url(path)
                .delete()
                .addHeader("X-Cassandra-Token", authToken)
                .build();

        Response response = client.newCall(request).execute();
        assertStatusCode(response, expectedStatusCode);

        return response.body() != null ? response.body().string() : null;
    }

    public static void assertStatusCode(Response response, int statusCode) throws IOException {
        try {
            assertThat(response.code()).isEqualTo(statusCode);
        } catch (AssertionError e) {
            if (response.body() != null) {
                logger.error(response.body().string());
            }
            throw e;
        }
    }
}
