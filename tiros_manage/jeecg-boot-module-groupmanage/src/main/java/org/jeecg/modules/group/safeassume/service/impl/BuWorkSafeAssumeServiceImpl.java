package org.jeecg.modules.group.safeassume.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.group.safeassume.bean.BuWorkSafeAssume;
import org.jeecg.modules.group.safeassume.bean.BuWorkSafeAssumeRead;
import org.jeecg.modules.group.safeassume.bean.vo.BuWorkSafeAssumeQueryVO;
import org.jeecg.modules.group.safeassume.mapper.BuWorkSafeAssumeMapper;
import org.jeecg.modules.group.safeassume.service.BuWorkSafeAssumeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 安全预想 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-12
 */
@Slf4j
@Service
public class BuWorkSafeAssumeServiceImpl extends ServiceImpl<BuWorkSafeAssumeMapper, BuWorkSafeAssume> implements BuWorkSafeAssumeService {

    @Resource
    private BuWorkSafeAssumeMapper buWorkSafeAssumeMapper;


    /**
     * @see BuWorkSafeAssumeService#pageSafeAssume(BuWorkSafeAssumeQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<BuWorkSafeAssume> pageSafeAssume(BuWorkSafeAssumeQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        IPage<BuWorkSafeAssume> page = buWorkSafeAssumeMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);

        List<BuWorkSafeAssume> workSafeAssumeList = page.getRecords();
        if (CollectionUtils.isNotEmpty(workSafeAssumeList)) {
            for (BuWorkSafeAssume workSafeAssume : workSafeAssumeList) {
                fillProperties(workSafeAssume);
            }
        }

        return page;
    }

    /**
     * @see BuWorkSafeAssumeService#selectById(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public BuWorkSafeAssume selectById(String id) throws Exception {
        BuWorkSafeAssume workSafeAssume = buWorkSafeAssumeMapper.selectSafeAssumeById(id);
        if (null == workSafeAssume) {
            throw new JeecgBootException("安全预想不存在");
        }

        fillProperties(workSafeAssume);

        return workSafeAssume;
    }

    /**
     * @see BuWorkSafeAssumeService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));

        buWorkSafeAssumeMapper.deleteBatchIds(idList);

        return true;
    }

    private void fillProperties(BuWorkSafeAssume workSafeAssume) {
        Date readDate = workSafeAssume.getReadDate();
        int readUserCount = 0;

        List<BuWorkSafeAssumeRead> readUserList = new ArrayList<>();
        List<BuWorkSafeAssumeRead> unReadUserList = new ArrayList<>(workSafeAssume.getNeedReadUserList());

        List<BuWorkSafeAssumeRead> readList = workSafeAssume.getReadList();
        for (BuWorkSafeAssumeRead workSafeAssumeRead : readList) {
            if (DateUtils.isSameDay(workSafeAssumeRead.getReadTime(), readDate)) {
                readUserCount++;
                readUserList.add(workSafeAssumeRead);
                String userId = workSafeAssumeRead.getUserId();
                unReadUserList.removeIf(readUser -> readUser.getUserId().equals(userId));
            }
        }

        String readUserListStr = readUserList.stream().map(BuWorkSafeAssumeRead::getUserName).collect(Collectors.joining(","));
        String unReadUserListStr = unReadUserList.stream().map(BuWorkSafeAssumeRead::getUserName).collect(Collectors.joining(","));

        workSafeAssume.setReadUserCount(readUserCount);
        workSafeAssume.setReadUserListStr(readUserListStr);
        workSafeAssume.setUnReadUserListStr(unReadUserListStr);
    }

}
