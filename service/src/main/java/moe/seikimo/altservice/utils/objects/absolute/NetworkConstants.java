package moe.seikimo.altservice.utils.objects.absolute;

import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v589.Bedrock_v589;

/**
 * Constants related to networking.
 */
public interface NetworkConstants {
    /* This will be the latest supported version. */
    BedrockCodec PACKET_CODEC = Bedrock_v589.CODEC;
    /* This is the compression level used by Minecraft. */
    int COMPRESSION_LEVEL = 6;
}
