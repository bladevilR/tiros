package org.jeecg.modules.tiros.uuv.service;

/**
 * <p>
 * uuv同步服务类
 * </p>
 *
 * @author yuyougen
 * @since 2020-05-27
 */
public interface UUVService {

    /**
     * 同步uuv的组织人员信息
     *
     * @param syncUser   是否同步人员，默认是
     * @param userId     操作人id
     * @param syncWorkshopId 同步的车间id
     * @return 同步结果
     * @throws RuntimeException 异常
     */
    String syncDeptAndUser(boolean syncUser, String userId, String syncWorkshopId) throws RuntimeException;

}
