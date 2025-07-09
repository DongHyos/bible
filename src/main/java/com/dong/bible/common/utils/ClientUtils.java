package com.dong.bible.common.utils;

/**
 * K-MaaS Client utils
 *
 * @author Won Gilho
 * @since Version 1.0
 * <pre>
 * ===================== Change history ======================
 * DATE          AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2023-10-11    Won Gilho     최초 생성
 * </pre>
 */
public class ClientUtils {

    // get current time millis
    private static long getCurrentTimeMillis() {
        final long currentTimeMillis = System.currentTimeMillis();
        final long time_low = (currentTimeMillis & 0x0000_0000_FFFF_FFFFL) << 32;
        final long time_mid = ((currentTimeMillis >> 32) & 0xFFFF) << 16;
        final long time_hi = ((currentTimeMillis >> 48) & 0x0FFF);
        return time_low | time_mid | time_hi;
    }

    // get 36 radix, zero padding string
    private static String getMaxRadixWithPadZero(long source, int desiredLength) {
        String result = Long.toString(source, Character.MAX_RADIX);
        return result.length() < desiredLength ? String.format("%0" + (desiredLength - result.length()) + "d%s", 0, result) : result;
    }
}
