package moe.seikimo.altservice.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import moe.seikimo.altservice.utils.enums.AlgorithmType;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.luaj.vm2.LuaTable;

import java.io.Reader;
import java.math.BigInteger;
import java.security.SignatureException;
import java.util.Base64;

public interface EncodingUtils {
    Gson JSON = new GsonBuilder()
            .serializeSpecialFloatingPointValues()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    /**
     * Encodes a byte array to Base64.
     * @param data The data.
     * @return The Base64-encoded data.
     */
    static String base64Encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * Decodes a Base64-encoded string.
     * @param data The Base64-encoded data.
     * @return The decoded data.
     */
    static String base64Decode(byte[] data) {
        return new String(Base64.getDecoder().decode(data));
    }

    /**
     * Decodes a Base64-encoded string.
     * @param data The Base64-encoded data.
     * @return The decoded data.
     */
    static String base64Decode(String data) {
        return new String(Base64.getDecoder().decode(data));
    }

    /**
     * Decodes a Base64-encoded string.
     * @param data The Base64-encoded data.
     * @return The decoded data.
     */
    static byte[] base64DecodeToBytes(String data) {
        return Base64.getDecoder().decode(data);
    }

    /**
     * Encodes an object into a JSON object.
     * @param object The object.
     * @return The JSON object.
     */
    static String jsonEncode(Object object) {
        return EncodingUtils.JSON.toJson(object);
    }

    /**
     * Decodes a JSON object into an object.
     * @param json The JSON object.
     * @return The object.
     */
    static <T> T jsonDecode(String json, Class<T> type) {
        return EncodingUtils.JSON.fromJson(json, type);
    }

    /**
     * Decodes a JSON object into an object.
     * @param json The JSON object.
     * @return The object.
     */
    static <T> T jsonDecode(Reader json, Class<T> type) {
        return EncodingUtils.JSON.fromJson(json, type);
    }

    /**
     * Converts a big integer (long) to a byte array.
     * @param integer The integer.
     * @return The byte array.
     */
    static byte[] longToBytes(BigInteger integer) {
        var array = integer.toByteArray();
        if (array[0] == 0) {
            var newArray = new byte[array.length - 1];
            System.arraycopy(array, 1, newArray, 0, newArray.length);
            return newArray;
        }

        return array;
    }

    /**
     * Create a JOSE signature from a DER signature.
     * @param signature The DER signature.
     * @param algorithmType The algorithm type.
     * @return The JOSE signature.
     */
    static byte[] derToJose(byte[] signature, AlgorithmType algorithmType) throws SignatureException {
        var derEncoded = signature[0] == 0x30 && signature.length != algorithmType.ecNumberSize * 2;
        if (!derEncoded) {
            throw new SignatureException("Invalid DER signature format.");
        }

        var joseSignature = new byte[algorithmType.ecNumberSize * 2];

        // Skip 0x30.
        var offset = 1;
        if (signature[1] == (byte) 0x81) {
            // Skip sign.
            offset++;
        }

        // Convert to unsigned. Should match DER length - offset.
        var encodedLength = signature[offset++] & 0xff;
        if (encodedLength != signature.length - offset) {
            throw new SignatureException("Invalid DER signature format.");
        }

        // Skip 0x02.
        offset++;

        // Obtain R number length (Includes padding) and skip it.
        var rLength = signature[offset++];
        if (rLength > algorithmType.ecNumberSize + 1) {
            throw new SignatureException("Invalid DER signature format.");
        }
        var rPadding = algorithmType.ecNumberSize - rLength;
        // Retrieve R number.
        System.arraycopy(signature, offset + Math.max(-rPadding, 0), joseSignature, Math.max(rPadding, 0), rLength + Math.min(rPadding, 0));

        // Skip R number and 0x02.
        offset += rLength + 1;

        // Obtain S number length. (Includes padding)
        var sLength = signature[offset++];
        if (sLength > algorithmType.ecNumberSize + 1) {
            throw new SignatureException("Invalid DER signature format.");
        }
        var sPadding = algorithmType.ecNumberSize - sLength;

        // Retrieve R number.
        System.arraycopy(signature, offset + Math.max(-sPadding, 0), joseSignature, algorithmType.ecNumberSize + Math.max(sPadding, 0), sLength + Math.min(sPadding, 0));

        return joseSignature;
    }

    /**
     * Converts a LuaTable to a Vector3i.
     *
     * @param position The LuaTable.
     * @return The Vector3i.
     */
    static Vector3i tableToBlock(LuaTable position) {
        return Vector3i.from(
                position.get("x").toint(),
                position.get("y").toint(),
                position.get("z").toint()
        );
    }

    /**
     * Converts a LuaTable to a Vector3f.
     *
     * @param position The LuaTable.
     * @return The Vector3f.
     */
    static Vector3f tableToPosition(LuaTable position) {
        return Vector3f.from(
                position.get("x").tofloat(),
                position.get("y").tofloat(),
                position.get("z").tofloat()
        );
    }

    /**
     * Converts an ItemData instance to a LuaTable.
     *
     * @param item The item.
     * @return The LuaTable.
     */
    static LuaTable itemToTable(ItemData item) {
        var table = new LuaTable();

        table.set("identifier", item.getDefinition().getIdentifier());
        table.set("count", item.getCount());
        table.set("damage", item.getDamage());

        return table;
    }
}
