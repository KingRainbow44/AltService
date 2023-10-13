package moe.seikimo.altservice;

import com.beust.jcommander.Parameter;
import lombok.Data;
import lombok.Getter;

@Data
public final class Arguments {
    @Getter private static final Arguments instance = new Arguments();

    @Parameter(names = {"--port", "-p"}, description = "Port to listen on.")
    private int port = 8080;
}
