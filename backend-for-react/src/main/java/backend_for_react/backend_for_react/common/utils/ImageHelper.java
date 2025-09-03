package backend_for_react.backend_for_react.common.utils;

import java.util.Base64;

public class ImageHelper {
    public static String encodeToBase64(byte[] data) {
        return data != null ? Base64.getEncoder().encodeToString(data) : null;
    }
}
