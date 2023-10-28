package moe.seikimo.altservice.utils;

import com.google.protobuf.GeneratedMessageV3;

import java.util.Base64;

@SuppressWarnings("RedundantCast")
public interface BinaryUtils {
    /**
     * Decodes a Base64 string.
     *
     * @param input The input string.
     * @return The decoded string.
     */
    static byte[] base64Decode(String input) {
        return Base64.getUrlDecoder().decode(input);
    }

    /**
     * Encodes a byte array to Base64.
     *
     * @param input The input byte array.
     * @return The encoded string.
     */
    static String base64Encode(byte[] input) {
        return Base64.getUrlEncoder().encodeToString(input);
    }

    /**
     * Decodes a protobuf message.
     *
     * @param input The input byte array.
     * @param clazz The class of the protobuf message.
     * @return The decoded protobuf message.
     */
    static <T extends GeneratedMessageV3> T decodeFromProto(byte[] input, Class<T> clazz) {
        try {
            return clazz.cast(clazz.getDeclaredMethod("parseFrom", byte[].class)
                    .invoke(null, (Object) input));
        } catch (Exception ignored) {
            return null;
        }
    }
}
