package org.jeecg.modules.board.homepage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.board.homepage.bean.BuRptBoardDataItem;

import java.util.List;

/**
 * <p>
 * 统计数据项表（首页） 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-30
 */
public interface BuRptBoardDataItemService extends IService<BuRptBoardDataItem> {

    /**
     * 根据所属数据分类id获取统计数据项
     *
     * @param categoryId 数据分类id
     * @return 统计数据项
     * @throws Exception 异常信息
     */
    List<BuRptBoardDataItem> listByCategoryId(String categoryId) throws Exception;

}
