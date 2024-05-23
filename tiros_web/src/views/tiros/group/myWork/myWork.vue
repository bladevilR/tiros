<template>
  <div id="myWorkContent">
    <na-splitter :defaultSize="350" style="height: calc(100vh - 115px)" @resize="resizeSplitter">
      <div slot="left-panel" style="height: 100%; overflow-y: hidden; overflow-x: hidden; padding-right: 2px">
        <div class="titleBar bg-primary-1">任务搜索</div>
        <div class="table-page-search-wrapper">
          <a-form layout="inline">
            <a-row :gutter="12">
              <a-col :md="12" :sm="24">
                <a-form-item>
                  <a-date-picker format="YYYY-MM-DD" style="width: 100%" v-model="startDate" />
                </a-form-item>
              </a-col>
              <a-col :md="12" :sm="24">
                <a-form-item>
                  <j-dict-select-tag
                    v-model="queryParam.status"
                    dictCode="bu_order_task_status"
                    placeholder="任务状态"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="12" :sm="24">
                <a-form-item :wrapperCol="{ span: 24 }">
                  <!-- <div @click="openPlanModel"> -->
                  <a-select
                    allow-clear
                    placeholder="请选择列计划"
                    :open="false"
                    :showArrow="true"
                    v-model="queryParam.planName"
                    ref="myPlanSelect"
                    @dropdownVisibleChange="openPlanModel"
                    @change="changePlanSelect"
                    style="width: 100%"
                  >
                    <a-icon slot="suffixIcon" type="ellipsis" />
                  </a-select>
                  <!-- </div> -->
                </a-form-item>
              </a-col>
              <a-col :md="12" :sm="24">
                <a-form-item :wrapperCol="{ span: 24 }">
                  <!-- <div @click="openOrderModel"> -->
                  <a-select
                    allow-clear
                    placeholder="请选择工单"
                    :open="false"
                    :showArrow="true"
                    v-model="queryParam.orderName"
                    ref="myOrderSelect"
                    @dropdownVisibleChange="openOrderModel"
                    @change="changeOrderSelect"
                    style="width: 100%"
                  >
                    <a-icon slot="suffixIcon" type="ellipsis" />
                  </a-select>
                  <!-- </div> -->
                </a-form-item>
              </a-col>
              <a-col :md="24" :sm="24">
                <a-form-item>
                  <a-input placeholder="请输入任务名称" v-model="queryParam.taskName" allow-clear></a-input>
                </a-form-item>
              </a-col>
            </a-row>
            <a-row>
              <a-form-item>
                <a-button type="primary" @click="loadTaskList" block>搜索</a-button>
              </a-form-item>
            </a-row>
            <a-row>
              <!-- 这里还有控制权限 -->
              <!--            <a-form-item>
              <a-button v-if="selectTask && selectTask.taskStatus !== 2" v-has="'mywork:submittask'" type="dashed" class="border-primary-6 bg-primary-2" block @click="submitTask">任务提交</a-button>
              &lt;!&ndash;<a-button style="margin-left:8px" size="small" @click="changeUnit">部件更换</a-button>&ndash;&gt;
            </a-form-item>-->
            </a-row>
          </a-form>
        </div>
        <div class="titleBar bg-primary-1">任务列表</div>
        <div style="padding: 10px">
          <a-button
            v-if="!readOnly && selectTask"
            v-has="'mywork:submittask'"
            type="dashed"
            class="border-primary-6 bg-primary-2"
            block
            @click="submitTask"
            :loading="loading"
            >任务提交</a-button
          >
        </div>
        <div style="height: calc(100vh - 435px)">
          <vxe-table
            :stripe="true"
            border
            style="height: calc(100vh - 435px)"
            row-id="id"
            ref="listTable"
            :align="allAlign"
            :data="tableDataTask"
            max-height="100%"
            show-overflow="tooltip"
            :radio-config="{ trigger: 'row', highlight: true, reserve: true }"
            @radio-change="onTaskSelectChange"
          >
            <vxe-table-column type="radio" width="35"></vxe-table-column>
            <vxe-table-column field="taskName" title="任务名称" align="left" width="195"></vxe-table-column>
            <!-- <vxe-table-column field="progress" title="进度" align="left" width="60"></vxe-table-column>-->
            <vxe-table-column field="taskStatus_dictText" title="状态" align="left" width="65"></vxe-table-column>
            <vxe-table-column field="trainNo" title="车号" align="left" width="65"></vxe-table-column>
            <vxe-table-column field="orderCode" title="工单号" align="left" width="80"></vxe-table-column>
            <vxe-table-column
              field="orderName"
              title="工单名称"
              align="left"
              header-align="center"
              width="165"
            ></vxe-table-column>
          </vxe-table>
        </div>
      </div>
      <div slot="right-panel" style="padding-left: 5px; padding-right: 3px; height: calc(100% - 0px)">
        <!-- 计划任务 -->
        <a-tabs
          size="small"
          v-model="curTabKey"
          @change="onChangeTab"
          style="height: calc(100% - 0px)"
          v-if="
            selectTask &&
            selectTask.outTask === 0 &&
            (selectTask.taskType === 1 ||
              selectTask.taskType === 3 ||
              selectTask.taskType === 4 ||
              selectTask.taskType === 5)
          "
        >
          <a-tab-pane key="1" tab="作业要求" style="overflow: auto">
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
              <p class="safeContent" v-if="requirement && requirement.safetyExpectation" v-html="requirement.safetyExpectation"></p>
            </div>
            <!--            <div class="requireBox">
              <div class="requireTitle"><span> 安全要求</span></div>
              <p v-if="requirement && requirement.safeNotice">{{ requirement.safeNotice }}</p>
            </div>
            <div class="requireBox">
              <div class="requireTitle"><span> 技术要求</span></div>
              <p v-if="requirement && requirement.requirement">{{ requirement.requirement }}</p>
            </div>-->

            <!-- 任务规程 -->
            <div class="requireBox" style="min-height: auto">
              <div class="requireTitle"><span style="border-color: #00b8ff"> 任务规程</span></div>
              <TaskRegusView v-if="regus.length > 0" :tableData="regus" style="min-height: 162px"></TaskRegusView>
              <div v-else style="height: 162px"></div>
            </div>
            <!-- 作业指导书 -->
            <div class="requireBox">
              <div class="requireTitle"><span style="border-color: #00b8ff"> 作业指导书</span></div>
              <div ref="bookStepDom" style="padding: 15px"></div>
            </div>
          </a-tab-pane>
          <a-tab-pane key="2" :forceRender="true" na-flex-height-full>
            <div class="badge-tab-pane" slot="tab">
              <span>作业表单</span>
              <a-badge :hidden="formsBadge === 0" :count="formsBadge" />
            </div>
            <div v-if="taskFormsList && taskFormsList.length > 0" na-flex-height-full>
              <a-tabs
                size="small"
                v-if="taskFormsList.length"
                v-model="tabFormKey"
                @change="selectForm"
                style="min-height: 45px"
              >
                <a-tab-pane v-for="(form, index) in taskFormsList" :key="index">
                  <div class="badge-tab-pane" slot="tab">
                    <span>{{form.formName}}</span>
                    <a-badge :hidden="getFormBadgeByRow(form) === 0" :count="getFormBadgeByRow(form)" />
                  </div>
                </a-tab-pane>
              </a-tabs>

              <TaskCheckForm
                v-if="curForm && curForm.instType === 4"
                :taskFormsList="taskFormsList"
                :selectTask="selectTask"
                :workOrderInfo="workOrderInfo"
                @taskFormNoWork="onTaskFormNoWork"
                @checkEnd="getFormsItem"
                ref="checkRecordForm"
              ></TaskCheckForm>

              <div v-if="curForm && curForm.instType === 3" na-flex-height-full>
                <div class="buttonDiv">
                  <!-- 1 自检  2 互检  3 专检    4  抽检   5 过程检  6 过程检确认  7 交接检  8 交接检确认 9 专工验收 -->
                  <a-space v-if="selectTask && selectTask.taskStatus !== 2">
                    <a-button @click="handleCheck(1, '自检')">自检</a-button>
                    <a-button @click="handleCheck(2, '互检')">互检</a-button>
                    <a-button @click="handleCheck(3, '专检')">专检</a-button>
                    <!-- 专验暂时注释，在另外的界面进行操作 -->
                    <!--                      <a-button @click="handleCheck(9,'专验')">专验</a-button>-->
                    <a-button @click="createFault">故障提报</a-button>
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
                      <th style="width: 90px; text-align: right">原部件序列号:</th>
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
                <div
                  :style="{
                    height:
                      curRecordForm && curRecordForm.updown && curRecordForm.updown === 1
                        ? 'calc(100vh - 297px)'
                        : 'calc(100vh - 258px)',
                  }"
                >
                  <vxe-table
                    :stripe="true"
                    border
                    auto-resize
                    max-height="auto"
                    row-id="id"
                    ref="recordListTable"
                    :align="allAlign"
                    v-if="curRecordForm"
                    :data="curRecordForm.detailList"
                    show-overflow="tooltip"
                    :span-method="mergeRowMethod"
                  >
                    <vxe-table-column type="checkbox" width="40"></vxe-table-column>
                    <vxe-table-column type="index" title="序号" align="center" width="60"></vxe-table-column>
                    <!--                    <vxe-table-column field="itemNo" title="序号" align="center" width="70"></vxe-table-column>-->
                    <vxe-table-column field="reguTitle" title="作业项目" align="center" width="120"></vxe-table-column>
                    <vxe-table-column
                      field="workContent"
                      title="检修内容"
                      align="left"
                      min-width="250"
                    ></vxe-table-column>
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
                    <vxe-table-column
                      field="workInfo"
                      title="作业情况"
                      align="left"
                      min-width="120"
                      header-align="center"
                    >
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
              <div v-if="curForm && curForm.instType === 1" style="height: calc(100vh - 210px); position: relative">
                <div
                  v-show="showSheetSave"
                  style="position: absolute; top: 5px; left: 90px; width: 50px; z-index: 1000"
                >
                  <a-button size="small" type="primary" @click.stop="saveCustomForm"
                    ><a-icon type="save" /> &nbsp;保存</a-button
                  >
                </div>

                <div :id="'sheet'" style="margin: 0px; padding: 0px; width: 100%; height: calc(100% - 0px)"></div>
              </div>
            </div>
            <div style="margin: 20px" v-if="!taskFormsList.length">
              <div style="padding: 15px; color: #cccccc">该作业无须填写表单信息</div>
              <a-space v-if="!readOnly">
                <a-button @click="createFault">提报故障</a-button>
                <a-button @click="createOpenItem">设置为开口项</a-button>
              </a-space>
            </div>
          </a-tab-pane>
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
              <a-badge :hidden="materialBadge === 0" :count="materialBadge" />
            </div>
            <div class="tableHeight" na-flex-height-full>
              <vxe-table
                border
                ref="MaterialListTable"
                auto-resize
                max-height="auto"
                :align="allAlign"
                :data.sync="materials"
                :keep-source="true"
                show-overflow="ellipsis"
                :row-style="checkMaterialShow"
                :edit-rules="materialInputValidRules"
                :checkbox-config="{ highlight: true, range: true }"
                :edit-config="{ key: 'id', trigger: 'manual', mode: 'row', autoClear: false, showStatus: true }"
              >
                <vxe-table-column type="checkbox" width="40"></vxe-table-column>
                <vxe-table-column field="code" title="物料编码" width="15%" :edit-render="{ name: 'input' }">
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
                  width="15%"
                  :edit-render="{ name: 'input' }"
                  align="left"
                  header-align="center"
                >
                  <template v-slot:edit="{ row }">
                    {{ row.name }}
                  </template>
                </vxe-table-column>
                <vxe-table-column field="opType_dictText" title="领用类型" width="120px"></vxe-table-column>
                <vxe-table-column field="amount" title="所需数量" width="15%" :edit-render="{ name: 'input' }">
                  <template v-slot:edit="{ row }">
                    {{ row.amount }}
                    <!--                    <a-input-number v-model="row.amount" :defaultValue="1" :min="1" :max="10000" style="width: 100%" />-->
                  </template>
                </vxe-table-column>
                <vxe-table-column
                  field="actAmount"
                  title="实际消耗"
                  align="left"
                  :edit-render="{ name: '$input', props: { type: 'number' } }"
                ></vxe-table-column>
                <vxe-table-column field="unit" title="单位" align="left" width="15%" :edit-render="{ name: 'input' }">
                  <template v-slot:edit="{ row }">
                    {{ row.unit }}
                  </template>
                </vxe-table-column>
                <vxe-table-column
                  field="remark"
                  title="消耗备注"
                  align="left"
                  :edit-render="{ name: '$input', props: { type: 'text' } }"
                ></vxe-table-column>
                <vxe-table-column title="操作" width="120" v-if="!readOnly">
                  <template v-slot="{ row }">
                    <template v-if="$refs.MaterialListTable.isActiveByRow(row)">
                      <a-space>
                        <a-button type="dashed" size="small" @click.stop="saveMaterialRowEvent(row)">保存</a-button>
                        <a-button type="dashed" size="small" @click.stop="cancelMaterialRowEvent(row)">取消</a-button>
                      </a-space>
                    </template>
                    <template v-else>
                      <a-button
                        type="dashed"
                        :disabled="!selectTask && selectTask.taskStatus === 2"
                        size="small"
                        @click.stop="editMaterialRowEvent(row)"
                        >填写</a-button
                      >
                      <!-- <a-button type="dashed" size="small" @click="delRowEvent(row)">删除</a-button>-->
                    </template>
                  </template>
                </vxe-table-column>
              </vxe-table>
            </div>
          </a-tab-pane>
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
            <div class="tableHeight" :style="`height: calc(100% - ${readOnly ? '4px' : '40px'})`">
              <vxe-table
                border
                ref="ToolsListTable"
                auto-resize
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
                <vxe-table-column
                  field="name"
                  title="工器具名称"
                  :edit-render="{ name: 'input' }"
                  align="left"
                  class-name="allow-max-height"
                >
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
                  title="出厂号(实际工具)"
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
          <a-tab-pane key="9" tab="作业工时">
            <TaskWorkTime
              :staffArranges.sync="staffArranges"
              :selectTask.sync="selectTask"
              :operator="4"
              :read-only="readOnly"
            ></TaskWorkTime>
          </a-tab-pane>
          <!--          <a-tab-pane key="3" tab="必换件清单">
            <div class="tableHeight">
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
          <a-tab-pane key="7" tab="任务信息">
            <orderOrTaskDetail :workOrderInfo="workOrderInfo" :taskInfo="taskInfo" />
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
            ></ReceiveMaterial>
          </a-tab-pane>
          <a-tab-pane key="11" tab="退还物料">
            <ReceiveMaterial
              :taskMaterials="materials"
              :selectTask="selectTask"
              :opType="3"
              @add="onReceiveMaterialAdd"
              @edit="onReceiveMaterialEdit"
            ></ReceiveMaterial>
          </a-tab-pane>
        </a-tabs>
        <!-- 委外任务 -->
        <a-tabs
          size="small"
          v-model="curTabKey"
          @change="onChangeTab"
          style="height: calc(100% - 0px)"
          v-if="selectTask && selectTask.outTask !== 0 && (selectTask.taskType === 6 || selectTask.taskType === 7)"
        >
          <a-button
            v-if="selectTask && selectTask.taskStatus !== 2 && curTabKey === '2'"
            type="primary"
            @click="saveOutInInfo"
            slot="tabBarExtraContent"
            size="small"
            style="margin-right: 10px"
            :loading="loading"
            >保存</a-button
          >
          <a-tab-pane key="1" tab="作业要求" style="overflow: auto">
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
              <p class="safeContent" v-if="requirement && requirement.safetyExpectation">
                {{ requirement.safetyExpectation }}
              </p>
            </div>
          </a-tab-pane>
          <a-tab-pane key="2" tab="作业填报">
            <div style="padding: 10px; height: 100%; overflow-y: auto">
              <div v-if="selectTask && selectTask.outTask === 1" style="height: calc(100% - 18px)">
                <!--  委外送修 -->
                <div class="fieldset-wrapper">
                  <h4 class="title">交接信息</h4>
                  <a-form-model
                    :disabled="selectTask && selectTask.taskStatus !== 2"
                    ref="outInfoForm"
                    :model="outModelInfo"
                    :label-col="{ span: 6 }"
                    :wrapper-col="{ span: 18 }"
                    :rules="outRules"
                  >
                    <a-row>
                      <a-col :span="11">
                        <a-form-model-item label="交接单号" prop="billNo">
                          <a-input placeholder="交接单号" v-model="outModelInfo.billNo" disabled />
                        </a-form-model-item>
                      </a-col>
                      <a-col :span="11">
                        <a-form-model-item label="交接单名" prop="outinName">
                          <a-input placeholder="交接单名" v-model="outModelInfo.outinName" />
                        </a-form-model-item>
                      </a-col>
                      <a-col :span="11">
                        <a-form-model-item label="线路" prop="lineId">
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
                        <a-form-model-item label="车号" prop="trainNo">
                          <j-dict-select-seach-tag
                            :triggerChange="true"
                            v-model="outModelInfo.trainNo"
                            :dictCode="dictTrainStr"
                            @select="onTrainSelect"
                          />
                        </a-form-model-item>
                      </a-col>

                      <a-col :span="11">
                        <a-form-model-item label="交接日期" prop="transferDateM">
                          <a-date-picker
                            style="width: 100%"
                            placeholder="交接日期"
                            v-model="outModelInfo.transferDateM"
                          ></a-date-picker>
                        </a-form-model-item>
                      </a-col>
                      <a-col :span="11">
                        <a-form-model-item label="工程师" prop="engineerId">
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
                        <a-form-model-item label="作业班组" prop="sendGroupId">
                          <j-dict-select-tag
                            :triggerChange="true"
                            v-model="outModelInfo.sendGroupId"
                            :dictCode="dictGroupStr"
                          />
                        </a-form-model-item>
                      </a-col>
                      <a-col :span="11">
                        <a-form-model-item label="移交人" prop="transferUserId">
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
                        <a-form-model-item label="接收厂商" prop="supplierId">
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
                        <a-form-model-item label="接收人员" prop="receiveUser">
                          <a-input placeholder="接收人员" v-model="outModelInfo.receiveUser" />
                        </a-form-model-item>
                      </a-col>
                      <a-col :span="11">
                        <a-form-model-item label="所属合同" prop="contractId">
                          <j-dict-select-tag
                            :trigger-change="true"
                            v-model="outModelInfo.contractId"
                            dictCode="bu_contract_info,contract_name,id,status<>2"
                          />
                        </a-form-model-item>
                      </a-col>
                      <a-col :span="11">
                        <a-form-model-item label="备注" prop="remark">
                          <a-input placeholder="备注" v-model="outModelInfo.remark" />
                        </a-form-model-item>
                      </a-col>
                    </a-row>
                  </a-form-model>
                </div>
                <div class="fieldset-wrapper">
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
                    <vxe-table-column field="assetName" title="委外部件" min-width="140"></vxe-table-column>
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
              <div v-if="selectTask && selectTask.outTask === 2" style="height: calc(100% - 18px)">
                <!-- 委外接收 -->
                <div class="fieldset-wrapper">
                  <h4 class="title">交接信息</h4>
                  <a-form-model
                    ref="inInfoForm"
                    :model="inModelInfo"
                    :label-col="{ span: 6 }"
                    :wrapper-col="{ span: 18 }"
                    :rules="inRules"
                  >
                    <a-row>
                      <a-col :span="11">
                        <a-form-model-item label="交接单号" prop="billNo">
                          <a-input placeholder="交接单号" v-model="inModelInfo.billNo" disabled />
                        </a-form-model-item>
                      </a-col>
                      <a-col :span="11">
                        <a-form-model-item label="交接单名" prop="outinName">
                          <a-input placeholder="交接单名" v-model="inModelInfo.outinName" />
                        </a-form-model-item>
                      </a-col>
                      <a-col :span="11">
                        <a-form-model-item label="线路" prop="lineId">
                          <j-dict-select-tag
                            :triggerChange="true"
                            v-model="inModelInfo.lineId"
                            dictCode="bu_mtr_line,line_name,line_id"
                            @change="onLineChange"
                          />
                        </a-form-model-item>
                      </a-col>
                      <a-col :span="11">
                        <a-form-model-item label="车号" prop="trainNo">
                          <j-dict-select-seach-tag
                            :triggerChange="true"
                            v-model="inModelInfo.trainNo"
                            :dictCode="dictTrainStr"
                            @select="onTrainSelect"
                          />
                        </a-form-model-item>
                      </a-col>

                      <a-col :span="11">
                        <a-form-model-item label="接收日期" prop="transferDate">
                          <a-date-picker
                            style="width: 100%"
                            placeholder="交接日期"
                            v-model="inModelInfo.transferDateM"
                          ></a-date-picker>
                        </a-form-model-item>
                      </a-col>
                      <a-col :span="11">
                        <a-form-model-item label="工程师" prop="engineerId">
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
                        <a-form-model-item label="作业班组" prop="sendGroupId">
                          <j-dict-select-tag
                            :triggerChange="true"
                            v-model="inModelInfo.sendGroupId"
                            :dictCode="dictGroupStr"
                          />
                        </a-form-model-item>
                      </a-col>
                      <a-col :span="11">
                        <a-form-model-item label="接收人" prop="receiveUser">
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
                        <a-form-model-item label="移交厂商" prop="supplierId">
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
                        <a-form-model-item label="移交人员" prop="transferUserId">
                          <a-input placeholder="移交人员" v-model="inModelInfo.transferUserId" />
                        </a-form-model-item>
                      </a-col>
                      <a-col :span="11">
                        <a-form-model-item label="所属合同" prop="contractId">
                          <j-dict-select-tag
                            :trigger-change="true"
                            v-model="inModelInfo.contractId"
                            dictCode="bu_contract_info,contract_name,id,status<>2"
                          />
                        </a-form-model-item>
                      </a-col>
                      <a-col :span="11">
                        <a-form-model-item label="备注" prop="remark">
                          <a-input placeholder="备注" v-model="inModelInfo.remark" />
                        </a-form-model-item>
                      </a-col>
                    </a-row>
                  </a-form-model>
                </div>
                <div class="fieldset-wrapper">
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
                    <vxe-table-column field="assetName" title="委外部件" min-width="140"></vxe-table-column>
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
          </a-tab-pane>
          <a-tab-pane key="9" tab="作业工时">
            <TaskWorkTime
              :staffArranges.sync="staffArranges"
              :selectTask.sync="selectTask"
              :operator="4"
            ></TaskWorkTime>
          </a-tab-pane>
          <a-tab-pane key="7" tab="任务信息">
            <orderOrTaskDetail type="outsources" :workOrderInfo="workOrderInfo" :taskInfo="selectTask" />
          </a-tab-pane>
          <a-tab-pane key="8" tab="附件">
            <TaskAttached :files.sync="attachedfiles" @addFile="onAddFile"></TaskAttached>
          </a-tab-pane>
        </a-tabs>
        <!-- 故障处理任务 -->
        <a-tabs
          size="small"
          v-model="curTabKey"
          @change="onChangeTab"
          style="height: calc(100% - 0px)"
          v-if="selectTask && selectTask.taskType === 2 && selectTask.outTask === 0"
        >
          <a-button
            v-if="selectTask && selectTask.taskStatus !== 2 && curTabKey === '2'"
            type="primary"
            @click="saveFaultInfo"
            slot="tabBarExtraContent"
            size="small"
            style="margin-right: 10px"
            :loading="loading"
            >保存</a-button
          >
          <a-tab-pane key="1" tab="作业要求" style="overflow: auto">
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
              <p class="safeContent" v-if="requirement && requirement.safetyExpectation">
                {{ requirement.safetyExpectation }}
              </p>
            </div>
            <!--            <div class="requireBox" >
                          <div class="requireTitle"><span> 安全要求</span></div>
                          <p v-if="requirement && requirement.safeNotice">{{ requirement.safeNotice }}</p>
                        </div>-->
            <!--            <div class="requireBox" >
                          <div class="requireTitle"><span> 技术要求</span></div>
                          <p v-if="requirement && requirement.requirement">{{ requirement.requirement }}</p>
                        </div>-->
            <!-- 任务规程 -->
            <!--            <div class="requireBox">
              <div class="requireTitle"><span style="border-color: #00b8ff"> 任务规程</span></div>
              <TaskRegusView v-if="regus.length > 0" :tableData="regus"></TaskRegusView>
            </div>-->
            <!-- 作业指导书 -->
            <!--            <div class="requireBox">
              <div class="requireTitle"><span style="border-color: #00b8ff"> 作业指导书</span></div>
              <div ref="bookStepDom" style="padding: 15px"></div>
            </div>-->
          </a-tab-pane>
          <a-tab-pane key="2" tab="作业填报" :forceRender="true" style="padding-top: 10px; padding-left: 5px">
            <BreakdownFormItem
              ref="breakdownForm"
              :from-write-report="true"
              v-if="selectTask && selectTask.taskType === 2 && selectTask.outTask === 0"
              @ok="onFaultSaveOk"
              @fail="onFaultSaveFail"
            ></BreakdownFormItem>
          </a-tab-pane>
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
              <a-badge :hidden="materialBadge === 0" :count="materialBadge" />
            </div>
            <div class="tableHeight" na-flex-height-full>
              <vxe-table
                border
                ref="MaterialListTable"
                auto-resize
                max-height="auto"
                :align="allAlign"
                :data.sync="materials"
                :keep-source="true"
                show-overflow="tooltip"
                :row-style="checkMaterialShow"
                :edit-rules="materialInputValidRules"
                :checkbox-config="{ highlight: true, range: true }"
                :edit-config="{ key: 'id', trigger: 'manual', mode: 'row', autoClear: false, showStatus: true }"
              >
                <vxe-table-column type="checkbox" width="40"></vxe-table-column>
                <vxe-table-column field="code" title="物料编码" width="15%" :edit-render="{ name: 'input' }">
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
                  width="15%"
                  :edit-render="{ name: 'input' }"
                  align="left"
                  header-align="center"
                >
                  <template v-slot:edit="{ row }">
                    {{ row.name }}
                  </template>
                </vxe-table-column>
                <vxe-table-column field="opType_dictText" title="领用类型" width="120px"></vxe-table-column>
                <vxe-table-column field="amount" title="所需数量" width="15%" :edit-render="{ name: 'input' }">
                  <template v-slot:edit="{ row }">
                    {{ row.amount }}
                    <!--                    <a-input-number v-model="row.amount" :defaultValue="1" :min="1" :max="10000" style="width: 100%" />-->
                  </template>
                </vxe-table-column>
                <vxe-table-column field="unit" title="单位" align="left" width="15%" :edit-render="{ name: 'input' }">
                  <template v-slot:edit="{ row }">
                    {{ row.unit }}
                  </template>
                </vxe-table-column>
                <vxe-table-column
                  field="actAmount"
                  title="实际消耗"
                  align="left"
                  :edit-render="{ name: '$input', props: { type: 'number' } }"
                ></vxe-table-column>
                <vxe-table-column
                  field="remark"
                  title="消耗备注"
                  align="left"
                  :edit-render="{ name: '$input', props: { type: 'text' } }"
                ></vxe-table-column>
                <vxe-table-column title="操作" width="120" v-if="!readOnly">
                  <template v-slot="{ row }">
                    <template v-if="$refs.MaterialListTable.isActiveByRow(row)">
                      <a-space>
                        <a-button type="dashed" size="small" @click.stop="saveMaterialRowEvent(row)">保存</a-button>
                        <a-button type="dashed" size="small" @click.stop="cancelMaterialRowEvent(row)">取消</a-button>
                      </a-space>
                    </template>
                    <template v-else>
                      <a-button
                        type="dashed"
                        :disabled="!selectTask && selectTask.taskStatus === 2"
                        size="small"
                        @click.stop="editMaterialRowEvent(row)"
                        >填写</a-button
                      >
                      <!-- <a-button type="dashed" size="small" @click="delRowEvent(row)">删除</a-button>-->
                    </template>
                  </template>
                </vxe-table-column>
              </vxe-table>
            </div>
          </a-tab-pane>
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
            <div class="tableHeight" :style="`height: calc(100% - ${readOnly ? '4px' : '40px'})`">
              <vxe-table
                border
                ref="ToolsListTable"
                auto-resize
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
                <vxe-table-column
                  field="name"
                  title="工器具名称"
                  :edit-render="{ name: 'input' }"
                  align="left"
                  class-name="allow-max-height"
                >
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
                      <template v-for="(form, index) in curFormIndexList">
                        <a-select-option :value="form.id" :key="index" v-if="form.instType === 3">
                          {{ form.formIndex }}-{{ form.name }} {{ ' -' + form.assetName }}
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
                  title="出厂号(实际工具)"
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
          <a-tab-pane key="9" tab="作业工时">
            <TaskWorkTime
              :staffArranges.sync="staffArranges"
              :selectTask.sync="selectTask"
              :operator="4"
            ></TaskWorkTime>
          </a-tab-pane>
          <a-tab-pane key="7" tab="任务信息">
            <orderOrTaskDetail type="outsources" :workOrderInfo="workOrderInfo" :taskInfo="selectTask" />
          </a-tab-pane>
          <a-tab-pane key="8" tab="附件">
            <TaskAttached :files.sync="attachedfiles" @addFile="onAddFile"></TaskAttached>
          </a-tab-pane>
          <a-tab-pane key="10" tab="临时领料">
            <ReceiveMaterial
              :taskMaterials="materials"
              :selectTask="selectTask"
              :opType="2"
              @add="onReceiveMaterialAdd"
              @edit="onReceiveMaterialEdit"
            ></ReceiveMaterial>
          </a-tab-pane>
          <a-tab-pane key="11" tab="退还物料">
            <ReceiveMaterial
              :taskMaterials="materials"
              :selectTask="selectTask"
              :opType="3"
              @add="onReceiveMaterialAdd"
              @edit="onReceiveMaterialEdit"
            ></ReceiveMaterial>
          </a-tab-pane>
        </a-tabs>
        <!-- 监修任务 -->
        <a-tabs
          size="small"
          v-model="curTabKey"
          @change="onChangeTab"
          style="height: calc(100% - 0px)"
          v-if="selectTask && selectTask.outTask === 0 && selectTask.taskType === 8"
        >
          <a-button
            v-if="selectTask && selectTask.taskStatus !== 2 && curTabKey === '2'"
            type="primary"
            @click="save8Task"
            slot="tabBarExtraContent"
            size="small"
            style="margin-right: 10px"
            :loading="loading"
            >保存</a-button
          >
          <a-tab-pane key="1" tab="作业要求" style="overflow: auto">
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
              <p class="safeContent" v-if="requirement && requirement.safetyExpectation">
                {{ requirement.safetyExpectation }}
              </p>
            </div>
            <!--            <div class="requireBox" >
                          <div class="requireTitle"><span> 安全要求</span></div>
                          <p v-if="requirement && requirement.safeNotice">{{ requirement.safeNotice }}</p>
                        </div>-->
            <!--            <div class="requireBox" >
                          <div class="requireTitle"><span> 技术要求</span></div>
                          <p v-if="requirement && requirement.requirement">{{ requirement.requirement }}</p>
                        </div>-->

            <!-- 任务规程 -->
            <!--            <div class="requireBox">
              <div class="requireTitle"><span style="border-color: #00b8ff"> 任务规程</span></div>
              <TaskRegusView v-if="regus.length > 0" :tableData="regus"></TaskRegusView>
            </div>-->
            <!-- 作业指导书 -->
            <!--            <div class="requireBox">
              <div class="requireTitle"><span style="border-color: #00b8ff"> 作业指导书</span></div>
              <div ref="bookStepDom" style="padding: 15px"></div>
            </div>-->
          </a-tab-pane>
          <a-tab-pane key="2" tab="作业填报" :forceRender="true">
            <div style="padding: 10px; height: 100%; overflow-y: auto; overflow-x: hidden; padding-top: 25px">
              <SuperviseItemForm
                ref="superviseForm"
                @ok="onTask8SaveSuccess"
                @fail="onTask8SaveFail"
              ></SuperviseItemForm>
            </div>
          </a-tab-pane>
          <a-tab-pane key="7" tab="任务信息">
            <orderOrTaskDetail type="outsources" :workOrderInfo="workOrderInfo" :taskInfo="selectTask" />
            <div style="padding: 20px">
              <div class="info-wrapper info-top-wrapper">
                <h4>工单信息</h4>
                <a-descriptions title="" bordered>
                  <a-descriptions-item label="工单编号">
                    {{ workOrderInfo.orderCode }}
                  </a-descriptions-item>
                  <a-descriptions-item label="工单类型">
                    {{ workOrderInfo.orderType_dictText }}
                  </a-descriptions-item>
                  <a-descriptions-item label="工单名称">
                    {{ workOrderInfo.orderName }}
                  </a-descriptions-item>
                  <a-descriptions-item label="作业班组">
                    {{ workOrderInfo.groupName }}
                  </a-descriptions-item>
                  <a-descriptions-item label="线路">
                    {{ workOrderInfo.lineName }}
                  </a-descriptions-item>
                  <a-descriptions-item label="车号">
                    {{ workOrderInfo.trainNo }}
                  </a-descriptions-item>
                  <a-descriptions-item label="开始日期">
                    {{ workOrderInfo.startTime }}
                  </a-descriptions-item>
                  <a-descriptions-item label="结束日期">
                    {{ workOrderInfo.finishTime }}
                  </a-descriptions-item>
                </a-descriptions>
              </div>
            </div>
          </a-tab-pane>
          <a-tab-pane key="8" tab="附件">
            <TaskAttached :files.sync="attachedfiles" @addFile="onAddFile"></TaskAttached>
          </a-tab-pane>
        </a-tabs>
      </div>
    </na-splitter>
    <train-plan-list ref="planModalForm" @ok="onSelectPlan"></train-plan-list>
    <work-order-select
      :workGroupId="curDepartId"
      ref="workOrderSelect"
      @ok="onSelectOrder"
      :orderStatus="[7]"
    ></work-order-select>
    <check-modal ref="checkModalForm" :title="checkModalTitle" @ok="onCheckRecord"></check-modal>
    <task-submit-modal ref="taskSubmitModal" @ok="onTaskSubmit"></task-submit-modal>
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
  </div>
