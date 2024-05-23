package org.jeecg.modules.outsource.mapper;

import org.jeecg.modules.outsource.bean.BuOutsourceRateingAnnex;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 评分附件 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020 -10-16
 */
public interface BuOutsourceRateingAnnexMapper extends BaseMapper<BuOutsourceRateingAnnex> {

    /**
     * Delete file by id.
     *
     * @param id the id
     */
    void deleteFileById(String id);
}
