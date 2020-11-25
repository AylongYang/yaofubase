package com.yaofu.basesdk.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

public class MD5Utils {

    private static final char[] m_hexCodes = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final int[] m_shifts = new int[]{60, 56, 52, 48, 44, 40, 36, 32, 28, 24, 20, 16, 12, 8, 4, 0};

    public static String getStringMD5(String value) {
        if (value == null || value.trim().length() < 1) {
            return null;
        }
        try {
            return getMD5(value.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String getMD5(byte[] source) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return toHex(md5.digest(source));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String getStreamMD5(String filePath) {
        String hash = null;
        byte[] buffer = new byte[4096];
        BufferedInputStream in = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            in = new BufferedInputStream(new FileInputStream(filePath));
            int numRead = 0;
            while ((numRead = in.read(buffer)) > 0) {
                md5.update(buffer, 0, numRead);
            }
            in.close();
            hash = toHex(md5.digest());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return hash;
    }

    private static String toHex(long var0, int var2) {
        StringBuilder var3 = new StringBuilder(var2);
        for (int var4 = 0; var4 < var2; ++var4) {
            int var5 = (int) (var0 >> m_shifts[var4 + (16 - var2)] & 15L);
            var3.append(m_hexCodes[var5]);
        }
        return var3.toString();
    }

    public static String toHex(byte[] var0) {
        return toHex(var0, 0, var0.length);
    }

    public static String toHex(byte var0) {
        return toHex((long) var0, 2);
    }

    public static String toHex(byte[] var0, int var1, int var2) {
        StringBuilder var3 = new StringBuilder();

        for (var2 += var1; var1 < var2; ++var1) {
            var3.append(toHex(var0[var1]));
        }
        return var3.toString();
    }
}
