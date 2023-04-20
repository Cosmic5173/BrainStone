package moe.seikimo.user;

import moe.seikimo.Brain;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Timer;
import java.util.UUID;

public record StasisChamber(User user, UUID id, UUID key) {

    public void activate() {
        activate(0);
    }

    public void activate(int callRecursion) {
        if (callRecursion > 4) {
            System.out.println("Failed to activate " + user.getName() + "'s stasis chamber: Too many failed attempts.");
            return;
        }

        Brain.getInstance().getHttpClient().newCall(new Request.Builder()
                .url(Brain.getInstance().getConfiguration().getWirelessRedstoneEndpoint() + key)
                .build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Failed to activate stasis chamber: " + e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                var body = response.body().string();
                if (response.code() != 200) {
                    System.out.println("Failed to activate " + user.getName() + "'s stasis chamber: " + body);
                } else if (body.equals("true")) {
                    System.out.println("Successfully activated " + user.getName() + "'s stasis chamber.");

                    // Call once more to reset block status to off
                    new Timer().schedule(new java.util.TimerTask() {
                        @Override
                        public void run() {
                            Brain.getInstance().getHttpClient().newCall(new Request.Builder()
                                    .url(Brain.getInstance().getConfiguration().getWirelessRedstoneEndpoint() + key)
                                    .build()).enqueue(new Callback() {
                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                                }

                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                                }
                            });
                        }
                    }, 1000);
                }
            }
        });
    }

    @Override
    public String toString() {
        return String.format("StasisChamber{id=%s, key=%s}", id, key);
    }
}
