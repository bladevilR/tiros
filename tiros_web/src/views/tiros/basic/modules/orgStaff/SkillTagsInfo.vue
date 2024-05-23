<template>
  <div class="info-wrapper info-bottom-wrapper">
    <h4>技能标签</h4>
    <template v-for="(tag, index) in tagTitleList">
      <a-tooltip v-if="tag.length > 20" :key="tag" :title="tag">
        <a-tag :key="tag" :closable="true" color="blue" @close="() => handleClose(tag)">
          {{ `${tag.slice(0, 20)}...` }}
        </a-tag>
      </a-tooltip>
      <a-tag v-else :key="tag" :closable="true" color="blue" @close="() => handleClose(tag)">
        {{ tag }}
      </a-tag>
    </template>
    <a-input
      v-if="inputVisible"
      ref="input"
      type="text"
      size="small"
      :style="{ width: '78px' }"
      :value="inputValue"
      @change="handleInputChange"
      @blur="handleInputConfirm"
      @keyup.enter="handleInputConfirm"
    />
    <a-tag color="blue" v-else style="background: #fff; borderStyle: dashed;" @click="showInput" size="">
      <a-icon type="plus" />
      添加标签
    </a-tag>
  </div>
</template>

<script>
import { getUser } from '@api/tirosApi'

export default {
  props: {
    tagTitleList: {
      type: Array,
      default: () => []
    },
    userId:{
      type:String,
      default: ''
    }
  },
  mounted () {
    getUser({id:this.userId}).then((res)=>{
      if(res.success){
        this.$nextTick(()=>{
          this.$emit('update:tagTitleList', res.result.tagTitleList)
        })
      }
    })
  },

  data () {
    return {
      inputVisible: false,
      inputValue: '',
      tagColor: 'blue'
    }
  },
  methods: {
    handleClose (removedTag) {
      const tags = this.tagTitleList.filter(tag => tag !== removedTag)
      this.trainingList =tags
      this.$emit('update:tagTitleList', this.tagTitleList)

    },

    showInput () {
      this.inputVisible = true
      this.$nextTick(function () {
        this.$refs.input.focus()
      })
    },

    handleInputChange (e) {
      this.inputValue = e.target.value

    },

    handleInputConfirm () {
      const inputValue = this.inputValue
      let tags = this.tagTitleList
      if (inputValue && tags.indexOf(inputValue) === -1) {
       // tags = [...tags, inputValue]
         if(inputValue.length>32){
           this.$message.warning('输入长度不能超过32个字符!')
         }else{
           tags.push(inputValue)
           this.inputVisible=false
           this.inputValue=''
           this.$emit('update:tagTitleList', tags)
         }
      }
    }
  }
}
</script>

<style>
.info-wrapper {
  border: 1px solid #eee;
  position: relative;
  border-radius: 8px;
  padding: 10px;
  margin-bottom: 15px;
  margin-top: 20px;
}

.info-wrapper h4 {
  position: absolute;
  top: -14px;
  padding: 1px 8px;
  margin-left: 16px;
  color: #777;
  border-radius: 2px 2px 0 0;
  background: #fff;
  font-size: 14px;
  width: auto;
}
</style>