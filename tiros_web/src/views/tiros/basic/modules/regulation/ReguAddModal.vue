<template>
  <a-modal
    :title="title"
    :width="'100%'"
    dialogClass="fullDialog"
    :visible="visible"
    :confirmLoading="confirmLoading"
    :destroyOnClose="true"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-tabs v-model="curTabKey"  v-if="reguTypeId == '2'">
        <a-tab-pane class="tabPaneBox" key="0" tab="基本信息">
          <a-form :form="form">
            <a-row style="width: 100%">
              <a-col :span="24 / 2">
                <a-form-item
                  :labelCol="labelCol1"
                  :wrapperCol="wrapperCol1"
                  label="规程序号"
                >
                  <a-input-number
                    style="width: 100%"
                    :min="1"
                    :max="999999"
                    placeholder="规程序号"
                    v-decorator="['no', validatorRules.no]"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="24 / 2">
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="名称">
                  <a-input
                    placeholder="规程名称"
                    v-decorator="['title', validatorRules.title]"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="24 / 2">
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="类型">
                  <j-dict-select-tag
                    :triggerChange="true"
                    v-decorator="['type', validatorRules.type]"
                    @change="handleChangeType"
                    dictCode="bu_regu_type"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="24 / 2">
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="上级">
                  <a-select
                    placeholder="请选择"
                    :open="false"
                    :showArrow="true"
                    v-decorator.trim="['parentName', validatorRules.parentName]"
                    @focus="openModal"
                    ref="mySelect"
                  >
                    <a-icon slot="suffixIcon" type="ellipsis" />
                  </a-select>
                </a-form-item>
              </a-col>
            </a-row>
            <a-row>
              <a-col :span="24">
                <a-form-item
                  :labelCol="labelCol2"
                  :wrapperCol="wrapperCol2"
                  label="安全提示"
                >
                  <a-textarea
                    placeholder="请输入内容"
                    v-decorator="['safeNotice', validatorRules.safeNotice]"
                  />
                </a-form-item>
              </a-col>
            </a-row>

            <a-row>
              <a-col :span="24">
                <a-form-item
                  :labelCol="labelCol2"
                  :wrapperCol="wrapperCol2"
                  label="备注描述"
                >
                  <a-textarea
                    placeholder="请输入内容"
                    v-decorator="['remark', validatorRules.remark]"
                  />
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
          <a-form :form="form2">
            <a-row style="width: 100%">
              <a-col :span="24 / 2">
                <a-form-item
                  :labelCol="labelCol1"
                  :wrapperCol="wrapperCol1"
                  label="维保手段"
                >
                  <j-multi-select-tag
                    :triggerChange="true"
                    v-decorator="['method', validatorRules2.method]"
                    dictCode="bu_regu_method"
                    @change="handleChangeMethod"
                  >
                  </j-multi-select-tag>
                </a-form-item>
              </a-col>
              <a-col :span="24 / 2">
                <a-form-item
                  :labelCol="labelCol1"
                  :wrapperCol="wrapperCol1"
                  label="质量等级"
                >
                  <j-dict-select-tag
                    :triggerChange="true"
                    v-decorator="['qualityLevel', validatorRules2.qualityLevel]"
                    dictCode="bu_regu_quality_level"
                    @change="handleChangeQualityLevel"
                  />
                </a-form-item>
              </a-col>
            </a-row>
            <a-row style="width: 100%">
              <a-col :span="24 / 2">
                <a-form-item
                  :labelCol="labelCol1"
                  :wrapperCol="wrapperCol1"
                  label="关联设备"
                >
                  <a-select
                    placeholder="请选择关联设备"
                    :open="false"
                    :showArrow="true"
                    v-model="trainTypeSysName"
                    @focus="openModal2"
                    ref="mySelect2"
                  >
                    <a-icon slot="suffixIcon" type="ellipsis" />
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :span="24 / 2">
                <a-form-item
                  :labelCol="labelCol1"
                  :wrapperCol="wrapperCol1"
                  label="所需工时"
                >
                  <a-input-number
                    :min="0"
                    :max="999999"
                    style="width: 100%"
                    v-decorator="['workTime', validatorRules2.workTime]"
                    @change="handleChangeWorkTime"
                  />
                </a-form-item>
              </a-col>
            </a-row>
            <a-row>
              <a-col :span="24 / 4">
                <a-form-item
                  :labelCol="labelCol4"
                  :wrapperCol="wrapperCol4"
                  label="是否委外"
                >
                  <a-switch v-model="outsourceSwitch" @change="handleChangeOutsource" />
                </a-form-item>
              </a-col>
              <a-col :span="24 / 4">
                <a-form-item
                  :labelCol="labelCol4"
                  :wrapperCol="wrapperCol4"
                  label="重要工序"
                >
                  <a-switch v-model="importantSwitch" @change="handleChangeImportant" />
                </a-form-item>
              </a-col>
              <a-col :span="24 / 4">
                <a-form-item
                  :labelCol="labelCol4"
                  :wrapperCol="wrapperCol4"
                  label="是否必换"
                >
                  <a-switch
                    v-model="mustReplaceSwitch"
                    @change="handleChangeMustReplace"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="24 / 4">
                <a-form-item
                  :labelCol="labelCol4"
                  :wrapperCol="wrapperCol4"
                  label="是否测量"
                >
                  <a-switch v-model="measureSwitch" @change="handleChangeMeasure" />
                </a-form-item>
              </a-col>
            </a-row>
            <a-row>
              <a-col :span="24">
                <a-form-item
                  :labelCol="labelCol2"
                  :wrapperCol="wrapperCol2"
                  label="技术要求"
                >
                  <a-textarea
                    placeholder="请输入内容"
                    v-decorator="['requirement', validatorRules2.requirement]"
                    @change="handleChangeRequirement"
                  />
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
        </a-tab-pane>
        <a-tab-pane class="tabPaneBox" key="1" tab="额定物料">
          <div style="padding-bottom: 5px">
            <a-space>
              <a-button @click="openAddModal(1)">添加</a-button>
              <a-button @click="delTableData(1)">删除</a-button>
            </a-space>
          </div>
          <div class="tableBox">
            <vxe-table
              border
              :edit-rules="validRules"
              ref="reguMaterialslistTable"
              :align="allAlign"
              :data="detail.reguMaterials"
              height="auto"
              :keep-source="true"
              show-overflow="tooltip"
              :checkbox-config="{ trigger: 'cell', highlight: true }"
              :edit-config="{
                key: 'id',
                trigger: 'manual',
                mode: 'row',
                autoClear: false,
                showStatus: true,
              }"
            >
              <vxe-table-column type="seq" width="40"></vxe-table-column>
              <vxe-table-column type="checkbox" width="40"></vxe-table-column>
              <vxe-table-column
                field="code"
                title="物料编码"
                width="150"
              ></vxe-table-column>
              <vxe-table-column
                field="name"
                title="物料名称"
                align="left"
                header-align="center"
              ></vxe-table-column>
              <vxe-table-column field="useCategory_dictText" title="类别" :edit-render="{name: 'input'}">
                <template v-slot:edit="{row}">
                  <!-- {{row.useCategory_dictText}} -->
                  <j-dict-select-tag
                    v-model="row.useCategory"
                    dictCode="bu_material_type"
                    :hiddenArray="[-1]"
                    style="width: 100%"
                    @select="onCategoryChange"
                    @blur="$refs.reguMaterialslistTable.clearValidate()"
                    :allowClear="false"
                  />
                </template>
              </vxe-table-column>
              <vxe-table-column field="amount" title="所需数量" :edit-render="{name: 'input'}">
                <template v-slot:edit="{ row }">
                  <a-input-number v-model="row.amount" :defaultValue="1" :min="0" :max="999999" style="width: 100%" />
                </template>
              </vxe-table-column>
              <vxe-table-column field="unit" title="单位" width="100"></vxe-table-column>
              <vxe-table-column title="操作" width="150">
                <template v-slot="{ row }">
                  <template v-if="$refs.reguMaterialslistTable.isActiveByRow(row)">
                    <a-space>
                      <a-button type="dashed" size="small" @click.stop="saveRowEvent(row,1)"
                        >保存</a-button
                      >
                      <a-button
                        type="dashed"
                        size="small"
                        @click.stop="cancelRowEvent(row,1)"
                        >取消</a-button
                      >
                    </a-space>
                  </template>
                  <template v-else>
                    <a-button type="dashed" size="small" @click.stop="editRowEvent(row,1)"
                      >编辑</a-button
                    >
                    <!-- <a-button type="dashed" size="small" @click="delRowEvent(row)">删除</a-button>-->
                  </template>
                </template>
              </vxe-table-column>
            </vxe-table>
          </div>
        </a-tab-pane>
        <a-tab-pane class="tabPaneBox" key="2" tab="所需工器具">
          <div style="padding-bottom: 5px">
            <a-space>
              <a-button @click="openAddModal(2)">添加</a-button>
              <a-button @click="delTableData(2)">删除</a-button>
            </a-space>
          </div>
          <div class="tableBox">
            <vxe-table
              border
              ref="reguToolslistTable"
              :edit-rules="validRules2"
              :align="allAlign"
              :data="detail.reguTools"
              height="auto"
              show-overflow="tooltip"
              checkbox-all
              :keep-source="true"
              :edit-config="{
                key: 'id',
                trigger: 'manual',
                mode: 'row',
                autoClear: false,
                showStatus: true,
              }"
              :checkbox-config="{ trigger: 'row', highlight: true }"
            >
              <vxe-table-column type="seq" width="40"></vxe-table-column>
              <vxe-table-column type="checkbox" width="40"></vxe-table-column>
              <vxe-table-column
                field="code"
                title="工器具类型编码"
                width="150"
              ></vxe-table-column>
              <vxe-table-column
                field="name"
                title="工器具名称"
                align="left"
                header-align="center"
              ></vxe-table-column>
              <vxe-table-column
                field="category1_dictText"
                title="工器具分类"
                width="150"
              ></vxe-table-column>
              <vxe-table-column field="amount" title="所需数量" :edit-render="{name: 'input'}">
                <template v-slot:edit="{ row }">
                  <a-input-number v-model="row.amount" :defaultValue="1" :min="0" :max="999999" style="width: 100%" />
                </template>
              </vxe-table-column>
              <vxe-table-column field="unit" title="单位" width="100"></vxe-table-column>
              <vxe-table-column title="操作" width="150">
                <template v-slot="{ row }">
                  <template v-if="$refs.reguToolslistTable.isActiveByRow(row)">
                    <a-space>
                      <a-button type="dashed" size="small" @click.stop="saveRowEvent(row,2)"
                        >保存</a-button
                      >
                      <a-button
                        type="dashed"
                        size="small"
                        @click.stop="cancelRowEvent(row,2)"
                        >取消</a-button
                      >
                    </a-space>
                  </template>
                  <template v-else>
                    <a-button type="dashed" size="small" @click.stop="editRowEvent(row,2)"
                      >编辑</a-button
                    >
                    <!-- <a-button type="dashed" size="small" @click="delRowEvent(row)">删除</a-button>-->
                  </template>
                </template>
              </vxe-table-column>
            </vxe-table>
          </div>
        </a-tab-pane>
        <a-tab-pane class="tabPaneBox" key="5" tab="作业指导">
          <div style="padding-bottom: 5px">
            <a-space>
              <a-button @click="openAddModal(5)">添加</a-button>
              <a-button @click="delTableData(5)">删除</a-button>
            </a-space>
          </div>
          <div class="tableBox">
            <vxe-table
              border
              ref="reguSopsListTable"
              :align="allAlign"
              height="auto"
              checkbox-all
              :data="detail.techBookDetails"
              show-overflow="tooltip"
              :checkbox-config="{ trigger: 'row', highlight: true }"
            >
              <vxe-table-column type="checkbox" width="40"></vxe-table-column>
              <vxe-table-column field="op" title="" width="60">
                <template v-slot="{ row }">
                  <a @click.stop="showSopDeatil(row)"> 查看</a>
                </template>
              </vxe-table-column>
              <vxe-table-column
                field="bookStepNo"
                title="步骤"
                width="60"
              ></vxe-table-column>
              <vxe-table-column
                field="bootStepTitle"
                title="步骤标题"
                align="left"
                header-align="center"
              ></vxe-table-column>
              <vxe-table-column
                field="requireCertificate"
                title="所属作业指导书"
                max-width="200"
              ></vxe-table-column>
            </vxe-table>
          </div>
        </a-tab-pane>
        <a-tab-pane class="tabPaneBox" key="3" tab="所需人员">
          <div style="padding-bottom: 5px">
            <a-space>
              <a-button @click="openAddModal(3)">添加</a-button>
              <a-button @click="delTableData(3)">删除</a-button>
            </a-space>
          </div>
          <div class="tableBox">
            <vxe-table
              border
              ref="reguPersonsListTable"
              :align="allAlign"
              checkbox-all
              height="auto"
              :data="detail.reguPersons"
              show-overflow="tooltip"
              :edit-config="{ trigger: 'manual', mode: 'row' }"
              :checkbox-config="{ trigger: 'row', highlight: true }"
            >
              <vxe-table-column type="seq" width="40"></vxe-table-column>
              <vxe-table-column type="checkbox" width="40"></vxe-table-column>
              <vxe-table-column
                field="requirePostion"
                title="人员岗位"
              ></vxe-table-column>
              <vxe-table-column field="requireTech" title="技术要求"></vxe-table-column>
              <vxe-table-column
                field="requireCertificate"
                title="证书要求"
              ></vxe-table-column>
              <vxe-table-column
                field="amount"
                title="所需人数"
                width="100"
              ></vxe-table-column>
            </vxe-table>
          </div>
        </a-tab-pane>
      </a-tabs>
      <!--  -->
      <a-form v-else :form="form">
        <a-row style="width: 100%">
          <a-col :span="24 / 2">
            <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="规程序号">
              <a-input-number
                style="width: 100%"
                :min="1"
                :max="999999"
                placeholder="规程序号"
                v-decorator="['no', validatorRules.no]"
              />
            </a-form-item>
          </a-col>
          <a-col :span="24 / 2">
            <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="名称">
              <a-input
                placeholder="规程名称"
                v-decorator="['title', validatorRules.title]"
              />
            </a-form-item>
          </a-col>
          <a-col :span="24 / 2">
            <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="类型">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator="['type', validatorRules.type]"
                @change="handleChangeType"
                dictCode="bu_regu_type"
              />
            </a-form-item>
          </a-col>
          <a-col :span="24 / 2">
            <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="上级">
              <a-select
                placeholder="请选择"
                :open="false"
                :showArrow="true"
                v-decorator.trim="['parentName', validatorRules.parentName]"
                @focus="openModal"
                ref="mySelect"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="24">
            <a-form-item :labelCol="labelCol2" :wrapperCol="wrapperCol2" label="安全提示">
              <a-textarea
                placeholder="请输入内容"
                v-decorator="['safeNotice', validatorRules.safeNotice]"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="24">
            <a-form-item :labelCol="labelCol2" :wrapperCol="wrapperCol2" label="备注描述">
              <a-textarea
                placeholder="请输入内容"
                v-decorator="['remark', validatorRules.remark]"
              />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
      <regulation-list
        ref="regulationModalForm"
        :regu-id="reguId"
        :train-type-id="trainTypeId"
        :only-project="true"
        @ok="addTarget"
      ></regulation-list>
      <train-asset-type
        ref="selectForm"
        :multiple="false"
        :trainTypeId="trainTypeId"
        @ok="selectTrainSys"
      ></train-asset-type>
      <material-list
        ref="MaterialModalForm"
        :multiple="true"
        @ok="addMaterialList"
      ></material-list>
      <ToolTypeSelect
        ref="ToolsModalForm"
        :multiple="true"
        :canSelectType="[4, 5]"
        @ok="addToolTypeSelect"
      ></ToolTypeSelect>
      <SopDetailList
        ref="sopDetailModalForm"
        :multiple="true"
        @ok="addSopDetailList"
      ></SopDetailList>
      <WorkSopView ref="workSopViewModal"></WorkSopView>
      <PersonRequirement
        ref="PersonModalForm"
        @ok="addPersonRequirement"
      ></PersonRequirement>
    </a-spin>
  </a-modal>
