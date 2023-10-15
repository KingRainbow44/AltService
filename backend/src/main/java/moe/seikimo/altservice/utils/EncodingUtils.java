package moe.seikimo.altservice.utils;

import java.util.Base64;

public interface EncodingUtils {
    /**
     * Encodes bytes to Base64.
     *
     * @param bytes The bytes to encode.
     * @return The encoded string.
     */
    static String base64Encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Decodes a Base64 string.
     *
     * @param string The string to decode.
     * @return The decoded bytes.
     */
    static byte[] base64Decode(String string) {
        return Base64.getDecoder().decode(string);
    }
}
