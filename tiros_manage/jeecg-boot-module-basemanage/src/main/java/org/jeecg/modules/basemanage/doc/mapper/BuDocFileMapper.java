package org.jeecg.modules.basemanage.doc.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.doc.bean.BuDocFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.basemanage.doc.bean.BuFileAndDirectory;
import org.jeecg.modules.basemanage.doc.bean.vo.BuDocFileQueryVO;

import java.util.List;

/**
 * <p>
 * 文件信息 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-08-17
 */
public interface BuDocFileMapper extends BaseMapper<BuDocFile> {
    /**
     * 分页查询
     * @param page
     * @param file
     * @return
     */
    List<BuFileAndDirectory> listPage(Page<BuFileAndDirectory> page,@Param("file") BuDocFileQueryVO file);
    List<String> selectFileType();

}
