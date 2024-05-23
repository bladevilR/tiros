<template>
  <a-card id="leftQuality">
    <div class="typeTitle">
      <a-icon type="read" style="margin-right: 4px"/>
      系统分类
    </div>
    <div class="table-page-search-wrapper" style='margin-top: 2px'>
      <a-form layout="inline">
        <a-row :gutter="12">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车型">
              <j-dict-select-tag
                v-model="trainTypeId"
                :placeholder="'请选择车型'"
                dictCode="bu_train_type,name,id"/>
            </a-form-item>
          </a-col>
          <a-col :md="24" :sm="24">
            <a-form-item  :labelCol="labelCol" :wrapperCol="wrapperCol" label="名称">
              <a-input placeholder="名称或编码"  v-model='searchName' allowClear></a-input>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-form-item>
            <a-button type="primary" @click="onSearch" block>搜索</a-button>
          </a-form-item>
        </a-row>
      </a-form>
    </div>
    <div style="width:80%;margin:0 auto" v-if="treeLoad">
      <a-tree
        v-if="selectTreeNode.length"
        :tree-data="selectTreeNode"
        :replaceFields="{title: 'name',key:'id'}"
        @select="onSelect"
        :load-data="loadSelectNodeMethod"
      />
    </div>
  </a-card>
</template>

<script>
import { trainTypeSysList } from '@api/tirosApi'
import { everythingIsEmpty } from '@/utils/util'

export default {
  name: 'LeftSupplier',
  data() {
    return {
      searchName:'',
      treeData: [],
      dataList: [],
      selectTreeNode:[],
      autoExpandParent: true,
      expandedKeys: [],
      treeLoad:true,
      titleTree: {
        title: 'title'
      },
      searchValue: '',
      trainTypeId:'',
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
      }
    }
  },
  watch: {
    trainTypeId: {
      immediate: false,
      handler(id) {
        this.selectTree(id)
      }
    }
  },
  methods: {
    loadSelectNodeMethod (node) {
      let param = {
        trainTypeId: this.trainTypeId,
        id: node.dataRef.id
      }
      return new Promise((resolve) => {
        trainTypeSysList(param).then((res) => {
          if (res.success) {
            node.dataRef.children = res.result
            node.dataRef.children.map((item) => {
              item.isLeaf = !item.hasChildren
            })
            this.selectTreeNode = [...this.selectTreeNode]
          } else {
            this.$message.warning(res.message)
          }
        })
        resolve()
      })
    },

    selectTree(value) {
      trainTypeSysList({ trainTypeId: value }).then((res) => {
        if (res.success) {
          this.selectTreeNode = res.result
        }
      })
    },
    onSearch() {
      let param = {
        trainTypeId: this.trainTypeId,
        searchText: this.searchName,
      }
      trainTypeSysList(param).then((res) => {
        if (res.success) {
          this.loading = false
          this.selectTreeNode=[]
          this.selectTreeNode =res.result
          if(!everythingIsEmpty(this.searchName)){
            this.selectTreeNode.map((item) => {
              item.isLeaf = true
            })
          }else{
            this.treeLoad = false
            this.$nextTick(()=>{
              this.treeLoad = true
            })
          }
        }
      })


    },
    onSelect(selectedKeys, info) {
      this.selectedKeys = selectedKeys
      this.$emit('input', this.selectedKeys[0])
    }
  }
}
</script>

<style lang="less">
#leftQuality {
  .ant-card-body {
    padding: 0 0 24px 0;
    height: calc(100vh - 120px);
    overflow-y: auto;
  }

  .typeTitle {
    background: #eee;
    padding: 15px;
    text-align: center;
  }

  .btn {
    padding: 10px 0;
    text-align: center;
  }
}

</style>