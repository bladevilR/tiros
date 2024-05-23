package org.jeecg.modules.third.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * <p>
 * 带层级、纯数字、 编码生成工具
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-26
 */
public class LevelCodeUtil {

    /**
     * 字符数组
     */
    private static final char[] CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    /**
     * 默认每一级长度：3
     */
    private static final int LEVEL_LENGTH = 3;


    /**
     * 根据当前最大的兄弟编码获取下个编码
     *
     * @param brotherMax 当前最大的兄弟编码
     * @return 下个编码
     */
    public static String getNextByBrotherMax(String brotherMax) {
        return getNextByBrotherMax(brotherMax, LEVEL_LENGTH);
    }

    public static String getNextByBrotherMax(String brotherMax, int levelLength) {
        if (StringUtils.isBlank(brotherMax)) {
            return minCannotBeZero(getMin(levelLength));
        }

        checkLength(brotherMax, levelLength);

        int length = brotherMax.length();
        String prefix = brotherMax.substring(0, length - levelLength);
        String suffix = brotherMax.substring(length - levelLength);

        char[] suffixChars = suffix.toCharArray();
        char[] newSuffixChars = getNextCharArray(suffixChars, levelLength);

        return prefix + minCannotBeZero(String.valueOf(newSuffixChars));
    }

    /**
     * 根据上级编码获取首个下级编码
     *
     * @param parent 上级编码
     * @return 首个下级编码
     */
    public static String getFirstByParent(String parent) {
        return getFirstByParent(parent, LEVEL_LENGTH);
    }

    public static String getFirstByParent(String parent, int levelLength) {
        if (StringUtils.isBlank(parent)) {
            return minCannotBeZero(getMin(levelLength));
        }

        checkLength(parent, levelLength);
        return parent + minCannotBeZero(getMin(levelLength));
    }


    /**
     * 检查支持的wbs最大长度
     *
     * @param maxWbsLength wbs最大长度
     * @return 检查结果
     */
    public static String checkSupportWbsLength(int maxWbsLength) {
        return checkSupportWbsLength(maxWbsLength, LEVEL_LENGTH);
    }

    public static String checkSupportWbsLength(int maxWbsLength, int levelLength) {
        StringBuilder result = new StringBuilder();
        long levelSupportCount = 1L;
        for (int i = 0; i < levelLength; i++) {
            levelSupportCount = levelSupportCount * CHARS.length;
        }

        for (int i = 1; i < 1000; i++) {
            int wbsLength = getWbsLength(i + 1, levelLength);
            if (wbsLength > maxWbsLength) {
                result.append("wbs最高长度").append(maxWbsLength).
                        append("，每一级长度").append(levelLength).append("(支持").append(levelSupportCount).append("个节点)")
                        .append("，支持到[级别").append(i).append("]：")
                        .append("[级别").append(i).append("]的wbs长度").append(getWbsLength(i, levelLength))
                        .append("，[级别").append(i + 1).append("]的wbs长度").append(wbsLength);
                break;
            }
        }

        return result.toString();
    }

    private static int getWbsLength(int level, int levelLength) {
        int cuLen = levelLength;
        int cuWbsLen = cuLen;
        for (int i = 1; i <= level; i++) {
            cuLen = levelLength * i;
            cuWbsLen = i == 1 ? cuLen : cuWbsLen + 1 + cuLen;
        }
        return cuWbsLen;
    }

    private static char[] getNextCharArray(char[] oldChars, int levelLength) {
        int[] indexes = new int[oldChars.length];
        for (int i = 0; i < oldChars.length; i++) {
            char oldChar = oldChars[i];
            int index = getCharIndex(oldChar);
            indexes[i] = index;
        }

        boolean allFull = true;
        for (int index : indexes) {
            if (index < CHARS.length - 1) {
                allFull = false;
                break;
            }
        }
        if (allFull) {
            throw new RuntimeException("增加后编码长度超过" + levelLength + "，请正确设置");
        }

        int[] newIndexes = indexes.clone();
        for (int i = indexes.length - 1; i >= 0; i--) {
            int nextIndex = indexes[i] + 1;
            if (nextIndex > CHARS.length - 1) {
                newIndexes[i] = 0;
                newIndexes[i - 1] = indexes[i - 1] + 1;
            } else {
                newIndexes[i] = nextIndex;
                break;
            }
        }

        char[] newChars = new char[oldChars.length];
        for (int i = 0; i < newIndexes.length; i++) {
            newChars[i] = CHARS[newIndexes[i]];
        }

        return newChars;
    }

    private static int getCharIndex(char inChar) {
        for (int i = 0; i < CHARS.length; i++) {
            if (CHARS[i] == inChar) {
                return i;
            }
        }
        return 0;
    }

    private static String getMin(int levelLength) {
        StringBuilder minResult = new StringBuilder(String.valueOf(CHARS[0]));
        while (minResult.length() < levelLength) {
            minResult.insert(0, CHARS[0]);
        }
        return minResult.toString();
    }

    private static String minCannotBeZero(String min) {
        if (min.charAt(min.length() - 1) == CHARS[0]) {
            StringBuilder stringBuilder = new StringBuilder(min);
            min = stringBuilder.replace(min.length() - 1, min.length(), String.valueOf(CHARS[1])).toString();
        }
        return min;
    }


    public static void checkLengthAlphanumeric(String input) {
        checkLengthAlphanumeric(input, LEVEL_LENGTH);
    }

    public static void checkLengthAlphanumeric(String input, int levelLength) {
        checkAlphanumeric(input);
        checkLength(input, levelLength);
    }

    public static void checkLength(String input) {
        checkLength(input, LEVEL_LENGTH);
    }

    public static void checkLength(String input, int levelLength) {
        int length = input.length();
        if (length < levelLength) {
            throw new RuntimeException("编码长度需大于" + levelLength);
        }
        if (length % levelLength != 0) {
            throw new RuntimeException("编码长度需为" + levelLength + "的倍数");
        }
    }

    public static void checkAlphanumeric(String input) {
        Pattern pattern = Pattern.compile("[0-9A-Za-z]*");
        boolean matches = pattern.matcher(input).matches();
        if (!matches) {
            throw new RuntimeException("编码只能包含数字和大小写字母");
        }
    }

}