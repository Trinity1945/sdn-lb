package com.example.faslbloadbalancer.common.util;

import io.netty.util.internal.EmptyArrays;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/5/9  9:56
 */
public final class HexString {
    private static final char[] CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final int FIRST_DIGIT = 1;
    private static final int SECOND_DIGIT_OR_COLON = 2;
    private static final int COLON = 3;
    private static final int SAVE_BYTE = 4;

    private HexString() {
    }

    public static String toHexString(byte[] bytes) {
        int lenBytes = bytes.length;
        if (lenBytes == 0) {
            return "";
        } else {
            char[] arr = new char[lenBytes * 2 + (lenBytes - 1)];
            int charPos = 0;
            int i = 0;

            while(true) {
                arr[charPos++] = CHARS[bytes[i] >>> 4 & 15];
                arr[charPos++] = CHARS[bytes[i] & 15];
                ++i;
                if (i >= lenBytes) {
                    return new String(arr, 0, arr.length);
                }

                arr[charPos++] = ':';
            }
        }
    }

    public static String toHexString(long val, int padTo) {
        int valBytes = (64 - Long.numberOfLeadingZeros(val) + 7) / 8;
        int lenBytes = valBytes > padTo ? valBytes : padTo;
        char[] arr = new char[lenBytes * 2 + (lenBytes - 1)];

        for(int charPos = arr.length - 1; charPos >= 0; --charPos) {
            if ((charPos + 1) % 3 == 0) {
                arr[charPos] = ':';
            } else {
                arr[charPos] = CHARS[(int)val & 15];
                val >>>= 4;
            }
        }

        return new String(arr, 0, arr.length);
    }

    public static String toHexString(long val) {
        return toHexString(val, 8);
    }

    /** @deprecated */
    @Deprecated
    public static byte[] fromHexString(String values) throws NumberFormatException {
        return toBytes(values);
    }

    public static byte[] toBytes(String values) throws NumberFormatException {
        int start = 0;
        int len = values.length();
        if (len == 0) {
            return EmptyArrays.EMPTY_BYTES;
        } else {
            int numColons = 0;

            for(int i = 0; i < len; ++i) {
                if (values.charAt(i) == ':') {
                    ++numColons;
                }
            }

            byte[] res = new byte[numColons + 1];
            int pos = 0;
            int state = 1;
            byte b = 0;

            while(start < len) {
                char c = values.charAt(start++);
                switch (state) {
                    case 1:
                        int digit = Character.digit(c, 16);
                        if (digit < 0) {
                            throw new NumberFormatException("Invalid char at index " + start + ": " + values);
                        }

                        b = (byte)digit;
                        state = start < len ? 2 : 4;
                        break;
                    case 2:
                        if (c != ':') {
                            int digit2 = Character.digit(c, 16);
                            if (digit2 < 0) {
                                throw new NumberFormatException("Invalid char at index " + start + ": " + values);
                            }

                            b = (byte)(b << 4 | digit2);
                            state = start < len ? 3 : 4;
                        } else {
                            state = 4;
                        }
                        break;
                    case 3:
                        if (c != ':') {
                            throw new NumberFormatException("Separator expected at index " + start + ": " + values);
                        }

                        state = 4;
                        break;
                    default:
                        throw new IllegalStateException("Should not be in state " + state);
                }

                if (state == 4) {
                    res[pos++] = b;
                    b = 0;
                    state = 1;
                }
            }

            if (pos != res.length) {
                throw new NumberFormatException("Invalid hex string: " + values);
            } else {
                return res;
            }
        }
    }

    public static long toLong(String value) throws NumberFormatException {
        int shift = 0;
        long result = 0L;
        int sinceLastSeparator = 0;

        for(int charPos = value.length() - 1; charPos >= 0; --charPos) {
            char c = value.charAt(charPos);
            if (c == ':') {
                if (sinceLastSeparator == 0) {
                    throw new NumberFormatException("Expected hex digit at index " + charPos + ": " + value);
                }

                if (sinceLastSeparator == 1) {
                    shift += 4;
                }

                sinceLastSeparator = 0;
            } else {
                int digit = Character.digit(c, 16);
                if (digit < 0) {
                    throw new NumberFormatException("Invalid hex digit at index " + charPos + ": " + value);
                }

                result |= (long)digit << shift;
                shift += 4;
                ++sinceLastSeparator;
                if (sinceLastSeparator > 2) {
                    throw new NumberFormatException("Expected colon at index " + charPos + ": " + value);
                }
            }

            if (shift > 64) {
                throw new NumberFormatException("Too many bytes in hex string to convert to long: " + value);
            }
        }

        return result;
    }
}
