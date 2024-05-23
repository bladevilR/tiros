package org.jeecg.common.system.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 字典模型，返回前端时区分value是数字类型时，返回对象由DictModel变为此对象
 * </p>
 *
 * @author zhaiyantao
 * @since 2020/10/20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DictNumberModel implements Serializable {

    private static final long serialVersionUID = 1L;

    public DictNumberModel() {
    }

    public DictNumberModel(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 字典value
     */
    private Integer value;
    /**
     * 字典文本
     */
    private String text;

    /**
     * 字典项排序，仅用于后端
     */
    @JsonIgnore
    private Integer sortOrder;

}
