package org.jeecg.common.util;

/**
 * <p>
 * 计算hashmap大小
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-04
 */
public class MapSizeUtil {

    private static final int MAXIMUM_CAPACITY = 0x40000000;

    public static int tableSizeFor(int c) {
        // n = (n-1)|/2|/4|/8|/16|/32;

        int n = c - 1;// n = c-1;
        n |= n >>> 1; // n/=2;  96|48  0b1100000|0b0110000 => 0b1110000  112
        n |= n >>> 2; // n/=4;  112|28 0b1110000|0b0011100 => 0b1111100  124
        n |= n >>> 4; // n/=8;  124|31 0b1111100|0b0011111 => 0b1111111	127
        n |= n >>> 8; // n/=16; 127|7  0b1111111|0b0000111 => 0b1111111  127
        n |= n >>> 16;// n/=32; 127|3  0b1111111|0b0000011 => 0b1111111  127

        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }
}
