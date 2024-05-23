<template>
  <div id="myWorkContent" na-flex-height-full>
    <div style="padding-left: 5px; padding-right: 3px" na-flex-height-full>
      <!-- 计划任务 -->
      <a-tabs
        size="small"
        v-model="curTabKey"
        @change="onChangeTab"
        style="height: calc(100% - 0px)"
        v-if="
          selectTask &&
          (selectTask.taskType === 1 ||
            selectTask.taskType === 2 ||
            selectTask.taskType === 3 ||
            selectTask.taskType === 4 ||
            selectTask.taskType === 5 ||
            selectTask.taskType === 6 ||
            selectTask.taskType === 7 ||
            selectTask.taskType === 8)
        "
      >
        <a-tab-pane key="1" tab="作业要求" na-flex-height-full fill-m-45>
          <div style="overflow-y: auto" na-flex-height-full>
            <div
              style="padding-top: 25px; padding-left: 10px; padding-bottom: 15px; text-align: center"
              v-if="!readed && !readOnly"
            >
              <a-button type="dashed" style="border-color: #cc0000; border-width: 2px" @click="onReadClick"
              >我已阅读</a-button
              >
              <span class="readTip"> &lt;- 点我确认已阅读作业要求</span>
            </div>
            <div class="requireBox">
              <div class="requireTitle"><span> 安全预想</span></div>
              <p class="safeContent" v-if="requirement && requirement.safetyExpectation" v-html="requirement.safetyExpectation">
              </p>
            </div>
            <!-- 任务规程 -->
            <div class="requireBox" style="min-height: auto" v-if="regus.length>0">
              <div class="requireTitle"><span style="border-color: #00b8ff"> 任务规程</span></div>
              <TaskRegusView v-if="regus.length > 0" :tableData="regus" style="min-height: 162px"></TaskRegusView>
              <div v-else style="height: 162px"></div>
            </div>
            <!-- 作业指导书 -->
            <div class="requireBox">
              <div class="requireTitle"><span style="border-color: #00b8ff"> 作业指导书</span></div>
              <div ref="bookStepDom" style="padding: 15px"></div>
            </div>
          </div>
        </a-tab-pane>
        <!-- 填报信息 -->
        <a-tab-pane
          key="3"
          tab="作业填报"
          v-if="
            selectTask &&
            (selectTask.taskType === 2 ||
              selectTask.taskType === 6 ||
              selectTask.taskType === 7 ||
              selectTask.taskType === 8)
          "
          :forceRender="true"
          style="padding-top: 10px; padding-left: 5px"
        >
          <!-- 故障信息填报 -->
          <BreakdownFormItem
            v-if="selectTask && selectTask.taskType === 2"
            ref="breakdownForm"
            :from-write-report="true"
            @ok="onFaultSaveOk"
            @fail="onFaultSaveFail"
            @cancel="onFaultSaveCancel"
          ></BreakdownFormItem>
          <!-- 委外出入场单填报 -->
          <div
            v-if="selectTask && selectTask.outTask !== 0 && (selectTask.taskType === 6 || selectTask.taskType === 7)"
            style="padding: 10px; height: 100%; overflow-y: auto"
          >
            <div v-if="selectTask && selectTask.outTask === 1" style="height: calc(100% - 15px)">
              <!--  委外送修 -->
              <div class="fieldset-wrapper">
                <h4 class="title">交接信息</h4>
                <a-form-model
                  :disabled="selectTask && selectTask.taskStatus !== 2"
                  ref="outInfoForm"
                  layout="inline"
                  class="work-form"
                  :model="outModelInfo"
                  :label-col="{ span: 6 }"
                  :wrapper-col="{ span: 18 }"
                  :rules="outRules"
                >
                  <a-row>
                    <a-col :span="11">
                      <a-form-model-item label="交接单号" :wrapper-col="{ span: 18 }" prop="billNo">
                        <a-input placeholder="交接单号" v-model="outModelInfo.billNo" disabled />
                      </a-form-model-item>
                    </a-col>
                    <a-col :span="11">
                      <a-form-model-item label="交接单名" :wrapper-col="{ span: 18 }" prop="outinName">
                        <a-input placeholder="交接单名" v-model="outModelInfo.outinName" />
                      </a-form-model-item>
                    </a-col>
                    <a-col :span="11">
                      <a-form-model-item label="线路" :wrapper-col="{ span: 18 }" prop="lineId">
                        <j-dict-select-tag
                          :triggerChange="true"
                          ref="lineSelect"
                          v-model="outModelInfo.lineId"
                          dictCode="bu_mtr_line,line_name,line_id"
                          @change="onLineChange"
                        />
                      </a-form-model-item>
                    </a-col>
                    <a-col :span="11">
                      <a-form-model-item label="车号" :wrapper-col="{ span: 18 }" prop="trainNo">
                        <j-dict-select-seach-tag
                          :triggerChange="true"
                          v-model="outModelInfo.trainNo"
                          :dictCode="dictTrainStr"
                          @select="onTrainSelect"
                        />
                      </a-form-model-item>
                    </a-col>
                    <a-col :span="11">
                      <a-form-model-item :key="oTransferDateMKey" label="交接日期" :wrapper-col="{ span: 18 }" prop="transferDateM">
                        <a-date-picker
                          style="width: 100%"
                          placeholder="交接日期"
                          v-model="outModelInfo.transferDateM"
                          @change="otransFerDateMkey"
                        ></a-date-picker>
                      </a-form-model-item>
                    </a-col>
                    <a-col :span="11">
                      <a-form-model-item label="工程师" :wrapper-col="{ span: 18 }" prop="engineerId">
                        <div @click="showUserModal(2)">
                          <a-select
                            ref="userSelect2"
                            v-model="outModelInfo.engineerName"
                            placeholder="请选择人员"
                            :open="false"
                          >
                            <a-icon slot="suffixIcon" type="ellipsis" />
                          </a-select>
                        </div>
                      </a-form-model-item>
                    </a-col>
                    <a-col :span="11">
                      <a-form-model-item label="作业班组" :wrapper-col="{ span: 18 }" prop="sendGroupId">
                        <j-dict-select-tag
                          :triggerChange="true"
                          v-model="outModelInfo.sendGroupId"
                          :dictCode="dictGroupStr"
                        />
                      </a-form-model-item>
                    </a-col>
                    <a-col :span="11">
                      <a-form-model-item label="移交人" :wrapper-col="{ span: 18 }" prop="transferUserId">
                        <div @click="showUserModal(1)">
                          <a-select
                            ref="userSelect1"
                            v-model="outModelInfo.transferUserName"
                            placeholder="请选择人员"
                            :open="false"
                          >
                            <a-icon slot="suffixIcon" type="ellipsis" />
                          </a-select>
                        </div>
                      </a-form-model-item>
                    </a-col>
                    <a-col :span="11">
                      <a-form-model-item label="接收厂商" :wrapper-col="{ span: 18 }" prop="supplierId">
                        <!--                          <j-dict-select-tag
                          :triggerChange="true"
                          v-model="outModelInfo.supplierId"
                          dictCode="bu_base_supplier,name,id"
                        />-->
                        <a-select
                          v-model="outModelInfo.supplierName"
                          placeholder="请选择厂商"
                          :open="false"
                          :showArrow="true"
                          @focus="openSupplierModal(1)"
                          ref="mySupplierSelectOut"
                        >
                          <a-icon slot="suffixIcon" type="ellipsis" />
                        </a-select>
                      </a-form-model-item>
                    </a-col>
                    <a-col :span="11">
                      <a-form-model-item label="接收人员" :wrapper-col="{ span: 18 }" prop="receiveUser">
                        <a-input placeholder="接收人员" v-model="outModelInfo.receiveUser" />
                      </a-form-model-item>
                    </a-col>
                    <a-col :span="11">
                      <a-form-model-item label="所属合同" :wrapper-col="{ span: 18 }" prop="contractId">
                        <j-dict-select-tag
                          :trigger-change="true"
                          v-model="outModelInfo.contractId"
                          dictCode="bu_contract_info,contract_name,id,status<>2"
                        />
                      </a-form-model-item>
                    </a-col>
                    <a-col :span="11">
                      <a-form-model-item label="备注" :wrapper-col="{ span: 18 }" prop="remark">
                        <a-input placeholder="备注" v-model="outModelInfo.remark" />
                      </a-form-model-item>
                    </a-col>
                  </a-row>
                </a-form-model>
              </div>
              <div class="fieldset-wrapper" style="height: calc(100% - 284px)">
                <h4 class="title">交接部件</h4>
                <div v-if="selectTask && selectTask.taskStatus !== 2" style="padding-bottom: 10px">
                  <a-space>
                    <a-button @click="onAddOutPart">添加</a-button>
                    <a-button @click="onDeleteOutPart">删除</a-button>
                  </a-space>
                </div>
                <vxe-table
                  key="outTable"
                  border
                  ref="outTable"
                  align="center"
                  :data="outModelInfo.outinDetails"
                  show-overflow="tooltip"
                  :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
                >
                  <vxe-table-column type="checkbox" width="40"></vxe-table-column>
                  <vxe-table-column field="trainNo" title="车号" width="100"></vxe-table-column>
                  <vxe-table-column field="assetName" title="委外部件" header-align="center" align="left" min-width="140"></vxe-table-column>
                  <vxe-table-column
                    field="assetNo"
                    title="部件编号"
                    min-width="140"
                    align="left"
                    header-align="center"
                  ></vxe-table-column>
                  <vxe-table-column field="facadeStatus_dictText" title="外观状态" width="100"></vxe-table-column>
                  <vxe-table-column field="mixtoolStatus_dictText" title="工装状态" width="100"></vxe-table-column>
                  <vxe-table-column field="fault_dictText" title="是否故障" width="100"></vxe-table-column>
                  <vxe-table-column
                    field="remark"
                    title="备注"
                    min-width="160"
                    header-align="center"
                    align="left"
                  ></vxe-table-column>
                </vxe-table>
              </div>
            </div>
            <div v-if="selectTask && selectTask.outTask === 2" style="height: calc(100% - 15px)">
              <!-- 委外接收 -->
              <div class="fieldset-wrapper">
                <h4 class="title">交接信息</h4>
                <a-form-model
                  ref="inInfoForm"
                  layout="inline"
                  class="work-form"
                  :model="inModelInfo"
                  :label-col="{ span: 6 }"
                  :wrapper-col="{ span: 18 }"
                  :rules="inRules"
                >
                  <a-row>
                    <a-col :span="11">
                      <a-form-model-item label="交接单号" :wrapperCol="{ span: 18 }" prop="billNo">
                        <a-input placeholder="交接单号" v-model="inModelInfo.billNo" disabled />
                      </a-form-model-item>
                    </a-col>
                    <a-col :span="11">
                      <a-form-model-item label="交接单名" :wrapperCol="{ span: 18 }" prop="outinName">
                        <a-input placeholder="交接单名" v-model="inModelInfo.outinName" />
                      </a-form-model-item>
                    </a-col>
                    <a-col :span="11">
                      <a-form-model-item label="线路" :wrapperCol="{ span: 18 }" prop="lineId">
                        <j-dict-select-tag
                          :triggerChange="true"
                          v-model="inModelInfo.lineId"
                          dictCode="bu_mtr_line,line_name,line_id"
                          @change="onLineChange"
                        />
                      </a-form-model-item>
                    </a-col>
                    <a-col :span="11">
                      <a-form-model-item label="车号" :wrapperCol="{ span: 18 }" prop="trainNo">
                        <j-dict-select-seach-tag
                          :triggerChange="true"
                          v-model="inModelInfo.trainNo"
                          :dictCode="dictTrainStr"
                          @select="onTrainSelect"
                        />
                      </a-form-model-item>
                    </a-col>

                    <a-col :span="11">
                      <a-form-model-item label="接收日期" :wrapperCol="{ span: 18 }" prop="transferDateM">
                        <a-date-picker
                          :key="transferDateMKey"
                          style="width: 100%"
                          placeholder="交接日期"
                          @change="transferDateMKey++"
                          v-model="inModelInfo.transferDateM"
                        ></a-date-picker>
                      </a-form-model-item>
                    </a-col>
                    <a-col :span="11">
                      <a-form-model-item label="工程师" :wrapperCol="{ span: 18 }" prop="engineerId">
                        <div @click="showUserModal(2)">
                          <a-select
                            ref="userSelect2"
                            v-model="inModelInfo.engineerName"
                            placeholder="请选择人员"
                            :open="false"
                          >
                            <a-icon slot="suffixIcon" type="ellipsis" />
                          </a-select>
                        </div>
                      </a-form-model-item>
                    </a-col>
                    <a-col :span="11">
                      <a-form-model-item label="作业班组" :wrapperCol="{ span: 18 }" prop="sendGroupId">
                        <j-dict-select-tag
                          :triggerChange="true"
                          v-model="inModelInfo.sendGroupId"
                          :dictCode="dictGroupStr"
                        />
                      </a-form-model-item>
                    </a-col>
                    <a-col :span="11">
                      <a-form-model-item label="接收人" :wrapperCol="{ span: 18 }" prop="receiveUser">
                        <div @click="showUserModal(1)">
                          <a-select
                            ref="userSelect1"
                            v-model="inModelInfo.receiveUserName"
                            placeholder="请选择人员"
                            :open="false"
                          >
                            <a-icon slot="suffixIcon" type="ellipsis" />
                          </a-select>
                        </div>
                      </a-form-model-item>
                    </a-col>
                    <a-col :span="11">
                      <a-form-model-item label="移交厂商" :wrapperCol="{ span: 18 }" prop="supplierId">
                        <!--                          <j-dict-select-tag
                          :triggerChange="true"
                          v-model="inModelInfo.supplierId"
                          dictCode="bu_base_supplier,name,id"
                        />-->
                        <a-select
                          v-model="inModelInfo.supplierName"
                          placeholder="请选择厂商"
                          :open="false"
                          :showArrow="true"
                          @focus="openSupplierModal(2)"
                          ref="mySupplierSelectIn"
                        >
                          <a-icon slot="suffixIcon" type="ellipsis" />
                        </a-select>
                      </a-form-model-item>
                    </a-col>
                    <a-col :span="11">
                      <a-form-model-item label="移交人员" :wrapperCol="{ span: 18 }" prop="transferUserId">
                        <a-input :maxLength="17" placeholder="移交人员" v-model="inModelInfo.transferUserId" />
                      </a-form-model-item>
                    </a-col>
                    <a-col :span="11">
                      <a-form-model-item label="所属合同" :wrapperCol="{ span: 18 }" prop="contractId">
                        <j-dict-select-tag
                          :trigger-change="true"
                          v-model="inModelInfo.contractId"
                          dictCode="bu_contract_info,contract_name,id,status<>2"
                        />
                      </a-form-model-item>
                    </a-col>
                    <a-col :span="11">
                      <a-form-model-item label="备注" :wrapperCol="{ span: 18 }" prop="remark">
                        <a-input :maxLength="201" placeholder="备注" v-model="inModelInfo.remark" />
                      </a-form-model-item>
                    </a-col>
                  </a-row>
                </a-form-model>
              </div>
              <div class="fieldset-wrapper" style="height: calc(100% - 284px)">
                <h4 class="title">交接部件</h4>
                <div v-if="selectTask && selectTask.taskStatus !== 2" style="padding-bottom: 10px">
                  <a-space>
                    <a-button @click="onAddInPart">添加</a-button>
                    <a-button @click="onDeleteInPart">删除</a-button>
                  </a-space>
                </div>
                <vxe-table
                  key="inTable"
                  border
                  ref="inTable"
                  :align="'center'"
                  :data="inModelInfo.outinDetails"
                  :edit-config="{ trigger: 'manual', mode: 'row' }"
                  show-overflow="tooltip"
                  :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
                >
                  <vxe-table-column type="checkbox" width="40"></vxe-table-column>
                  <vxe-table-column field="trainNo" title="车号" width="100"></vxe-table-column>
                  <vxe-table-column field="assetName" header-align="center" align="left" title="委外部件" min-width="140"></vxe-table-column>
                  <vxe-table-column field="assetNo" title="部件编号" min-width="140"></vxe-table-column>
                  <vxe-table-column field="facadeStatus_dictText" title="外观状态" width="100"></vxe-table-column>
                  <vxe-table-column field="mixtoolStatus_dictText" title="工装状态" width="100"></vxe-table-column>
                  <vxe-table-column
                    field="remark"
                    title="备注"
                    min-width="160"
                    header-align="center"
                    align="left"
                  ></vxe-table-column>
                </vxe-table>
              </div>
            </div>
          </div>
          <!-- 检修申请填报 -->
          <div
            v-if="selectTask && selectTask.outTask === 0 && selectTask.taskType === 8"
            style="padding: 10px; height: 100%; overflow-y: auto; overflow-x: hidden; padding-top: 25px"
          >
            <SuperviseItemForm ref="superviseForm" @ok="onTask8SaveSuccess" @fail="onTask8SaveFail"></SuperviseItemForm>
          </div>
        </a-tab-pane>
        <!-- 作业表单 -->
        <a-tab-pane key="2" :forceRender="true" na-flex-height-full>
          <div class="badge-tab-pane" slot="tab">
            <span>作业表单</span>
            <a-badge :count="formsBadge" />
          </div>
          <div v-if="taskFormsList && taskFormsList.length > 0">
            <a-tabs
              size="small"
              v-model="tabFormKey"
              v-if="taskFormsList.length"
              @change="selectForm"
              style="height: 40px"
            >
              <a-tab-pane v-for="(form, index) in taskFormsList" :key="index">
                <div class="badge-tab-pane ellipsis-main" slot="tab">
                  <a-tooltip placement="bottom" :mouseEnterDelay="0.3">
                    <template slot="title">
                      <span>{{ form.formName + getFormAssetName(form) }}</span>
                    </template>
                    <span >{{ form.formName + getFormAssetName(form) }}</span>
                    <a-badge :count="form.alert || 0"/>
                  </a-tooltip>
                </div>
              </a-tab-pane>
            </a-tabs>
            <!-- 检查记录表 -->
            <div v-if="curForm && curForm.instType === 4" style="height: calc(100vh - 110px)">
              <TaskCheckForm
                :taskFormsList="taskFormsList"
                :selectTask="selectTask"
                :workOrderInfo="workOrderInfo"
                @taskFormNoWork="onTaskFormNoWork"
                @checkEnd="getFormsItem"
                ref="checkRecordForm"
              ></TaskCheckForm>
            </div>
            <!-- 作业记录表 -->
            <div v-if="curForm && curForm.instType === 3" style="height: calc(100vh - 110px)">
              <div class="buttonDiv">
                <!-- 1 自检  2 互检  3 专检    4  抽检   5 过程检  6 过程检确认  7 交接检  8 交接检确认 9 专工验收 -->
                <a-space v-if="selectTask && selectTask.taskStatus !== 2">
                  <a-button v-if="curForm.workRecordType===2" @click.stop="saveCustomForm"><a-icon type="save" /> &nbsp;保存</a-button>
                  <a-button @click="handleCheck(1, '自检')">自检</a-button>
                  <a-button @click="handleCheck(2, '互检')">互检</a-button>
                  <a-button @click="handleCheck(3, '专检')">专检</a-button>
                  <!-- 专验暂时注释，在另外的界面进行操作 -->
                  <!--                      <a-button @click="handleCheck(9,'专验')">专验</a-button>-->
                  <a-button @click="createFault" :loading="createFaultLoading">故障提报</a-button>
                  <a-dropdown>
                    <a-menu slot="overlay" @click="handleMenuClick">
                      <a-menu-item key="1"> 清除自检 </a-menu-item>
                      <a-menu-item key="2"> 清除互检 </a-menu-item>
                      <a-menu-item key="3"> 清除专检 </a-menu-item>
                      <a-menu-item key="4"> 清除忽略 </a-menu-item>
                    </a-menu>
                    <a-button style="margin-left: 8px"> 清除 <a-icon type="down" /> </a-button>
                  </a-dropdown>
                  <!--                      <a-dropdown>
                    <a-menu slot="overlay" @click="moreBtnClick">
                      <a-menu-item key="1">
                        故障提报
                      </a-menu-item>
                      <a-menu-item key="2">
                        设为开口项
                      </a-menu-item>
                    </a-menu>
                    <a-button> 更多 <a-icon type="down" /> </a-button>
                  </a-dropdown>-->

                </a-space>
              </div>
              <!-- 老板作业记录表填写 -->
              <div v-if="curForm && curForm.workRecordType === 1" :style="{ height: formStyleHeight }">
                <div v-if="curRecordForm && curRecordForm.updown && curRecordForm.updown === 1">
                  <table style="width: 100%; margin-bottom: 5px" class="vxe-table--header">
                    <tr>
                      <!--                     <th style="width: 50px;text-align: right;">原部件:</th>
                                           <td style="width: 150px">
                                             <div @click.stop="showAssetModal">
                                               <a-select
                                                 placeholder="请选择"
                                                 v-model="recordTable.trainAssetName"
                                                 :open="false"
                                                 :showArrow="true"
                                                 ref="assetSelect"
                                                 style="width: 100%;"
                                               >
                                                 <a-icon slot="suffixIcon" type="ellipsis" />
                                               </a-select>
                                             </div>
                                           </td>-->
                      <th style="width: 92px; text-align: right">原部件序列号:</th>
                      <td>
                        <a-input style="width: 100%" v-model="curRecordForm.manufNo" placeholder="原部件序列号" />
                      </td>
                      <!--                     <th style="width: 70px;text-align: right;">新部件:</th>
                                           <td style="width: 150px;">
                                             <div @click.stop="showAssetModal">
                                               <a-select
                                                 placeholder="请选择"
                                                 v-model="recordTable.trainAssetName"
                                                 :open="false"
                                                 :showArrow="true"
                                                 ref="assetSelect"
                                                 style="width: 100%;"
                                               >
                                                 <a-icon slot="suffixIcon" type="ellipsis" />
                                               </a-select>
                                             </div>
                                           </td>-->
                      <th style="width: 120px; text-align: right">新部件序列号:</th>
                      <td>
                        <a-input style="width: 100%" v-model="curRecordForm.manufNoUp" placeholder="新部件序列号" />
                      </td>
                    </tr>
                  </table>
                </div>
                <div style="height:100%">
                  <vxe-table
                    :stripe="true"
                    border
                    auto-resize
                    height="auto"
                    row-id="id"
                    ref="recordListTable"
                    :align="allAlign"
                    v-if="curRecordForm"
                    :data="curRecordForm.detailList"
                    show-overflow="tooltip"
                    :span-method="mergeRowMethod"
                    :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
                  >
                    <vxe-table-column type="checkbox" width="40"></vxe-table-column>
                    <vxe-table-column type="index" title="序号" align="center" width="60"></vxe-table-column>
                    <!--                    <vxe-table-column field="itemNo" title="序号" align="center" width="70"></vxe-table-column>-->
                    <vxe-table-column field="reguTitle" title="作业项目" align="center" width="120"></vxe-table-column>
                    <vxe-table-column field="workContent" title="检修内容" align="left" width="350"></vxe-table-column>
                    <vxe-table-column field="result_dictText" title="结果" align="center" width="80">
                      <template v-slot="{ row }">
                        <span v-if="row.result === 1" style="color: red">异常</span>
                        <span
                          v-if="row.result === 0 && (row.selfCheck || row.guarderCheck || row.monitorCheck)"
                          style="color: green"
                        >正常</span
                        >
                      </template>
                    </vxe-table-column>
                    <vxe-table-column field="selfCheck" title="自检" align="center" width="80"></vxe-table-column>
                    <vxe-table-column field="guarderCheck" title="互检" align="center" width="80"></vxe-table-column>
                    <vxe-table-column field="monitorCheck" title="专检" align="center" width="80"></vxe-table-column>
                    <vxe-table-column field="workInfo" title="作业情况" align="left" header-align="center">
                      <template v-slot="{ row }">
                        <span v-if="row.isIgnore === 1">{{ row.ignoreDesc }}</span>
                        <span v-else>{{ row.workInfo }}</span>
                      </template>
                    </vxe-table-column>
                    <vxe-table-column
                      field="isIgnore_dictText"
                      title="忽略？"
                      align="center"
                      width="80"
                    ></vxe-table-column>
                  </vxe-table>
                </div>
              </div>
              <!-- 新版作业记录表填写 -->
              <div v-if="curForm && curForm.workRecordType === 2" :id="'sheetWorkRecord'" style="margin: 0px; padding: 0px; width: 100%; height: calc(100% - 52px)">
              </div>
            </div>
            <!-- 数据记录表 -->
            <div v-if="curForm && curForm.instType === 1" style="height: calc(100vh - 110px)">
              <div class="buttonDiv">
                <a-row>
                  <a-col :span="8">
                    <a-button v-if="showSheetSave" @click.stop="saveCustomForm"><a-icon type="save" /> &nbsp;保存</a-button>
                    <span style="line-height: 30px; padding-left: 15px; color:#828080;">
                    &lt;-- 表单填写后，记得点击“保存表单”进行保存哦
                      </span>
                  </a-col>
                  <a-col :span="16">
                    <div v-if="selectCellItem" style="line-height: 30px; padding-left: 15px; font-weight: bold; color: red">
                      {{ selectCellItem.itemName }}：{{ selectCellItem.linkDomain }} {{ selectCellItem.operator_dictText }}
                      {{ selectCellItem.threshold }}
                      <span v-if="selectCellItem.operator2 && selectCellItem.threshold2">
                        且 {{ selectCellItem.operator2_dictText }} {{ selectCellItem.threshold2 }}
                      </span>
                    </div>
                  </a-col>
                </a-row>

              </div>
              <div :id="'sheet'" style="margin: 0px; padding: 0px; width: 100%; height: calc(100% - 52px)"></div>
            </div>
          </div>
          <div style="margin: 20px" v-if="!taskFormsList.length">
            <div style="padding: 15px; color: #cccccc">该作业无须填写表单信息</div>
            <a-space v-if="!readOnly">
              <a-button @click="createFault" :loading="createFaultLoading">提报故障</a-button>
              <a-button @click="createOpenItem">设置为开口项</a-button>
            </a-space>
          </div>
        </a-tab-pane>
        <!-- 物料消耗 -->
        <a-tab-pane key="4">
          <!-- 添加删除按钮去掉，因为物料变化需要进行变更 -->
          <!--            <a-form v-if="!readOnly" layout="inline">
            <a-row :gutter="24">
              <a-col :md="6" :sm="8">
                <a-form-item>
                  <a-space>
                    <a-button type="dashed" class="primary-color" @click="onAddMaterial">添加</a-button>
                    <a-button type="dashed" @click="onDelMaterial">删除</a-button>
                  </a-space>
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>-->
          <div class="badge-tab-pane" slot="tab">
            <span>物料消耗</span>
            <a-badge :count="materialBadge" />
          </div>
          <div na-flex-height-full>
            <vxe-table
              border
              ref="MaterialListTable"
              max-height="auto"
              :align="allAlign"
              :data.sync="materials"
              :keep-source="true"
              show-overflow="tooltip"
              :edit-rules="materialInputValidRules"
              :row-style="checkMaterialShow"
              :checkbox-config="{ highlight: true, range: true }"
              :edit-config="{ key: 'id', trigger: 'manual', mode: 'row', autoClear: false, showStatus: true }"
            >
              <vxe-table-column type="checkbox" width="40"></vxe-table-column>
              <vxe-table-column field="code" title="物料编码" width="150" :edit-render="{ name: 'input' }">
                <template v-slot:edit="{ row }">
                  {{ row.code }}
                  <!--                    <div @click.stop="openMaterialSelectModal(row)">
                    <a-select ref="materialSelect" v-model="row.code" placeholder="请选择物料" :open="false" style="width: 100%">
                      <a-icon slot="suffixIcon" type="ellipsis" @click.stop="openMaterialSelectModal(row)" />
                    </a-select>
                  </div>-->
                </template>
              </vxe-table-column>
              <vxe-table-column
                field="name"
                title="物料名称"
                min-width="200"
                :edit-render="{ name: 'input' }"
                align="left"
                header-align="center"
              >
                <template v-slot:edit="{ row }">
                  {{ row.name }}
                </template>
              </vxe-table-column>
              <vxe-table-column field="useCategory_dictText" title="类别" width="100"> </vxe-table-column>
              <vxe-table-column field="opType_dictText" title="领用类型" width="120" v-if="false"></vxe-table-column>
              <!--     后端没有返回哦，先注释         <vxe-table-column field="sourceLocationAndPalletName" title="发放信息" v-if="false"></vxe-table-column>-->
              <vxe-table-column
                field="amount"
                title="所需数量"
                width="120"
                :edit-render="{ name: 'input' }"
                align="right"
              >
                <template v-slot:edit="{ row }">
                  {{ row.amount }}
                  <!--                    <a-input-number v-model="row.amount" :defaultValue="1" :min="1" :max="10000" style="width: 100%" />-->
                </template>
              </vxe-table-column>
              <vxe-table-column
                width="120"
                field="actAmount"
                title="实际消耗"
                align="right"
              >
                <!-- :edit-render="{ name: '$input', props: { type: 'number' } }" -->
                <template v-slot:edit="{ row }">
                  {{ row.actAmount }}
                </template>
              </vxe-table-column>
              <vxe-table-column field="unit" title="单位" width="100" :edit-render="{ name: 'input' }">
                <template v-slot:edit="{ row }">
                  {{ row.unit }}
                </template>
              </vxe-table-column>
              <vxe-table-column field="systemName" title="所需系统" width="150"></vxe-table-column>
              <vxe-table-column field="workstationName" title="所需工位" width="150"></vxe-table-column>
              <vxe-table-column
                field="remark"
                title="消耗备注"
                align="left"
              >
                <!-- :edit-render="{ name: '$input', props: { type: 'text' } }" -->
                <!-- <template v-slot="{ row }">
                  <a-input v-model="row.remark" placeholder="请输入备注，最多255个字符" :maxLength="255"></a-input>
                </template> -->
              </vxe-table-column>
              <vxe-table-column title="操作" width="120" v-if="!readOnly">
                <template v-slot="{ row, rowIndex }">
                  <!-- <template v-if="$refs.MaterialListTable.isActiveByRow(row)">
                    <a-space>
                      <a-button type="dashed" size="small" @click.stop="saveMaterialRowEvent(row)">保存</a-button>
                      <a-button type="dashed" size="small" @click.stop="cancelMaterialRowEvent(row)">取消</a-button>
                    </a-space>
                  </template> -->
                  <template>
                    <a-button
                      type="dashed"
                      :disabled="!selectTask && selectTask.taskStatus === 2"
                      size="small"
                      @click.stop="editMaterialRowEvent(row,rowIndex)"
                    >填写</a-button
                    >
                    <!-- <a-button type="dashed" size="small" @click="delRowEvent(row)">删除</a-button>-->
                  </template>
                </template>
              </vxe-table-column>
            </vxe-table>
          </div>
        </a-tab-pane>
        <!-- 工器具 -->
        <a-tab-pane key="5" tab="工器具">
          <a-form v-if="!readOnly" layout="inline">
            <a-row :gutter="24">
              <a-col :md="6" :sm="8">
                <a-form-item>
                  <a-space>
                    <a-button type="dashed" class="primary-color" @click="onAddTool">添加</a-button>
                    <a-button type="dashed" @click="onDelTool">删除</a-button>
                  </a-space>
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
          <div na-flex-height-full>
            <vxe-table
              border
              ref="ToolsListTable"
              max-height="auto"
              :align="allAlign"
              :data="tools"
              show-overflow="tooltip"
              :checkbox-config="{ highlight: true, range: true }"
              :edit-rules="toolInputValidRules"
              :edit-config="{ key: 'id', trigger: 'manual', mode: 'row', autoClear: false, showStatus: true }"
            >
              <vxe-table-column type="checkbox" width="40"></vxe-table-column>
              <vxe-table-column field="code" title="工器具类型编码" :edit-render="{ name: 'input' }">
                <template v-slot:edit="{ row }">
                  <div @click.stop="openToolTypeSelectModal(row)">
                    <a-select
                      ref="toolTypeSelect"
                      v-model="row.code"
                      placeholder="请选择工器具"
                      :open="false"
                      style="width: 100%"
                    >
                      <a-icon slot="suffixIcon" type="ellipsis" @click.stop="openToolTypeSelectModal(row)" />
                    </a-select>
                  </div>
                </template>
              </vxe-table-column>
              <vxe-table-column field="name" title="工器具名称" :edit-render="{ name: 'input' }" align="left">
                <template v-slot:edit="{ row }">
                  {{ row.name }}
                </template>
              </vxe-table-column>
              <vxe-table-column field="amount" title="所需数量" :edit-render="{ name: 'input' }">
                <template v-slot:edit="{ row }">
                  <a-input-number v-model="row.amount" :defaultValue="1" :min="1" :max="999999" style="width: 100%" />
                </template>
              </vxe-table-column>
              <vxe-table-column field="workRecordInstName" title="关联表单" :edit-render="{ name: 'input' }">
                <template v-slot:edit="{ row }">
                  <a-select v-model="row.workRecordInstId" style="width: 100%" @change="formSelectChange">
                    <template v-for="(form, index) in taskFormsList">
                      <a-select-option :value="form.formInstId" :key="index" v-if="form.instType === 3">
                        {{ form.formName }}
                      </a-select-option>
                    </template>
                  </a-select>
                </template>
              </vxe-table-column>
              <vxe-table-column field="category1_dictText" title="类别" :edit-render="{ name: 'input' }">
                <template v-slot:edit="{ row }">
                  {{ row.category1_dictText }}
                </template>
              </vxe-table-column>
              <vxe-table-column field="unit" title="单位" align="left" :edit-render="{ name: 'input' }">
                <template v-slot:edit="{ row }">
                  {{ row.unit }}
                </template>
              </vxe-table-column>
              <vxe-table-column
                field="assetCode"
                title="工器具资产编码"
                align="left"
                :edit-render="{ name: 'input' }"
                :formatter="serialNoFormatter"
              >
                <template v-slot:edit="{ row }">
                  <div @click.stop="openToolSelectModal(row)">
                    <a-select
                      ref="toolSelect"
                      v-model="row.assetCode"
                      placeholder="请选择工器具"
                      :open="false"
                      style="width: 100%"
                    >
                      <a-icon slot="suffixIcon" type="ellipsis" @click.stop="openToolSelectModal(row)" />
                    </a-select>
                  </div>
                </template>
              </vxe-table-column>
              <vxe-table-column title="操作" width="150" v-if="!readOnly">
                <template v-slot="{ row }">
                  <template v-if="$refs.ToolsListTable.isActiveByRow(row)">
                    <a-space>
                      <a-button type="dashed" size="small" @click.stop="saveToolRowEvent(row)">保存</a-button>
                      <a-button type="dashed" size="small" @click.stop="cancelToolRowEvent(row)">取消</a-button>
                    </a-space>
                  </template>
                  <template v-else>
                    <a-button
                      type="dashed"
                      :disabled="!selectTask && selectTask.taskStatus === 2"
                      size="small"
                      @click.stop="editToolRowEvent(row)"
                    >填写</a-button
                    >
                    <!-- <a-button type="dashed" size="small" @click="delRowEvent(row)">删除</a-button>-->
                  </template>
                </template>
              </vxe-table-column>
            </vxe-table>
          </div>
        </a-tab-pane>
        <!-- 作业工时 -->
        <a-tab-pane key="9" tab="作业工时">
          <TaskWorkTime
            :staffArranges.sync="staffArranges"
            :selectTask.sync="selectTask"
            :operator="4"
            :read-only="readOnly"
          ></TaskWorkTime>
        </a-tab-pane>
        <!--          <a-tab-pane key="12" tab="必换件清单">
          <div>
            <vxe-table
              border
              ref="MustListTable"
              :align="allAlign"
              :data="mustReplaces"
              show-overflow="ellipsis"
              :edit-config="{ trigger: 'click', mode: 'row' }"
            >
              <vxe-table-column type="checkbox" width="40"></vxe-table-column>
              <vxe-table-column field="code" title="必换件编码" width="15%"></vxe-table-column>
              <vxe-table-column field="name" title="必换件名称" width="15%"></vxe-table-column>
              <vxe-table-column field="initNum" title="数量" width="15%"></vxe-table-column>
              <vxe-table-column field="unit" title="单位" align="left" width="15%"></vxe-table-column>
              <vxe-table-column field="changed" title="更换状态" align="left" width="15%"></vxe-table-column>
              <vxe-table-column
                field="remark"
                title="备注"
                align="left"
                :edit-render="{ name: '$input', props: { type: 'text' } }"
              ></vxe-table-column>
            </vxe-table>
          </div>
        </a-tab-pane>-->
        <!--          <a-tab-pane key="6" tab="工艺文件">
                    <vxe-table
                      border
                      ref="fileListTable"
                      :data="files"
                      show-overflow="ellipsis"
                      :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
                    >
                      <vxe-table-column title="操作" width="120">
                        <template v-slot="{ row }">
                          <a-space>
                            <a-button type="dashed" size="small" @click.stop="viewFile(row)">查看</a-button>
                            <a-button type="dashed" size="small" @click.stop="downFile(row)">下载</a-button>
                          </a-space>
                        </template>
                      </vxe-table-column>
                      <vxe-table-column field="fileName" title="文件名称" align="left"></vxe-table-column>
                    </vxe-table>
                  </a-tab-pane>-->
        <!-- 任务信息 -->
        <a-tab-pane key="7" tab="任务信息">
          <orderOrTaskDetail
            v-if="selectTask && selectTask.outTask === 0"
            :workOrderInfo="workOrderInfo"
            :taskInfo="taskInfo"
          />
          <orderOrTaskDetail v-else type="outsources" :workOrderInfo="workOrderInfo" :taskInfo="selectTask" />
        </a-tab-pane>
        <a-tab-pane key="8" tab="附件">
          <TaskAttached :files.sync="attachedfiles" :selectTask="selectTask" @addFile="onAddFile"></TaskAttached>
        </a-tab-pane>
        <a-tab-pane key="10" tab="临时领料">
          <ReceiveMaterial
            :taskMaterials="materials"
            :selectTask="selectTask"
            :opType="2"
            @add="onReceiveMaterialAdd"
            @edit="onReceiveMaterialEdit"
            @submit="onSubmitMaterialApply"
          ></ReceiveMaterial>
        </a-tab-pane>
        <!-- <a-tab-pane key="11" tab="退还物料">
          <ReceiveMaterial
            :taskMaterials="materials"
            :selectTask="selectTask"
            :opType="3"
            @addReturnMateriles="onReturnMaterialAdd"
            @edit="onReceiveMaterialEdit"
          ></ReceiveMaterial>
        </a-tab-pane> -->
        <a-tab-pane key="12" tab="设备更换">
          <!-- 事件触发当前文件搜索 openWindowEvent -->
        </a-tab-pane>
        <!-- 按钮栏 -->
        <div slot="tabBarExtraContent" style="margin-right: 40px">
          <a-space>
            <!-- 故障保存按钮 -->
            <a-button
              v-if="selectTask && selectTask.taskType === 2 && selectTask.taskStatus !== 2 && curTabKey === '3'"
              type="primary"
              @click="saveFaultInfo"
              size="small"
              style="margin-right: 10px"
              :loading="loading"
            >保存
            </a-button>
            <!-- 委外任务保存按钮 -->
            <a-button
              v-if="
                selectTask &&
                selectTask.outTask !== 0 &&
                (selectTask.taskType === 6 || selectTask.taskType === 7) &&
                selectTask.taskStatus !== 2 &&
                curTabKey === '3'
              "
              type="primary"
              @click="saveOutInInfo"
              size="small"
              style="margin-right: 10px"
              :loading="loading"
            >保存
            </a-button>
            <!-- 监修申请保存 -->
            <a-button
              v-if="
                selectTask &&
                selectTask.outTask === 0 &&
                selectTask.taskType === 8 &&
                selectTask.taskStatus !== 2 &&
                curTabKey === '3'
              "
              type="primary"
              @click="save8Task"
              size="small"
              style="margin-right: 10px"
              :loading="loading"
            >保存</a-button
            >
            <!-- 作业提交 -->
            <a-button
              type="primary"
              size="small"
              v-if="!readOnly && selectTask"
              v-has="'mywork:submittask'"
              :loading="loading"
              @click="submitTask"
            >作业提交
            </a-button>
          </a-space>
        </div>
      </a-tabs>
    </div>
    <train-plan-list ref="planModalForm" @ok="onSelectPlan"></train-plan-list>
    <work-order-select :workGroupId="curDepartId" ref="workOrderSelect" @ok="onSelectOrder"></work-order-select>
    <check-modal ref="checkModalForm" v-if="curForm" :title="checkModalTitle" :form-inst-id="curForm.formInstId" :work-record-type="curForm.workRecordType" @scan="onCheckRecord" @ok="onCheckRecord"></check-modal>
