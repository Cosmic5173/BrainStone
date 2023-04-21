package moe.seikimo.brainstone.user;

import moe.seikimo.brainstone.Brain;
import moe.seikimo.brainstone.util.Callback;
import okhttp3.Request;
import org.eclipse.jetty.util.FuturePromise;

import java.util.UUID;
import java.util.concurrent.Future;

public record StasisChamber(
        User user, UUID id, UUID key
) {
    /**
     * Attempts to activate a stasis chamber.
     */
    public Future<Boolean> activate() {
        return activate(0);
    }

    /**
     * Attempts to activate a stasis chamber.
     *
     * @param calls The number of times this method has been called recursively.
     * @return A future that will return true if the stasis chamber was activated, false otherwise.
     */
    private Future<Boolean> activate(int calls) {
        // Check if the method has been called too many times.
        if (calls > 4) {
            System.out.printf("Failed to activate %s's stasis chamber. Too many failed attempts.%n", user.getName());
            return new FuturePromise<>(false);
        }

        // Activate the stasis chamber.
        var request = new Request.Builder()
                .url(Brain.endpoint(this.key))
                .build();
        var future = new FuturePromise<>(false);
        Callback.makeRequest(request, response -> {
            try {
                // Check if the response is null.
                if (response == null) {
                    future.succeeded(false); return;
                }
                // Check the response code of the request.
                if (response.code() != 200) {
                    future.succeeded(false); return;
                }

                // Check the response body of the request.
                var body = response.body();
                if (body == null) {
                    future.succeeded(false); return;
                }

                // Check if the response body is equal to "true".
                if (!body.string().equals("true")) {
                    future.succeeded(activate(calls + 1).get());
                } else {
                    future.succeeded(true);
                    System.out.println("Successfully activated " + user.getName() + "'s stasis chamber.");

                    // Reset status of redstone block.
                    var resetRequest = new Request.Builder()
                            .url(Brain.endpoint(this.key))
                            .build();
                    Callback.makeRequest(resetRequest, resetResponse -> {}, 3L);
                }
            } catch (Exception ignored) {
                future.succeeded(false);
                System.out.println("Failed to activate " + user.getName() + "'s stasis chamber.");
            }
        });

        return future;
    }

    @Override
    public String toString() {
        return String.format("StasisChamber{id=%s, key=%s}", id, key);
    }
}
