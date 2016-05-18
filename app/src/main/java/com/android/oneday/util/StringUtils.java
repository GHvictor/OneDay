package com.android.oneday.util;

import java.security.MessageDigest;

/**
 * Created by dell on 5/18/2016.
 */
public class StringUtils {
    private final static String[] hexDigits = {
            "0", "1", "2", "3", "4", "5", "6", "7",
            "8", "9", "a", "b", "c", "d", "e", "f"
    };

    public static String MD5(String transcoding) {
        String resultString = null;
        try {
            resultString = new String(transcoding);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md5.digest(resultString.getBytes()));
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
        return resultString;
    }

    private static String byteArrayToHexString(byte[] b) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            buffer.append(byteToHexString(b[i]));
        }
        return buffer.toString();
    }


    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }
}
