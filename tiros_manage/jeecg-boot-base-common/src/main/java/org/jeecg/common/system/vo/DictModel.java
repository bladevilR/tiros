package org.jeecg.common.system.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DictModel implements Serializable {
    private static final long serialVersionUID = 1L;

    public DictModel() {
    }

    public DictModel(String value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 字典value
     */
    private String value;
    /**
     * 字典文本
     */
    private String text;

    /**
     * 特殊用途： JgEditableTable
     *
     * @return
     */
    public String getTitle() {
        return this.text;
    }

    /**
     * 扩展字段信息
     */
    private Map<String, Object> extFields;

    /**
     * 字典dictCode，仅用于后端
     */
    @JsonIgnore
    private String dictCode;

    /**
     * 字典项排序，仅用于后端
     */
    @JsonIgnore
    private Integer sortOrder;

}
