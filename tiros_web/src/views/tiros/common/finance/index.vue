<template>
  <div>
    <a-select
      v-if="type == 'project' || type == 'task'"
      allowClear
      style="width: 100%"
      show-search
      :placeholder="
        type == 'project' ? '请选择财务项目' : type == 'task' && '请选择财务任务'
      "
      option-filter-prop="children"
      :filter-option="filterOption"
      v-model="modelValue"
      @focus="handleFocus"
      @change="handleChange"
    >
      <a-select-option v-for="(item, k) in selectList" :key="k" :value="item.id">
        {{ item.name }} - {{item.code}}
      </a-select-option>
    </a-select>
    <span v-else style="color: red">
      参数 “type” 不能为空 参数详阅组件 src/components/finance/index.vue
    </span>
  </div>
</template>

<script>
import { getFinanceProject, getFinanceTask } from "@/api/tirosOutsourceApi.js";
// 财务项目 / 财务任务 下拉框选择组件
export default {
  name: "finance",
  model: {
    prop: "value",
    event: "change",
  },
  // type 选择组件的类型  'project' = 财务项目  'task' = 财务任务   当选择组件为财务任务时 必传 'id' 根据 id 查询财务任务
  props: ["value", "type", "id"],
  data() {
    return {
      modelValue: undefined,
      selectList: [],
    };
  },
  watch: {
    value: {
      handler(newVal) {
        // console.log(newVal,11111);
        this.modelValue = newVal;
      },
      immediate: true,
    },
    id: {
      handler(value) {
        // console.log(value, 22222)
        this.getSelectList();
      },
      immediate: true,
    },
  },
  created() {
    this.getSelectList();
  },
  methods: {
    filterOption(input, option) {
      return (
        option.componentOptions.children[0].text
          .toLowerCase()
          .indexOf(input.toLowerCase()) >= 0
      );
    },
    handleFocus() {
      if (this.type == "task" && !this.id) {
        this.$message.warning("请先选择财务项目！");
      }
    },
    handleChange(value) {
      this.$emit("change", value);
    },
    getSelectList() {
      this.selectList = [];
      const requerst =
        this.type == "project"
          ? getFinanceProject
          : this.type == "task" && getFinanceTask;
      if (requerst) {
        if (this.type == "task" && !this.id) {
          return;
        }
        requerst(this.type == "task" && { projectId: this.id }).then((res) => {
          console.log(res, 111);
          if (res.success && res.result.length) {
            this.selectList = res.result;
          }
        });
      }
    },
  },
};
</script>

<style lang="less" scoped></style>
