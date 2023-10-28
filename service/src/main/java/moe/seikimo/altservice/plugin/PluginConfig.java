package moe.seikimo.altservice.plugin;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.Objects;

@Data
public final class PluginConfig {
    private String name = "";
    private String description = "";
    private String version = "";
    private String author = "";

    @SerializedName(value = "apiVersion", alternate = "api")
    private int apiVersion = -1;
    private String mainClass = "";

    /**
     * @return Validates the plugin's config.
     */
    public boolean validPlugin() {
        return !Objects.equals(this.getName(), "") &&
                !Objects.equals(this.getMainClass(), "") &&
                this.getApiVersion() != -1;
    }
}
