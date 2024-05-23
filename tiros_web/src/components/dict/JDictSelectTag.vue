<template>
  <a-radio-group v-if="tagType == 'radio'" @change="handleInput" :value="getValueSting" :disabled="disabled">
    <a-radio v-for="(item, key) in dictOptions" :key="key" :value="item.value">{{ item.text }}</a-radio>
  </a-radio-group>

  <a-radio-group
    v-else-if="tagType == 'radioButton'"
    buttonStyle="solid"
    @change="handleInput"
    :value="getValueSting"
    :disabled="disabled"
  >
    <a-radio-button v-for="(item, key) in dictOptions" :key="key" :value="item.value">{{ item.text }}</a-radio-button>
  </a-radio-group>
  <!--  :getPopupContainer="target => target.parentNode"  -->
  <a-select
    v-else-if="tagType == 'select'"
    :allowClear="allowClear"
    :placeholder="placeholder"
    :disabled="disabled"
    :value="getValueSting"
    :key="key"
    @focus="focus"
    @blur="blur"
    @change="handleInput"
    @select="onSelect"
  >
    <a-select-option :value="undefined" v-if="allowClear">请选择</a-select-option>
    <a-select-option
      v-for="(item, key) in dictOptions"
      :key="key"
      :value="item.value"
      :class="{'hidden-jdtag': getIsHidden(item.value)}"
      :disabled="disabledArray && disabledArray.length > 0 ? disabledArray.indexOf(Number(item.value)) === -1 : false"
    >
      <span style="display: inline-block; width: 100%" :title="item.text || item.label">
        {{ item.text || item.label }}
      </span>
    </a-select-option>
  </a-select>
</template>

<script>
import { everythingIsEmpty } from '@/utils/util'
import { ajaxGetDictItems, getDictItemsFromCache } from '@/api/api'

export default {
  name: 'JDictSelectTag',
  props: {
    dictCode: String,
    placeholder: String,
    triggerChange: Boolean,
    disabled: Boolean,
    value: [String, Number],
    type: String,
    defaultFirst: {
      type: Boolean,
      default: false,
    },
    disabledArray: Array,
    hiddenArray: Array,
    allowClear: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      dictOptions: [],
      tagType: '',
      key: 0,
    }
  },
  watch: {
    value: {
      immediate: true,
      handler(val, oldVal) {
        /*if (!oldVal) {
          this.triggerDefaultSelect()
        }*/
        // console.log('value change:', this.dictCode, this.value)
      },
    },
    dictCode: {
      // immediate: true,
      handler(val, oldVal) {
        /* 下面代码主要是解决 dictCode 改变后，值没有清除的问题, 注意只有是在从非空状态下变化才执行*/
        if (!everythingIsEmpty(oldVal)) {
          this.$emit('input', null)
        }
        this.initDictData()
      },
    },
  },
  created() {
    // console.log(this.dictCode);
    if (!this.type || this.type === 'list') {
      this.tagType = 'select'
    } else {
      this.tagType = this.type
    }
    //获取字典数据
    this.initDictData()
  },
  computed: {
    getValueSting() {
      return this.value != null ? this.value.toString() : undefined
    },
  },
  mounted() {},
  methods: {
    initDictData() {
      if (this.dictCode) {
        //优先从缓存中读取字典配置
        if (getDictItemsFromCache(this.dictCode)) {
          this.dictOptions = getDictItemsFromCache(this.dictCode)
          // console.log('DictItem %s from cache:',this.dictCode,this.dictOptions)
          this.setDefaultFirst()
          this.$emit('loaderData')
          return
        }

        //根据字典Code, 初始化字典数组
        ajaxGetDictItems(this.dictCode, null).then((res) => {
          if (res.success) {
            this.dictOptions = res.result
            this.$emit('loaderData')
            this.setDefaultFirst()
            // console.log('DictItem %s from remote:', this.dictCode, res.result)
          } else {
            console.error('获取字典数据异常：', res.message)
          }
        })
      } else {
        this.dictOptions = []
      }
    },
    // 设置默认选择第一项
    setDefaultFirst() {
      if (this.defaultFirst && this.dictOptions.length) {
        let val = this.dictOptions[0].value
        this.$emit('input', val)
        if (this.triggerChange) {
          this.$emit('change', val)
        }
      }
      this.triggerDefaultSelect()
    },
    // 默认选中时触发 select change 事件
    triggerDefaultSelect() {
      if (this.getValueSting) {
        this.dictOptions.map((item) => {
          if (item.value === this.getValueSting) {
            this.$emit('select', this.getValueSting, item)
            if(this.triggerChange) {
              this.$emit('change', this.getValueSting, item)
            }
          }
        })
      }
    },
    handleInput(e, option) {
      let optionTitle = ''
      if (option && option.componentOptions.children[0].data) {
        optionTitle = option.componentOptions.children[0].data.attrs.title
      }
      let val
      if (this.tagType === 'radio') {
        val = e.target.value
      } else {
        val = e
      }

      this.$emit('input', val) // add by jakgong 解决设置了triggerChange时，选中后无显示的问题
      this.$emit('changeName', optionTitle)
      // console.log(option)
      if (this.triggerChange) {
        if (val) {
          let optionData = this.dictOptions[option.key]
          this.$emit('change', val, optionData)
        } else {
          this.$emit('change', val, null)
        }
      }
    },
    onSelect(v, op) {
      if (v) {
        let optionData = this.dictOptions[op.data.key]
        this.$emit('select', v, optionData)
      } else {
        this.$emit('select', '', null)
      }
    },
    setCurrentDictOptions(dictOptions) {
      this.dictOptions = dictOptions
    },
    getCurrentDictOptions() {
      return this.dictOptions
    },
    // 获取当前选择项
    getSelectItem(v) {
      let find = this.getValueSting
      if (v) {
        find = v
      }
      let finds = this.dictOptions.filter((item) => {
        return item.value === find
      })
      if (finds.length > 0) {
        return finds[0]
      } else {
        return null
      }
    },
    getSelectItemText(v) {
      let item = this.getSelectItem(v)
      if (item) {
        return item.text || item.label
      } else {
        return ''
      }
    },
    focus() {
      this.$emit('focus')
    },
    blur() {
      this.$emit('blur')
    },
    getIsHidden(value){
      return (this.hiddenArray && this.hiddenArray.length > 0 ? this.hiddenArray.indexOf(Number(value)) !== -1 : false )
    }
  },
}
</script>

<style >
.hidden-jdtag{
  display: none !important;
}
</style>
