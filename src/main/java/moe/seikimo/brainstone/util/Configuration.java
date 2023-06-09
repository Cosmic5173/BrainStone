package moe.seikimo.brainstone.util;

import com.google.gson.annotations.SerializedName;

public final class Configuration {
    @SerializedName("port")
    private int port;
    @SerializedName("webhook-url")
    private String webhookUrl;
    @SerializedName("wireless-redstone-endpoint")
    private String wirelessRedstoneEndpoint;

    public int getPort() {
        return port;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public String getWirelessRedstoneEndpoint() {
        return wirelessRedstoneEndpoint;
    }
}