</template>

<script>
import moment from 'moment'
import clone from 'clone'
import TrainPlanList from '@/views/tiros/common/selectModules/TrainPlanList'
import WorkOrderSelect from '@views/tiros/common/selectModules/WorkOrderSelect'
import TaskWorkTime from '@views/tiros/group/myWork/TaskWorkTime'
import TaskCheckForm from '@views/tiros/group/myWork/taskCheckForm/TaskCheckForm'
import checkModal from './checkModal'
import taskSubmitModal from '@views/tiros/group/myWork/taskSubmitModal'
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
import ReceiveMaterial from '@views/tiros/group/myWork/ReceiveMaterial'

import {
  delMaterial,
  delTool,
  getOutTaskInfo,
  getTaskRecordForm,
  getTaskRelevanceInfo,
  getTrainPlanOnlineFormById,
  getTrainPlanRecordFormById,
  getWorkTaskList,
  saveFormValue,
  saveMaterial,
  saveTool,
  saveOutInInfo,
  saveSafeAssumeRead,
  submitTask,
  saveRcordTableAssetNo,
  getCheckRecordDetail,
} from '@/api/tirosGroupApi'
import { getFormContent } from '@api/tirosApi'
import { download } from '@api/tirosFileApi'
import NaSplitter from '@comp/tiros/Na-splitter'
import { everythingIsEmpty, randomUUID } from '@/utils/util'
import SupplierList from '@views/tiros/common/selectModules/SupplierList'
import { getBreakdownInfo, addWorkOrderAttached } from '@api/tirosDispatchApi'
import { getSuperviseByOrderTask } from '@api/tirosOutsourceApi'
import { getGroupStockSum } from '@api/tirosMaterialApi'
import luckyexcelUtil from '@views/tiros/util/LuckyexcelUtil'

