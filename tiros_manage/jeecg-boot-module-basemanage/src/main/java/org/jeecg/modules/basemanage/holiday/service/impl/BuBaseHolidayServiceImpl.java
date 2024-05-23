package org.jeecg.modules.basemanage.holiday.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.basemanage.holiday.entity.BuBaseHoliday;
import org.jeecg.modules.basemanage.holiday.entity.bo.*;
import org.jeecg.modules.basemanage.holiday.mapper.BuBaseHolidayMapper;
import org.jeecg.modules.basemanage.holiday.service.IBuBaseHolidayService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 * 节假日信息表，用于存放节假日信息，包括要上班的特殊日期 服务实现类
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-06
 */
@Slf4j
@Service
public class BuBaseHolidayServiceImpl extends ServiceImpl<BuBaseHolidayMapper, BuBaseHoliday> implements IBuBaseHolidayService {

    @Resource
    private BuBaseHolidayMapper buBaseHolidayMapper;


    /**
     * @see IBuBaseHolidayService#saveHoliday(BuBaseHoliday)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveHoliday(BuBaseHoliday buBaseHoliday) throws Exception {
        if (isNameRepeated(buBaseHoliday)) {
            throw new JeecgBootException("名称重复");
        }
        checkDateRepeated(buBaseHoliday);

        countDays(buBaseHoliday);
        buBaseHolidayMapper.insert(buBaseHoliday);

        return true;
    }

    /**
     * @see IBuBaseHolidayService#updateHoliday(BuBaseHoliday)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateHoliday(BuBaseHoliday buBaseHoliday) throws Exception {
        if (isNameRepeated(buBaseHoliday)) {
            throw new JeecgBootException("名称重复");
        }
        checkDateRepeated(buBaseHoliday);

        countDays(buBaseHoliday);
        buBaseHolidayMapper.updateById(buBaseHoliday);

        return true;
    }

    /**
     * @see IBuBaseHolidayService#deleteHolidayBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteHolidayBatch(String ids) throws Exception {
        if (StringUtils.isBlank(ids)) {
            throw new JeecgBootException("请选择要删除的节假日");
        }

        List<String> holidayIdList = Arrays.asList(ids.split(","));
        buBaseHolidayMapper.deleteBatchIds(holidayIdList);

        return true;
    }

    /**
     * @see IBuBaseHolidayService
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveDayByNetWork(String result) {
       /* if (null == year || year < 1900) {
            year = Calendar.getInstance().get(Calendar.YEAR);
        }*/

        // 从网络获取数据
      /*  List<HolidayBO> holidayBOList = queryHolidayFromBaidu(year);
        if (CollectionUtils.isEmpty(holidayBOList)) {
            holidayBOList = queryHolidayFromTimor(year);
        }*/
        Integer year = Calendar.getInstance().get(Calendar.YEAR);
        List<HolidayBO> holidayBOList = queryHolidayFromTimor(result);

        List<BuBaseHoliday> buBaseHolidayList = transformToBuBaseHolidayList(holidayBOList, year);

        // 删除year年数据
        LambdaQueryWrapper<BuBaseHoliday> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(BuBaseHoliday::getYear, year);
        buBaseHolidayMapper.delete(deleteWrapper);

        // 插入网络获取数据
        saveBatch(buBaseHolidayList);

