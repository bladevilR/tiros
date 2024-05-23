package org.jeecg.modules.third.dynamic;

/**
 * <p>
 * 数据源枚举
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-30
 */
public enum DataSourceEnum {

    jdxdb("jdxdb"),
    maximodb("maximodb"),
    workshopdb("workshopdb");

    private final String value;

    DataSourceEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
