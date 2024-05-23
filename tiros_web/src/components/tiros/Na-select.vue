<template>
  <a-select
    allow-clear
    :value="getValueSting"
    @change="onChange"
    :placeholder="placeholder">
    <a-select-option :value="undefined">请选择</a-select-option>
    <a-select-option
      v-for="(item,index) in options"
      :key="index"
      :value="item[valueField+'']">
      <span style="display: inline-block;width: 100%" :title="item[''+textField]">
        {{ item[textField+''] }}
      </span>
    </a-select-option>
  </a-select>
</template>

<script>
  //option {label:,value:}
  export default {
    name: 'na-select',
    computed: {
      getValueSting() {
        return this.value != null ? this.value.toString() : undefined
      }
    },
    props: {
      placeholder:{
        type: String,
        default:'',
        required: false
      },
      value:{
        type: String,
        required: false
      },
      options:{
        type: Array,
        required: true
      },
      triggerChange:{
        type: Boolean,
        required: false,
        default: false
      },
      textField:{
        type: String,
        required: false,
        default:'text'
      },
      valueField:{
        type: String,
        required: false,
        default:'value'
      }
    },
    data(){
      return {
        arrayValue:!this.value?[]:this.value.split(",")
      }
    },
    watch:{
      value (val) {
       /* if(!val){
          this.arrayValue = []
        }else{
          this.arrayValue = this.value.split(",")
        }*/
      }
    },
    methods:{
      onChange (val,option) {
        console.log('selectedValue:', val)

        let optionTitle = ''
        if (option && option.componentOptions.children[0].data) {
          optionTitle = option.componentOptions.children[0].data.attrs.title
        }
        this.$emit('input', val)
        this.$emit('changeName', optionTitle)
        /*if (selectedValue instanceof Array) {
          if (this.triggerChange) {
            this.$emit('change', selectedValue.join(','))
          } else {
            this.$emit('input', selectedValue.join(','))
          }
        } else {
          if (this.triggerChange) {
            this.$emit('change', selectedValue)
          } else {
            this.$emit('input', selectedValue)
          }
        }*/
      },
    },

  }
</script>
