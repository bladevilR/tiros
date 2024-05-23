package org.jeecg.common.tiros.dataisolation.service;

import org.jeecg.common.tiros.dataisolation.bean.LineWorkshopCompany;

import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 查询线路车间公司信息 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2022/8/26
 */
public interface LineWorkshopCompanyService {

    Map<String, LineWorkshopCompany> mapLineWorkshopCompany();

    Map<String, Map<String, Set<String>>> treeCompanyWorkshopLine();

}