</template>

<script>
import { getReguDeteil, saveReguDetail } from "@/api/tirosApi";
import { randomUUID } from "@/utils/util";
import { everythingIsEmpty } from "@/utils/util";
import RegulationList from "@views/tiros/common/selectModules/RegulationList";
import ItemForm from "@views/tiros/basic/modules/regulation/ItemForm";
import MaterialList from "../../../common/selectModules/MaterialList";
import JMultiSelectTag from "@comp/dict/JMultiSelectTag";
import TrainAssetType from "../../../common/selectModules/TrainAssetType";
import ToolTypeSelect from "@views/tiros/common/selectModules/ToolTypeSelect";
import SopDetailList from "@views/tiros/common/selectModules/SopDetailList";
import WorkSopView from "@views/tiros/basic/modules/worksop/WorkSopView";
import PersonRequirement from "../../../common/inputModules/PersonRequirement";

export default {
  components: {
    ItemForm,
    RegulationList,
    MaterialList,
    JMultiSelectTag,
    TrainAssetType,
    ToolTypeSelect,
    SopDetailList,
    WorkSopView,
    PersonRequirement,
  },
  name: "ReguAddModal",
  props: ["reguId", "trainTypeId"],
  data() {
    return {
      curTabKey: '0',
      curEditMode: 0, // 0 没有编辑，1 新增， 2 修改
      curEditMode2: 0, // 0 没有编辑，1 新增， 2 修改
      title: "操作",
      confirmLoading: false,
      visible: false,
      outsourceSwitch: false,
      importantSwitch: false,
      mustReplaceSwitch: false,
      measureSwitch: false,
      allAlign: "center",
      parent: {},
      detail: {},
      trainTypeSysName: "",
      validRules: {
        code: [{ required: true, message: "请选择物料" }],
        useCategory_dictText: [{ required: true, message: "请选择类别" }],
        amount: [{ required: true, message: "数量必须填写" }],
        remark: [{ max: 200, type: "string", message: "备注不能超过200个字符" }],
      },
      validRules2:{
        amount: [{ required: true, message: "数量必须填写" }],
      },
      form: null, // this.$form.createForm(this),
      form2: null, // this.$form.createForm(this),
      itemFormData: {},
      reguTypeId: "",
      parentId: "",
      parentName: "",
      itemFormField: true,
      validatorRules: {
        no: { rules: [{ required: true, message: "请输入规程序号!" }] },
        title: {
          rules: [
            { required: true, message: "请输入规程名称!" },
            { max: 128, message: "输入长度不能超过32字符!" },
          ],
        },
        type: { rules: [{ required: true, message: "请选择类型!" }] },
        parentId: { rules: [] },
        safeNotice: { rules: [{ max: 255, message: "输入长度不能超过255字符!" }] },
        remark: { rules: [{ max: 255, message: "输入长度不能超过255字符!" }] },
      },

      validatorRules2: {
        method: { rules: [{ required: true, message: "请选择维保手段!" }] },
        qualityLevel: { rules: [{ required: true, message: "请选择质量等级!" }] },
        workTime: { rules: [{ required: true, message: "输入所需工时!" }] },
        requirement: { rules: [{ max: 450, message: "输入长度不能超过450字符!" }] },
      },
      selectTreeNode: [],
      labelCol1: {
        xs: { span: 24 },
        sm: { span: 4 },
      },
      wrapperCol1: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
      labelCol2: {
        xs: { span: 24 },
        sm: { span: 2 },
      },
      wrapperCol2: {
        xs: { span: 24 },
        sm: { span: 20 },
      },
      labelCol4: {
        xs: { span: 24 },
        sm: { span: 8 },
      },
      wrapperCol4: {
        xs: { span: 24 },
        sm: { span: 12 },
      },
    };
  },
  created() {
    this.findSelectList();
  },
  methods: {
    validAllEvent () {
      return new Promise((resolve, reject) => {
        this.$refs.reguMaterialslistTable.validate(true).then((res) => {
          resolve();
        }).catch((err) => {
          reject()
        });
      });
    },
    validAllEvent2 () {
      return new Promise((resolve, reject) => {
        this.$refs.reguToolslistTable.validate(true).then((res) => {
          resolve();
        }).catch((err) => {
          reject()
        });
      });
    },
    saveRowEvent (row,type) {
      if(type == 1){
        this.$refs.reguMaterialslistTable.validate(row, valid => {
          if (!valid) {
            if(this.curEditMode === 1){
              this.detail.reguMaterials.push(row)
            }
            this.$refs.reguMaterialslistTable.clearActived()
            this.curEditMode = 0
          } else {
            for (const validKey in valid) {
              let vals = valid[validKey]
              vals.forEach(item => {
                if (item.rule) {
                  this.$message.error(item.rule.message)
                }
              })
            }
          }
        })
      }else if(type == 2){
        this.$refs.reguToolslistTable.validate(row, valid => {
          if (!valid) {
            if(this.curEditMode2 === 1){
              this.detail.reguTools.push(row)
            }
            this.$refs.reguToolslistTable.clearActived()
            this.curEditMode2 = 0
          }else {
            for (const validKey in valid) {
              let vals = valid[validKey]
              vals.forEach(item => {
                if (item.rule) {
                  this.$message.error(item.rule.message)
                }
              })
            }
          }
        })
      }
    },
    cancelRowEvent (row,type) {
      if(type == 1){
        this.$refs.reguMaterialslistTable.clearActived()
        console.log(this.curEditMode)
        if (this.curEditMode === 1) {
          // 新增,点击取消
          this.$refs.reguMaterialslistTable.remove(row)
        } else if (this.curEditMode === 2) {
          // 还原行数据
          this.$refs.reguMaterialslistTable.revertData(row)
        }
        this.curEditMode = 0
      }else if(type == 2){
        this.$refs.reguToolslistTable.clearActived()
        console.log(this.curEditMode2)
        if (this.curEditMode2 === 1) {
          // 新增,点击取消
          this.$refs.reguToolslistTable.remove(row)
        } else if (this.curEditMode2 === 2) {
          // 还原行数据
          this.$refs.reguToolslistTable.revertData(row)
        }
        this.curEditMode2 = 0
      }
    },
    editRowEvent (row,type) {
      if(type == 1){
        this.curEditMode = 2
        this.$refs.reguMaterialslistTable.setActiveRow(row)
      }else{
        this.curEditMode2 = 2
        this.$refs.reguToolslistTable.setActiveRow(row)
      }
    },
    onCategoryChange(value, option) {
      let record = this.$refs.reguMaterialslistTable.getActiveRecord()
      if (option) {
        record.row.useCategory_dictText = option.title
      } else {
        record.row.useCategory_dictText = undefined
      }
      this.$forceUpdate()
    },
    handleChangeMethod(value) {
      this.detail.method = value;
    },
    handleChangeQualityLevel(value) {
      this.detail.qualityLevel = value;
    },
    openModal2() {
      this.$refs.selectForm.showModal();
      this.$refs.mySelect2.blur();
    },
    selectTrainSys(data) {
      if (data.length) {
        this.detail.assetTypeId = data[0].id;
        this.trainTypeSysName = data[0].name;
      }
    },
    handleChangeWorkTime(value) {
      this.detail.workTime = value;
    },
    handleChangeOutsource(value) {
      this.outsourceSwitch = value;
      let t = value ? 1 : 0;
      this.detail.outsource = t;
    },
    handleChangeImportant(value) {
      this.importantSwitch = value;
      let t = value ? 1 : 0;
      this.detail.important = t;
    },
    handleChangeMustReplace(value) {
      this.mustReplaceSwitch = value;
      let t = value ? 1 : 0;
      this.detail.mustReplace = t;
    },
    handleChangeMeasure(value) {
      this.measureSwitch = value;
      let t = value ? 1 : 0;
      this.detail.measure = t;
    },
    handleChangeRequirement(value) {
      this.detail.requirement = value.target.value;
    },
    openAddModal(activeTab) {
      switch (activeTab) {
        case 1:
          this.$refs.MaterialModalForm.showModal();
          break;
        case 2:
          this.$refs.ToolsModalForm.showModal();
          break;
        case 3:
          this.$refs.PersonModalForm.add();
          break;
        case 5:
          // 添加作业指导书明细
          this.$refs.sopDetailModalForm.showModal();
          break;
      }
    },
    delTableData(activeTab) {
      switch (activeTab) {
        case 1:
          let m = this.$refs.reguMaterialslistTable.getCheckboxRecords();

          m.map((item1, index1) => {
            this.detail.reguMaterials.map((item2, index2) => {
              if (item1.id === item2.id) {
                this.detail.reguMaterials.splice(index2, 1);
              }
            });
          });
          break;
        case 2:
          let t = this.$refs.reguToolslistTable.getCheckboxRecords();

          t.map((item1, index1) => {
            this.detail.reguTools.map((item2, index2) => {
              if (item1.id === item2.id) {
                this.detail.reguTools.splice(index2, 1);
              }
            });
          });
          break;
        case 3:
          let r = this.$refs.reguPersonsListTable.getCheckboxRecords();

          r.map((item1, index1) => {
            this.detail.reguPersons.map((item2, index2) => {
              if (item1.id === item2.id) {
                this.detail.reguPersons.splice(index2, 1);
              }
            });
          });
          break;
        case 5:
          let records = this.$refs.reguSopsListTable.getCheckboxRecords();
          records.map((item1, index1) => {
            this.detail.techBookDetails.map((item2, index2) => {
              if (item1.id === item2.id) {
                this.detail.techBookDetails.splice(index2, 1);
              }
            });
          });
          break;
      }
    },
    addMaterialList(data) {
      console.log(data)
      if(data && data.length > 0){
        this.curEditMode = 1;
        const taskMaterials = this.detail.reguMaterials;
        data.forEach((element, index) => {
          if(element.useCategory == -1){
            element.useCategory_dictText = '';
          }
          if (taskMaterials.find((e) => e.materialTypeId === element.id)) {
            this.$message.warn(`你选择的${element.name}已经在列表中存在`)
            return
          }else{
            let newRow = this.transformMaterialToReguMaterial(element)
            if(data.length == 1){
              this.$refs.reguMaterialslistTable.insertAt(newRow, -1).then(({ row }) => {
                this.$refs.reguMaterialslistTable.setActiveCell(row, 'amount')
              })
            }else{
              this.$refs.reguMaterialslistTable.insertAt(newRow, -1)
              this.detail.reguMaterials.push(newRow)
            }
          }
        })
      }
      // data.map((item) => {
      //   let tempIndex = this.detail.reguMaterials.findIndex((m) => {
      //     return m.materialTypeId === item.id;
      //   });
      //   if (tempIndex < 0) {
      //     // this.detail.reguMaterials.push(item)
      //     this.detail.reguMaterials.push(this.transformMaterialToReguMaterial(item));
      //   } else {
      //     this.$message.error("此物料已添加");
      //   }
      // });
    },
    transformMaterialToReguMaterial(material) {
      let reguMaterial = {};
      reguMaterial.id = randomUUID();
      reguMaterial.reguDetailId = this.reguDetailId;
      reguMaterial.materialTypeId = material.id;
      reguMaterial.amount = material.num;
      reguMaterial.code = material.code;
      reguMaterial.name = material.name;
      reguMaterial.unit = material.unit;
      reguMaterial.category1 = material.category1;
      reguMaterial.category1_dictText = material.category1_dictText;
      reguMaterial.category2 = material.category2;
      reguMaterial.category2_dictText = material.category2_dictText;
      reguMaterial.useCategory = material.useCategory == -1 ? '' : material.useCategory;
      reguMaterial.useCategory_dictText = material.useCategory == -1 ? '' : material.useCategory_dictText;
      return reguMaterial;
    },
    addToolTypeSelect(data) {
      console.log(data)
      if(data && data.length > 0){
        this.curEditMode2 = 1;
        data.map((item) => {
          let tempIndex = this.detail.reguTools.findIndex((m) => {
            return m.toolTypeId === item.id;
          });
          if (tempIndex < 0) {
            // this.reguTools.push(item)
            // this.detail.reguTools.push(this.transformToolToReguTool(item));
            let newRow = this.transformToolToReguTool(item);
            if(data.length == 1){
              this.$refs.reguToolslistTable.insertAt(newRow, -1).then(({ row }) => {
                this.$refs.reguToolslistTable.setActiveCell(row, 'amount')
              })
            }else{
              this.$refs.reguToolslistTable.insertAt(newRow, -1)
              this.detail.reguTools.push(newRow)
            }

          } else {
            this.$message.error("此工器具已添加");
          }
        });
        // const taskMaterials = this.detail.reguTools;
        // data.forEach((element, index) => {
        //   if (taskMaterials.find((e) => e.materialTypeId === element.id)) {
        //     this.$message.warn(`你选择的${element.name}已经在列表中存在`)
        //     return
        //   }else{
        //     let newRow = this.transformMaterialToReguMaterial(element)
        //     if(data.length == 1){
        //       this.$refs.reguMaterialslistTable.insertAt(newRow, -1).then(({ row }) => {
        //         this.$refs.reguMaterialslistTable.setActiveCell(row, 'amount')
        //       })
        //     }else{
        //       this.$refs.reguMaterialslistTable.insertAt(newRow, -1)
        //       this.detail.reguMaterials.push(newRow)
        //     }
        //   }
        // })
      }

    },
    transformToolToReguTool(tool) {
      let reguTool = {};
      reguTool.id = randomUUID();
      reguTool.reguDetailId = this.reguDetailId;
      reguTool.toolTypeId = tool.id;
      reguTool.amount = tool.num;
      reguTool.code = tool.code;
      reguTool.name = tool.name;
      reguTool.unit = tool.unit;
      reguTool.category1 = tool.category1;
      reguTool.category1_dictText = tool.category1_dictText;
      reguTool.category2 = tool.category2;
      reguTool.category2_dictText = tool.category2_dictText;

      return reguTool;
    },
    addSopDetailList(data) {
      // 作业指导书步骤新增
      data.forEach((item) => {
        if (this.detail.techBookDetails.find((e) => e.bookDetailId === item.id)) {
          this.$message.error(`${item.stepTitle}步骤已经添加了`);
        } else {
          this.detail.techBookDetails.push(this.transformFileToReguSop(item));
        }
      });
    },
    transformFileToReguSop(file) {
      let reguSop = {};
      reguSop.id = randomUUID();
      reguSop.reguDetailId = this.reguDetailId;
      reguSop.bookDetailId = file.id;
      reguSop.bookStepNo = file.stepNum;
      reguSop.bootStepContent = file.stepContent;
      reguSop.bootStepTitle = file.stepTitle;
      reguSop.techBookId = file.bookId;

      return reguSop;
    },
    showSopDeatil(row) {
      this.$refs.workSopViewModal.showSopDetail([row.bookDetailId]);
    },
    addPersonRequirement(data) {
      if (data) {
        this.detail.reguPersons.push(data);
      }
    },
    handleOk() {
      this.form.validateFields((err, values) => {
        if (!err) {
          (async () => {
            if (this.reguTypeId == "2") {
              // this.$refs.itemForm.handleCheckFiled();
              // 验证基本信息输入
              this.form2.validateFields((err, values) => {
                this.itemFormField = !err;
              });

              // 验证额定物资输入
              this.$refs.reguMaterialslistTable && await this.validAllEvent().then((res) => {
                this.itemFormField = true;
              }).catch((err) => {
                this.curEditMode = 2;
                this.itemFormField = false;
                this.curTabKey = '1';
                this.$message.warning('额定物料输入验证失败，请检查')
              });

              // 验证工器具输入
              if(this.itemFormField == true){
                this.$refs.reguToolslistTable && await this.validAllEvent2().then((res) => {
                  this.itemFormField = true;
                }).catch((err) => {
                  this.curEditMode2 = 2;
                  this.itemFormField = false;
                  this.curTabKey = '2';
                  this.$message.warning('所需工器具输入验证失败，请检查')
                });
              }
            }
            if (this.itemFormField) {
              let p = Object.assign({}, this.detail, values, {
                reguId: this.reguId,
                parentId: this.parent.id || "",
              });
              this.confirmLoading = true;
              delete p.type_dictText;
              console.log(p);
              saveReguDetail(p)
                .then((res) => {
                  if (res.success) {
                    if (p.id) {
                      this.$emit("reloadTreeChilds", this.parent.id);
                    } else {
                      if (this.parent && this.parent.id) {
                        this.$emit("reloadTreeChilds", this.parent.id);
                      } else {
                        this.$emit("ok");
                      }
                    }
                    this.$message.success(res.message);
                    this.close();
                  } else {
                    this.$message.error(res.message);
                  }
                })
                .finally(() => {
                  this.confirmLoading = false;
                });
            }
          })();
        }else{
          this.curTabKey = '0';
        }
      });
    },
    openModal() {
      this.$refs.regulationModalForm.showModal();
      this.$refs.mySelect.blur();
    },
    addTarget(data) {
      if (this.model.id && data[0].id == this.model.id) {
        this.$message.warn("上级不能是自身!");
        return;
      }
      this.form.setFieldsValue({
        parentName: data[0].title,
      });
      this.parent.name = data[0].title;
      this.parent.id = data[0].id;
    },
    add(parent) {
      this.parent = parent;
      this.edit({
        reguMaterials: [],
        reguPersons: [],
        reguTechFiles: [],
        reguTools: [],
        techBookDetails: [],
        method: null,
        method_dictText: null,
        qualityLevel: null,
        qualityLevel_dictText: null,
        assetTypeId: null,
        assetTypeName: null,
        workTime: 0,
        important: 0,
        outsource: 0,
        measure: 0,
        mustReplace: 0,
        requirement: null,
      });
    },
    edit(record) {
      this.visible = true;
      this.form = this.$form.createForm(this);
      this.form2 = this.$form.createForm(this);
      this.form.resetFields();
      this.model = Object.assign({}, record);
      this.reguTypeId = this.model.type;
      if (!everythingIsEmpty(record)) {
        this.parent = { id: record.parentId, name: record.parentName };
        this.detail = record;
        this.trainTypeSysName = this.detail.assetTypeName;
        this.importantSwitch = this.detail.important ? true : false;
        this.outsourceSwitch = this.detail.outsource ? true : false;
        this.measureSwitch = this.detail.measure ? true : false;
        this.mustReplaceSwitch = this.detail.mustReplace ? true : false;
        if (this.reguTypeId == 2) {
          this.form2.resetFields();
          this.$nextTick(() => {
            this.form2.setFieldsValue({
              method: this.detail.method,
              qualityLevel: this.detail.qualityLevel,
              workTime: this.detail.workTime,
              requirement: this.detail.requirement,
            });
          });
        }
      }
      this.$nextTick(() => {
        this.form.setFieldsValue({
          no: this.model.no,
          title: this.model.title,
          type: this.model.type,
          parentName: this.parent.name || "",
          safeNotice: this.model.safeNotice,
          remark: this.model.remark,
        });
      });
    },
    handleChangeType(value) {
      console.log(value);
      let detail = this.form.getFieldsValue();
      detail.type = value;
      this.reguTypeId = value;

      Object.assign(this.detail, {
        reguMaterials: [],
        reguPersons: [],
        reguTechFiles: [],
        reguTools: [],
        techBookDetails: [],
        method: null,
        method_dictText: null,
        qualityLevel: null,
        qualityLevel_dictText: null,
        assetTypeId: null,
        assetTypeName: null,
        workTime: 0,
        important: 0,
        outsource: 0,
        measure: 0,
        mustReplace: 0,
        requirement: null,
      });

      this.$nextTick(() => {
        console.log(detail);
        console.log(1111);
        this.form.setFieldsValue(detail);
        console.log(this.form.getFieldsValue());
      });
    },
    findSelectList() {
      let param = {
        reguId: this.reguId,
        type: 1,
      };
      getReguDeteil(param).then((res) => {
        if (res.success) {
          this.loading = false;
          this.tableData = res.result;
          this.selectTreeNode = res.result.map((item) => {
            return this.genSelectTreeNode(item);
          });
        }
      });
    },
    loadSelectNodeMethod(node) {
      let param = {
        reguId: this.reguId,
        parentId: node.dataRef.id,
        type: this.reguTypeId,
      };
      return new Promise((resolve) => {
        getReguDeteil(param).then((res) => {
          if (res.success) {
            let childrenNode = res.result.map((item) => {
              this.selectTreeNode = this.selectTreeNode.concat(
                this.genSelectTreeNode(item)
              );
            });
          } else {
            this.$message.warning(res.message);
          }
        });
        resolve();
      });
    },
    genSelectTreeNode(node) {
      return {
        id: node.id,
        pId: node.parentId,
        value: node.id,
        title: node.title,
        isLeaf: node.hasChildren ? false : true,
      };
    },
    handleBack() {
      let value = {
        reguId: this.reguId,
        trainTypeId: this.trainTypeId,
      };
      this.$router.push({
        name: "tiros-basic-regulation",
        params: {
          value: value,
        },
      });
      // this.$router.back();
    },
    // 关闭
    handleCancel() {
      this.close();
    },
    close() {
      this.$emit("close");
      this.visible = false;
      // this.curTabKey = '0';
      // this.detail = {};
      Object.assign(this.$data,this.$options.data());
    },
  },
};
</script>

<style lang="less" scope>
.tabPaneBox {
  height: calc(100vh - 174px);
  overflow: auto;
}
.tableBox {
  height: calc(100vh - 216px);
}
</style>
