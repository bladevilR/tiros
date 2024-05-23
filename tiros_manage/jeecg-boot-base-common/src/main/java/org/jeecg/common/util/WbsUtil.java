package org.jeecg.common.util;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author yyg
 */
public class WbsUtil {
  /*  @Resource
    private WbsCommonMapper wbsCommonMapper;
    private static WbsUtil wbsUtil;

    @PostConstruct
    public void init() {
        wbsUtil = this;
        wbsUtil.wbsCommonMapper = this.wbsCommonMapper;
    }

    public static String createWbs(String tableName, String conditions, String value, String parentId, String wbs) {
        //如果上级节点为空，就查找父类节点为空的最大wbs
        if (StrUtil.isEmpty(parentId)) {
            Integer parentWbs = wbsUtil.wbsCommonMapper.selectParentMaxWbs(tableName, conditions, value);
            parentWbs = Objects.nonNull(parentWbs) ? parentWbs + 1 : 1;
            return parentWbs.toString();
        } else {
            if (StrUtil.isNotEmpty(wbs)) {
                Integer childWbs = wbsUtil.wbsCommonMapper.selectChildMaxWbs(tableName, conditions, value, wbs);
                childWbs = Objects.nonNull(childWbs) ? childWbs + 1 : 1;
                return wbs + "." + childWbs;
            }
        }
        return null;
    }*/

}
