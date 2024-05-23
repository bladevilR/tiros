<template>
  <div style="height: 100%">
    <vxe-table border ref="listTable" align="center" height="auto" :data="data" :loading="loading">
      <vxe-table-column field="fieldName" fixed="left" align="center" width="240px" class-name="na-planinfo-cell">
        <template v-slot="{ row }">
          <div class="cell-flex-panel">
            <div class="cell-box">
              <div class="item-task" v-for="task in maxColumn(row)" :key="task.id" style="text-align: left">
                <span>{{ task.Name }}</span>
              </div>
            </div>
            <div class="cell-fieldname">{{ row.fieldName }}</div>
          </div>
        </template>
      </vxe-table-column>
      <vxe-table-column
        :field="item.field"
        :title="item.title"
        v-for="item in tableColumn"
        :key="item.title"
        align="center"
        min-width="15%"
        class-name="na-planinfo-cell"
      >
        <template v-slot="{ row }" style="padding: 5px 0">
          <div class="cell-box">
            <div class="item-task" v-for="task in row[item.field]" :key="task.id" style="text-align: left">
              <div class="point-status" :class="taskStatusClass(task)"></div>
              <span class="na-ellipsis">{{ task.Name }}</span>
            </div>
          </div>
        </template>
      </vxe-table-column>
    </vxe-table>
  </div>
</template>
<script>
export default {
  name: 'PlanInfoItem',
  model: {
    prop: 'tableData',
    event: 'change',
  },
  props: {
    tableData: Array,
  },
  data() {
    return {
      tableColumn: [],
      data: [],
      loading: true,
    }
  },
  watch: {
    tableData: function (value) {
      this.formatData(value)
    },
  },
  mounted() {},
  methods: {
    formatData(value) {
      if (value) {
        this.tableColumn = value.map((e) => {
          return {
            field: e.Name,
            title: `${e.Name}(${this.$moment(e.startTime).format('M.D')})`,
            width: '10%',
          }
        })
        // console.log('the value:', value)
        let list = []
        value.forEach((element) => {
          if (element && element.children) {
            element.children.forEach((record) => {
              let itemIndex = list.findIndex((e) => e.fieldName === record.Name)
              if (itemIndex === -1) {
                list.push({
                  fieldName: record.Name,
                })
                itemIndex = list.length - 1
              }
              if(record && record.children) {
                record.children.forEach((task) => {
                  ;(list[itemIndex][element.Name] || (list[itemIndex][element.Name] = [])).push(task)
                })
              }
            })
          }
        })

        this.$nextTick(() => {
          this.data = list
          this.loading = false
        })
      }
    },
    maxColumn(row) {
      let maxColumn = []
      for (const key in row) {
        if (Object.hasOwnProperty.call(row, key)) {
          const element = row[key]
          if (typeof element === 'object') {
            if (element.length > maxColumn.length) {
              maxColumn = element
            }
          }
        }
      }
      return maxColumn
    },
    taskStatusClass(task){
      // console.log(task)
      return {
        'nostart': task.status === 1,
        'end': task.status === 2,
        'pause': task.status === 3
      }
    }
  },
}
</script>
<style lang="less" scoped>
.cell-box {
  padding: 8px 0;
}
.item-task{
  display: flex;
  align-items: center;
}
.point-status {
  border-radius: 100%;
  width: 12px;
  min-width: 12px;
  height: 12px;
  display: none;


  & + span{
    margin-left: 8px;
  }

  &.nostart{
    display: initial;
    background-color: #D0021B;
  }
  &.end{
    display: initial;
    background-color: #7ED321;
  }
  &.pause{
    display: initial;
    background-color: #F5A623;
  }
}
.cell-fieldname {
  position: absolute;
}
.cell-flex-panel {
  display: flex;
  justify-content: center;
  align-items: center;

  .cell-box {
    opacity: 0;
  }
}

h3 {
  text-align: center;
  margin: 0;
  border: 1px solid #e8eaec;
  padding: 8px;
  color: #606266;
}
</style>