<!-- 提交时填写工时不需要了   <task-submit-modal ref="taskSubmitModal" @ok="onTaskSubmit"></task-submit-modal>-->
    <ToolsList ref="toolModalForm" :multiple="false" @ok="onSelectTool" :can-select-type="[4, 5, 6]"></ToolsList>
    <ToolTypeSelect
      ref="toolTypeModalForm"
      :multiple="false"
      @ok="onSelectToolType"
      :can-select-type="[4, 5, 6]"
    ></ToolTypeSelect>
    <doc-preview-modal :title="fileName" ref="docPreview"></doc-preview-modal>
    <open-item-modal ref="openItemModal"></open-item-modal>
    <BreakdownModal ref="breakdownModal" :showWorkStation="true"></BreakdownModal>
    <material-list ref="materialModalForm" :multiple="false" @ok="onSelectMaterial"></material-list>
    <user-list ref="tUserModalForm" :multiple="false" @ok="onUserSelect"></user-list>
    <out-part-modal
      ref="outPartModal"
      @ok="onOutPartOk"
      :lineId="outModelInfo.lineId"
      :trainNo="outModelInfo.trainNo"
      :exist-assets="outModelInfo.outinDetails"
    ></out-part-modal>
    <in-part-modal
      ref="inPartModal"
      @ok="onInPartOk"
      :lineId="inModelInfo.lineId"
      :trainNo="inModelInfo.trainNo"
      :exist-assets="outModelInfo.outinDetails"
    ></in-part-modal>
    <supplier-list ref="supplierModal" :multiple="false" @ok="supplierSelect"></supplier-list>
    <WorkActAmount ref="WorkActAmount" @ok="workActAmountOk"/>
  </div>
