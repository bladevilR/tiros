<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    centered
    @ok="handleOk"
    @cancel="handleCancel"
    :destroyOnClose="true"
    cancelText="关闭"
  >
    <div style="padding: 0px 20px">
    <a-form-model  ref="outPartForm" :model="modalInfo"  :rules="modalRules" >
      <a-form-model-item :label-col="labelCol" :wrapper-col="wrapperCol" label="线路" prop="lineId">
        <j-dict-select-tag
          disabled
          :triggerChange="true"
          v-model="modalInfo.lineId"
          dictCode="bu_mtr_line,line_name,line_id"
          @change="onLineChange"
        />
      </a-form-model-item>
      <a-form-model-item :label-col="labelCol" :wrapper-col="wrapperCol" label="车号" prop="trainNo">
        <j-dict-select-seach-tag
          disabled
          :triggerChange="true"
          v-model="modalInfo.trainNo"
          :dictCode="dictTrainStr"
        />
      </a-form-model-item>
      <a-form-model-item :label-col="labelCol" :wrapper-col="wrapperCol" label="设备选择" prop="turnoverAssetId">
        <div @click="selectAsset">
          <a-select
            placeholder="请选择送修设备"
            allow-clear
            :open="false"
            :showArrow="true"
            ref="assetSelect"
            v-model="modalInfo.assetName"
          >
            <a-icon slot="suffixIcon" type="ellipsis" />
          </a-select>
        </div>
      </a-form-model-item>
<!--      <a-form-model-item label="部件编号" prop="assetNo">
        <a-input v-model="modalInfo.assetNo" disabled></a-input>
      </a-form-model-item>-->
      <a-form-model-item :label-col="labelCol" :wrapper-col="wrapperCol" label="外观状态" prop="facadeStatus">
        <a-select v-model="modalInfo.facadeStatus">
          <a-select-option value="0">
            无问题
          </a-select-option>
          <a-select-option value="1">
            有问题
          </a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item :label-col="labelCol" :wrapper-col="wrapperCol" v-if="modalInfo.facadeStatus === '1'" label="外观描述" prop="facadeDesc">
        <a-input v-model="modalInfo.facadeDesc"></a-input>
      </a-form-model-item>
      <a-form-model-item :label-col="labelCol" :wrapper-col="wrapperCol" label="工装状态" prop="mixtoolStatus">
        <a-select v-model="modalInfo.mixtoolStatus">
          <a-select-option value="0">
            无问题
          </a-select-option>
          <a-select-option value="1">
            有问题
          </a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item :label-col="labelCol" :wrapper-col="wrapperCol" v-if="modalInfo.mixtoolStatus === '1'" label="工装描述" prop="mixtoolDesc">
        <a-input v-model="modalInfo.mixtoolDesc"></a-input>
      </a-form-model-item>
      <a-form-model-item :label-col="labelCol" :wrapper-col="wrapperCol" label="存在故障" prop="fault">
        <a-select v-model="modalInfo.fault">
          <a-select-option value="0">
            不存在
          </a-select-option>
          <a-select-option :label-col="labelCol" :wrapper-col="wrapperCol" value="1">
            存在
          </a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item :label-col="labelCol" :wrapper-col="wrapperCol" label="备注" prop="remark">
        <a-textarea v-model="modalInfo.remark" :auto-size="{ minRows: 3, maxRows: 6 }" placeholder="请输入备注信息">
        </a-textarea>
      </a-form-model-item>
    </a-form-model>
    </div>
    <CarDeviceSelectNew
      ref="assetSelectModal"
      :trainNo="modalInfo.trainNo"
      :lineId="modalInfo.lineId"
      :multiple="false"
      @ok="onSelectAsset"
    >
    </CarDeviceSelectNew>
    <!-- <train-asset-list ref="assetSelectModal" @ok="onSelectAsset"  :can-select-type="[3,4]"></train-asset-list> -->
  </a-modal>
</template>

