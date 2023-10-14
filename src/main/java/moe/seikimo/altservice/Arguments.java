package moe.seikimo.altservice;

import com.beust.jcommander.Parameter;
import lombok.Data;
import lombok.Getter;

@Data
public final class Arguments {
    @Getter private static final Arguments instance = new Arguments();

    @Parameter(names = {"--ws-port", "-wp"}, description = "Port to listen on.")
    private int webSocket = 8080;

    @Parameter(names = {"--http-port", "-hp"}, description = "Port to listen on.")
    private int httpPort = 8081;
}
