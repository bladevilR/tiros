package org.jeecg.modules.basemanage.productionnotice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Mapper;
import org.jeecg.modules.basemanage.productionnotice.entity.BuProductionNotice;
import org.jeecg.modules.basemanage.productionnotice.entity.vo.BuProductionNoticeFormProgressVO;
import org.jeecg.modules.basemanage.productionnotice.entity.vo.BuProductionNoticeProgressDetailVO;

import java.util.List;

@Mapper
public interface BuProductionNoticeMapper extends BaseMapper<BuProductionNotice> {

    @Select("SELECT COUNT(1) FROM bu_production_notice_order_rel r LEFT JOIN bu_work_order o ON o.id = r.order_id " +
            "WHERE r.notice_id = #{noticeId} AND r.train_no = #{trainNo} AND r.del_flag = 0 AND (o.id IS NULL OR o.order_status <> 9)")
    Integer countActiveBindingByTrain(@Param("noticeId") String noticeId, @Param("trainNo") String trainNo);

    @Select("SELECT COUNT(DISTINCT r.train_no) FROM bu_production_notice_order_rel r " +
            "LEFT JOIN bu_work_order o ON o.id = r.order_id " +
            "WHERE r.notice_id = #{noticeId} AND r.del_flag = 0 AND r.train_no IS NOT NULL AND r.train_no <> '' AND o.order_status = 4")
    Integer countClosedTrainByNoticeId(@Param("noticeId") String noticeId);

    @Select("SELECT COUNT(DISTINCT r.train_no) FROM bu_production_notice_order_rel r " +
            "LEFT JOIN bu_work_order o ON o.id = r.order_id " +
            "WHERE r.notice_id = #{noticeId} AND r.del_flag = 0 AND r.train_no IS NOT NULL AND r.train_no <> '' " +
            "AND o.id IS NOT NULL AND o.order_status <> 9")
    Integer countBoundTrainByNoticeId(@Param("noticeId") String noticeId);

    @Select("SELECT r.train_no AS trainNo, COUNT(DISTINCT CASE WHEN o.id IS NOT NULL AND o.order_status <> 9 THEN r.order_id END) AS totalOrders, " +
            "SUM(CASE WHEN o.order_status = 4 THEN 1 ELSE 0 END) AS completedOrders, MAX(o.act_finish) AS lastFinishTime " +
            "FROM bu_production_notice_order_rel r " +
            "LEFT JOIN bu_work_order o ON o.id = r.order_id " +
            "WHERE r.notice_id = #{noticeId} AND r.del_flag = 0 " +
            "GROUP BY r.train_no " +
            "ORDER BY r.train_no")
    List<BuProductionNoticeProgressDetailVO> listProgressDetails(@Param("noticeId") String noticeId);

    @Select("SELECT r.train_no AS trainNo, r.order_id AS orderId, r.order_code AS orderCode, " +
            "f.form_inst_id AS formInstId, COALESCE(wr.code, wr_inst.code) AS formCode, COALESCE(wr.title, wr_inst.title) AS formTitle, " +
            "COALESCE(pl.status, 0) AS fillStatus, pl.check_result AS checkResult, pl.check_date AS checkDate " +
            "FROM bu_production_notice_order_rel r " +
            "LEFT JOIN bu_work_order o ON o.id = r.order_id " +
            "LEFT JOIN bu_work_order_task_form_inst f ON f.work_order_id = r.order_id AND f.inst_type = 3 " +
            "LEFT JOIN bu_pl_work_record pl ON pl.id = f.form_inst_id " +
            "LEFT JOIN bu_work_record wr ON wr.id = pl.form_obj_id " +
            "LEFT JOIN bu_work_record wr_inst ON wr_inst.id = f.form_inst_id " +
            "WHERE r.notice_id = #{noticeId} AND r.del_flag = 0 AND o.id IS NOT NULL AND o.order_status <> 9 " +
            "ORDER BY r.train_no, r.order_code, f.form_inst_id")
    List<BuProductionNoticeFormProgressVO> listFormProgress(@Param("noticeId") String noticeId);
}
