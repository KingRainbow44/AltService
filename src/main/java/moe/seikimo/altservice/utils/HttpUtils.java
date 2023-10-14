package moe.seikimo.altservice.utils;

import moe.seikimo.altservice.AltService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public interface HttpUtils {
    OkHttpClient client = new OkHttpClient();

    /**
     * Performs a GET request.
     *
     * @param url The URL to request.
     * @return The response.
     */
    static Response get(String url) throws Exception {
        var request = new Request.Builder()
                .url(url)
                .build();
        return client.newCall(request).execute();
    }

    /**
     * Parses a response into a class.
     *
     * @param response The response to parse.
     * @param clazz The class to parse into.
     * @return The parsed class.
     */
    static <T> T parse(Response response, Class<T> clazz) throws Exception {
        var body = response.body();
        if (body == null) throw new IOException("Response body is null.");

        return AltService.getGson()
                .fromJson(body.string(), clazz);
    }

    /**
     * Parses a response into a byte array.
     *
     * @param response The response to parse.
     * @return The parsed byte array.
     */
    static byte[] parse(Response response) throws Exception {
        var body = response.body();
        if (body == null) throw new IOException("Response body is null.");

        return body.bytes();
    }
}
