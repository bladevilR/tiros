<template>
  <a-card :body-style='cardStyle'>
    <!--    <div v-show='isequ'>-->
    <div>
      <a-form :form='form'>
        <a-row :gutter='24'>
          <a-col :md='10' :sm='24'>
            <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='设备编码'>
              <a-input v-decorator.trim="[ 'code' ]" disabled />
            </a-form-item>
          </a-col>
          <a-col :md='10' :sm='24'>
            <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='设备名称'>
              <a-input v-decorator.trim="[ 'name' ]" disabled />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter='24'>
          <a-col :md='10' :sm='24'>
            <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='上级设备'>
              <j-dict-select-tag
                disabled
                :triggerChange='true'
                v-decorator.trim="['parentName' ]"
              />
            </a-form-item>
          </a-col>
          <a-col :md='10' :sm='24'>
            <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='设备类型'>
              <j-dict-select-tag
                disabled
                :triggerChange='true'
                v-decorator.trim="['maximoAssetType' ]"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter='24'>
          <a-col :md='10' :sm='24'>
            <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='设备品牌'>
              <a-input v-decorator.trim="[ 'brand' ]" disabled />
            </a-form-item>
          </a-col>
          <a-col :md='10' :sm='24'>
            <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='规格型号'>
              <a-input v-decorator.trim="[ 'model' ]" disabled />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter='24'>
          <a-col :md='10' :sm='24'>
            <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='设备厂商'>
              <a-input v-decorator.trim="[ 'vendor' ]" disabled />
            </a-form-item>
          </a-col>
          <a-col :md='10' :sm='24'>
            <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='出厂编号'>
              <a-input v-decorator.trim="[ 'manufNo' ]" disabled />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter='24'>
          <a-col :md='10' :sm='24'>
            <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='所属位置'>
              <a-input v-decorator.trim="[ 'locationCode' ]" disabled />
            </a-form-item>
          </a-col>
          <a-col :md='10' :sm='24'>
            <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='位置名称'>
              <a-input v-decorator.trim="[ 'locationName' ]" disabled />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter='24'>
          <a-col :md='24' :sm='24'>
            <a-form-item :labelCol='labelCol1' :wrapperCol='wrapperCol1' label='备注'>
              <a-input v-decorator.trim="[ 'remark' ]" disabled />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>
<!--    <div v-show='!isequ'>-->
<!--      <a-form :form='form'>-->
<!--        <a-row :gutter='24'>-->
<!--          <a-col :md='10' :sm='24'>-->
<!--            <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='上级结构'>-->
<!--              <j-dict-select-tag-->
<!--                disabled-->
<!--                :triggerChange='true'-->
<!--                v-decorator.trim="['parentName' ]"-->
<!--              />-->
<!--            </a-form-item>-->
<!--          </a-col>-->
<!--          <a-col :md='10' :sm='24'>-->
<!--            <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='所属类型'>-->
<!--              <j-dict-select-tag-->
<!--                disabled-->
<!--                :triggerChange='true'-->
<!--                v-decorator.trim="['structType_dictText' ]"-->
<!--              />-->
<!--            </a-form-item>-->
<!--          </a-col>-->
<!--        </a-row>-->
<!--        <a-row :gutter='24'>-->
<!--          <a-col :md='10' :sm='24'>-->
<!--            <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='结构编码'>-->
<!--              <a-input v-decorator.trim="[ 'code' ]" disabled />-->
<!--            </a-form-item>-->
<!--          </a-col>-->
<!--          <a-col :md='10' :sm='24'>-->
<!--            <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='结构名称'>-->
<!--              <a-input v-decorator.trim="[ 'name' ]" disabled />-->
<!--            </a-form-item>-->
<!--          </a-col>-->
<!--        </a-row>-->
<!--        <a-row :gutter='24'>-->
<!--          <a-col :md='10' :sm='24'>-->
<!--            <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='所属位置'>-->
<!--              <a-input v-decorator.trim="[ 'placeName' ]" disabled />-->
<!--            </a-form-item>-->
<!--          </a-col>-->
<!--          <a-col :md='10' :sm='24'>-->
<!--            <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='位置描述'>-->
<!--              <a-input v-decorator.trim="[ 'placeDesc' ]" disabled />-->
<!--            </a-form-item>-->
<!--          </a-col>-->
<!--        </a-row>-->
<!--        <a-row :gutter='24'>-->
<!--          <a-col :md='24' :sm='24'>-->
<!--            <a-form-item :labelCol='labelCol1' :wrapperCol='wrapperCol1' label='备注'>-->
<!--              <a-input v-decorator.trim="[ 'remark']" disabled />-->
<!--            </a-form-item>-->
<!--          </a-col>-->
<!--        </a-row>-->
<!--      </a-form>-->
<!--    </div>-->
  </a-card>
</template>

<script>
import { getMaximoAsset } from '@api/tirosProductionApi'

export default {
  name: 'ResBasicInfo',
  props: ['trainNo', 'structureDetail'],
  data() {
    return {
      cardStyle: {
        'padding': '10px',
        'height': 'calc(100vh - 185px)'
      },
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 15 }
      },
      labelCol1: {
        xs: { span: 24 },
        sm: { span: 2 }
      },
      wrapperCol1: {
        xs: { span: 24 },
        sm: { span: 21 }
      },

      queryParam: {
        structureDetailId: '',
        trainNo: ''
      },
      allAlign: 'center',
      tableData: [],
      form: this.$form.createForm(this),
      // isequ: false
    }
  },
  // created() {
  //   this.findList()
  //   console.log(this.structureDetail);
  // },
  watch: {
    structureDetail: {
      immediate: true,
      handler(id) {
        this.queryParam.structureDetailId = id
        this.findList()
      }
    }
    /* trainNo: {
      handler (trainNo) {
        this.queryParam.trainNo = trainNo
        console.log("change trainNo:", this.queryParam)
        this.findList()
      }
    }*/
  },
  methods: {
    findList() {
      this.queryParam.maximoAssetId = this.structureDetail
      this.queryParam.trainNo = this.trainNo

      getMaximoAsset({maximoAssetId : this.structureDetail}).then((res) => {
        this.tableData = res.result
        if (res.result != null) {
          this.form.setFieldsValue({
            code: this.tableData.code,
            name: this.tableData.name,
            parentName: this.tableData.parentName,
            maximoAssetType: this.tableData.maximoAssetType,
            locationCode: this.tableData.locationCode,
            locationName: this.tableData.locationName,
            remark: this.tableData.remark
          })
          if (res.result.ext != null) {
            this.form.setFieldsValue({
              brand: this.tableData.ext.brand,
              model: this.tableData.ext.model,
              vendor: this.tableData.ext.vendor,
              manufNo: this.tableData.ext.manufNo
            })
          }
        }
      })
    }
  }
}
</script>

<style scoped>

</style>