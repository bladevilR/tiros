<template>
<span>
 <a-select
     allow-clear
     :value="getValueSting"
     :key="key"
     @change="handleChange"
     @select="onSelect"
     :disabled="disabled"
     placeholder="线路"
 >
    <a-select-option :value="undefined">请选择</a-select-option>
    <a-select-option v-for="(item, key) in optionData" :key="key" :value="item.lineId">
      <span style="display: inline-block;width: 100%" :title="item.lineName">
        {{ item.lineName }}
      </span>
    </a-select-option>
  </a-select>
</span>
</template>

<script>
import { getLineList } from '@api/tirosApi'

export default {
  name: 'LineSelectList',
  props: { value: [String], triggerChange: Boolean, disabled: Boolean, defaultFirst: Boolean },
  data () {
    return {
      optionData: [],
      selectValue: '',
      key: 0
    }
  },
  created () {
    this.findList()
  },
  computed: {
    getValueSting () {
      return this.value != null ? this.value.toString() : undefined
    }
  },
  methods: {
    findList () {
      getLineList().then((res) => {
        if (res.success) {
          this.optionData = res.result

          if (this.defaultFirst && this.optionData && this.optionData.length > 0) {
            setTimeout(() => {
              this.$emit('change', this.optionData[0].lineId)
              this.$emit('input', this.optionData[0].lineId)
            }, 500)
          }

        }
      })
    },
    handleChange (e, option) {
      let optionTitle = ''
      if (option && option.componentOptions.children[0].data) {
        optionTitle = option.componentOptions.children[0].data.attrs.title
      }
      this.$emit('change', e)
      this.$emit('input', e)
      this.$emit('changeName', optionTitle)
      if (this.triggerChange) {
        if (e) {
          let optionData = this.optionData[option.key]
          this.$emit('change', e, optionData)
        } else {
          this.$emit('change', e, null)
        }
      }
    },
    onSelect (v, op) {
      if (v) {
        let optionData = this.optionData[op.data.key]
        this.$emit('select', v, optionData)
      } else {
        this.$emit('select', '', null)
      }
    }
  }
}
</script>

<style scoped>

</style>