<template>
  <a-card id="leftPerform">
    <div class="typeTitle">
      <a-icon type="read" style="margin-right: 4px"/>
      合同列表
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
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="名称">
              <a-input placeholder="请输入内容" v-model='searchName' allowClear></a-input>
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
    <div style="width:80%;margin:0 auto">
      <a-tree
        v-if="treeData.length"
        :tree-data="treeData"
        :auto-expand-parent="autoExpandParent"
        :expanded-keys="expandedKeys"
        :selectedKeys="selectedKeys"
        @expand="onExpand"
        @select="onSelect"
        :replaceFields="{title: 'name',key:'id'}"
      >
        <template slot="title" slot-scope="{name}">
        <span v-if="name.indexOf(searchValue) > -1">
          {{ name.substr(0, name.indexOf(searchValue)) }}
          <span style="color: #f50">{{ searchValue }}</span>
          {{ name.substr(name.indexOf(searchValue) + searchValue.length) }}
        </span>
          <span v-else>{{ name }}</span>
        </template>
      </a-tree>
    </div>
  </a-card>
</template>

<script>
import { vendorTree } from '@api/tirosOutsourceApi'
import { everythingIsEmpty } from '@/utils/util'

export default {
  name: 'LeftPerform',
  props: ['contractIds'],
  data() {
    return {
      searchName: '',
      selectedKeys: [],
      treeData: [],
      dataList: [],
      autoExpandParent: true,
      expandedKeys: [],
      titleTree: {
        title: 'title'
      },
      searchValue: '',
      trainTypeId: '',
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
    },
    searchValue: {
      immediate: false,
      handler(value) {
        if (everythingIsEmpty(value)) {
          this.$emit('input', '')
          this.selectedKeys = []
        }
      }
    }

  },
  methods: {
    generateList(data) {
      for (let i = 0; i < data.length; i++) {
        const node = data[i]
        const key = node.id
        this.dataList.push({ key, title: node.name })
        if (node.children) {
          this.generateList(node.children)
        }
      }
    },
    setScopedSlots(tree) {
      tree.forEach(item => {
        item['scopedSlots'] = this.titleTree
        if (item.children) {
          this.setScopedSlots(item.children)
        }
      })
      return tree
    },
    selectTree(value) {
      vendorTree({ trainTypeId: value }).then((res) => {
        if (res.success) {
          this.treeData = this.setScopedSlots(res.result)
          this.generateList(this.treeData)
        }
      })
    },
    onExpand(expandedKeys) {
      this.expandedKeys = expandedKeys
      this.autoExpandParent = false
    },
    getParentKey(title, tree) {
      let parentKey
      for (let i = 0; i < tree.length; i++) {
        const node = tree[i]
        if (node.children) {
          node.children.map(item => {
            if (item.name === title) {
              parentKey = node.id
            } else if (this.getParentKey(title, node.children)) {
              parentKey = this.getParentKey(title, node.children)
            }
          })

        }
      }
      return parentKey
    },
    onSearch() {
      this.selectedKeys = []
      this.$emit('input', '')
      //e.target.value
      const value = this.searchName
      const expandedKeys = this.dataList.map(item => {
        if (item.title.indexOf(value) > -1) {
          return this.getParentKey(item.title, this.treeData)
        }
      }).filter((item, i, self) => item && self.indexOf(item) === i)
      Object.assign(this, {
        expandedKeys,
        searchValue: value,
        autoExpandParent: true
      })
    },
    onSelect(selectedKeys, info) {
      let tempContractIds
      if (selectedKeys.length > 0) {
        this.selectedKeys = selectedKeys
        this.$emit('input', this.selectedKeys[0])

        tempContractIds = info.node.dataRef.contractIds
        this.$emit('changeContractIds', tempContractIds)
      }else {
        this.selectedKeys =[]
        this.$emit('changeContractIds', '')
      }
    }
  }
}
</script>

<style lang="less">
#leftPerform {
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