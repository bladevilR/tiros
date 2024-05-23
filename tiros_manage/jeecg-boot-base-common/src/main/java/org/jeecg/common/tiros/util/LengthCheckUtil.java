package org.jeecg.common.tiros.util;

import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;

/**
 * <p>
 * 长度校验工具类：临时使用，以后换更好的方式
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/12/7
 */
public class LengthCheckUtil {

    public static void maxLength(String input, String fieldName, Integer length) {
        if (StringUtils.isNotBlank(input) && null != length && input.length() > length) {
            throw new JeecgBootException(fieldName + "长度不能大于" + length + "（当前长度" + input.length() + "）");
        }
    }

}
