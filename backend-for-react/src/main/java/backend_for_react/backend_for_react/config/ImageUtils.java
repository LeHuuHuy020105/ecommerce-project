package backend_for_react.backend_for_react.config;

import java.util.Base64;

public class ImageUtils {
    public static byte[] base64ToBytes(String base64Str) {
        return Base64.getDecoder().decode(base64Str);
    }
    public static String bytesToBase64(byte[] data) {
        if (data == null || data.length == 0) return null;
        return Base64.getEncoder().encodeToString(data);
    }
}
