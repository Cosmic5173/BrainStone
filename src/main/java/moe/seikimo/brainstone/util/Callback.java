package moe.seikimo.brainstone.util;

import moe.seikimo.brainstone.Brain;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/** Functional interface for HTTP callbacks. */
public interface Callback extends okhttp3.Callback {
    /**
     * Makes a request using the Brainstone HTTP client.
     *
     * @param request The request.
     * @param callback The callback.
     */
    static void makeRequest(Request request, Callback callback) {
        Brain.getInstance().getHttpClient().newCall(request).enqueue(callback);
    }

    /**
     * Makes a request using the Brainstone HTTP client.
     *
     * @param request The request.
     * @param callback The callback.
     * @param delay The amount of time to wait before making the request in seconds.
     */
    static void makeRequest(Request request, Callback callback, long delay) {
        new Thread(() -> {
            try {
                Thread.sleep(delay * 1000L);
                Callback.makeRequest(request, callback);
            } catch (InterruptedException ignored) { }
        }).start();
    }

    /**
     * Invoked for a received HTTP response.
     * @param response The HTTP response.
     */
    void execute(@Nullable Response response);

    @Override
    default void onResponse(
            @NotNull Call call,
            @NotNull Response response
    ) {
        this.execute(response);
    }

    /**
     * Invoked when a network exception occurred talking to the server or when an unexpected
     * exception occurred creating the request or processing the response.
     *
     * @param call The call that failed.
     * @param exception The exception that occurred.
     */
    @Override
    default void onFailure(
            @NotNull Call call,
            @NotNull IOException exception
    ) {
        this.execute(null);
    }
}