        return true;
    }


    private boolean isNameRepeated(BuBaseHoliday buBaseHoliday) {
        LambdaQueryWrapper<BuBaseHoliday> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BuBaseHoliday::getName, buBaseHoliday.getName())
                .eq(BuBaseHoliday::getYear, buBaseHoliday.getYear());
        List<BuBaseHoliday> holidayList = buBaseHolidayMapper.selectList(wrapper);

        if (CollectionUtils.isEmpty(holidayList)) {
            return false;
        }
        if (StringUtils.isBlank(buBaseHoliday.getId())) {
            return true;
        }
        return !buBaseHoliday.getId().equals(holidayList.get(0).getId());
    }

    private void checkDateRepeated(BuBaseHoliday buBaseHoliday) throws Exception {
        LambdaQueryWrapper<BuBaseHoliday> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BuBaseHoliday::getYear, buBaseHoliday.getYear());
        List<BuBaseHoliday> holidayList = buBaseHolidayMapper.selectList(wrapper);

        Date start = buBaseHoliday.getStartTime();
        Date end = buBaseHoliday.getEnd();
        for (BuBaseHoliday holiday : holidayList) {
            if (!holiday.getId().equals(buBaseHoliday.getId())) {
                boolean startIn = DateUtil.isIn(start, holiday.getStartTime(), holiday.getEnd());
                boolean endIn = DateUtil.isIn(end, holiday.getStartTime(), holiday.getEnd());
                if (startIn || endIn) {
                    throw new JeecgBootException(getDateErrorMessage(holiday));
                }
            }
        }
    }

    private String getDateErrorMessage(BuBaseHoliday holiday) {
        return "日期冲突:“" + holiday.getName() + "” " +
                DateUtils.date_sdf.get().format(holiday.getStartTime()) +
                " 至 " +
                DateUtils.date_sdf.get().format(holiday.getEnd());
    }

    private void countDays(BuBaseHoliday buBaseHoliday) {
        long days = DateUtil.between(buBaseHoliday.getStartTime(), buBaseHoliday.getEnd(), DateUnit.DAY);
        buBaseHoliday.setDays(Math.toIntExact(days) + 1);
    }

    private List<HolidayBO> queryHolidayFromBaidu(int year) {
        String queryUrl = "https://sp0.baidu.com/8aQDcjqpAAV3otqbppnN2DJv/api.php?query=$&resource_id=6018";
        queryUrl = queryUrl.replace("$", String.valueOf(year));
        String result = HttpUtil.get(queryUrl, CharsetUtil.CHARSET_UTF_8);

        BaiduHolidayResponse baiduHolidayResponse = JSON.parseObject(result, BaiduHolidayResponse.class);
        if (!"0".equals(baiduHolidayResponse.getStatus())) {
            throw new JeecgBootException("未能成功获取网络节假日信息");
        }

        Map<Date, HolidayBO> dateHolidayBOMap = new HashMap<>(32);
        List<BaiduHolidayData> baiduHolidayDataList = baiduHolidayResponse.getData();
        if (CollectionUtils.isEmpty(baiduHolidayDataList)) {
            return new ArrayList<>();
        }
        for (BaiduHolidayData baiduHolidayData : baiduHolidayDataList) {
            List<BaiduHoliday> holidayList = baiduHolidayData.getHoliday();
            if (CollectionUtils.isEmpty(holidayList)) {
                continue;
            }
            holidayList.sort(Comparator.comparing(BaiduHoliday::getFestival, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(BaiduHoliday::getName, Comparator.nullsLast(Comparator.naturalOrder())));
            for (BaiduHoliday holiday : holidayList) {
                List<BaiduHolidayDate> dateList = holiday.getList();
                if (CollectionUtils.isEmpty(dateList)) {
                    continue;
                }
                dateList.sort(Comparator.comparing(BaiduHolidayDate::getDate, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(BaiduHolidayDate::getStatus, Comparator.nullsLast(Comparator.naturalOrder())));
                for (BaiduHolidayDate holidayDate : dateList) {
                    Date date = holidayDate.getDate();
                    String status = holidayDate.getStatus();

                    HolidayBO holidayBO = new HolidayBO()
                            .setDate(date)
                            .setName(holiday.getName())
                            .setRemark(holiday.getDesc());
                    if ("1".equals(status)) {
                        holidayBO.setWork(0);
                    } else if ("2".equals(status)) {
                        holidayBO.setWork(1);
                    }

                    dateHolidayBOMap.put(holidayBO.getDate(), holidayBO);
                }

            }
        }

        return new ArrayList<>(new HashSet<>(dateHolidayBOMap.values()));
    }

    private List<HolidayBO> queryHolidayFromTimor(String result) {
        // String queryUrl = "http://timor.tech/api/holiday/year/" + year;
        // String result = HttpUtil.get(queryUrl, CharsetUtil.CHARSET_UTF_8);

        TimorHolidayResponse timorHolidayResponse = JSON.parseObject(result, TimorHolidayResponse.class);
        if (0 != timorHolidayResponse.getCode()) {
            throw new JeecgBootException("未能成功获取网络节假日信息");
        }

        Map<Date, HolidayBO> dateHolidayBOMap = new HashMap<>(32);
        Map<String, TimorHolidayDate> holidayDateMap = timorHolidayResponse.getHoliday();
        if (null == holidayDateMap || holidayDateMap.size() == 0) {
            return new ArrayList<>();
        }
        List<TimorHolidayDate> holidayDateList = new ArrayList<>(holidayDateMap.values());
        holidayDateList.sort(Comparator.comparing(TimorHolidayDate::getDate, Comparator.nullsLast(Comparator.naturalOrder())));

        String currentName = null;
        boolean needChangeCurrentName = false;
        for (TimorHolidayDate holidayDate : holidayDateList) {
            Date date = holidayDate.getDate();
            Boolean holiday = holidayDate.getHoliday();
            String name = holidayDate.getName();
            String target = holidayDate.getTarget();
            Boolean after = holidayDate.getAfter();

            if (StringUtils.isBlank(currentName)) {
                currentName = StringUtils.isNotBlank(target) ? target : name;
            }
            if (needChangeCurrentName) {
                currentName = StringUtils.isNotBlank(target) ? target : name;
                needChangeCurrentName = false;
            }
            if (null != after) {
                if (!after) {
                    currentName = StringUtils.isNotBlank(target) ? target : name;
                    needChangeCurrentName = false;
                } else {
                    needChangeCurrentName = true;
                }
            }

            HolidayBO holidayBO = new HolidayBO()
                    .setDate(date)
                    .setName(currentName)
                    .setRemark(name);
            if (null != holiday && holiday) {
                holidayBO.setWork(0);
            } else {
                holidayBO.setWork(1);
            }

            dateHolidayBOMap.put(holidayBO.getDate(), holidayBO);
        }

        return new ArrayList<>(new HashSet<>(dateHolidayBOMap.values()));
    }

    private List<BuBaseHoliday> transformToBuBaseHolidayList(List<HolidayBO> holidayBOList, int year) {
        holidayBOList.sort(Comparator.comparing(HolidayBO::getDate));

        Map<Date, BuBaseHoliday> endDateHolidayMap = new HashMap<>(16);
        Date latestDate = null;
        Integer latestWork = -1;
        for (HolidayBO holidayBO : holidayBOList) {
            Date date = holidayBO.getDate();
            Integer work = holidayBO.getWork();

            boolean isContinuousDate = isContinuousDate(latestDate, date);
            boolean isSameWork = work.equals(latestWork);
            BuBaseHoliday buBaseHoliday;
            if (isContinuousDate && isSameWork) {
                buBaseHoliday = endDateHolidayMap.get(latestDate);
                buBaseHoliday
                        .setYear(year)
                        .setName(transformBaseHolidayName(holidayBO.getName(), work))
                        .setEnd(date)
                        .setDays(buBaseHoliday.getDays() + 1)
                        .setWork(work);
                if (!buBaseHoliday.getRemark().equals(holidayBO.getRemark())) {
                    buBaseHoliday.setRemark(buBaseHoliday.getRemark() + ";" + holidayBO.getRemark());
                }
            } else {
                buBaseHoliday = new BuBaseHoliday()
                        .setYear(year)
                        .setName(transformBaseHolidayName(holidayBO.getName(), work))
                        .setStartTime(date)
                        .setEnd(date)
                        .setDays(1)
                        .setWork(work)
                        .setRemark(holidayBO.getRemark());
            }
            endDateHolidayMap.put(buBaseHoliday.getEnd(), buBaseHoliday);

            latestDate = date;
            latestWork = work;
        }

        return new ArrayList<>(new HashSet<>(endDateHolidayMap.values()));
    }

    private static boolean isContinuousDate(Date date, Date nextDate) {
        if (null == date || null == nextDate) {
            return false;
        }
        Date addDays = org.apache.commons.lang3.time.DateUtils.addDays(date, 1);
        return org.apache.commons.lang3.time.DateUtils.isSameDay(addDays, nextDate);
    }

    private String transformBaseHolidayName(String name, Integer work) {
        if (null == work || 1 != work) {
            return name;
        }

        return name + "(调休)";
    }

}
