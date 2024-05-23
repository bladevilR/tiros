<template>
  <div>
    <a-modal
      title='设置可替换物资'
      :width="'70%'"
      :visible='visible'
      :confirmLoading='confirmLoading'
      @cancel='handleCancel'
      @ok='saveCanReplaceInfo'
      :destroyOnClose='true'
    >
      <div class='fieldset-wrapper'>
        <h4 class='title'>物资信息</h4>
        <a-form-model
          layout='inline'
          :model='form'
          :label-col='labelCol'
          :wrapper-col='wrapperCol'
        >
          <a-row>
            <a-col :span='8'>
              <a-form-model-item class='modelItemWarp' label='物资编码'>
                <a-input :value='form.code' disabled />
              </a-form-model-item>
            </a-col>
            <a-col :span='8'>
              <a-form-model-item class='modelItemWarp' label='物资名称'>
                <a-input :value='form.name' disabled />
              </a-form-model-item>
            </a-col>
            <a-col :span='8'>
              <a-form-model-item class='modelItemWarp' label='物资描述'>
                <a-input :value='form.spec' disabled />
              </a-form-model-item>
            </a-col>
          </a-row>
        </a-form-model>
      </div>
      <div class='fieldset-wrapper'>
        <h4 class='title'>可替换物资</h4>
        <div style='padding-bottom: 10px'>
          <a-space>
            <a-button @click='$refs.modalForm.showModal()'>添加</a-button>
            <a-button @click='deleteEvent'>删除</a-button>
          </a-space>
        </div>
        <div style='height: calc(100vh - 600px)'>
          <vxe-table
            key='outTable'
            border
            height='auto'
            ref='outTable'
            align='center'
            :data='form.canReplaceList'
            @checkbox-all='selectChangeEvent'
            @checkbox-change='selectChangeEvent'
            show-overflow='tooltip'
            :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
          >
            <vxe-table-column type='checkbox' width='40'></vxe-table-column>
            <vxe-table-column field='code' width='120' title='物资编码'></vxe-table-column>
            <vxe-table-column field='name' width='150' title='物资名称'></vxe-table-column>
            <vxe-table-column field='spec' min-width='100' title='物资描述'></vxe-table-column>
          </vxe-table>
        </div>
      </div>
    </a-modal>
    <material-list
      ref='modalForm'
      :multiple='false'
      :mode='[2,1]'
      @ok='onSelectMaterial'
    ></material-list>
  </div>
</template>

<script>
import MaterialList from '@views/tiros/common/selectModules/MaterialList'
import { listMaterialCanReplace, saveMaterialCanReplace } from '@api/tirosMaterialApi'

export default {
  name: 'MaterialCanReplaceModal',
  components: { MaterialList },
  data() {
    return {
      visible: false,
      confirmLoading: false,
      form: {
        id: null,
        code: null,
        name: null,
        spec: null,
        canReplaceList: []
      },
      selectedList: [],
      labelCol: { span: 6 },
      wrapperCol: { span: 18 }
    }
  },
  methods: {
    selectChangeEvent({ records }) {
      this.selectedList = JSON.parse(JSON.stringify(records))
    },
    deleteEvent() {
      this.selectedList.forEach((item, index, arr) => {
        for (let i = 0, len = this.form.canReplaceList.length; i < len; i++) {
          if (item.id === this.form.canReplaceList[i].id) {
            this.form.canReplaceList.splice(i, 1)
            break
          }
        }
      })
    },
    onSelectMaterial(data) {
      if (data.length) {
        const list = this.form.canReplaceList.filter((item) => {
          return item.id === data[0].id //条件;
        })

        if (list.length) {
          return this.$message.warning('添加的物资类型已经存在列表中')
        }
        this.form.canReplaceList.push({
          id: data[0].id,
          code: data[0].code,
          name: data[0].name,
          spec: data[0].spec
        })
      }
    },
    show(data) {
      this.form = { ...this.form, ...data }

      listMaterialCanReplace({ id: data.id }).then((res) => {
        if (res.success && res.result) {
          this.form.canReplaceList = res.result
          this.selectedList = []
        }
      })

      this.visible = true
    },
    saveCanReplaceInfo() {
      this.confirmLoading = true
      saveMaterialCanReplace(this.form).then((res) => {
        if (res.success) {
          this.$emit('ok')
          this.$message.success(res.message)
          this.handleCancel()
        } else {
          this.$message.warning(res.message)
        }
        this.confirmLoading = false
      })
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false
    }
  }
}
</script>

<style lang='less' scoped>
.modelItemWarp {
  width: 100%;
}
</style>