</template>

<script>
import moment from 'moment'
import clone from 'clone'
import TrainPlanList from '@/views/tiros/common/selectModules/TrainPlanList'
import WorkOrderSelect from '@views/tiros/common/selectModules/WorkOrderSelect'
import TaskWorkTime from '@views/tiros/group/myWork/TaskWorkTime'
import checkModal from './checkModal'
// import taskSubmitModal from '@views/tiros/group/myWork/taskSubmitModal'
import ToolTypeSelect from '@views/tiros/common/selectModules/ToolTypeSelect'
import ToolsList from '@views/tiros/common/selectModules/ToolsList'
import DocPreviewModal from '@views/tiros/common/doc/DocPreviewModal'
import OpenItemModal from '@views/tiros/quality/modules/OpenItemModal'
import BreakdownModal from '@views/tiros/dispatch/breakdown/BreakdownModal'
import MaterialList from '@views/tiros/common/selectModules/MaterialList'
import UserList from '@views/tiros/common/selectModules/UserList'
import OutPartModal from '@views/tiros/group/myWork/OutPartModal'
import InPartModal from '@views/tiros/group/myWork/InPartModal'
import TaskAttached from '@views/tiros/dispatch/workOrder/TaskAttached'
import BreakdownFormItem from '@views/tiros/dispatch/breakdown/BreakdownFormItem'
import SuperviseItemForm from '@views/tiros/outsource/modules/SuperviseItemForm'
import TaskRegusView from '@views/tiros/group/modules/TaskRegusView'
import orderOrTaskDetail from '@views/tiros/group/modules/orderOrTaskDetail'
import WorkActAmount from '@views/tiros/group/modules/WorkActAmount'
import TaskCheckForm from '@views/tiros/group/myWork/taskCheckForm/TaskCheckForm'
import ReceiveMaterial from '@views/tiros/group/myWork/ReceiveMaterial'

// import TaskBooksView from '@views/tiros/group/modules/TaskBooksView'
import {
  delMaterial,
  delTool,
  getOutTaskInfo,
  getTaskRecordForm,
  getTaskRelevanceInfo,
  getTrainPlanOnlineFormById,
  getTrainPlanRecordFormById,
  saveFormValue,
  saveMaterial,
  saveOutInInfo,
  saveRcordTableAssetNo,
  saveSafeAssumeRead,
  saveTool,
  submitTask,
  getCheckRecordDetail,
  getWorkTaskList,
  saveWorkRecordExcelData,
  getWorkRecordExcelData,
} from '@/api/tirosGroupApi'
import { getRowColThreshold } from '@api/tirosQualityApi'
import { getWorkOrderDetail,deleteCheckRecord } from '@api/tirosDispatchApi'
import { getFormContent, getDataJson } from '@api/tirosApi'
import { download } from '@api/tirosFileApi'
import NaSplitter from '@comp/tiros/Na-splitter'
import { everythingIsEmpty, randomUUID } from '@/utils/util'
import SupplierList from '@views/tiros/common/selectModules/SupplierList'
import { addWorkOrderAttached, getBreakdownInfo } from '@api/tirosDispatchApi'
import { getSuperviseByOrderTask } from '@api/tirosOutsourceApi'
import luckyexcelUtil from '@views/tiros/util/LuckyexcelUtil'

