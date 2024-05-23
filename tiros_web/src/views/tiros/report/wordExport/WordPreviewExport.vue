<template>
  <div>
    <div v-if="visible" class="warp_box">
      <div
        class="openOnBox"
        @click="openOnBoxEvent"
        :style="{ left: `${anchorBoxWidth}px` }"
      >
        <a-icon  v-if="anchorBoxWidth == 10" type="right" />
        <a-icon v-else type="left" />
      </div>
      <!-- 锚点 -->
      <div class="anchorBoxWarp" :style="`width: ${anchorBoxWidth}px`">
        <a-anchor
          ref="anchorDom"
          @click="anchorEvent"
          :targetOffset="30"
          showInkInFixed
          :getContainer="() => $refs.getContainer"
          :wrapperStyle="{
            padding: '20px',
            maxHeight: 'calc(100vh - 216px)',
            marginLeft: '20px',
          }"
        >
          <a-anchor-link
            href="#1"
            :title="`架大修车间 ${allData.getSummaryDetail.lineId} 号线第 ${allData.getSummaryDetail.repairIndex} 列车(${allData.getSummaryDetail.trainNo})架修总结`"
          >
            <a-anchor-link href="#1-1" title="一、 总体情况"> </a-anchor-link>
            <a-anchor-link href="#1-2" title="二、 具体生产情况">
              <!--  -->
              <a-anchor-link href="#1-2-1" title="1、生产进度控制情况">
                <a-anchor-link href="#1-2-1-1" title="1) 本列车生产进度控制情况" />
                <a-anchor-link href="#1-2-1-2" title="2) 近两列车生产任务进度情况" />
              </a-anchor-link>
              <!--  -->
              <a-anchor-link href="#1-2-2" title="2、架修周期控制情况">
                <a-anchor-link href="#1-2-2-1" title="1) 本列车架修周期情况" />
                <a-anchor-link
                  href="#1-2-2-2"
                  :title="`2) ${allData.getSummaryDetail.lineId} 号线架修列车架修周期趋势`"
                />
              </a-anchor-link>
              <!--  -->
              <a-anchor-link href="#1-2-3" title="3、架修成本情况">
                <a-anchor-link href="#1-2-3-1" title="1) 本列车架修成本情况">
                  <a-anchor-link href="#1-2-3-1-1" title="① 各系统物资消耗">
                  </a-anchor-link>
                  <a-anchor-link href="#1-2-3-1-2" title="② 物资主要消耗占比">
                  </a-anchor-link>
                </a-anchor-link>
                <a-anchor-link
                  href="#1-2-3-2"
                  :title="`2) ${allData.getSummaryDetail.lineId} 号线所有列车架修成本情况`"
                />
              </a-anchor-link>
              <!--  -->
              <a-anchor-link href="#1-2-4" title="4、架修质量控制情况">
                <a-anchor-link href="#1-2-4-1" title="1) 本列车实际架修工作日内质量情况">
                  <a-anchor-link href="#1-2-4-1-1" title="① 各系统架修故障情况" />
                  <a-anchor-link href="#1-2-4-1-2" title="② 主要架修故障汇总" />
                </a-anchor-link>
                <a-anchor-link
                  href="#1-2-4-2"
                  :title="`2) ${allData.getSummaryDetail.lineId} 号线所有架修列车惯性故障汇总`"
                />
                <a-anchor-link
                  href="#1-2-4-3"
                  :title="`3) ${allData.getSummaryDetail.lineId} 号线所有架修列车实际架修周期内质量情况`"
                />
                <a-anchor-link href="#1-2-4-4" title="4) 质保期故障情况">
                  <a-anchor-link href="#1-2-4-4-1" title="① 总体质保期故障情况">
                  </a-anchor-link>
                  <a-anchor-link href="#1-2-4-4-2" title="② 本列车架修期内重要质保期故障">
                  </a-anchor-link>
                </a-anchor-link>
              </a-anchor-link>
              <!--  -->
              <a-anchor-link href="#1-2-5" title="5、委外维修部件维修进度控制情况">
                <a-anchor-link href="#1-2-5-1" title="1) 近三列车委外部件送修周期情况" />
                <a-anchor-link href="#1-2-5-2" title="2) 委外部件验收问题" />
              </a-anchor-link>
              <!--  -->
              <a-anchor-link href="#1-2-6" title="6、架修作业工时控制情况">
                <a-anchor-link href="#1-2-6-1" title="1) 本列车架修作业工时控制情况" />
                <a-anchor-link href="#1-2-6-2" title="2) 近三列车工班作业工时情况" />
                <a-anchor-link
                  href="#1-2-6-3"
                  :title="`3) ${allData.getSummaryDetail.lineId} 号线所有列车架修作业工时情况`"
                />
              </a-anchor-link>
              <!--  -->
              <a-anchor-link
                href="#1-2-7"
                title="7、本列车架修过程中存在的问题及处理措施"
              ></a-anchor-link>
              <!--  -->
            </a-anchor-link>
          </a-anchor-link>
        </a-anchor>
      </div>
      <!-- 界面 -->
      <div
        ref="getContainer"
        class="contentWarpBox"
        :style="`width :calc(100% - ${
          anchorWidth + 30
        }px);margin-left:${anchorBoxWidth}px;`"
      >
        <div class="contentBox">
          <div id="word_demo">
            <!-- 1 -->
            <div>
              <div id="1" style="text-align: right; margin-bottom: 20px">
                <img
                  width="150px"
                  height="50px"
                  style="width: 150px; height: auto"
                  :src="images.img3"
                  alt=""
                />
              </div>
              <h1
                style="
                  text-align: center;
                  line-height: 40px;
                  margin-bottom: 0;
                  font-size: 36px;
                  color: #2f6eba;
                "
              >
                <img
                  :src="images.img1"
                  style="margin-bottom: 5px; width: 68px; height: 40px"
                  width="34"
                  height="20"
                  alt=""
                />
                架大修车间 {{ allData.getSummaryDetail.lineId }} 号线第
                {{ allData.getSummaryDetail.repairIndex }} 列车（{{
                  allData.getSummaryDetail.trainNo
                }}）架修总结
              </h1>
              <hr />
              <div style="text-align: center">
                <br />
                <br />
                <br />
                <img
                  :src="images.img2"
                  style="width: 90%; height: auto"
                  width="498px"
                  height="465px"
                  alt=""
                />
                <br />
                <br />
                <br />
                <p style="font-size: 28px; font-weight: bold">
                  {{ getTitleDate(allData.getSummaryDetail.actualDeliverTime) }}
                </p>
                <br />
                <br />
                <br />
              </div>
            </div>
            <!-- 2 -->
            <div>
              <h2 id="1-1">一、 总体情况</h2>
              <p>
                &nbsp;&nbsp;{{ allData.getSummaryDetail.lineId }} 号线第
                {{ allData.getSummaryDetail.repairIndex }} 列
                {{ allData.getSummaryDetail.trainNo }} 车计划
                {{ dateToText(allData.getSummaryDetail.receiveTime) }} 日接车，{{
                  dateToText(allData.getSummaryDetail.deliverTime)
                }}临时交车，实际
                {{ dateToText(allData.getSummaryDetail.actualReceiveTime) }}接车，{{
                  dateToText(allData.getSummaryDetail.actualDeliverTime)
                }}临时交车。本列车提前
                {{ allData.getSummaryDetail.receiveEarlierDays }} 个工作日接车,提前
                {{ allData.getSummaryDetail.deliverEarlierDays }}
                个工作日临时移交检修车间。实际架修
                {{ allData.getSummaryDetail.repairDays }} 个工作日【注 1】，架修周期
                {{ allData.getSummaryDetail.periodDays }}
                个工作日【注 2】。实际架修工作日与架修周期保持一致。
              </p>
              <p>【注 1】实际架修工作日:从接车到完成试车线动调所需天数</p>
              <p>
                【注 2】架修周期:由于试车线动调计划的不确定性，动调时间按计 划的 3
                天进行计算，即架修周期=接车至完成 3 天动调的时间。
              </p>
              <h2 id="1-2">二、 具体生产情况</h2>
              <h3 id="1-2-1">1、 生产进度控制情况</h3>
              <h4 id="1-2-1-1">1) 本列车生产进度控制情况</h4>
              <p>
                &nbsp;&nbsp;架修列车生产进度控制主要分为三大块:一是列车分解前作业，
                主要依次进行列车镟轮、预检、吹扫、车顶设备拆装，按计划完成;
                二是各系统拆装及检修作业,主要同步开展车上及车下设备拆装及检
                修，按计划完成;三是列车调试作业，主要依次进行单车称重、静调、
                淋雨、试车线动调，按计划完成。
              </p>
              <br />
              <img
                width="554px"
                height="341px"
                style="width: 100%; height: auto"
                :src="getProgressImg"
                alt=""
              />
              <br />
              <br />
            </div>
            <!-- 3 -->
            <div>
              <h4 id="1-2-1-2">2) 近两列车生产任务进度情况</h4>
              <div v-if="allData.getSummaryProgress.lastTwo.length">
                <p v-for="(v, k) in allData.getSummaryProgress.lastTwo" :key="k">
                  &nbsp;&nbsp;第 {{ v.repairIndex }} 列
                  {{ v.trainNo }} 车:开始日期比原计划提前
                  {{ v.startTimeEarlierDays }} 个自然日进行，试车线动态调试提前
                  {{ v.endTimeEarlierDays }} 个自然日完成。
                </p>
                <br />
                <!-- 表格 -->
                <table
                  border="1"
                  cellpadding="5"
                  cellspacing="0"
                  style="
                    width: 100%;
                    text-align: center;
                    word-wrap: break-word;
                    word-break: break-all;
                  "
                >
                  <tr>
                    <td colspan="2">
                      {{ dateToText(allData.getSummaryDetail.actualReceiveTime, 2) }}年
                    </td>
                    <td>维修进度</td>
                    <td>完成情况</td>
                  </tr>
                  <!--  -->
                  <tbody v-for="(v, k) in allData.getSummaryProgress.lastTwo" :key="k">
                    <tr>
                      <td style="width: 15%" rowspan="2">
                        {{ v.lineId }}号线第{{ v.repairIndex }}列
                      </td>
                      <td style="width: 15%" rowspan="2">{{ v.trainNo }}车</td>
                      <td align="left">{{ progressText(v.periodDays, 1) }}</td>
                      <!-- □□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□ -->
                      <td style="width: 15%" rowspan="2">
                        {{ v.progressStatus == 1 ? "已完成" : "进行中" }}
                      </td>
                    </tr>
                    <tr>
                      <td align="right">
                        {{ progressText(v.repairDays, 2, v.progressStatus == 1) }}
                      </td>
                      <!-- ●◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆● -->
                    </tr>
                  </tbody>
                </table>
              </div>
              <p v-else>无。</p>
              <br />
              <h3 id="1-2-2">2、 架修周期控制情况</h3>
              <h4 id="1-2-2-1">
                1) 本列车架修周期情况
                {{ allData.getSummaryPeriod.currentPeriodDay.trainNo }} 车计划架修周期
                {{ allData.getSummaryPeriod.currentPeriodDay.plannedDays }}
                个工作日，实际架修周期
                {{ allData.getSummaryPeriod.currentPeriodDay.actualDays }} 个工作日。
              </h4>
              <h4 id="1-2-2-2">
                2) {{ allData.getSummaryDetail.lineId }} 号线架修列车架修周期趋势
              </h4>
              <br />
              <img
                width="554px"
                height="341px"
                style="width: 100%; height: auto"
                :src="getPeriodImg"
                alt=""
              />
              <br />
            </div>
            <!-- 4 -->
            <div style="margin-top: 40px">
              <h3 id="1-2-3">3、 架修成本情况</h3>
              <h4 id="1-2-3-1">1) 本列车架修成本情况</h4>
              <h5 id="1-2-3-1-1">① 各系统物资消耗</h5>
              <br />
              <img
                width="554px"
                height="256px"
                style="width: 100%; height: auto"
                :src="getConsumeImg"
                alt=""
              />
              <div style="margin-top: 20px"></div>
              <br />
              <h5 id="1-2-3-1-2">② 物资主要消耗占比</h5>
              <br />
              <img
                width="554px"
                height="302px"
                style="width: 100%; height: auto"
                :src="getMaterialsImg"
                alt=""
              />
              <br />
              <h4 id="1-2-3-2">
                2) {{ allData.getSummaryDetail.lineId }} 号线所有列车架修成本情况
              </h4>
              <p>
                &nbsp;&nbsp;架修生产过程中，车间实际物资消耗及委外维修费用的总额，截
                止目前为止，{{ allData.getSummaryDetail.lineId }}
                号线平均每列车的架修成本约为
                {{ allData.getSummaryCost.line.trainAverage }} 万/列(约
                {{ allData.getSummaryCost.line.carAverage }} 万/节)。
              </p>
              <br />
              <img
                width="554px"
                height="461px"
                style="width: 100%; height: auto"
                :src="getCostingImg"
                alt=""
              />
              <br />
              <div style="margin-top: 20px"></div>
              <p>&nbsp;&nbsp; {{ allData.getSummaryCost.line.currentLastCompareInfo }}</p>
              <!-- 暂时无数据 -->
              <!-- <p>&nbsp;&nbsp;备品备件成本增加原因:</p>
              <p>&nbsp;&nbsp;1、必换件成本基本持平。</p>
              <p>
                &nbsp;&nbsp;2、故障件中，0208 车大金额故障件主要有锥型橡胶弹簧 11 个，
                含税总价 1.96 万元;2 号线橡胶物资报废消耗，含税总价 2.11 万元;
                灭弧器备件若干，含税总价 6.45 万元;箱体内电阻、过滤网等备件 若干，含税总价
                3.44 万元。0206 车大金额故障件主要有连杆组成 6 个，含税总价 1.62 万元;轮对
                1 条，含税总价 2.79 万元;锥型橡胶 弹簧 18 个，含税总价 3.49 万元;风扇 10
                个，含税总价 1.54 万元; 弹簧支撑及上支承座若干，含税总价 1.23
                万元;贯通道备件若干， 含税总价 0.68 万元;司控器备件若干，含税总价 0.61
                万元。
              </p>
              <p>&nbsp;&nbsp;耗材成本减少原因:</p>
              <p>
                &nbsp;&nbsp;0208 车消耗超声波探头和连接器若干，含税总价 2.85 万元;0206
                车消耗涡流探伤头 1 个，含税总价 1.44 万元。
              </p> -->
            </div>
            <!-- 5 -->
            <div style="margin-top: 40px">
              <h3 id="1-2-4">4、 架修质量控制情况</h3>
              <h4 id="1-2-4-1">1) 本列车实际架修工作日内质量情况</h4>
              <p>
                &nbsp;&nbsp;{{ allData.getSummaryDetail.trainNo }} 车架修共发现故障
                {{ allData.getSummaryFault.current.total }} 项，其中 A/B 类故障
                {{ allData.getSummaryFault.current.levelAB }} 项，已处理
                {{ allData.getSummaryFault.current.handled }}
                项，未处理 {{ allData.getSummaryFault.current.unhandled }} 项，跟踪
                {{ allData.getSummaryFault.current.tracking }} 项。
              </p>
              <h5 id="1-2-4-1-1">① 各系统架修故障情况</h5>
              <br />
              <img
                width="554px"
                height="230px"
                style="width: 100%; height: auto"
                :src="getFaultImg"
                alt=""
              />
              <br />
              <div style="margin-top: 20px"></div>
              <h5 id="1-2-4-1-2">② 主要架修故障汇总</h5>
            </div>
            <!-- 6 -->
            <div>
              <br />
              <!-- 表格 -->
              <table
                border="1"
                cellpadding="5"
                cellspacing="0"
                style="
                  width: 100%;
                  text-align: center;
                  word-wrap: break-word;
                  word-break: break-all;
                "
              >
                <tr>
                  <th style="width: 15%">故障日期</th>
                  <th style="width: 15%">故障系统</th>
                  <th>故障情况</th>
                  <th style="width: 30%">故障原因及后续处理</th>
                </tr>
                <tr
                  v-for="(item, k) in allData.getSummaryFault.current.importantFaultList"
                  :key="k"
                >
                  <td>{{ item.happenTime }}</td>
                  <td>{{ item.systemName }}</td>
                  <td>{{ item.faultConditions }}</td>
                  <td>{{ item.solutionDesc }}</td>
                </tr>
                <tr v-if="!allData.getSummaryFault.current.importantFaultList.length">
                  <td colspan="4">暂无</td>
                </tr>
              </table>
              <br />
              <h4 id="1-2-4-2">
                2) {{ allData.getSummaryDetail.lineId }} 号线所有架修列车惯性故障汇总
              </h4>
              <p style="text-align: right">涉及比例:涉及数量/已架修列车数量</p>
              <!-- 表格 -->
              <table
                border="1"
                cellpadding="5"
                cellspacing="0"
                style="
                  width: 100%;
                  text-align: center;
                  word-wrap: break-word;
                  word-break: break-all;
                "
              >
                <tr>
                  <th style="width: 15%">故障描述</th>
                  <th style="width: 15%">故障日期</th>
                  <th style="width: 10%">车号</th>
                  <th>故障情况</th>
                  <th style="width: 15%">故障原因及后续处理</th>
                  <th style="width: 15%">涉及内部件数量</th>
                  <th style="width: 8%">涉及比例</th>
                </tr>
                <tbody v-for="(v, k) in allData.getSummaryFault.lineOftenList" :key="k">
                  <tr v-for="(item, index) in v.faultList" :key="index">
                    <td v-if="index == 0" :rowspan="v.faultList.length">
                      {{ v.reasonDesc }}
                    </td>
                    <td>{{ item.happenTime }}</td>
                    <td>{{ item.trainNo }}</td>
                    <td>{{ item.faultConditions }}</td>
                    <td v-if="index == 0" :rowspan="v.faultList.length">
                      {{ v.solutionDesc }}
                    </td>
                    <td>{{ item.faultAssetCount }}</td>
                    <td v-if="index == 0" :rowspan="v.faultList.length">
                      {{ v.faultAssetPercent }}
                    </td>
                  </tr>
                </tbody>
              </table>
              <br />
              <h4 id="1-2-4-3">
                3)
                {{ allData.getSummaryDetail.lineId }}
                号线所有架修列车实际架修周期内质量情况
              </h4>
              <br />
              <img
                width="554px"
                height="258px"
                style="width: 100%; height: auto"
                :src="getFaultNum"
                alt=""
              />
              <br />
            </div>
            <!-- 7 -->
            <div style="margin-top: 40px">
              <h4 id="1-2-4-4">4) 质保期故障情况</h4>
              <h5 id="1-2-4-4-1">① 总体质保期故障情况</h5>
              <br />
              <img
                width="554px"
                height="230px"
                style="width: 100%; height: auto"
                :src="getGuaranteeFaultImg"
                alt=""
              />
              <br />
              <div style="margin-top: 20px"></div>
              <h5 id="1-2-4-4-2">② 本列车架修期内重要质保期故障</h5>
              <br />
              <!-- 表格 -->
              <table
                border="1"
                cellpadding="5"
                cellspacing="0"
                style="
                  width: 100%;
                  text-align: center;
                  word-wrap: break-word;
                  word-break: break-all;
                "
              >
                <tr>
                  <th style="width: 15%">故障日期</th>
                  <th style="width: 15%">故障系统</th>
                  <th>故障情况</th>
                  <th style="width: 30%">故障原因及后续处理</th>
                </tr>
                <tr
                  v-for="(item, k) in allData.getSummaryFault.warranty.importantFaultList"
                  :key="k"
                >
                  <td>{{ item.happenTime }}</td>
                  <td>{{ item.systemName }}</td>
                  <td>{{ item.faultConditions }}</td>
                  <td>{{ item.solutionDesc }}</td>
                </tr>
                <tr v-if="!allData.getSummaryFault.warranty.importantFaultList.length">
                  <td colspan="4">暂无</td>
                </tr>
              </table>
              <br />
              <h3 id="1-2-5">5、 委外维修部件维修进度控制情况</h3>
              <h4 id="1-2-5-1">1) 近三列车委外部件送修周期情况</h4>
              <br />
              <img
                width="554px"
                height="323px"
                style="width: 100%; height: auto"
                :src="getOutMaintainImg"
                alt=""
              />
              <br />
              <div style="margin-top: 20px"></div>
              <!-- 表格 -->
              <table
                border="1"
                cellpadding="5"
                cellspacing="0"
                style="
                  width: 100%;
                  text-align: center;
                  word-wrap: break-word;
                  word-break: break-all;
                "
              >
                <tr>
                  <th style="width: 10%">列车号</th>
                  <th style="width: 15%">项目</th>
                  <th style="width: 15%">送件日期</th>
                  <th style="width: 15%">返回日期</th>
                  <th style="width: 10%">延误天数</th>
                  <th style="width: 15%">是否影响架修进度</th>
                  <th>备注</th>
                </tr>
                <tbody
                  v-for="(v, k) in allData.getSummaryOutsource.trainItemDetailList"
                  :key="k"
                >
                  <tr v-for="(item, index) in v.itemList" :key="index">
                    <td v-if="index == 0" :rowspan="v.itemList.length">
                      {{ `第${v.repairIndex}列${v.trainNo}车` }}
                    </td>
                    <td>{{ item.itemName }}</td>
                    <td>{{ item.outDate }}</td>
                    <td>{{ item.inDate }}</td>
                    <td>{{ item.delayDays }}</td>
                    <td>{{ item.affectProgress_dictText }}</td>
                    <td>{{ item.remark }}</td>
                  </tr>
                </tbody>
              </table>
              <br />
            </div>
            <!-- 8 -->
            <div>
              <h4 id="1-2-5-2">2) 委外部件验收问题</h4>
              <p>
                &nbsp;&nbsp;截至
                {{ dateToText(allData.getSummaryOutsource.inProblemInfo.outDate) }}，{{
                  allData.getSummaryOutsource.inProblemInfo.itemCount
                }}
                个委外项目验收过程中累计发现问题
                {{ allData.getSummaryOutsource.inProblemInfo.total }} 项，已完成处理
                {{ allData.getSummaryOutsource.inProblemInfo.handled }} 项。
              </p>
              <br />
              <img
                width="554px"
                height="323px"
                style="width: 100%; height: auto"
                :src="getProblemImg"
                alt=""
              />
              <br />
              <div style="margin-top: 20px"></div>
              <h3 id="1-2-6">6、 架修作业工时控制情况</h3>
              <h4 id="1-2-6-1">1) 本列车架修作业工时控制情况</h4>
              <p>
                &nbsp;&nbsp;第 {{ allData.getSummaryWorkTime.current.repairIndex }} 列
                {{ allData.getSummaryWorkTime.current.trainNo }} 车实际架修
                {{ allData.getSummaryWorkTime.current.workDays }}
                个工作日,累计消耗工时为
                {{ allData.getSummaryWorkTime.current.workHours }}
                人*小时，各工班工时消耗情况如下:
              </p>
              <br />
              <img
                width="554px"
                height="323px"
                style="width: 100%; height: auto"
                :src="getWorkloadImg"
                alt=""
              />
              <br />
              <div style="margin-top: 20px"></div>
              <!-- 表格 -->
              <table
                border="1"
                cellpadding="5"
                cellspacing="0"
                style="
                  width: 100%;
                  text-align: center;
                  word-wrap: break-word;
                  word-break: break-all;
                "
              >
                <tr>
                  <th style="width: 15%">作业工班</th>
                  <th>作业工位</th>
                  <th style="width: 15%">工时</th>
                  <th style="width: 15%">合计工时</th>
                  <th style="width: 15%">比率(%)</th>
                </tr>
                <tbody
                  v-for="(v, k) in allData.getSummaryWorkTime.current.groupHoursList"
                  :key="k"
                >
                  <tr v-for="(item, index) in v.workstationHoursList" :key="index">
                    <td v-if="index == 0" :rowspan="v.workstationHoursList.length">
                      {{ v.groupName }}
                    </td>
                    <td>{{ item.workstationName }}</td>
                    <td>{{ item.hours }}</td>
                    <td v-if="index == 0" :rowspan="v.workstationHoursList.length">
                      {{ v.hours }}
                    </td>
                    <td v-if="index == 0" :rowspan="v.workstationHoursList.length">
                      {{ v.percent }} %
                    </td>
                  </tr>
                </tbody>
              </table>
              <br />
              <h4 id="1-2-6-2">2) 近三列车工班作业工时情况</h4>
              <br />
              <img
                width="554px"
                height="323px"
                style="width: 100%; height: auto"
                :src="getGroupWorkTimeImg"
                alt=""
              />
              <br />
              <div style="margin-top: 20px"></div>
              <h4 id="1-2-6-3">
                3) {{ allData.getSummaryDetail.lineId }} 号线所有列车架修作业工时情况
              </h4>
              <br />
              <img
                width="554px"
                height="230px"
                style="width: 100%; height: auto"
                :src="getGroupWorkTimeImg2"
                alt=""
              />
              <br />
              <div style="margin-top: 20px"></div>
              <h3 id="1-2-7">7、本列车架修过程中存在的问题及处理措施</h3>
              <p>无。</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import imgs from "@/views/tiros/report/wordExport/staticImgBase64.js";
import $ from "jquery";
import moment from "moment";
require("@/utils/jquery.wordexport.js");
import {
  chart1,
  chart2,
  chart3,
  chart4,
  chart5,
  chart6,
  chart7,
  chart8,
  chart9,
  chart10,
  chart11,
  chart12,
  chart13,
} from "@/views/tiros/report/wordExport/EchartToImg.js";

export default {
  data() {
    return {
      anchorBoxWidth: "auto",
      anchorWidth: 0,
      images: imgs,
      visible: false,
      getProgressImg: "",
      getPeriodImg: "",
      getConsumeImg: "",
      getMaterialsImg: "",
      getCostingImg: "",
      getFaultImg: "",
      getFaultNum: "",
      getGuaranteeFaultImg: "",
      getOutMaintainImg: "",
      getProblemImg: "",
      getWorkloadImg: "",
      getGroupWorkTimeImg: "",
      getGroupWorkTimeImg2: "",
      allData: {
        getSummaryDetail: {},
        getSummaryCost: {},
        getSummaryFault: {},
        getSummaryWorkTime: {},
        getSummaryPeriod: {},
        getSummaryOutsource: {},
        getSummaryProgress: {},
      },
    };
  },
  methods: {
    openOnBoxEvent() {
      if (this.anchorBoxWidth == 10) {
        this.anchorBoxWidth = JSON.parse(JSON.stringify(this.anchorWidth));
      } else {
        this.anchorBoxWidth = 10;
      }
    },
    anchorEvent(e) {
      e.preventDefault();
    },
    exportWord() {
      $("#word_demo").wordExport(`架大修车间 ${this.allData.getSummaryDetail.lineId} 号线第 ${this.allData.getSummaryDetail.repairIndex} 列车(${this.allData.getSummaryDetail.trainNo})架修总结`);
    },
    show(data) {
      Object.assign(this.allData, data);
      this.visible = true;
      // console.log(this.allData);
      this.$nextTick(() => {
        this.anchorWidth = this.$refs.anchorDom.$el.offsetWidth;
        this.anchorBoxWidth = this.$refs.anchorDom.$el.offsetWidth;
      });
      this.getProgress(this.allData);
    },
    dateToText(date, type = 1) {
      if (type == 1) {
        return moment(date).format(" YYYY 年 MM 月 DD 日");
      } else if (type == 2) {
        return moment(date).year();
      }
    },
    progressText(num, type, progressStatus) {
      console.log(num, type, progressStatus);
      let text = "";
      for (let i = 0; i < num; i++) {
        if (type == 1) {
          text += "□";
        } else {
          if (i == 0 || (i == num - 1 && progressStatus)) {
            text += "●";
          } else {
            text += "◆";
          }
        }
      }
      return text;
    },
    getTitleDate(data) {
      if (!data) {
        return "暂无";
      }
      const obj = {
        0: "〇",
        1: "一",
        2: "二",
        3: "三",
        4: "四",
        5: "五",
        6: "六",
        7: "七",
        8: "八",
        9: "九",
      };
      let text = "";
      const date = moment(data);
      // 年
      let year = date.year().toString().split("");
      for (let i = 0, len = year.length; i < len; i++) {
        year[i] = obj[year[i]];
      }
      year = year.join("");
      text += year + "年";
      // 月
      let _month = date.month() + 1;
      let month = getNumberToString(_month);
      text += month + "月";
      // 日
      let _day = date.date();
      let day = getNumberToString(_day);
      text += day + "日";
      //
      function getNumberToString(item) {
        let result = "";
        if (item < 10) {
          result = obj[item];
        } else if (item == 10) {
          result = "十";
        } else if (item == 20) {
          result = "二十";
        } else if (item == 30) {
          result = "三十";
        } else {
          const arr = item.toString().split("");
          for (let i = 0, len = arr.length; i < len; i++) {
            arr[i] = obj[arr[i]];
          }
          if (item > 10 && item < 20) {
            result = "十" + arr[1];
          } else {
            result = arr.join("十");
          }
        }
        return result;
      }

      return text;
    },
    // 获取架修进度
    getProgress(data) {
      this.getProgressImg = chart1(data);
      this.getPeriodImg = chart2(data);
      this.getConsumeImg = chart3(data);
      this.getMaterialsImg = chart4(data);
      this.getCostingImg = chart5(data);
      this.getFaultImg = chart6(data);
      this.getFaultNum = chart7(data);
      this.getGuaranteeFaultImg = chart8(data);
      this.getOutMaintainImg = chart9(data);
      this.getProblemImg = chart10(data);
      this.getWorkloadImg = chart11(data);
      this.getGroupWorkTimeImg = chart12(data);
      this.getGroupWorkTimeImg2 = chart13(data);
    },
  },
};
</script>

<style lang="less" scoped>
/deep/.ant-anchor-link-title {
  font-size: 10px;
}
// .buttonPosition {
//   position: absolute;
//   top: 8px;
//   right: 50px;
// }

@transition: 150ms;

.warp_box {
  overflow: auto;
  display: flex;
  height: calc(100vh - 176px);
  flex-flow: row nowrap;
  justify-content: center;
  background: #f3f3f3;
  // padding: 20px 0;
  box-sizing: border-box;
  border: 20px solid #f3f3f3;
  border-right: 0;
  border-left: 0;
  .contentWarpBox {
    transition: all linear @transition;
    overflow: auto;
    padding-right: 20px;
    .contentBox {
      margin-left: 30px;
      background: #fff;
      // width: 280mm;
      // flex: 1;
      min-height: calc(100vh - 107px);
      padding: 1cm 1cm;
      position: relative;
    }
  }
  .anchorBoxWarp {
    overflow: hidden;
    position: absolute;
    left: 0;
    transition: all linear @transition;
  }
  .openOnBox {
    transition: all linear @transition;
    width: 30px;
    height: 60px;
    background: #fff;
    position: absolute;
    top: 0;
    bottom: 0;
    margin: auto;
    display: flex;
    align-items: center;
    justify-content: center;
    border-top-right-radius: 8px;
    border-bottom-right-radius: 8px;
    font-size:18px;
    color: #c1c1c1;
  }
}
</style>