export default {
  components: {
    NaSplitter,
    BreakdownFormItem,
    SuperviseItemForm,
    TrainPlanList,
    WorkOrderSelect,
    checkModal,
    taskSubmitModal,
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
  },
  computed: {
    readOnly: function () {
      return this.selectTask && this.selectTask.taskStatus === 2
    },
    materialBadge: function(){
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
        !(this.selectTask.taskStatus !== 2 && this.curTabKey === '2')
      ) {
        return 0
      }
      if (this.materials && this.materials.length) {
        // let isOk = true
        let count = 0
        this.materials.forEach((e) => {
          if (e.actAmount === 0 && e.amount !== 0 && (e.opType === 1 || (e.opType === 2 && e.isGenOrder === 1))) {
            count++
          }
        })
        return count
      }
      return 0
    },
    formsBadge: function(){
      let count = 0
      this.taskFormItemList.forEach(element =>{
        if (!element.isIgnore || element.isIgnore !== 1) {
          if (!element.selfCheck || !element.monitorCheck || !element.guarderCheck) {
            count++
          }
        }
      })
      this.checkFormItemList.forEach(element => {
        if (!element.checkUserId || !element.workTime) {
          count++
        }
      });
      return count
    }
  },
  data() {
    return {
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
        orderStatusList: [8], // 工单状态过滤，只能是已领料，没有提交的
        groupId: this.$store.getters.userInfo.departId,
      },
      taskFormsList: [],
      taskFormItemList: [],
      checkFormItemList: [],
      defaultSelectTaskRow: '',
      selectTask: null,
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
      curDepartId: this.$store.getters.userInfo.departId,
      defaultOptions: {
        container: 'luckysheet', //luckysheet为容器id
        title: 'sheet',
        column: 26, // 列数
        row: 50, // 行数
        lang: 'zh', // 设定表格语言
        allowEdit: true, // 允许编辑
        showinfobar: false, // 名称栏
        sheetFormulaBar: false,
        showsheetbar: false, // 底部sheet页
        showstatisticBar: true, // 底部计数栏
        enableAddRow: false, // 允许添加行
        enableAddCol: false, // 允许添加列
        showtoolbar: false, // 是否第二列显示工具栏
        showtoolbarConfig: {
          /* save: true,*/
          undoRedo: true,
        },
      },
      colNames: [],
      checkModalTitle: '自检',
      outModelInfo: {},
      inModelInfo: {},
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
        // transferDate: [{ required: true, message: '请选择交接日期!' }],
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
    }
  },
  created() {
    this.loadTaskList()
  },
  mounted() {
    for (let i = 65; i < 91; i++) {
      this.colNames.push(String.fromCharCode(i))
    }
  },
  methods: {
    moment,
    showAssetModal() {
      /*if (!this.faultModel.trainNo) {
        this.$message.warn('请先选择车辆!')
      } else {
        this.$refs.assetSelect.showModal(this.trainStructId)
        this.$refs.faultAssetSelect.blur()
      }*/
      this.$refs.assetSelectModal.showModal(this.trainStructId)
    },
    onSelectAsset(data) {
      if (!everythingIsEmpty(data)) {
        /* this.faultModel.trainStructureId = data[0].id
        this.faultModel.faultAssetName = data[0].name
        this.$refs.form.validateField('faultAssetName')*/
      }
    },
    saveFaultInfo() {
      this.loading = true
      this.$refs.breakdownForm.save()
    },
    onFaultSaveOk() {
      this.loading = false
    },
    onFaultSaveFail() {
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
        if (this.curTabKey === '2' && this.selectTask.outTask === 0) {
          this.loadCurForm()
        }
        this.saveAssetNo()
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
          // 修改任务状态
          for (let i = 0; i < this.tableDataTask.length; i++) {
            if (this.tableDataTask[i].id == this.selectTask.id) {
              this.tableDataTask[i].taskStatus_dictText = '已开始'
              this.tableDataTask[i].taskStatus = 2
              break
            }
          }
        } else {
          console.error('保存安全预想阅读记录失败：', res.message)
        }
      })
      this.readed = true
    },
    // 检查
    handleCheck(type, title) {
      let selectRecords = this.$refs.recordListTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        let idsStr = selectRecords
          .map((obj, index) => {
            return obj.id
          })
          .join(',')
        this.checkModalTitle = title
        this.$refs.checkModalForm.showModal(idsStr, type)
      } else {
        this.$message.error('请选择要检查的作业记录明细!')
      }
    },
    // 检查回调
    onCheckRecord(result) {
      if (result.saveSuccess) {
        this.loadRecordFormDetail()
        this.getFormsItem()
      }
    },
    openPlanModel() {
      this.$refs.planModalForm.showModal()
      this.$refs.myPlanSelect.blur()
    },
    openOrderModel() {
      this.$refs.workOrderSelect.showModal()
      this.$refs.myOrderSelect.blur()
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
    // 加载任务列表
    loadTaskList() {
      this.selectTask = null
      this.queryParam.startDate = this.startDate ? moment(this.startDate).format('YYYY-MM-DD') : undefined

      if (this.$store.getters.userInfo.username === 'admin') {
        this.queryParam.groupId = null
      } else {
        this.queryParam.groupId = this.$store.getters.userInfo.departId
      }

      getWorkTaskList(this.queryParam).then((res) => {
        if (res.success) {
          this.tableDataTask = res.result
          if (this.tableDataTask && this.tableDataTask.length > 0) {
            // this.defaultSelectTaskRow = res.result[0].id
            this.onTaskSelectChange({ row: this.tableDataTask[0] })
            this.$refs.listTable.setRadioRow(this.tableDataTask[0])
          }
        } else {
          this.$message.warning(res.message)
        }
      })
    },
    // 任务选择改变
    onTaskSelectChange({ row }) {
      this.saveAssetNo()

      // 这里要判断任务是否为委外任务，如果是委外，需要看委外的处理界面
      // row.outTask 0 不是委外任务 1 委外送修任务 2 委外接收任务 3 其他委外任务
      this.selectTask = row
      this.readed = false
      this.curTabKey = '1'
      this.taskFormItemList = []
      this.checkFormItemList = []

      this.$nextTick(() => {
        if (this.$refs.outInfoForm) {
          this.$refs.outInfoForm.clearValidate()
        }
        if (this.$refs.inInfoForm) {
          this.$refs.inInfoForm.clearValidate()
        }
      })

      this.getTaskInfo(this.selectTask.id)

      // 计划任务
      if (this.selectTask.outTask === 0) {
        this.getTaskForms(this.selectTask.id)
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
    getTaskInfo(id) {
      getTaskRelevanceInfo(id).then((res) => {
        // console.log('get task info:', res)
        if (res.success) {
          this.materials = res.result.materials
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
          getTaskRelevanceInfo(id).then((rs) => {
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
                  orderTaskId: this.selectTask.id,
                })
              }
            }
          })
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
                  sendGroupId: this.workOrderInfo.groupId,
                })
              }
            }
          })
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
    selectForm(data) {
      this.curFormIndex = data
      this.saveAssetNo()
      if (this.taskFormsList && this.taskFormsList.length > 0) {
        this.curForm = this.taskFormsList[this.curFormIndex]
        this.loadCurForm()
      }
    },
    // 当前表单所有的检查明细
    getFormsItem(){
      this.taskFormItemList = []
      this.checkFormItemList = []
      this.taskFormsList.forEach(record =>{
        if (record.instType === 3) {
          getTrainPlanRecordFormById({
            id: record.formInstId,
            needCategory: false,
            needChecks: true,
            orderTaskId: this.selectTask.id,
          }).then((res) => {
            if (res.success && res.result) {
              this.taskFormItemList.push(...res.result.detailList)
            } else {
              this.$message.error('获取表单作业明细失败')
            }
          })
        } else if(record.instType === 4){
          getCheckRecordDetail({
            id: record.formInstId,
          }).then((res) => {
            if (res.success && res.result) {
              this.checkFormItemList.push(...res.result)
            } else {
              this.$message.error('获取表单作业明细失败')
            }
          })
        }
      })
    },
    // 根据ID获取表单徽标数
    getFormBadgeByRow(row){
      let count = 0
      if (row.instType === 3) {
        this.taskFormItemList.forEach(element =>{
          if (row.formInstId === element.workRecordId && (!element.isIgnore || element.isIgnore !== 1)) {
            if (!element.selfCheck || !element.monitorCheck || !element.guarderCheck) {
              count++
            }
          }
        })
      } else if(row.instType === 4){
        this.checkFormItemList.forEach(element => {
          if (row.formInstId === element.checkId && (!element.checkUserId || !element.workTime)) {
            count++
          }
        });
      }


      // console.log(count);
      return count
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
    // 加载第几份表单明细
    loadCurForm() {
      // 如果是不作业填报tab页，则不加载？
      if (this.curTabKey !== '2') {
        return
      }

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
        this.loadRecordFormDetail()
      }
      // 在线自定义表单
      if (this.curForm.instType === 1) {
        this.loadOnlineFormDetail()
      }
      // 检查记录表
      if (this.curForm.instType === 4) {
        // this.loadCheckRecordForm()
        this.$nextTick(() => {
          this.$refs.checkRecordForm.findList(this.curForm.formInstId)
        })
      }
    },

    // 获取作业记录明细
    loadRecordFormDetail() {
      // 获取作业记录表明细
      const params = {
        id: this.curForm.formInstId,
        needChecks: true,
        needCategory: false,
        orderTaskId: this.selectTask.id,
      }
      getTrainPlanRecordFormById(params)
        .then((res) => {
          if (res.success) {
            this.curRecordForm = res.result
            // console.log('the record table:', this.curRecordForm)
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
                  this.initSheet(sheet)
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
    // 初始化自定义表单
    initSheet(sheet) {
      const options = clone(this.defaultOptions)
      options.container = 'sheet'

      // 任务已经完成的不能编辑
      if (this.selectTask && this.selectTask.taskStatus === 2) {
        this.showSheetSave = false
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

      setTimeout(() => {
        luckyexcelUtil.setRangeConditionalOncell(this.curOnlineForm.formObjId, this.curOnlineForm.id)
      }, 200)

      // 能编辑情况下，绑定保存南
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
    saveCustomForm() {
      // alert('save')
      // this.form.content = JSON.stringify(luckysheet.getAllSheets()[0])
      const saveForm = clone(this.curOnlineForm)
      saveForm.result = JSON.stringify(luckysheet.getAllSheets()[0].data)
      saveForm.writeDate = this.$moment().format('YYYY-MM-DD hh:mm:ss')
      saveForm.writeUserId = this.$store.getters.userInfo.id
      saveForm.writeGroupId = this.$store.getters.userInfo.departId
      saveForm.orderId = this.selectTask.orderId
      saveForm.orderTaskId = this.selectTask.id
      // console.log('save form:', saveForm)

      saveFormValue(saveForm)
        .then((res) => {
          if (res.success) {
            this.$message.success('保存成功')
          } else {
            this.$message.error('保存失败')
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
        return true
      }
      let isOk = true
      this.materials.forEach((e) => {
        if (e.actAmount === 0 && e.amount !== 0 && (e.opType === 1 || (e.opType === 2 && e.isGenOrder == 1))) {
          isOk = false
        }
      })
      return isOk
    },
    // 保存前检查作业表单是否完成
    // 保存前检查作业表单是否完成
    async checkTaskCanSubmit(checkType = 1) {
      // checkType 1：作业记录表  2：作业检查表
      let result = {
        success: true,
        forms: [],
        records: [],
        checkFomrs: [],
      }
      let list = []
      if (
        !(
          this.selectTask.outTask === 0 &&
          (this.selectTask.taskType === 1 ||
            this.selectTask.taskType === 3 ||
            this.selectTask.taskType === 4 ||
            this.selectTask.taskType === 5)
        )
      ) {
        return result
      }
      this.taskFormsList.forEach((element) => {
        if (element.instType === 3) {
          if (checkType === 1) {
            list.push(
              getTrainPlanRecordFormById({
                id: element.formInstId,
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
                id: element.formInstId,
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
    // 任务提交
    async submitTask() {
      this.saveAssetNo()
      if (!this.readed) {
        this.$message.warn('请先确认已阅读作业要求')
        return
      }
      if (this.selectTask.taskStatus === 0) {
        this.$message.warn('请先确认已阅读作业要求，并完成填报操作')
        return
      }
      // this.$refs.taskSubmitModal.showModal(this.selectTask.id)
      let isTaskMaterialOk = await this.checkMaterialList()
      if (!isTaskMaterialOk) {
        this.$message.warn('物料消耗填报未完成，请检查！')
        this.curTabKey = '4'
        return
      }
      let isOk = await this.checkTaskCanSubmit(1)
      // console.log('检查表单验证');
      // console.log(isOk);
      if (!isOk.success) {
        this.$message.warn(`${isOk.formsName}未完成，请检查！`)
        this.curTabKey = '2'
        // this.$nextTick(()=>{
        this.tabFormKey = this.taskFormsList.findIndex((e) => e == isOk.checkFomrs[0])
        this.selectForm(this.tabFormKey)
        // })
        return
      }
      let isOkCheckRecord = await this.checkTaskCanSubmit(2)

      if (!isOkCheckRecord.success) {
        this.$message.warn(`${isOkCheckRecord.formsName}未完成，请检查！`)
        this.curTabKey = '2'
        // this.$nextTick(()=>{
        this.tabFormKey = this.taskFormsList.findIndex((e) => e == isOkCheckRecord.checkFomrs[0])
        this.selectForm(this.tabFormKey)
        // })
        return
      }
      this.$confirm({
        content: '你确定提交该任务？',
        onOk: () => {
          // 如果是监修任务
          if (this.selectTask.taskType === 8) {
            if (!this.superviseModel) {
              this.$message.warn('你还没有保存监修申请，不能提交任务，请先保存监修申请信息')
              return
            }
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
          for (let i = 0; i < this.tableDataTask.length; i++) {
            if (this.tableDataTask[i].id == this.selectTask.id) {
              this.tableDataTask[i].taskStatus_dictText = '已完成'
              this.tableDataTask[i].taskStatus = 2
              break
            }
          }
        } else {
          this.$message.error(res.message ? res.message : '提交失败')
          console.error('提交检查信息错误：', res.message)
        }
      })
    },
    // 任务提交回调
    onTaskSubmit() {
      this.loadTaskList()
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
          console.log('保存拆装设备成功')
        } else {
          console.log('保存拆装设备失败')
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
    editMaterialRowEvent(row) {
      this.curMaterialEditMode = 2
      this.$refs.MaterialListTable.setActiveRow(row)
    },
    saveMaterialRowEvent(row) {
      this.$refs.MaterialListTable.validate(row, (valid) => {
        if (!valid) {
          // 判断工单班组库存量是否足够
          getGroupStockSum
            .call(this, {
              groupId: this.workOrderInfo.groupId,
              materialTypeId: row.materialTypeId,
            })
            .then((resSum) => {
              if (resSum.success) {
                if (row.actAmount > resSum.result) {
                  this.$message.warn('库存量不足,该物资当前班组库存量：' + resSum.result)
                  return
                }
                this.saveMaterialByRow(row)
              } else {
                this.$message.error(res.message)
                return
              }
            })
        }
      })
    },
    saveMaterialByRow(row) {
      saveMaterial(row)
        .then((res) => {
          if (res.success) {
            this.$message.success('保存成功')
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
    cancelMaterialRowEvent(row) {
      this.$refs.MaterialListTable.clearActived()
      if (this.curMaterialEditMode === 1) {
        // 新增,点击取消
        this.$refs.MaterialListTable.remove(row)
      } else if (this.curMaterialEditMode === 2) {
        // 还原行数据
        this.$refs.MaterialListTable.revertData(row)
      }
      this.curMaterialEditMode = 0
    },
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

      this.$refs.breakdownModal.setWorkStations(this.staffArranges)
      this.$refs.breakdownModal.edit({
        workOrderId: this.selectTask.orderId,
        orderTaskId: this.selectTask.id,
        formType: 2,
        createType: 2, //  1 表示来自调度， 2 表示来自工班
        happenTime: this.$moment().format('YYYY-MM-DD'),
        faultDesc: '',
        phase: 1,
        status: 1
      })
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
            this.$refs.outInfoForm.validateField('engineerId')
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
      this.$message.success('保存失败')
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
    onReceiveMaterialEdit(row) {
      this.curMaterialEditMode = 2
      this.saveMaterialByRow(row)
    },
    checkMaterialShow({ row }) {
      if ((row.opType !== 1 && row.opType !== 2) || (row.opType === 2 && (!row.isGenOrder || row.isGenOrder == 0 ))) {
        return 'display: none;'
      }
    },
    onTaskFormNoWork(data) {
      this.tabFormKey = data
      this.selectForm(data)
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

  .allow-max-height {
    div {
      max-height: initial !important;
    }
  }
}
.badge-tab-pane{
  display: flex;
  .ant-badge{
    margin-left: 5px;
    &[hidden]{
      display: none;
    }
  }
}
</style>