export default {
  name: 'workReportsContent',
  components: {
    NaSplitter,
    BreakdownFormItem,
    SuperviseItemForm,
    TrainPlanList,
    WorkOrderSelect,
    checkModal,
    // taskSubmitModal,
    ToolTypeSelect,
    ToolsList,
    DocPreviewModal,
    OpenItemModal,
    BreakdownModal,
    MaterialList,
    UserList,
    OutPartModal,
    InPartModal,
    TaskAttached,
    SupplierList,
    TaskWorkTime,
    TaskRegusView,
    orderOrTaskDetail,
    TaskCheckForm,
    ReceiveMaterial,
    WorkActAmount,
  },
  computed: {
    readOnly: function () {
      return this.selectTask && this.selectTask.taskStatus === 2
    },
    materialBadge: function () {
      if (!this.selectTask) {
        return 0
      }
      if (
        !(
          this.selectTask.outTask === 0 &&
          (this.selectTask.taskType === 1 ||
            this.selectTask.taskType === 3 ||
            this.selectTask.taskType === 4 ||
            this.selectTask.taskType === 5)
        ) &&
        !(this.selectTask.taskStatus !== 2)
      ) {
        return 0
      }
      if (this.materials.length) {
        // let isOk = true
        let count = 0
        this.materials.forEach((e) => {
          //   这里只有是出现在这里的都需要
          /*if (e.actAmount === 0 && e.amount !== 0 && (e.opType === 1 || (e.opType === 2 && e.isGenOrder == 1))) {
            count++
          }*/
          // 填写为0后就算填写了
          if (e.amount !== 0 && (e.actAmount === '' || e.actAmount == null) && (e.opType === 1 || (e.opType === 2 && e.isGenOrder === 1))) {
            count++
          }
        })
        return count
      }
      return 0
    },
    formsBadge: function () {
      let count = 0
      this.taskFormDetail.forEach((element) => {
        if (!element.isIgnore || element.isIgnore !== 1) {
          if (!element.selfCheck || !element.monitorCheck || !element.guarderCheck) {
            count++
          }
        }
      })
      this.checkFormDetail.forEach((element) => {
        if (!element.checkUserId || !element.workTime) {
          count++
        }
      })
      return count
    },
    formStyleHeight: function () {
      return `calc(100% - ${
        25 +
        (this.curRecordForm && this.curRecordForm.updown && this.curRecordForm.updown === 1 ? 39 : 0) +
        (this.selectTask && this.selectTask.taskStatus !== 2 ? 39 : 0)
      }px)`
    },
  },
  props: {
    businessKey: {
      type: String,
      default: '',
    },
    isReadonly: {
      type: Boolean,
      default: false,
    },
    fromFlow: {
      type: Boolean,
      default: false,
    },
  },
  watch: {
    curTabKey(val, oldValue) {
      if (val === '12') {
        this.curTabKey = oldValue
        this.openWindowEvent()
      }
    },
    checkFormItemList(){
      let tempList = JSON.parse(JSON.stringify(this.taskFormsList))
      this.taskFormsList = []
      this.taskFormsList = tempList
    }
  },
  data() {
    return {
      editorRowIndex: -1,
      oldActive: null,
      transferDateMKey: 0,
      oTransferDateMKey: 0,
      createFaultLoading: false,
      selectTask: null,
      loading: false,
      showSheetSave: false,
      dictGroupStr:
        "bu_mtr_workshop_group,group_name,id,workshop_id='" + this.$store.getters.userInfo.workshopId + "'|workshop_id",
      readed: false,
      curTabKey: '1',
      tabFormKey: 0,
      toolInputValidRules: {
        taskName: [{ required: true, message: '请选择任务' }],
        code: [{ required: true, message: '请选择工器具' }],
        amount: [{ required: true, message: '数量必须填写' }],
        formName: [
          {
            validator: ({ row }) => {
              if (row.category1 === 6) {
                if (!row.workRecordInstId) {
                  return new Error('计量器具必须指定一个记录表')
                }
              }
            },
          },
        ],
        assetCode: [
          {
            validator: ({ row }) => {
              if (row.category1 === 6) {
                if (!row.assetCode) {
                  return new Error('计量器具必须指定具体的工器具')
                }
              }
            },
          },
        ],
      },
      materialInputValidRules: {
        taskName: [{ required: true, message: '请选择任务' }],
        code: [{ required: true, message: '请选择物料' }],
        amount: [{ required: true, message: '所需数量必须填写' }],
        actAmount: [{ required: true, message: '实际数量必须填写' }],
      },
      startDate: this.$moment(),
      tableDataTask: [],
      serialList: [],
      workOrderList: [],
      // activeTab: '1',
      allAlign: 'center',
      curToolEditMode: 0, // 0 没有编辑，1 新增， 2 修改
      curMaterialEditMode: 0, // 0 没有编辑，1 新增， 2 修改
      queryParam: {
        planId: null,
        planName: undefined,
        startDate: null,
        status: null,
        taskName: null,
        orderName: undefined,
        workOrderId: null,
        // planStatusList: [2], // 列计划状态, 	0 草稿 1 审批中 2 已审批
        orderStatusList: [8], // 工单状态过滤，只能是已下达且已接收，没有提交的
        groupId: null,
      },
      taskFormsList: [],
      taskFormDetail: [],
      checkFormDetail: [],
      defaultSelectTaskRow: '',
      // 表单序号（表示是该工单的第几个表单）
      curFormIndex: 0,
      // 当前选中表单
      curForm: null,
      // 当前作业记录表对象
      curRecordForm: null,
      // 当前在线表单
      curOnlineForm: null,
      // 作业记录表明细
      //  recordFormDetail: [],
      // 当前选中在线表单的内容
      // onlineFormContent: null,
      requirement: {},
      materials: [],
      tools: [],
      files: [],
      attachedfiles: [],
      staffArranges: [],
      regus: [],
      bookSteps: [],
      fileName: '',
      mustReplaces: [],
      workOrderInfo: {},
      taskInfo: {},
      maximoAssetChangeUrl: null,
      curDepartId: this.$store.getters.userInfo.departId,
      needWriteOther: false,
      defaultOptions: {
        container: 'luckysheet', //luckysheet为容器id
        title: 'sheet',
        column: 26, // 列数
        row: 50, // 行数
        lang: 'zh', // 设定表格语言
        allowEdit: true, // 允许编辑
        showinfobar: false, // 名称栏
        sheetFormulaBar: true, // 公式栏
        showsheetbar: false, // 底部sheet页
        showstatisticBar: true, // 底部计数栏
        enableAddRow: false, // 允许添加行
        enableAddCol: false, // 允许添加列
        showtoolbar: true, // 是否第二列显示工具栏
        showtoolbarConfig: {
          undoRedo: true, //撤销重做，注意撤消重做是两个按钮，由这一个配置决定显示还是隐藏
          paintFormat: true, //格式刷
          currencyFormat: true, //货币格式
          percentageFormat: true, //百分比格式
          numberDecrease: true, // '减少小数位数'
          numberIncrease: true, // '增加小数位数
          moreFormats: true, // '更多格式'
          font: true, // '字体'
          fontSize: true, // '字号大小'
          bold: true, // '粗体 (Ctrl+B)'
          italic: true, // '斜体 (Ctrl+I)'
          strikethrough: true, // '删除线 (Alt+Shift+5)'
          underline: true, // '下划线 (Alt+Shift+6)'
          textColor: true, // '文本颜色'
          fillColor: true, // '单元格颜色'
          border: true, // '边框'
          mergeCell: false, // '合并单元格'
          horizontalAlignMode: true, // '水平对齐方式'
          verticalAlignMode: true, // '垂直对齐方式'
          textWrapMode: true, // '换行方式'
          textRotateMode: false, // '文本旋转方式'
          image:false, // '插入图片'
          link:false, // '插入链接'
          chart: false, // '图表'（图标隐藏，但是如果配置了chart插件，右击仍然可以新建图表）
          postil:  false, //'批注'
          pivotTable: false,  //'数据透视表'
          function: false, // '公式'
          frozenMode: false, // '冻结方式'
          sortAndFilter: false, // '排序和筛选'
          conditionalFormat: false, // '条件格式'
          dataVerification: false, // '数据验证'
          splitColumn: false, // '分列'
          screenshot: false, // '截图'
          findAndReplace: false, // '查找替换'
          protection:false, // '工作表保护'
          print:false, // '打印'
        },
        cellRightClickConfig:{
          copy: false, // 复制
          copyAs: false, // 复制为
          paste: false, // 粘贴
          insertRow: false, // 插入行
          insertColumn: false, // 插入列
          deleteRow: false, // 删除选中行
          deleteColumn: false, // 删除选中列
          deleteCell: false, // 删除单元格
          hideRow: false, // 隐藏选中行和显示选中行
          hideColumn: false, // 隐藏选中列和显示选中列
          rowHeight: false, // 行高
          columnWidth: false, // 列宽
          clear: false, // 清除内容
          matrix: false, // 矩阵操作选区
          sort: false, // 排序选区
          filter: false, // 筛选选区
          chart: false, // 图表生成
          image: false, // 插入图片
          link: false, // 插入链接
          data: false, // 数据验证
          cellFormat: false // 设置单元格格式
        },
        hook: {
          cellMousedown: (cell, postion, sheetFile, ctx) => {
            // console.log('cell click:', cell, postion)
            this.getRowColData(postion.r, postion.c)
          },
          workbookCreateAfter: (options) => {
            // 写入其他信息
            this.writeOtherExcelInfo()
          }
        }
      },
      colNames: [],
      checkModalTitle: '自检',
      outModelInfo: {
        transferDateM:''
      },
      inModelInfo: {},
      outIsSave: false,
      inIsSave: false,
      outRules: {
        billNo: [{ required: false, message: "请填写交接单号!" }],
        outinName: [{ required: true, message: "请填写交接单名称!" }],
        lineId: [{ required: true, message: "请选择线路!" }],
        trainNo: [{ required: true, message: "请选择车辆!" }],
        transferUserId: [{ required: true, message: "请选择移交人!" }],
        receiveUser: [{ max: 32, message: "不能超过32个字符!" }],
        transferDateM: [{ required: true, message: "请选择交接日期!" }],
        engineerId: [{ required: true, message: "请选择工程师!" }],
        sendGroupId: [{ required: true, message: "请选择作业班组!" }],
        supplierId: [{ required: true, message: "请选择接收厂商!" }],
        contractId: [{ required: true, message: "请选择所属合同!" }],
        remark: [{ required: false, max: 200, message: "不能超过200个字符!" }],
      },
      inRules: {
        billNo: [{ required: false, message: "请填写交接单号!" }],
        outinName: [{ required: true, message: "请填写交接单名称!" }],
        lineId: [{ required: true, message: "请选择线路!" }],
        trainNo: [{ required: true, message: "请选择车辆!" }],
        receiveUser: [{ required: true, message: "请选择接收人!" }],
        transferUserId: [{ max: 32, message: "不能超过32个字符!" }],
        transferDateM: [{ required: true, message: "请选择交接日期!" }],
        engineerId: [{ required: true, message: "请选择工程师!" }],
        sendGroupId: [{ required: true, message: "请选择作业班组!" }],
        supplierId: [{ required: true, message: "请选择移交厂商!" }],
        contractId: [{ required: true, message: "请选择所属合同!" }],
        remark: [{ required: false, max: 200, message: "不能超过200个字符!" }],
      },
      userSelectType: 1,
      dictTrainStr: '',
      curSelectTrainStructId: '',
      supplierSelectType: 1,
      superviseModel: null,
      bookStepContent: '',
      orderDetail: '',
      selectCellItem: null,
      dataJson:{},
      checkTypeMap:{"C1":-1,"C2":-1, 'C3': -1}
    }
  },
  mounted() {
    for (let i = 65; i < 91; i++) {
      this.colNames.push(String.fromCharCode(i))
    }

    if (this.businessKey) {
      this.loadTaskList()
    }
  },
  beforeDestroy() {
    // 离开销毁Luckysheet
    luckysheet.destroy()
  },
  methods: {
    moment,
    handleMenuClick({key}){
      let params={
        formInstId: this.curForm.formInstId,
        ids: [],
        checkType:key,
        workRecordType: this.curForm.workRecordType
      }
      if(this.curForm.workRecordType === 1) {// 旧的作业记录表
        let selectRecords = this.$refs.recordListTable.getCheckboxRecords()
        if (selectRecords.length > 0) {
          let idsStr = selectRecords
            .map((obj, index) => {
              return obj.id
            })
          params.ids=idsStr
        } else {
          this.$message.error('请选择要检查的作业记录明细!')
        }
      } else if (this.curForm.workRecordType === 2) { // 新的作业记录表
        // 新版作业记录表
        let rows = this.getSelectCheckRow()
        if (rows.length > 0) {
          params.ids = rows.map(item => item + '')
        }
      }

      deleteCheckRecord(params).then((res) => {
        if (res.success) {
          this.$message.success('清除成功')
          this.clearCheckInfo(params.ids,params.checkType)
          this.loadRecordFormDetail()
          this.getFormsItem()
        } else {
          this.$message.warning(res.message)
        }
      })
    },
    // 清除excel 表格中的检查信息
    clearCheckInfo (rows, type) {
      rows.forEach(row => {
        if(type === '1' || type === '2' || type === '3') {
          let colIndex = this.checkTypeMap['C' + type]
          luckyexcelUtil.setCellValue(row, colIndex, '')
        } else {
          if(luckyexcelUtil.checkProperty(this.dataJson,'checkDetail')
            && luckyexcelUtil.checkProperty(this.dataJson.checkDetail,'rows')
            && this.dataJson.checkDetail.rows.length>0
            && luckyexcelUtil.checkProperty(this.dataJson.checkDetail.rows[0],'cells')
          ) {
            const cells = this.dataJson.checkDetail.rows[0].cells
            if(luckyexcelUtil.checkProperty(cells,'jg')){
              luckyexcelUtil.setCellValue(row, cells.jg.colIndex, '□')
            }
            if(luckyexcelUtil.checkProperty(cells,'zyqk')) {
              luckyexcelUtil.setCellValue(row, cells.zyqk.colIndex, '')
            }
            if(luckyexcelUtil.checkProperty(cells,'zijian')) {
              luckyexcelUtil.setCellValue(row, cells.zijian.colIndex, '')
            }
            if(luckyexcelUtil.checkProperty(cells,'hujian')) {
              luckyexcelUtil.setCellValue(row, cells.hujian.colIndex, '')
            }
            if(luckyexcelUtil.checkProperty(cells,'zhuanjian')) {
              luckyexcelUtil.setCellValue(row, cells.zhuanjian.colIndex, '')
            }
          }
        }
      })
      // 保存exceljs
      this.saveWorkRecord()
    },
    otransFerDateMkey(){
      this.oTransferDateMKey++;
      this.$nextTick(()=>{
        this.$refs.outInfoForm.validateField('transferDateM')
      })
    },
    openWindowEvent() {
      const maximoAssetChangeUrl = this.maximoAssetChangeUrl
      if (!maximoAssetChangeUrl){
        this.$message.warn('该工单暂无设备更换链接，请稍候尝试')
      } else {
        window.open(maximoAssetChangeUrl)
      }
    },
    saveFaultInfo() {
      this.loading = true
      this.$refs.breakdownForm.save()
    },
    onFaultSaveOk() {
      this.$message.success('保存成功')
      this.loading = false
    },
    onFaultSaveFail() {
      this.$message.success('保存失败')
      this.loading = false
    },
    onFaultSaveCancel () {
      this.loading = false
    },
    openSupplierModal(type) {
      if (type === 1) {
        this.supplierSelectType = 1
        this.$refs.supplierModal.showModal()
        this.$refs.mySupplierSelectOut.blur()
      } else {
        this.supplierSelectType = 2
        this.$refs.supplierModal.showModal()
        this.$refs.mySupplierSelectIn.blur()
      }
    },
    supplierSelect(data) {
      if (!everythingIsEmpty(data)) {
        if (this.supplierSelectType === 1) {
          this.outModelInfo.supplierId = data[0].id
          this.outModelInfo.supplierName = data[0].name
          this.$refs.outInfoForm.validateField('supplierId')
        } else {
          this.inModelInfo.supplierId = data[0].id
          this.inModelInfo.supplierName = data[0].name
          this.$refs.inInfoForm.validateField('supplierId')
        }
      }
    },
    onChangeTab(key) {
      if (this.readed || this.readOnly) {
        this.curTabKey = key
        if (this.curTabKey === '2') {
          this.loadCurForm()
        }
        this.saveAssetNo()
        this.saveCustomForm(false)
      } else {
        this.curTabKey = '1'
        this.$message.warning('请求先认真阅读作业要求')
      }
    },
    onReadClick() {
      saveSafeAssumeRead({
        safeAssumeId: this.requirement.safetyExpectationId,
        orderTaskId: this.selectTask.id,
      }).then((res) => {
        if (res.success) {
          //.....
          this.selectTask.taskStatus = 1
          // 修改任务状态
          this.$emit('changeTaskStatus', { status: 1, text: '已开始' })
        } else {
          this.$message.warn('保存安全预想阅读记录失败')
          console.error('保存安全预想阅读记录失败：', res.message)
        }
        this.readed = true
      })
    },
    // 检查
    handleCheck(type, title) {
      if(this.curForm.workRecordType===1) { // 检查记录表
        let selectRecords = this.$refs.recordListTable.getCheckboxRecords()
        if (selectRecords.length > 0) {
          let idsStr = selectRecords.map((obj, index) => {
            return obj.id
          }).join(',')
          this.checkModalTitle = title
          let colIndex = this.checkTypeMap['C' + type]
          this.$refs.checkModalForm.showModal(idsStr, type, colIndex, this.curForm)
        } else {
          this.$message.error('请勾选要填写检查信息的作业记录明细!')
        }
      } else if (this.curForm.workRecordType === 2) { // 作业记录表
        if (!this.dataJson) {
          // 这个表单没有结构描述数据
          this.$message.warn('该表单不是标准格式的作业记录表，无法自动查找写入位置，请自行手动填写或重新上传标准格式模板')
          return
        }

        let checkRows = this.getSelectCheckRow()

        if (checkRows.length > 0) {
          this.checkModalTitle = title
          let colIndex = this.checkTypeMap['C' + type]
          this.$refs.checkModalForm.showModal(checkRows, type, colIndex, this.curForm)
        } else {
          this.$message.error('请选择要填写检查信息的数据行!')
        }
      }
    },
    // 获取当前选择的作业记录的行
    getSelectCheckRow () {
      let cells = luckysheet.getRangeValuesWithFlatte()
      // 用户存储选中的格子中，有哪些是合并单元格的，如果是合并单元格的辅格子，则不进行填充
      let mCells = []

      if (cells && cells.length > 0) {
        cells.forEach(cell => {
          if (luckyexcelUtil.checkProperty(cell, 'mc')) {
            const mc = cell.mc
            if (luckyexcelUtil.checkProperty(mc, 'rs') && luckyexcelUtil.checkProperty(mc, 'cs')) {
              const start=mc.r+1, end = mc.r + mc.rs - 1
              for(let i=start;i<=end;i++) {
                mCells.push(i)
              }
            }
          }
        })
      }

      let rows = []
      cells = luckysheet.getRangeWithFlatten()
      if (cells && cells.length > 0) {
        cells.forEach(cell=>{
          if(mCells.indexOf(cell.r) < 0 && !rows.includes(cell.r) ) { // 只有不在合并单元格中的格子才写入值
            rows.push(cell.r)
          }
        })
      }

      // 检查选择的行是否超出
      if (this.dataJson && luckyexcelUtil.checkProperty(this.dataJson,'checkDetail')) {
        let checkDetail = this.dataJson.checkDetail
        let outs = rows.filter(rIndex => rIndex < checkDetail.rowIndex || rIndex > checkDetail.rowIndexTo)
        if (outs.length>0) {
          this.$message.error('你选择的区域已超出检查项范围，请正确选择!')
          return []
        }
      }
/*
      if (!rows || rows.length < 1) {
        this.$message.error('请选取要填写检查信息的记录行!')
      }*/
      // console.log('rows:',mCells, rows)
      return rows
    },
    // 检查回调
    onCheckRecord(result) {
      if (result.saveSuccess) {
        if(this.curForm.workRecordType === 1) {  // 检查表回调
          this.loadRecordFormDetail()
        } else if (this.curForm.workRecordType === 2) {  // 作业记录表回调
          // result.data
          // 将相关数据写入excel
          const ids = result.data.detailIds
          ids.forEach(row => {
            // 写入检查姓名
            if(!result.data.isIgnore) { // 不是忽略
              let userName = result.data.checkUserName
              if (result.data.checkUserInfo) {
                userName = result.data.checkUserInfo.realname
                // console.log("result.data.checkUserInfo.togetherCheckUserNames")
                // console.log(result.data.checkUserInfo.togetherCheckUserNames)
                if (result.data.checkUserInfo.togetherCheckUserNames){
                  userName += (',' + result.data.checkUserInfo.togetherCheckUserNames)
                }
              }
              // console.log("userName")
              // console.log(userName)
              luckyexcelUtil.setCellValue(row, result.data.colIndex, userName)
            }
            // 写入结果和作业情况
            if(luckyexcelUtil.checkProperty(this.dataJson,'checkDetail')
              && luckyexcelUtil.checkProperty(this.dataJson.checkDetail,'rows')
              && this.dataJson.checkDetail.rows.length>0
              && luckyexcelUtil.checkProperty(this.dataJson.checkDetail.rows[0],'cells')
             ) {
              const cells = this.dataJson.checkDetail.rows[0].cells
              if(luckyexcelUtil.checkProperty(cells,'jg')){
                if(!result.data.isIgnore) {
                  if (result.data.result === 1) {
                    luckyexcelUtil.setCellValue(row, cells.jg.colIndex, '√')
                  } else {
                    luckyexcelUtil.setCellValue(row, cells.jg.colIndex, '×')
                  }
                }
              }
              if(luckyexcelUtil.checkProperty(cells,'zyqk')) {
                let resultDesc = result.data.resultDesc
                luckyexcelUtil.setCellValue(row, cells.zyqk.colIndex, resultDesc)
              }
              // 忽略设置成横线
              if (result.data.isIgnore === 1) {
                // 如果是忽略，将互检/专检/结果设置成
                luckyexcelUtil.setCellValue(row, cells.jg.colIndex, '——')
                luckyexcelUtil.setCellValue(row, cells.zijian.colIndex, '——')
                luckyexcelUtil.setCellValue(row, cells.hujian.colIndex, '——')
                luckyexcelUtil.setCellValue(row, cells.zhuanjian.colIndex, '——')
                // 设置水平居中
                luckyexcelUtil.setRangeFormat('ht', 0, row, luckyexcelUtil.index2Chart(cells.jg.colIndex), row, luckyexcelUtil.index2Chart(cells.zhuanjian.colIndex))
                luckyexcelUtil.setRangeFormat('vt', 0, row, luckyexcelUtil.index2Chart(cells.jg.colIndex), row, luckyexcelUtil.index2Chart(cells.zhuanjian.colIndex))
              }
            }
          })
          // 保存exceljs
          this.saveWorkRecord(false)

        }
        this.getFormsItem()
      }
    },
    // 保存作业记录表数据（excel)
    saveWorkRecord (tips=false) {
      // 保存exceljs
      let dataJson = JSON.stringify(luckysheet.getAllSheets()[0].data)
      const saveObj = { formInstId: this.curForm.formInstId, xlsJson: dataJson }
      saveWorkRecordExcelData(saveObj).then(res => {
        if (!res.success) {
          if(tips) {
            this.$message.error('保存作业记录表信息失败')
          }
          console.error('保存作业记录表excel信息失败', res)
        } else {
          if (tips) {
            this.$message.success('保存作业记录表信息成功')
          }
          console.log('保存作业记录表excel信息成功')
        }
      })
    },
    openPlanModel() {
      this.$refs.planModalForm.showModal()
    },
    openOrderModel() {
      this.$refs.workOrderSelect.showModal()
    },
    onSelectPlan(data) {
      this.queryParam.planId = data[0].id
      this.queryParam.planName = data[0].planName
    },
    changePlanSelect(value) {
      if (!value) {
        this.queryParam.planId = ''
      }
    },
    onSelectOrder(data) {
      this.queryParam.workOrderId = data[0].id
      this.queryParam.orderName = data[0].orderName
    },
    changeOrderSelect(value) {
      if (!value) {
        this.queryParam.workOrderId = ''
      }
    },
    // 加载工单任务，默认取第一条
    loadTaskList() {
      this.selectTask = null
      this.queryParam.workOrderId = this.businessKey

      getWorkTaskList(this.queryParam).then((res) => {
        if (res.success) {
          let tasks = res.result
          if (tasks && tasks.length > 0) {
            // 找到第一还没有完成的任务
            let uTask = tasks.find((t) => t.taskStatus !== 2)
            if (uTask) {
              this.selectTask = uTask
            } else {
              this.selectTask = tasks[0]
            }
            this.onTaskSelectChange()
          }
        } else {
          this.$message.warning(res.message)
        }
        this.$emit('loaded')
      })
    },
    // 任务选择改变
    async onTaskSelectChange() {
      this.saveAssetNo()
      this.saveCustomForm(false)
      // 这里要判断任务是否为委外任务，如果是委外，需要看委外的处理界面
      // row.outTask 0 不是委外任务 1 委外送修任务 2 委外接收任务 3 其他委外任务
      this.readed = false
      this.curTabKey = '1'
      this.taskFormDetail = []
      this.checkFormDetail = []

      this.$nextTick(() => {
        if (this.$refs.outInfoForm) {
          this.$refs.outInfoForm.clearValidate()
        }
        if (this.$refs.inInfoForm) {
          this.$refs.inInfoForm.clearValidate()
        }
      })
      await this.getTaskInfo(this.selectTask.id)
      // 获取任务的表单信息
      await this.getTaskForms(this.selectTask.id)

      // 计划任务
      if (this.selectTask.outTask === 0) {
      }
      if (this.selectTask.outTask === 1) {
        // 委外送修
        this.getOutTaskInfo(this.selectTask.id)
      }
      if (this.selectTask.outTask === 2) {
        // 委外接收
        this.getInTaskInfo(this.selectTask.id)
      }
      // 故障任务
      if (this.selectTask.taskType === 2 && this.selectTask.outTask === 0) {
        // 故障任务
        this.$nextTick(() => {
          getBreakdownInfo({ id: this.selectTask.taskObjId }).then((res) => {
            let data = res.result
            // 故障处理界面，默认是已处理
            if (data.status !== 1) {
              data.status = 1
            }
            this.$refs.breakdownForm.edit(data)
          })
        })
      }

      // 监修任务
      if (this.selectTask.taskType === 8 && this.selectTask.outTask === 0) {
        this.loadSupervise()
      }
    },
    // 获取任务及关联信息
    async getTaskInfo(id) {
      await getTaskRelevanceInfo(id).then((res) => {
        // console.log('get task info:', res)
        if (res.success) {
          let materials = res.result.materials
          if(materials && materials.length){
            Array.from(materials,(item,index)=>{
              if(item.actAmount <= 0){
                if((item.materialActsList && item.materialActsList.length<=0) && !item.remark){
                  item.actAmount = '';
                }
              }
            })
          }
          this.materials = materials
          this.requirement = res.result.requirement
          this.tools = res.result.tools
          this.files = res.result.otherData
          this.attachedfiles = res.result.annexList
          this.regus = res.result.regus
          this.bookSteps = res.result.bookSteps
          this.mustReplaces = res.result.mustReplaces
          this.staffArranges = res.result.staffArranges
          this.taskInfo = res.result.task
          this.workOrderInfo = res.result.workOrder
          this.selectTask = res.result.task
          this.maximoAssetChangeUrl = res.result.maximoAssetChangeUrl

          this.setBookStepDom()
        }
      })
    },
    // 获取委外任务-出库信息
    getOutTaskInfo(id) {
      getOutTaskInfo({ id: id }).then((res) => {
        if (res.success) {
          this.outModelInfo = res.result

          if (this.outModelInfo.transferDate) {
            this.outModelInfo.transferDateM = this.$moment(this.outModelInfo.transferDate)
          }
          if (this.outModelInfo.lineId) {
            this.dictTrainStr =
              'bu_train_info,train_no,train_no,line_id=' + this.outModelInfo.lineId + '|train_struct_id'
          }
          if (!this.outModelInfo.id) {
            // 如果没有id
            Object.assign(this.outModelInfo, {
              lineId: this.workOrderInfo.lineId,
              trainNo: this.workOrderInfo.trainNo,
              transferDateM: this.$moment(this.workOrderInfo.startTime),
              sendGroupId: this.workOrderInfo.groupId,
              workOrderId: this.workOrderInfo.id,
              orderTaskId: this.selectTask.id,
            })
          }

          if (!this.outModelInfo.contractId) {
            this.outIsSave = false
          } else {
            this.outIsSave = true
          }

          /*getTaskRelevanceInfo(id).then((rs) => {
            if (rs.success) {
              this.taskInfo = rs.result.task
              this.workOrderInfo = rs.result.workOrder
              this.selectTask = rs.result.task
              if (!this.outModelInfo.id) {
                // 如果没有id
                Object.assign(this.outModelInfo, {
                  lineId: this.workOrderInfo.lineId,
                  trainNo: this.workOrderInfo.trainNo,
                  transferDateM: this.$moment(this.workOrderInfo.startTime),
                  sendGroupId: this.workOrderInfo.groupId,
                  workOrderId: this.workOrderInfo.id,
                  orderTaskId: this.selectTask.id
                })
              }
            }
          })*/
        }
      })
    },
    // 获取委外任务-入库信息
    getInTaskInfo(id) {
      getOutTaskInfo({ id: id }).then((res) => {
        if (res.success) {
          this.inModelInfo = res.result
          if (this.inModelInfo.transferDate) {
            this.inModelInfo.transferDateM = this.$moment(this.inModelInfo.transferDate)
          }
          if (this.inModelInfo.lineId) {
            this.dictTrainStr =
              'bu_train_info,train_no,train_no,line_id=' + this.inModelInfo.lineId + '|train_struct_id'
          }
          if (!this.inModelInfo.id) {
            // 如果没有id
            Object.assign(this.inModelInfo, {
              lineId: this.workOrderInfo.lineId,
              trainNo: this.workOrderInfo.trainNo,
              transferDateM: this.$moment(this.workOrderInfo.startTime),
              sendGroupId: this.workOrderInfo.groupId,
            })
          }

          if (!this.outModelInfo.contractId) {
            this.inIsSave = false
          } else {
            this.inIsSave = true
          }

          /*
          getTaskRelevanceInfo(id).then((rs) => {
            if (rs.success) {
              this.taskInfo = rs.result.task
              this.workOrderInfo = rs.result.workOrder
              this.selectTask = rs.result.task
              if (!this.inModelInfo.id) {
                // 如果没有id
                Object.assign(this.inModelInfo, {
                  lineId: this.workOrderInfo.lineId,
                  trainNo: this.workOrderInfo.trainNo,
                  transferDateM: this.$moment(this.workOrderInfo.startTime),
                  sendGroupId: this.workOrderInfo.groupId
                })
              }
            }
          })*/
        }
      })
    },
    // 获取任务的表单
    async getTaskForms(id) {
      this.curForm = null
      this.taskFormsList = []
      this.curFormIndex = 0
      return getTaskRecordForm({ taskId: id }).then((res) => {
        if (res.success) {
          this.taskFormsList = res.result
          this.getFormsItem()
          // 设置当前表单明细
          this.selectForm(0)
        }
      })
    },
    // 当前表单改变
    selectForm(index) {
      this.curFormIndex = index
      this.saveAssetNo()
      this.saveCustomForm(false)
      if (this.taskFormsList && this.taskFormsList.length > 0) {
        this.curForm = this.taskFormsList[this.curFormIndex]
        this.loadCurForm()
      }
    },

    // 当前表单所有的检查明细
    getFormsItem() {
      this.taskFormDetail = []
      this.checkFormDetail = []
      this.taskFormsList.forEach(async (form) => {
        if (form.instType === 3) {
          if(form.workRecordType === 1) {
            getTrainPlanRecordFormById({
              task2InstId: form.id,
              formInstId: form.formInstId,
              needCategory: false,
              needChecks: true,
              orderTaskId: this.selectTask.id,
            }).then((res) => {
              if (res.success && res.result) {
                this.taskFormDetail.push(...res.result.detailList)
                this.countFormBadge(form)
              } else {
                this.$message.error('获取表单作业明细失败')
              }
            })
          } else if (form.workRecordType === 2) {
            let datajson = null
            await getDataJson(form.formObjId).then(res=>{
              datajson = JSON.parse(res.result)
            })
            if(!datajson){
               // 这个记录表没有结构描述数据, 直接结束
              return
            }
            // console.log('datajson:', datajson)

            // 获取数据库中的明细数据
            getTrainPlanRecordFormById({
              task2InstId: form.id,
              formInstId: form.formInstId,
              needCategory: false,
              needChecks: true,
              orderTaskId: this.selectTask.id
            }).then((res) => {
              if (res.success && res.result && res.result.detailList && res.result.detailList.length>0) {
                if (datajson.checkDetail && res.result.detailList.length === (datajson.checkDetail.rows.length - 1)) {
                  // 已有明细数据，且条数一样，说明都已填写了
                  this.taskFormDetail.push(...res.result.detailList)
                } else {
                  // 数量不一样，补差
                  let details = []
                  details.push(...res.result.detailList)
                  for (let i = 1; i < datajson.checkDetail.rows.length; i++) {
                    const row = datajson.checkDetail.rows[i]
                    let exist = details.filter(d => d.detailLineNum === row.zynr.rowIndex)
                    if (exist.length === 0) {
                      // 没有这个行号的哦,加入
                      details.push({
                        'id': '',
                        "workRecordId": form.formInstId,
                        "detailLineNum": row.zynr.rowIndex,
                        "selfCheck": null,
                        "guarderCheck": null,
                        "monitorCheck": null,
                        "isIgnore": 0
                      })
                    }
                  }
                  this.taskFormDetail.push(...details)
                }
              } else {
                // 咩有明细数据
                let details = []
                for (let i = 1; i < datajson.checkDetail.rows.length; i++) {
                  const row = datajson.checkDetail.rows[i]
                  details.push({
                    'id': '',
                    "workRecordId": form.formInstId,
                    "detailLineNum": row.zynr.rowIndex,
                    "selfCheck": null,
                    "guarderCheck": null,
                    "monitorCheck": null,
                  })
                }
                this.taskFormDetail.push(...details)
              }
              this.countFormBadge(form)
            })
          }
        } else if (form.instType === 4) {
          getCheckRecordDetail({
            task2InstId: form.id,
            formInstId: form.formInstId
          }).then((res) => {
            if (res.success && res.result) {
              this.checkFormDetail.push(...res.result)
              this.countFormBadge(form)
            } else {
              this.$message.error('获取表单检查明细失败')
            }
          })
        }
      })
    },
    // 根据ID获取表单徽标数
    countFormBadge(form) {
      let count = 0
      if (form.instType === 3) { // 作业记录表
        this.taskFormDetail.forEach((element) => {
          if (form.formInstId === element.workRecordId && (!element.isIgnore || element.isIgnore !== 1)) {
            if (!element.selfCheck || !element.monitorCheck || !element.guarderCheck) {
              count++
            }
          }
        })
      } else if (form.instType === 4) {
        this.checkFormDetail.forEach((element) => {
          if (form.formInstId === element.checkId && (!element.checkUserId || !element.workTime)) {
            count++
          }
        })
      }
      // return count
      form.alert = count
      this.$forceUpdate()
    },
    // 设置当前选中表单相关数据
    /* initForm() {
       if (!this.curForm) {
         return
       }
       this.loadCopyFormDetail()
     },*/
    // 改变当前表单的第几份
    /* changeFormIndex(value) {
       luckysheet.destroy()
       this.curCopyIndex = value
       // this.curForm = this.taskFormsList[this.curFormIndex]
       this.loadCopyFormDetail()
     },*/
    // 加载当前表单明细
    loadCurForm() {
      // 如果是不作业填报tab页，则不加载？
      if (this.curTabKey !== '2') {
        return
      }

      // 1 计划任务  2 故障任务  3  整改任务  4 作业项任务  5 自建任务 6 委外送修任务 7 委外接收任务  8 监修任务
      /* if ([2,6,7,8].indexOf(this.selectTask.taskType)>-1 ) {
        return
      }*/

      if (!this.taskFormsList || this.taskFormsList.length === 0 || !this.curForm) {
        return
      }
      try {
        luckysheet.destroy()
      } catch (e) {
        console.error('释放表格组件异常：', e)
      }
      // console.log('loadCurForm:', this.curForm)

      // 作业记录表
      if (this.curForm.instType === 3) {
        if (this.curForm.workRecordType === 1) {
          this.loadRecordFormDetail()
        } else if(this.curForm.workRecordType ===2){
          this.loadWorkRecordDetail()
        }
      }
      // 在线自定义表单
      if (this.curForm.instType === 1) {
        this.loadOnlineFormDetail()
      }

      // 检查记录表
      if (this.curForm.instType === 4) {
        // this.loadCheckRecordForm()
        this.$nextTick(() => {
          this.$refs.checkRecordForm.findList(this.curForm.id, this.curForm.formInstId)
        })
      }
    },
    // 获取作业记录明细
    loadRecordFormDetail() {
      // 获取作业记录表明细
      const params = {
        task2InstId: this.curForm.id,
        formInstId: this.curForm.formInstId,
        needChecks: true,
        needCategory: false,
        orderTaskId: this.selectTask.id,
      }
      getTrainPlanRecordFormById(params)
        .then((res) => {
          if (res.success) {
            this.curRecordForm = res.result
          } else {
            this.$message.error('获取作业记录表内容错误')
            console.error('获取作业记录表内容错误', res.message)
          }
        })
        .catch((err) => {
          this.$message.error('获取作业记录表内容异常')
          console.error('获取作业记录表内容异常', err)
        })
    },
    // 获取新版的作业记录表
    loadWorkRecordDetail () {
      // 1. 判断是否有excel数据
      // 2. 如果咩有，则从模板中加载，包括datajson
      // 获取模板
      getFormContent(this.curForm.formObjId).then((res) => {
        if (res.success) {
          let sheet = JSON.parse(res.result)
          // 获取数据
          getWorkRecordExcelData(this.curForm.formInstId).then(res => {
            if (res.success) {
              if (res.result) {
                let data = JSON.parse(res.result)
                sheet.data = []
                sheet.celldata = luckysheet.transToCellData(data)
              }
              this.initSheet('sheetWorkRecord', sheet, true)
            } else {
              this.needWriteOther = true
              this.initSheet('sheetWorkRecord', sheet, true)
            }
          })
        } else {
          this.$message.error('获取作业记录表模板数据失败')
          console.error('获取作业记录表模板数据失败', res.message)
        }
      })

      getDataJson(this.curForm.formObjId).then(res=>{
        if (res.success) {
          this.dataJson = JSON.parse(res.result)
          if (this.dataJson && this.dataJson.checkDetail && this.dataJson.checkDetail.rows && this.dataJson.checkDetail.rows.length > 0) {
            let detailTitle = this.dataJson.checkDetail.rows[0]

            if (detailTitle.cells.zijian) {
              this.checkTypeMap['C1'] = detailTitle.cells.zijian.colIndex
            }
            if (detailTitle.cells.hujian) {
              this.checkTypeMap['C2'] = detailTitle.cells.hujian.colIndex
            }
            if (detailTitle.cells.zhuanjian) {
              this.checkTypeMap['C3'] = detailTitle.cells.zhuanjian.colIndex
            }
          }
        }
      })
      // 获取datajson
    },
    // 写入其他公共信息，如：作业班组，作业日期，车号
    writeOtherExcelInfo () {
      if (this.needWriteOther) {
        // let cells = luckysheet.find('车号')
        let cell = luckyexcelUtil.searchCellByText('车号',luckysheet.getAllSheets()[0].data)
        if (cell) {
          let str = luckyexcelUtil.getCellText(cell.cell)
          str = str.replace('车号：', '车号: ' + this.workOrderInfo.trainNo)
          luckyexcelUtil.setCellValue(cell.r,cell.c,str)
        }

        cell = luckyexcelUtil.searchCellByText('作业日期',luckysheet.getAllSheets()[0].data)
        if (cell && this.workOrderInfo.actStart) {
          let str = luckyexcelUtil.getCellText(cell.cell)
          str = str.replace('作业日期：', '作业日期: '+ this.$moment(this.workOrderInfo.actStart).format('YYYY-MM-DD'))
          luckyexcelUtil.setCellValue(cell.r,cell.c,str)
        }

        /*cell = luckyexcelUtil.searchCellByText('完工日期',luckysheet.getAllSheets()[0].data)
        if (cell) {
          let str = luckyexcelUtil.getCellText(cell.cell)
          str = str.replace('完工日期: ', '完工日期: 2021-10-18')
          luckyexcelUtil.setCellValue(cell.r,cell.c,str)
        }*/

        cell = luckyexcelUtil.searchCellByText('作业班组',luckysheet.getAllSheets()[0].data)
        if (cell) {
          let str = luckyexcelUtil.getCellText(cell.cell)
          let groupName = ''
          if (this.$store.getters.userInfo.departName) {
            groupName = this.$store.getters.userInfo.departName
          }
          str = str.replace('作业班组：', '作业班组: ' + groupName)
          luckyexcelUtil.setCellValue(cell.r,cell.c,str)
        }
      }
    },
    //  获取在线表单明细
    loadOnlineFormDetail() {
      const params = {
        task2InstId: this.curForm.id,
        formInstId: this.curForm.formInstId,
        needValues: false,
      }
      getTrainPlanOnlineFormById(params)
        .then((re) => {
          if (re.success) {
            // 改成记录值里面只记录数据，excel的其他信息不记录
            this.curOnlineForm = re.result
            let data = null
            if (this.curOnlineForm) {
              if (this.curOnlineForm.result) {
                // result 不为null 表示填写过
                try {
                  data = JSON.parse(this.curOnlineForm.result)
                } catch (e) {
                  this.$message.error('转换表单填写结果异常')
                  console.error('转换表单填写结果异常', this.curOnlineForm.result)
                  return
                }
              }
              // 获取模板
              getFormContent(this.curOnlineForm.formObjId).then((res) => {
                if (res.success) {
                  let sheet = JSON.parse(res.result)
                  if (data) {
                    sheet.data = []
                    sheet.celldata = luckysheet.transToCellData(data)
                  }
                  this.initSheet('sheet', sheet, false)
                } else {
                  this.$message.error('获取表单模板数据失败')
                  console.error('获取表单模板数据失败', res.message)
                }
              })
            } else {
              this.$message.error('获取作业表单数据失败')
              console.error('获取作业表单数据失败：', re.message)
            }
          } else {
            // 没有获取到记录表的结果， 获取原始表单定义
            this.$message.error('获取记录表内容错误')
            console.error('获取记录表内容错误', res.message)
          }
        })
        .catch((err) => {
          this.$message.error('获取记录表内容异常')
          console.error('获取记录表内容异常', err)
        })
    },
    // 初始化自定义表单 containerId 绘制EXCEL的容器id， sheet excel 的sheet数据， 是否为作业记录表
    initSheet(containerId,sheet,isWorkRecord=false) {
      sheet.scrollLeft=0
      sheet.scrollTop = 0
      const options = clone(this.defaultOptions)
      options.container = containerId


      // 任务已经完成的不能编辑
      if (this.selectTask && this.selectTask.taskStatus === 2) {
        this.showSheetSave = false
        options.showtoolbar=false
        options.sheetFormulaBar = false
        options.showtoolbarConfig = {}
        if (sheet.config['authority']) {
          sheet.config['authority']['allowRangeList'] = []
        }
      } else {
        this.showSheetSave = true
      }
      options.data = [sheet]

      luckysheet.destroy()
      luckysheet.create(options)

      if(!isWorkRecord) {
        // 设置条件格式
        setTimeout(() => {
          luckyexcelUtil.setRangeConditionalOncell(this.curForm.formObjId, this.curForm.formInstId)
        }, 200)
      }

      // 能编辑情况下，绑定保存
      if (this.selectTask && this.selectTask.taskStatus !== 2) {
        // 不用工具栏中的按钮了，自己添加了一个浮动的按钮，所以不用这个了
        /*setTimeout(() => {
          if(document.querySelector('#luckysheet-icon-save')) {
            document.querySelector('#luckysheet-icon-save').addEventListener('click', () => {
              this.saveCustomForm()
            })
          }
        }, 1000)*/
      }

      luckysheet.setRangeShow('A1', { show: false })
    },
    resizeSplitter() {
      setTimeout(() => {
        // console.log('resize:', $('#luckysheet').width())
        if (this.curForm && this.curForm.instType === 1) {
          luckysheet.resize()
        }
      }, 100)
    },
    // 保存自定义表单数据
    saveCustomForm(tips = true) {
      // alert('save')
      // this.form.content = JSON.stringify(luckysheet.getAllSheets()[0])
      if (!luckysheet.getAllSheets() || luckysheet.getAllSheets().length <= 0) {
        return
      }
      if (this.curForm.instType === 3 ) {
        if (this.curForm.workRecordType === 2) {
          this.saveWorkRecord(tips)
        }
        return
      }
      luckysheet.exitEditMode() // 退出编辑模式
      const saveForm = clone(this.curOnlineForm)
      saveForm.result = JSON.stringify(luckysheet.getAllSheets()[0].data)
      saveForm.writeDate = this.$moment().format('YYYY-MM-DD hh:mm:ss')
      saveForm.writeUserId = this.$store.getters.userInfo.id
      saveForm.writeGroupId = this.$store.getters.userInfo.departId
      saveForm.orderId = this.selectTask.orderId
      saveForm.orderTaskId = this.selectTask.id

      saveFormValue(saveForm)
        .then((res) => {
          if (res.success) {
            if (tips) {
              this.$message.success('保存成功')
            }
          } else {
            if (tips) {
              this.$message.error('保存失败')
            }
            console.error('保存表单值失败：', res.message)
          }
        })
        .catch((err) => {
          this.$message.error('保存异常')
          console.error('保存表单值异常：', err)
        })
    },
    // 检查作业物资填报状况
    async checkMaterialList() {
      let checkResult = {
        success: true,
        message: [],
      }
      if (
        !(
          this.selectTask.outTask === 0 &&
          (this.selectTask.taskType === 1 ||
            this.selectTask.taskType === 3 ||
            this.selectTask.taskType === 4 ||
            this.selectTask.taskType === 5)
        ) &&
        !(this.selectTask.taskStatus !== 2 && this.curTabKey === '2')
      ) {
        // return true  调用此函数的地方报错！
        return checkResult
      }
      // let isOk = true
      this.materials.forEach((e) => {
        if (e.amount !== 0 && e.actAmount > e.amount && (e.opType === 1 || (e.opType === 2 && e.isGenOrder == 1))) {
          checkResult.success = false
          checkResult.message.push(`${e.name}的实际消耗数量大于所需数量`)
        }
      })
      return checkResult
    },
    //返回实际消耗为0的物料列表
    async getMaterialUseNullList(){
      let messageList = [] // 实际数量填写为0的物资列表
      let checkResult = {
        success: false, // 是否有填写为0的物资
        reportNum: 0, // 没有填写实际消耗的物资数量
        tips:[],  // 没有填写实际消耗的物资
        message: '',
      }
      this.materials.forEach((e) => {
        if(typeof e.actAmount === 'string'){
          checkResult.reportNum++;
          checkResult.tips.push(`请填写物资：${e.code} 的实际消耗`)
        }else if (e.amount !== 0 && e.actAmount == 0 && (e.opType === 1 || (e.opType === 2 && e.isGenOrder == 1))) {
          checkResult.success = true
          messageList.push(e.name)
        }
      })
      messageList.forEach((e, index) => {
        checkResult.message += `${e}${ index < messageList.length - 1 ? '、' : ' '}`
      })
      return checkResult
    },
    //返回人员工时0的列表
    async getWorkstationUserTimeNullList(){
      let messageList = [] // 人员工时填写为0的列表
      let checkResult = {
        success: false, // 是否有填写为0的人员工时
        reportNum: 0, // 没有填写人员工时的人员工时数据数量
        tips:[],  // 没有填写人员工时的数据
        message: '',
      }

      if (!this.staffArranges || this.staffArranges.length === 0) {
        checkResult.success = true
        checkResult.message = '人员工时不能为空，请填写'
      } else {
        this.staffArranges.forEach((e) => {
          if (typeof e.workTime === 'string') {
            checkResult.reportNum++;
            checkResult.tips.push(`请填写人员：${e.userName} 的实际工时`)
          } else if (e.workTime === 0) {
            checkResult.success = true
            messageList.push(e.userName)
          }
        })
        messageList.forEach((e, index) => {
          checkResult.message += `${e}${ index < messageList.length - 1 ? '、' : ' '}`
        })
      }
      return checkResult
    },
    // 保存前检查作业表单是否完成
    async checkTaskCanSubmit(checkType = 1) {
      // checkType 1：作业记录表  2：作业检查表
      let isOk = true
      let result = {
        success: true,
        forms: [],
        records: [],
        checkFomrs: [],
      }
      let list = []

      if (!this.taskFormsList || this.taskFormsList.length === 0) {
        return result
      }

      this.taskFormsList.forEach((element) => {
        if (element.instType === 3) {
          if (checkType === 1) {
            list.push(
              getTrainPlanRecordFormById({
                task2InstId: element.id,
                formInstId: element.formInstId,
                needCategory: false,
                needChecks: true,
                orderTaskId: this.selectTask.id,
              }).then((res) => {
                if (res.success && res.result) {
                  result.forms.push(res.result)
                  return res.result.detailList
                } else {
                  this.$message.error('获取表单作业明细失败')
                  return []
                }
              })
            )
          }
        }
        if (element.instType === 4) {
          if (checkType === 2) {
            list.push(
              getCheckRecordDetail({
                task2InstId: element.id,
                formInstId: element.formInstId
              }).then((res) => {
                result.forms.push(res.element)
                if (res.success && res.result) {
                  return res.result
                } else {
                  this.$message.error('获取表单作业明细失败')
                  return []
                }
              })
            )
          }
        }
      })
      return Promise.all(list).then((detailList) => {
        detailList.forEach((detailInfos) => {
          if (checkType === 1) {
            for (let index = 0; index < detailInfos.length; index++) {
              const element = detailInfos[index]
              if (!element.isIgnore || element.isIgnore !== 1) {
                if (!element.selfCheck || !element.monitorCheck || !element.guarderCheck) {
                  // 检查不通过
                  result.success = false
                  isOk = false
                  result.records.push(element)
                }
              }
            }
            if (result.forms.length > 0) {
              let checkFomrs = []
              result.records.forEach((element) => {
                if (!checkFomrs.find((e) => e.formInstId === element.workRecordId)) {
                  checkFomrs.push(this.taskFormsList.find((f) => f.formInstId === element.workRecordId))
                }
              })
              result.checkFomrs = checkFomrs
              result.formsName = checkFomrs.map((e) => `《${e.formName}》`).toString()
            }
          }
          if (checkType === 2) {
            for (let index = 0; index < detailInfos.length; index++) {
              const element = detailInfos[index]
              if (!element.checkUserId || !element.workTime) {
                // 检查不通过
                result.success = false
                isOk = false
                result.records.push(element)
              }
            }
            if (result.records.length > 0) {
              let checkFomrs = []
              result.records.forEach((element) => {
                if (!checkFomrs.find((e) => e.formInstId === element.checkId)) {
                  checkFomrs.push(this.taskFormsList.find((e) => e.formInstId === element.checkId))
                }
              })
              result.checkFomrs = checkFomrs
              result.formsName = checkFomrs.map((e) => `《${e.formName}》`).toString()
            }
          }
        })
        return result
      })
    },
    async getCheckValue() {
      let checkValue = true
      // 故障填报
      if (this.selectTask && this.selectTask.taskType === 2) {
        if (this.$refs.breakdownForm && this.$refs.breakdownForm.check) {
          await this.$refs.breakdownForm
            .check()
            .then(() => {
              checkValue = true
            })
            .catch((err) => {
              checkValue = false
              this.$message.warn(err)
            })
        }
      }

      // 委外- 送修
      if (this.selectTask && this.selectTask.taskType === 6) {
        if (!this.outIsSave) {
          this.$message.warn('你还没有保存委外交接信息，不能提交任务')
        }
        checkValue = this.outIsSave
      }

      // 委外- 返场
      if (this.selectTask && this.selectTask.taskType === 7) {
        if (!this.inIsSave) {
          this.$message.warn('你还没有保存委外交接信息，不能提交任务')
        }
        checkValue = this.inIsSave
      }

      // 监修任务
      if (this.selectTask && this.selectTask.taskType === 8) {
        if (!this.superviseModel) {
          this.$message.warn('你还没有保存监修申请，不能提交任务')
          checkValue = false
        }
      }

      if (!checkValue) {
        this.curTabKey = '3'
      }

      return checkValue
    },
    // 任务提交
    async submitTask() {
      this.saveAssetNo()
      this.saveCustomForm(false)
      if (!this.readed) {
        this.$message.warn('请先确认已阅读作业要求')
        return
      }
      if (this.selectTask.taskStatus === 0) {
        this.$message.warn('请先确认已阅读作业要求，并完成填报操作')
        return
      }

      // 检查作业填报是否有待填写的数据
      let ckValue = await this.getCheckValue()
      if (!ckValue) {
        return
      }
      // 检查物料是否填写
      let isTaskMaterialOk = await this.checkMaterialList()
      if (!isTaskMaterialOk.success) {
        isTaskMaterialOk.message.forEach((e) => {
          this.$message.warn(isTaskMaterialOk.message)
        })
        this.curTabKey = '4'
        return
      }
      let confirms = []
      // 检查作业记录表是否填写完成
      let isOk = await this.checkTaskCanSubmit(1)
      if (!isOk.success) {
        //this.$message.warn(`${isOk.formsName}未填写完成，请填写！`)
        this.curTabKey = '2'
        confirms.push('作业记录表：' + isOk.formsName + '未填写完成!')
        /*this.tabFormKey = this.taskFormsList.findIndex((e) => e == isOk.checkFomrs[0])
        this.selectForm(this.tabFormKey)
        return*/
      }

      // 检查检查记录表是否填写完成
      let isOkCheckRecord = await this.checkTaskCanSubmit(2)
      if (!isOkCheckRecord.success) {
        //this.$message.warn(`${isOkCheckRecord.formsName}未完成，请检查！`)
        this.curTabKey = '2'
        confirms.push('检查记录表：' + isOkCheckRecord.formsName + '未填写完成!')
        /*this.tabFormKey = this.taskFormsList.findIndex((e) => e == isOkCheckRecord.checkFomrs[0])
        this.selectForm(this.tabFormKey)
        return*/
      }

      let materialUseCheck = await this.getMaterialUseNullList()
      // 没有填写实际消耗的物资数量
      if(materialUseCheck.reportNum > 0){
        materialUseCheck.tips.forEach((item, index, arr)=>{
          this.$message.warning(item)
        })
        this.curTabKey = '4'
        return
      }
      // 是否有物资填写为0的物资
      let materialZero = materialUseCheck.success ? `${materialUseCheck.message} 实际消耗为0!`:''
      if (materialZero) {
        confirms.push(materialZero)
      }
      confirms.push('你确定提交该任务？')

      let workstationUserTimeCheck = await this.getWorkstationUserTimeNullList()
      if (workstationUserTimeCheck.success) {
        // 没有填写人员工时的数量
        if(workstationUserTimeCheck.reportNum > 0){
          workstationUserTimeCheck.tips.forEach((item, index, arr)=>{
            this.$message.warning(item)
          })
          this.curTabKey = '9'
          return
        } else {
          this.$message.warning(workstationUserTimeCheck.message)
          this.curTabKey = '9'
          return
        }
      }

      // 如果是监修任务， 检查是否保存检修申请
      if (this.selectTask.taskType === 8 && !this.superviseModel) {
        this.$message.warn('你还没有保存监修申请，不能提交任务')
        this.curTabKey = '3'
        return
      }
      const h = this.$createElement
      this.$confirm({
        width: 500,
        title: '提交确认！',
        content: confirms.map(text=>h('p', null, text)),
        onOk: () => {
          // 如果是监修任务
          if (this.selectTask.taskType === 8) {
            let businessKey = this.superviseModel.id
            let businessTitle = this.superviseModel.orderTaskName + '[监修申请]'
            // 如果是监修任务提交，那么先启动流程，在提交任务
            const params = {
              solutionCode: 'WF_OUT_SUPERVISE',
              businessKey: businessKey,
              title: businessTitle,
              variables: {},
            }

            this.$workflowApi.startSolution(params).then((res) => {
              if (res.success) {
                //  启动成功
                // this.$message.success("启动成功")
                this.submitTheTask()
              } else {
                console.error('提交监修任务时，启动流程失败：', res.message)
                this.$message.error('提交失败')
              }
            })
          } else {
            this.submitTheTask()
          }
        },
      })
    },
    submitTheTask() {
      let formData = {}
      formData.taskId = this.selectTask.id
      submitTask(formData).then((res) => {
        if (res.success) {
          this.$message.success(res.message)
          // 修改任务状态
          this.$emit('changeTaskStatus', { status: 2, text: '已完成' })
          this.$emit('close', 'ok')
        } else {
          this.$message.error(res.message ? res.message : '提交失败')
          console.error('提交检查信息错误：', res.message)
        }
      })
    },
    // 任务提交回调
    onTaskSubmit() {
      // this.loadTaskList()
    },
    // 保存记录表拆装设备
    saveAssetNo() {
      if (!this.curRecordForm) {
        return
      }
      let params = {
        id: this.curRecordForm.id,
        manufNo: this.curRecordForm.manufNo,
        manufNoUp: this.curRecordForm.manufNoUp,
      }

      saveRcordTableAssetNo(params).then((res) => {
        if (res.success) {
          // console.log('保存拆装设备成功')
        } else {
          console.error('保存拆装设备失败')
        }
      })
    },
    // 工具增加删除
    onAddTool() {
      if (this.$refs.ToolsListTable.getActiveRecord()) {
        return
      }
      this.curToolEditMode = 1
      let tool = {
        id: randomUUID(),
        orderTaskId: '',
      }
      if (this.selectTask) {
        Object.assign(tool, {
          orderId: this.selectTask.orderId,
          orderTaskId: this.selectTask.id,
          taskName: this.selectTask.taskName,
        })
      }
      this.$refs.ToolsListTable.insertAt(tool, -1).then(({ row }) => {
        this.$refs.ToolsListTable.setActiveCell(row, 'code')
      })
    },
    editToolRowEvent(row) {
      this.$refs.ToolsListTable.setActiveRow(row)
    },
    saveToolRowEvent(row) {
      this.$refs.ToolsListTable.validate(row, (valid) => {
        if (!valid) {
          saveTool(row)
            .then((res) => {
              if (res.success) {
                // 刷新
                if (this.curToolEditMode === 1) {
                  this.tools.push(row)
                }
                this.$refs.ToolsListTable.clearActived()
                this.curToolEditMode = 0
                this.$message.success('保存成功')
              } else {
                this.$message.error(res.message)
              }
            })
            .catch((err) => {
              this.$message.error('保存工器具异常')
              console.error('保存工器具异常:', err)
            })
          /* this.$refs.ToolsListTable.clearActived()
          this.curToolEditMode = 0*/
        }
      })
    },
    cancelToolRowEvent(row) {
      this.$refs.ToolsListTable.clearActived()
      if (this.curToolEditMode === 1) {
        // 新增,点击取消
        this.$refs.ToolsListTable.remove(row)
      } else if (this.curToolEditMode === 1) {
        // 还原行数据
        this.$refs.ToolsListTable.revertData(row)
      }
      this.curToolEditMode = 0
    },
    onDelTool() {
      const selectRecords = this.$refs.ToolsListTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}数据？`,
          onOk: () => {
            const idArr = selectRecords.map((obj, index) => {
              return obj.id
            })
            const ids = idArr.join(',')
            delTool({ ids: ids })
              .then((res) => {
                if (res.success) {
                  //刷新工器具
                  idArr.map((tid) => {
                    this.tools.map((tool, index) => {
                      if (tid === tool.id) {
                        this.tools.splice(index, 1)
                      }
                    })
                  })
                  this.$message.success('删除成功')
                } else {
                  this.$message.error('删除失败')
                  console.error('删除失败', res.message)
                }
              })
              .catch((err) => {
                this.$message.error('删除异常')
                console.error('删除异常', err)
              })
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },

    //  物料增加 删除
    onAddMaterial() {
      if (this.$refs.MaterialListTable.getActiveRecord()) {
        return
      }
      this.curMaterialEditMode = 1
      let material = {
        id: randomUUID(),
        orderId: '',
        orderTaskId: '',
      }

      if (this.selectTask) {
        Object.assign(material, {
          orderId: this.selectTask.orderId,
          orderTaskId: this.selectTask.id,
          taskName: this.selectTask.taskName,
        })
      }
      this.$refs.MaterialListTable.insertAt(material, -1).then(({ row }) => {
        this.$refs.MaterialListTable.setActiveCell(row, 'code')
      })
    },
    editMaterialRowEvent(row,rowIndex) {
      this.editorRowIndex = rowIndex;
      // this.curMaterialEditMode = 2
      // 如果没有填写，默认为额定量
      // if (row.actAmount == null || row.actAmount == undefined || row.actAmount == 0) {
      //   row.actAmount = row.amount
      // }
      // row.amount
      // this.$refs.MaterialListTable.setActiveRow(row)
      this.$refs.WorkActAmount.showModal(row)
    },
    workActAmountOk(data){
      Object.assign(this.materials[this.editorRowIndex], data);
    },
    // saveMaterialRowEvent(row) {
    //   this.$refs.MaterialListTable.validate(row, (valid) => {
    //     if (!valid) {
    //       // 判断工单班组库存量是否足够
    //       getGroupStockSum
    //         .call(this, {
    //           groupId: this.workOrderInfo.groupId,
    //           materialTypeId: row.materialTypeId,
    //         })
    //         .then((resSum) => {
    //           if (resSum.success) {
    //             if (row.actAmount > resSum.result) {
    //               this.$message.warn('库存量不足,该物资当前班组库存量：' + resSum.result)
    //               return
    //             }
    //             this.saveMaterialByRow(row)
    //             // saveMaterial
    //             //   .call(this, row)
    //             //   .then((res) => {
    //             //     if (res.success) {
    //             //       this.$message.success('保存成功')
    //             //       if (this.curMaterialEditMode === 1) {
    //             //         this.materials.push(row)
    //             //       }
    //             //       this.curMaterialEditMode = 0
    //             //       this.$refs.MaterialListTable.clearActived()
    //             //     } else {
    //             //       this.$message.error('保存错误')
    //             //       console.error('保存物料消耗错误：', res.message)
    //             //     }
    //             //   })
    //             //   .catch((err) => {
    //             //     this.$message.error('保存异常')
    //             //     console.error('保存物料消耗异常：', err)
    //             //   })
    //           } else {
    //             this.$message.error(res.message)
    //             return
    //           }
    //         })
    //     }
    //   })
    // },
    saveMaterialByRow(row) {
      if (row.opType === 2) { // 临时领料
        row.planAmount=row.amount
        row.needDispatchin = 1
        row.needDispatchin_dictText= '是'
      } else { // 退料
        row.planAmount=row.amount
        row.needDispatchin = 0
        row.needDispatchin_dictText= '否'
      }
      saveMaterial(row)
        .then((res) => {
          if (res.success) {
            // this.$message.success('保存成功')
            if (this.curMaterialEditMode === 1) {
              this.materials.push(row)
            }
            this.curMaterialEditMode = 0
            if (this.$refs.MaterialListTable) {
              this.$refs.MaterialListTable.clearActived()
            }
          } else {
            this.$message.error('保存错误')
            console.error('保存物料消耗错误：', res.message)
          }
        })
        .catch((err) => {
          this.$message.error('保存异常')
          console.error('保存物料消耗异常：', err)
        })
    },
    // cancelMaterialRowEvent(row) {
    //   this.$refs.MaterialListTable.clearActived()
    //   if (this.curMaterialEditMode === 1) {
    //     // 新增,点击取消
    //     this.$refs.MaterialListTable.remove(row)
    //   } else if (this.curMaterialEditMode === 2) {
    //     // 还原行数据
    //     this.$refs.MaterialListTable.revertData(row)
    //   }
    //   this.curMaterialEditMode = 0
    // },
    onDelMaterial() {
      const selectRecords = this.$refs.MaterialListTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}数据？`,
          onOk: () => {
            const idArr = selectRecords.map((obj, index) => {
              return obj.id
            })
            const ids = idArr.join(',')
            delMaterial({ ids: ids })
              .then((res) => {
                if (res.success) {
                  //刷新物料列表
                  idArr.map((mid) => {
                    this.materials.map((mat, index) => {
                      if (mid === mat.id) {
                        this.materials.splice(index, 1)
                      }
                    })
                  })
                  this.$message.success('删除成功')
                } else {
                  this.$message.error('删除失败')
                  console.error('删除失败', res.message)
                }
              })
              .catch((err) => {
                this.$message.error('删除异常')
                console.error('删除异常', err)
              })
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    serialNoFormatter({ cellValue }) {
      if (!cellValue) {
        return '无'
      }
      return cellValue
    },
    openToolSelectModal(row) {
      this.curRow = row
      this.$refs.toolModalForm.showModal(row.toolTypeId)
      this.$refs.toolSelect.blur()
    },
    openToolTypeSelectModal(row) {
      this.curRow = row
      this.$refs.toolTypeModalForm.showModal()
      this.$refs.toolTypeSelect.blur()
    },
    onSelectTool(data) {
      if (data && data.length > 0) {
        const tool = data[0]
        // 2021/05/07 选择计量器具时跳过判断 task #2015
        if (this.curRow.toolTypeId !== tool.materialTypeId && tool.category1 !== 6) {
          this.$message.error('你选择的工器具与需求类型不一样')
          return
        }

        this.curRow.toolId = tool.id
        this.curRow.assetCode = tool.assetCode
        /*this.$forceUpdate()*/
        this.$refs.ToolsListTable.validate(this.$refs.ToolsListTable.getActiveRecord().row, (errMap) => {})
      }
    },
    onSelectToolType(data) {
      if (data && data.length > 0) {
        const tool = data[0]
        let flag = 0
        this.tools.map((f) => {
          if (f.toolId === tool.id) {
            flag = 1
            return false
          }
        })

        if (flag === 1) {
          this.$message.error('你选择的工器具已经在列表中存在，请选择其他工器具')
          return
        }
        this.curRow.code = tool.code
        this.curRow.toolTypeId = tool.id
        this.curRow.name = tool.name
        this.curRow.kind = tool.kind
        this.curRow.unit = tool.unit
        this.curRow.kind_dictText = tool.kind_dictText
        this.curRow.category1 = tool.category1
        this.curRow.category1_dictText = tool.category1_dictText
        this.curRow.amount = tool.num
        this.curRow.toolId = ''
        // 4 工器具  5工装 6 计量器具
        this.curRow.measure = tool.category1 === 6 ? 1 : 0
      } else {
        this.curRow.code = ''
        this.curRow.toolTypeId = ''
        this.curRow.name = ''
        this.curRow.kind = ''
        this.curRow.kind_dictText = ''
        this.curRow.category1 = ''
        this.curRow.category1_dictText = ''
        this.curRow.unit = ''
        this.curRow.amount = 0
        this.curRow.toolId = ''
        this.curRow.measure = ''
        this.curRow.assetCode = ''
      }
      this.$refs.ToolsListTable.validate(this.$refs.ToolsListTable.getActiveRecord().row, (errMap) => {})
    },
    formSelectChange(value, option) {
      let forms = this.taskFormsList.filter((f) => {
        return f.formInstId === value
      })
      if (forms.length > 0) {
        this.$refs.ToolsListTable.getActiveRecord().row.workRecordInstName = forms[0].formName
        this.$refs.ToolsListTable.getActiveRecord().row.workRecordInstId = value
        this.$refs.ToolsListTable.validate(this.$refs.ToolsListTable.getActiveRecord().row, (errMap) => {})
      }
    },

    openMaterialSelectModal(row) {
      this.curRow = row
      this.$refs.materialModalForm.showModal()
      this.$refs.materialSelect.blur()
    },
    onSelectMaterial(data) {
      if (data && data.length > 0) {
        const material = data[0]

        let flag = 0
        this.materials.map((f) => {
          if (f.materialTypeId === material.id) {
            flag = 1
            return false
          }
        })

        if (flag === 1) {
          this.$message.error('你选择的物料已经在列表中存在，请选择其他物料')
          return
        }
        this.curRow.code = material.code
        this.curRow.materialTypeId = material.id
        this.curRow.name = material.name
        this.curRow.kind = material.kind
        this.curRow.unit = material.unit
        this.curRow.kind_dictText = material.kind_dictText
        this.curRow.category1 = material.category1
        this.curRow.category1_dictText = material.category1_dictText
        this.curRow.amount = material.num
        this.curRow.actAmount = 0
      } else {
        this.curRow.code = ''
        this.curRow.materialTypeId = ''
        this.curRow.name = ''
        this.curRow.kind = ''
        this.curRow.kind_dictText = ''
        this.curRow.category1 = ''
        this.curRow.category1_dictText = ''
        this.curRow.unit = ''
        this.curRow.amount = 0
        this.curRow.actAmount = 0
      }
      this.$refs.MaterialListTable.validate(this.$refs.MaterialListTable.getActiveRecord().row, (errMap) => {})
    },

    viewFile(file) {
      // let filePath = window._CONFIG['onlinePreviewDomainURL'] + '?url=' + encodeURIComponent(file.savepath)
      this.fileName = file.fileName
      this.$refs.docPreview.handleFilePath(file.savePath)
    },
    downFile(file) {
      download(file.savePath)
      /*try {
        let downer = document.createElement("a");
        downer.setAttribute('download', '');// download属性
        downer.setAttribute('href', window._CONFIG['minioUrl'] + file.savePath);// href链接
        downer.click()
      } catch (e) {
        this.$message.error("下载异常！");
      }*/
    },
    moreBtnClick(e) {
      switch (e.key) {
      case '1':
        // console.log('故障提报')
        this.createFault()
        break
      case '2':
        // console.log('设为开口项')
        this.createOpenItem()
        break
      default:
        console.log('default')
        break
      }
    },
    createOpenItem() {
      if (this.taskFormsList && this.taskFormsList > 0) {
        let selectRecords = this.$refs.recordListTable.getCheckboxRecords()
        if (selectRecords.length > 0) {
          let names = selectRecords
            .map((obj, index) => {
              return obj.workContent
            })
            .join(',')
          this.$refs.openItemModal.add(this.selectTask.orderId, this.selectTask.id, names)
        } else {
          this.$message.error('请选择要设为开口先项的作业记录明细!')
        }
      } else {
        this.$refs.openItemModal.add(this.selectTask.orderId, this.selectTask.id, '')
      }
    },
    createFault() {
      // this.$refs.breakdownModal.add()
      // 当前有多个作业记录表时就需要判断是第几个作业记录表
      /*let tableIndex = -1
      for (let i = 0; i <= this.curFormIndex; i++) {
        if (this.taskFormsList[i].formType === 3) {
          tableIndex++
        }
      }

      let selectRecords = this.$refs.recordListTable[tableIndex].getCheckboxRecords()
      if (selectRecords.length > 0) {
        if (selectRecords.length > 1) {
          this.$message.error('只能选择一条作业记录明细创建故障!')
          return
        }
        this.$refs.breakdownModal.add(this.selectTask.orderId, this.selectTask.id)
      } else {
        this.$message.error('请选择要创建故障的作业记录明细!')
      }*/

      ;(async () => {
        this.$refs.breakdownModal.setWorkStations(this.staffArranges)
        let _data = {}
        if (!this.orderDetail && this.selectTask.orderId) {
          this.createFaultLoading = true
          await getWorkOrderDetail(this.selectTask.orderId).then((res) => {
            if (res.success && res.result) {
              this.orderDetail = res.result
            }
          })
          this.createFaultLoading = false
        }
        if (this.orderDetail) {
          _data = {
            planName: this.orderDetail.planName,
            planId: this.orderDetail.planId,
            lineId: this.orderDetail.lineId,
            trainNo: this.orderDetail.trainNo,
          }
        }
        this.$refs.breakdownModal.edit({
          workOrderId: this.selectTask.orderId,
          orderTaskId: this.selectTask.id,
          formType: 2,
          createType: 2, //  1 表示来自调度， 2 表示来自工班
          happenTime: this.$moment().format('YYYY-MM-DD'),
          faultDesc: '',
          phase: 1,
          status: 1,
          ..._data,
        })
      })()
    },
    showUserModal(type) {
      this.userSelectType = type
      this.$refs.tUserModalForm.showModal()
      switch (type) {
      case 1:
        this.$refs.userSelect1.blur()
        break
      case 2:
        this.$refs.userSelect2.blur()
        break
      }
    },
    onUserSelect(data) {
      if (data.length) {
        if (this.userSelectType === 1) {
          if (this.selectTask.outTask === 1) {
            // 送修
            this.outModelInfo.transferUserId = data[0].id
            this.outModelInfo.transferUserName = data[0].realname
            this.$refs.outInfoForm.validateField('transferUserId')
          }
          if (this.selectTask.outTask === 2) {
            // 接收
            this.inModelInfo.receiveUser = data[0].id
            this.inModelInfo.receiveUserName = data[0].realname
            this.$refs.inInfoForm.validateField('receiveUser')
          }
        }
        if (this.userSelectType === 2) {
          if (this.selectTask.outTask === 1) {
            // 送修
            this.outModelInfo.engineerId = data[0].id
            this.outModelInfo.engineerName = data[0].realname
            this.$refs.outInfoForm.validateField('engineerId')
          }
          if (this.selectTask.outTask === 2) {
            // 接收
            this.inModelInfo.engineerId = data[0].id
            this.inModelInfo.engineerName = data[0].realname
            this.$refs.inInfoForm.validateField('engineerId')
          }
        }
      }
      this.$forceUpdate()
    },
    onLineChange(value, option) {
      if (value) {
        this.dictTrainStr = 'bu_train_info,train_no,train_no,line_id=' + value + '|train_struct_id'
      } else {
        this.dictTrainStr = ''
      }
    },
    saveOutInInfo() {
      if (this.selectTask.outTask === 1) {
        this.saveOutInfo()
      }
      if (this.selectTask.outTask === 2) {
        this.saveInInfo()
      }
    },
    loadSupervise() {
      let param = {
        orderTaskId: this.selectTask.id,
        workOrderId: this.selectTask.orderId,
      }
      getSuperviseByOrderTask(param).then((res) => {
        if (res.success) {
          this.superviseModel = res.result
          this.$refs.superviseForm.edit(this.superviseModel)
        } else {
          let obj = {
            orderTaskId: this.selectTask.id,
            workOrderId: this.selectTask.orderId,
            taskContent: this.selectTask.taskName,
            dispatchGroupId: this.workOrderInfo.groupId,
            trainNo: this.workOrderInfo.trainNo,
          }
          this.$refs.superviseForm.edit(obj)
        }
      })
    },
    // 保存监修申请
    save8Task() {
      this.$refs.superviseForm.validate().then(() => {
        this.$refs.superviseForm.save()
      })
    },
    onTask8SaveSuccess() {
      this.$message.success('保存成功')
      this.loadSupervise()
    },
    onTask8SaveFail() {
      this.$message.error('保存失败')
    },
    onAddOutPart() {
      if (!this.outModelInfo.lineId || !this.outModelInfo.trainNo) {
        this.$message.warning('请先选择线路和车辆！')
        return
      }
      if (!this.outModelInfo.outinDetails) {
        this.outModelInfo.outinDetails = []
      }
      this.$refs.outPartModal.showModal(this.curSelectTrainStructId)
    },
    onOutPartOk(data) {
      this.outModelInfo.outinDetails.push(data)
    },
    onDeleteOutPart() {
      let selectRecords = this.$refs.outTable.getCheckboxRecords()
      let ids = selectRecords.map((item) => item.id)
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}条数据？`,
          onOk: () => {
            for (let i = this.outModelInfo.outinDetails.length - 1; i >= 0; i--) {
              //
              let detail = this.outModelInfo.outinDetails[i]
              if (ids.indexOf(detail.id) > -1) {
                this.outModelInfo.outinDetails.splice(i, 1)
              }
            }
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    saveOutInfo() {
      this.$refs.outInfoForm.validate((valid) => {
        if (valid) {
          if (!this.outModelInfo.outinDetails || this.outModelInfo.outinDetails.length < 1) {
            this.$message.warning('没有添加任何交接部件，请添加')
            return
          }
          this.outModelInfo.transferDate = this.outModelInfo.transferDateM.format('YYYY-MM-DD')
          this.loading = true
          saveOutInInfo(this.outModelInfo)
            .then((res) => {
              if (res.success) {
                this.outIsSave = true
                this.$message.success('保存成功')
              } else {
                this.$message.error('保存失败')
                console.error('保存委外送修信息失败:', res.message)
              }
            })
            .finally(() => {
              this.loading = false
            })
        }
      })
    },
    onAddInPart() {
      if (!this.inModelInfo.lineId || !this.inModelInfo.trainNo) {
        this.$message.warning('请先选择线路和车辆！')
        return
      }
      if (!this.inModelInfo.outinDetails) {
        this.inModelInfo.outinDetails = []
      }
      this.$refs.inPartModal.showModal(this.curSelectTrainStructId)
    },
    onInPartOk(data) {
      this.inModelInfo.outinDetails.push(JSON.parse(JSON.stringify(data)))
    },
    onDeleteInPart() {
      let selectRecords = this.$refs.inTable.getCheckboxRecords()
      let ids = selectRecords.map((item) => item.id)
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}条数据？`,
          onOk: () => {
            for (let i = this.inModelInfo.outinDetails.length - 1; i >= 0; i--) {
              //
              let detail = this.inModelInfo.outinDetails[i]
              if (ids.indexOf(detail.id) > -1) {
                this.inModelInfo.outinDetails.splice(i, 1)
              }
            }
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    saveInInfo() {
      this.$refs.inInfoForm.validate((valid) => {
        if (valid) {
          if (!this.inModelInfo.outinDetails || this.inModelInfo.outinDetails.length < 1) {
            this.$message.warning('没有添加任何交接部件，请添加')
            return
          }

          this.inModelInfo.transferDate = this.inModelInfo.transferDateM.format('YYYY-MM-DD')
          this.loading = true
          saveOutInInfo(this.inModelInfo)
            .then((res) => {
              if (res.success) {
                this.inIsSave = true
                this.$message.success('保存成功')
              } else {
                this.$message.error('保存失败')
                console.error('保存委外接受信息失败:', res.message)
              }
            })
            .finally(() => {
              this.loading = false
            })
        }
      })
    },
    onTrainSelect(val, data) {
      if (data) {
        this.curSelectTrainStructId = data.extFields[0].train_struct_id
      }
    },
    onAddFile(item) {
      addWorkOrderAttached({
        deleteBefore: true,
        orderId: this.workOrderInfo.id,
        taskId: this.taskInfo.id,
        annexList: [...this.attachedfiles],
      }).then((res) => {
        if (!res.success) {
          this.$message.error('上传文件失败')
        }
      })
    },
    // 通用行合并函数（将相同多列数据合并为一行）
    mergeRowMethod({ row, _rowIndex, column, visibleData }) {
      const fields = ['reguTitle']
      const cellValue = row[column.property]
      if (cellValue && fields.includes(column.property)) {
        const prevRow = visibleData[_rowIndex - 1]
        let nextRow = visibleData[_rowIndex + 1]
        if (prevRow && prevRow[column.property] === cellValue) {
          return { rowspan: 0, colspan: 0 }
        } else {
          let countRowspan = 1
          while (nextRow && nextRow[column.property] === cellValue) {
            nextRow = visibleData[++countRowspan + _rowIndex]
          }
          if (countRowspan > 1) {
            return { rowspan: countRowspan, colspan: 1 }
          }
        }
      }
    },
    setBookStepDom() {
      let content = ''
      this.bookSteps.forEach((e) => {
        content += `<h2>${e.bookStepNo}<span style="margin-left:15px;">${e.bootStepTitle}</span></h2>`
        content += e.bootStepContent
      })
      if (content) {
        this.$refs.bookStepDom.innerHTML = content
      }
    },
    onReceiveMaterialAdd(row) {
      this.curMaterialEditMode = 1
      this.saveMaterialByRow(row)
    },
    onReturnMaterialAdd(rows) {
      this.materials = [...this.materials, ...rows]
    },
    onReceiveMaterialEdit(row) {
      this.curMaterialEditMode = 2
      this.saveMaterialByRow(row)
    },
    onSubmitMaterialApply() {
      this.$emit('close', 'ok')
    },
    checkMaterialShow({ row }) {
      // 如果不是额定新增，或者临时新增的不显示， 如果是需要发料，但是没有生成领料单的，不在这显示
      if ((row.opType !== 1 && row.opType !== 2) || (row.needDispatchin === 1 && row.isGenOrder === 0)) {
        return 'display: none;'
      }
    },
    onTaskFormNoWork(data) {
      this.tabFormKey = data
      this.selectForm(data)
    },
    getRowColData(row, col) {
      if (!(this.curForm && this.curForm.instType === 1)) {
        return
      }
      this.selectCellItem = null
      getRowColThreshold({
        customId: this.curForm.formObjId,
        row1: row,
        row2: row,
        col1: col,
        col2: col,
      }).then((res) => {
        if (res.success) {
          this.selectCellItem = res.result
        } else {
          console.error('获取行%s,列%s，错误', row, col, res.message)
        }
      })
    },
    getFormAssetName (form) {
      let assetName = form.trainAssetName || form.structDetailName || form.assetTypeName || ''
      if (form.structDetailWbs) {
        let wbs = form.structDetailWbs.split('.')
        assetName = wbs[0] +"_"+ assetName
      }
      return assetName
    }
  },
}
</script>
<style lang="less" scoped>
/deep/ .ant-tabs-content {
  height: calc(100% - 48px);
}
</style>
<style lang="less">
#luckysheet-wa-editor {
  padding: 3px 0 3px 15px !important;
}
#myWorkContent {
  .table-page-search-wrapper {
    margin-top: 5px;
  }
  .list-wrapper {
    padding: 0px;
    height: calc(100% - 255px);
    overflow-x: auto;
    overflow-y: hidden;
  }
  .titleBar {
    width: 100%;
    font-size: 14px;
    padding: 8px;
    text-align: center;
    font-weight: bold;
  }
  .buttonDiv {
    padding: 10px 0;
  }
  .requireBox {
    min-height: 200px;
    padding: 10px;
    .requireTitle {
      border-bottom: 1px solid #eee;
      font-size: 18px;
      font-weight: bold;
      padding: 5px;
      span {
        border-bottom: 5px solid red;
        padding: 5px;
      }
      margin-bottom: 10px;
    }
    .safeContent {
      font-size: 15px;
    }
  }

  .luckysheet-stat-area {
    background-color: transparent !important;
  }

  .readTip {
    color: #d53e3e;
    font-weight: bold;
    font-size: 15pt;
  }
}
.badge-tab-pane {
  display: flex;
  .ant-badge {
    margin-left: 5px;
    position: absolute;
    right: -10px;
  }
}
.work-form {
  .ant-form-item {
    width: 100%;
  }
}

.ellipsis-main{
  & > span{
    max-width: 240px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>
