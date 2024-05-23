package org.jeecg.common.tiros.util;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.lang.reflect.Field;

/**
 * <p>
 * mybatis wrapper操作 工具类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-23
 */
public class MybatisWrapperUtil {

    public static <T extends Model<T>> UpdateWrapper<T> getSetFieldNullByIdUpdateWrapper(T entity) {
        if (null == entity) {
            return null;
        }

        UpdateWrapper<T> updateWrapper = new UpdateWrapper<>();

        setFieldNull(updateWrapper, entity);
        setIdValue(entity, updateWrapper);

        return updateWrapper;
    }

    public static <T extends Model<T>> void setFieldNull(UpdateWrapper<T> updateWrapper, T entity) {
        if (null == entity) {
            return;
        }
        if (null == updateWrapper) {
            return;
        }

        Field[] fields = entity.getClass().getDeclaredFields();
        if (fields.length <= 0) {
            return;
        }

        try {
            for (Field field : fields) {
                field.setAccessible(true);

                boolean jsonIgnore = null != field.getAnnotation(JsonIgnore.class);
                TableField tableField = field.getAnnotation(TableField.class);
                boolean tableFieldNotExist = null != tableField && !tableField.exist();
                if (jsonIgnore || tableFieldNotExist) {
                    field.setAccessible(false);
                    continue;
                }

                Object fieldValue = field.get(entity);
                if (null == fieldValue) {
                    String columnName = LineHumpUtil.humpToLine(field.getName());
                    updateWrapper.set(columnName, null);
                }

                field.setAccessible(false);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static <T extends Model<T>> void setIdValue(T entity, UpdateWrapper<T> updateWrapper) {
        try {
            Field idField = null;
            idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            String idValue = (String) idField.get(entity);
            idField.setAccessible(false);
            updateWrapper.eq("id", idValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
