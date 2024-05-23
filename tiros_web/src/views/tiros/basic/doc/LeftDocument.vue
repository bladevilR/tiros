<template>
  <a-card id="leftDocument">
    <div class="typeTitle">
      <a-icon type="read" style="margin-right: 4px"/>
      文档中心
    </div>
    <div>
      <a-row :gutter="24" style="margin-top:5px">
        <a-col :md="19" :sm="24">
          <a-input-search placeholder="请输入内容" @change="onSearch"/>
        </a-col>
        <a-col :md="5" :sm="24" id="extension">
          <a-dropdown >
            <a-menu slot="overlay" @click="handleMenuClick">
              <a-menu-item key="1">展开所有</a-menu-item>
              <a-menu-item key="2">折叠所有</a-menu-item>
              <a-menu-item key="3">展开一级</a-menu-item>
            </a-menu>
            <a-button>
              <a-icon type="ellipsis"/>
            </a-button>
          </a-dropdown>
        </a-col>
      </a-row>
    </div>
    <div style="width:70%;margin:0 auto;"
    >
      <a-tree
        v-if="treeData.length"
        :tree-data="treeData"
        :auto-expand-parent="autoExpandParent"
        :expanded-keys="expandedKeys"
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
          <span v-else>{{ name}}</span>
        </template>
      </a-tree>
    </div>
  </a-card>

</template>

<script>
  import store from '@/store'
  import { getDocumentTree } from '@/api/tirosApi'

  export default {
    name: 'LeftDocument',
    data() {
      return {
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
    created() {
      this.selectTree()
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
      selectTree() {

        getDocumentTree({id:''}).then((res) => {
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
      onSearch(e) {
        const value = e.target.value
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
        this.selectedKeys = selectedKeys
        this.$emit('input', this.selectedKeys[0])
        this.$emit('select', selectedKeys, info)
      },
      getChildKey(data, allKey) {
        data.forEach(d => {
          allKey.push(d.id)
          if (d.children) {
            this.getChildKey(d.children, allKey)
          }
        })
      },
      handleMenuClick(e) {
        if (e.key === '1') {
          let allKey = []
          this.getChildKey(this.treeData, allKey)
          this.expandedKeys = allKey
        } else if (e.key === '2') {
          this.expandedKeys = []
        } else if (e.key === '3') {
          this.expandedKeys = []
          this.treeData.forEach(item => {
            this.expandedKeys.push(item.id)
          })

        }
      }
    }
  }
</script>

<style lang="less">
  #leftDocument {
    .ant-card-body {
      padding: 5px 10px 15px 10px;
      height: calc(100vh - 115px);
    }

    .typeTitle {
      background: #eee;
      padding: 15px;
      text-align: center;
    }

    .ser {
      padding: 10px 0;
      text-align: center;
    }
  }

  #extension {
    .ant-btn {
      margin-left: -12px !important;
    }
  }
</style>