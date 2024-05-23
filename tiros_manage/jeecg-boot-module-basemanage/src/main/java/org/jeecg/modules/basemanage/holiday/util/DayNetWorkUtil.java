package org.jeecg.modules.basemanage.holiday.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.basemanage.holiday.entity.BuBaseHoliday;

import java.util.*;

public class DayNetWorkUtil {

    public static String URLDATE = "https://sp0.baidu.com/8aQDcjqpAAV3otqbppnN2DJv/api.php?query=$&resource_id=6018";

    public static List<BuBaseHoliday> getNetWorkData() {

        int year = DayNetWorkUtil.getCurrentYear();
        URLDATE = URLDATE.replace("$", year + "");
        List<BuBaseHoliday> buBaseHolidayList = new ArrayList<>();
        String result = HttpUtil.get(URLDATE, CharsetUtil.CHARSET_UTF_8);
        JSONObject json = new JSONObject(result);
        JSONArray array = json.getJSONArray("data");
        for (int i = 0; i < array.size(); i++) {
            JSONObject data = array.getJSONObject(i);
            JSONArray holiday = data.getJSONArray("holiday");
            for (int j = 0; j < holiday.size(); j++) {
                BuBaseHoliday buBaseHoliday = new BuBaseHoliday();

                JSONObject h = holiday.getJSONObject(j);
                String desc = h.get("desc").toString();
                String name = h.get("name").toString();
                String dStart = h.get("festival").toString();
                buBaseHoliday.setName(name);
                Date dStartC = DateUtil.parse(dStart, "yyyy-MM-dd");
                buBaseHoliday.setStartTime(dStartC);
                JSONArray de = h.getJSONArray("list");
                int days = 0;
                for (int k = 0; k < de.size(); k++) {
                    JSONObject d = de.getJSONObject(k);
                    String date = d.get("date").toString();
                    String status = d.get("status").toString();
                    if (StringUtils.isNotBlank(status)) {
                        if ("1".equals(status)) {
                            days += 1;
                        } else {
                            BuBaseHoliday work = new BuBaseHoliday();
                            work.setName("调班");
                            Date dateTime = DateUtil.parse(date, "yyyy-MM-dd");
                            work.setStartTime(dateTime);
                            work.setWork(1);
                            work.setEnd(dateTime);
                            work.setDays(1);
                            work.setRemark(name + "假期调休");
                            work.setYear(year);
                            buBaseHolidayList.add(work);
                        }
                    }
                }
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(dStartC);
                calendar.add(Calendar.DATE, days);
                buBaseHoliday.setDays(days);
                buBaseHoliday.setEnd(calendar.getTime());
                buBaseHoliday.setWork(0);
                buBaseHoliday.setYear(year);
                buBaseHolidayList.add(buBaseHoliday);

            }
        }


        return buBaseHolidayList;
    }

    public static Integer getCurrentYear() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        int year = calendar.get(Calendar.YEAR);

        return year;
    }


}
