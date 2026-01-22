package com.ktb.file.util;

public class SizeUtil {
    private static final long KILOBYTE = 1024L;
    private static final long MEGABYTE = KILOBYTE * 1024;
    private static final long GIGABYTE = MEGABYTE * 1024;

    public static String getReadableSize(Number number) {
        long size = number.longValue();
        if (size < KILOBYTE) {
            return size + " B";
        } else if (size < MEGABYTE) {
            return String.format("%.2f KB", size / (double) KILOBYTE);
        } else if (size < GIGABYTE) {
            return String.format("%.2f MB", size / (double) MEGABYTE);
        } else {
            return String.format("%.2f GB", size / (double) GIGABYTE);
        }
    }
}