<script>
import { randomUUID } from '@/utils/util'
// import TrainAssetList from '@views/tiros/common/selectModules/TrainAssetList'
import CarDeviceSelectNew from "@views/tiros/common/selectModules/CarDeviceSelectNew";
export default {
  name: 'OutPartModal',
  components:{ CarDeviceSelectNew},
  props: {
    title: {
      type: String,
      default: '部件添加'
    },
    lineId: {
      type: String,
      default: ''
    },
    trainNo: {
      type: String,
      default: ''
    },
    existAssets:{
      type: Array,
      default: ()=>{
        return []}
    }
  },
  data () {
    return {
      visible: false,
      confirmLoading: false,
      modalInfo: {
        turnoverAssetId: '',
        facadeStatus: '0',
        mixtoolStatus: '0',
        fault: '0',
        remark: ''
      },
      modalRules: {
        turnoverAssetId:[{ required: true, message: '请选择部件' }],
        facadeStatus:[{ required: true, message: '请选择外观状态' }],
        mixtoolStatus:[{ required: true, message: '请选择工装状态' }],
        fault:[{ required: true, message: '请选择是否存在故障' }],
      },
      dictTrainStr: '',
      structureId: '',
      labelCol: { span: 3},
      wrapperCol: { span: 20 },
    }
  },
  methods: {
    showModal (structId) {
      this.structureId = structId
      this.visible=true
      this.modalInfo.lineId = this.lineId
      if (this.lineId) {
        this.dictTrainStr = 'bu_train_info,train_no,id,line_id=' + this.lineId
      }
      this.modalInfo.trainNo = this.trainNo

      this.modalInfo.assetName = ''
      this.modalInfo.turnoverAssetId = ''
      this.modalInfo.remark = ''
    },
    onLineChange (val, option) {
      if (option) {
        this.dictTrainStr = 'bu_train_info,train_no,id,line_id=' + option.value
      } else {
        this.dictTrainStr = ''
      }
    },
    // 确定
    handleOk () {
      this.$refs.outPartForm.validate((valid) => {
        if (valid) {
          if (this.checkAssetIsExist(this.modalInfo.turnoverAssetId)) {
            this.$message.warning('你选择的设备' + this.modalInfo.assetNo + '已经存在委外设备列表中,不能重复添加')
            return
          }
          this.$emit('ok', Object.assign({}, this.modalInfo))
          this.close()
        }
      })
    },
    checkAssetIsExist (assetId) {
      let sources=this.existAssets.filter(a=>{
        return a.turnoverAssetId === assetId
      })
      return sources.length > 0
    },
    // 关闭
    handleCancel () {
      this.close()
    },
    close () {
      this.$emit('close')
      this.visible = false
    },
    selectAsset () {
      // if (!this.structureId) {
      //   this.$message.warning('请先选择车号!')
      //   return
      // }
      this.$refs.assetSelectModal.showModal()
      this.$refs.assetSelect.blur();
    },
    onSelectAsset (assets) {
      console.log(assets)
      if (assets && assets.length > 0) {
        let asset = assets[0]
        this.modalInfo.id=randomUUID()

        this.modalInfo.assetTypeId = asset.id
        this.modalInfo.assetTypeName = asset.assetName
        //this.modalInfo.structDetailId=asset.structDetailId
        // this.modalInfo.structDetailName = asset.structDetailName
        this.modalInfo.turnoverAssetId =asset.id// 具体车辆设备, 与周转建ID一致
        this.modalInfo.assetName = asset.assetName
        this.modalInfo.assetNo = asset.code // structure.code

        this.modalInfo.facadeStatus_dictText = this.modalInfo.facadeStatus === '1' ? '有问题' : '无问题'
        this.modalInfo.mixtoolStatus_dictText = this.modalInfo.mixtoolStatus === '1' ? '有问题' : '无问题'
        this.modalInfo.fault_dictText = this.modalInfo.fault === '1' ? '是' : '否'
        this.$forceUpdate()
      }
    }
  }
}
</script>

<style scoped>

</style>