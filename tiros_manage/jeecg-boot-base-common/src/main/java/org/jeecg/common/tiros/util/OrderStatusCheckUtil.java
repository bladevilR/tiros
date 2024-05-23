package org.jeecg.common.tiros.util;

import org.jeecg.common.exception.JeecgBootException;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 工单状态检查工具
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-19
 */
public class OrderStatusCheckUtil {

    private static final List<Integer> MATERIAL_ORDER_TYPE = Arrays.asList(4, 5);


    /**
     * 未下发	0
     * 已下发(未核实)	1
     * 已下发(已核实)	2
     * 已提交	3
     * 已关闭	4
     * 已暂停	5
     * 已发料	6
     * 已领料	7
     * 填报中	8
     * 已取消	9
     */
    public static boolean check(String orderCode, Integer orderType, Integer currentStatus, Integer targetStatus) {
        if (null == targetStatus || null == currentStatus) {
            return true;
        }

        boolean executable = true;
        if (currentStatus == 9) {
            // 已取消的工单相当于删除，什么操作都不能做
            executable = false;
        } else {
            if (targetStatus == 1) {
                // 未下发的才可以 下发
                if (currentStatus != 0) {
                    executable = false;
                }
            }
            if (targetStatus == 2) {
                // 已下发未核实的才可以 核实
                if (currentStatus != 1) {
                    executable = false;
                }
            }
            if (targetStatus == 3) {
                // 填报中的才可以 提交给调度
                if (currentStatus != 8) {
                    executable = false;
                }
            }
            if (targetStatus == 4) {
                if (!MATERIAL_ORDER_TYPE.contains(orderType)) {
                    // 非领料工单：已提交的才可以 关闭工单
                    if (currentStatus != 3) {
                        executable = false;
                    }
                }
            }
            if (targetStatus == 5) {
                // 暂停状态未使用
            }
            if (targetStatus == 6) {
                // 已下发已核实、填报中、已提交的才可以 发料确认
                if (!Arrays.asList(2, 8, 3).contains(currentStatus)) {
                    executable = false;
                }
            }
            if (targetStatus == 7) {
                // 发料确认后才可以 领料确认
                if (currentStatus != 6) {
                    executable = false;
                }
            }
            if (targetStatus == 8) {
                // 已下发已核实、领料确认后才可以 填报
                if (!Arrays.asList(2, 7).contains(currentStatus)) {
                    executable = false;
                }
            }
        }

        if (!executable) {
            String currentStatusName = getStatusName(currentStatus);
            String errorMassage = "不支持操作，工单[" + orderCode + "]当前状态为[" + currentStatusName + "]";
            throw new JeecgBootException(errorMassage);
        }

        return true;
    }

    public static boolean checkWorkingOrderSubmitToMonitor(String orderCode, Integer currentStatus) {
        if (null == currentStatus) {
            return true;
        }

        // 填报中的才可以 提交给工班长（因为没有填报完成的状态，只能通过填报中状态判断）
        if (currentStatus != 8) {
            String currentStatusName = getStatusName(currentStatus);
            String errorMassage = "不支持操作，工单[" + orderCode + "]当前状态为[" + currentStatusName + "]";
            throw new JeecgBootException(errorMassage);
        }

        return true;
    }


    private static String getStatusName(Integer status) {
        if (null == status) {
            return null;
        }

        String statusName = null;
        switch (status) {
            case 0:
                statusName = "未下发";
                break;
            case 1:
                statusName = "已下发(未核实)";
                break;
            case 2:
                statusName = "已下发(已核实)";
                break;
            case 3:
                statusName = "已提交";
                break;
            case 4:
                statusName = "已关闭";
                break;
            case 5:
                statusName = "已暂停";
                break;
            case 6:
                statusName = "已发料";
                break;
            case 7:
                statusName = "已领料";
                break;
            case 8:
                statusName = "填报中";
                break;
            case 9:
                statusName = "已取消";
                break;
            default:
                break;
        }

        return statusName;
    }

}
