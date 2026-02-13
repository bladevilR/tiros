<template>
  <a-modal
    title="查看标准工序"
    :width="900"
    :visible="visible"
    :footer="null"
    @cancel="handleCancel"
  >
    <a-descriptions bordered :column="2" size="small">
      <a-descriptions-item label="工序编码">{{ model.processNo }}</a-descriptions-item>
      <a-descriptions-item label="工序名称">{{ model.processName }}</a-descriptions-item>
      <a-descriptions-item label="工序类型">
        <span v-if="model.processType === '1'">拆卸</span>
        <span v-else-if="model.processType === '2'">检修</span>
        <span v-else-if="model.processType === '3'">安装</span>
        <span v-else>{{ model.processType }}</span>
      </a-descriptions-item>
      <a-descriptions-item label="车型">{{ model.trainType || '-' }}</a-descriptions-item>
      <a-descriptions-item label="标准工时(分钟)">{{ model.standardDuration || 0 }}</a-descriptions-item>
      <a-descriptions-item label="创建时间">{{ formatDate(model.createTime) }}</a-descriptions-item>
    </a-descriptions>

    <a-divider>工序步骤</a-divider>
    <div v-if="stepRows.length === 0" style="text-align: center; padding: 20px; color: #999;">
      暂无步骤信息
    </div>
    <div v-else class="step-list">
      <div v-for="(step, index) in stepRows" :key="index" class="step-item">
        <div class="step-header">
          <a-tag color="blue">步骤 {{ step.stepNo }}</a-tag>
          <span class="step-name">{{ step.name }}</span>
        </div>
        <div v-if="step.content" class="step-content">
          <strong>内容：</strong>{{ step.content }}
        </div>
        <div v-if="step.checkPoint" class="step-content">
          <strong>检验点：</strong>{{ step.checkPoint }}
        </div>
        <div v-if="step.remark" class="step-content">
          <strong>备注：</strong>{{ step.remark }}
        </div>
        <div v-if="step.imageUrl" class="step-image">
          <img :src="step.imageUrl" alt="步骤图片" />
        </div>
      </div>
    </div>

    <a-divider v-if="model.remark">备注</a-divider>
    <div v-if="model.remark" style="padding: 12px; background: #fafafa; border-radius: 4px;">
      {{ model.remark }}
    </div>
  </a-modal>
</template>

<script>
import moment from 'moment'

export default {
  name: 'StandardProcessViewModal',
  data() {
    return {
      visible: false,
      model: {},
      stepRows: []
    }
  },
  methods: {
    view(record) {
      this.visible = true
      this.model = Object.assign({}, record)
      this.stepRows = this.parseStepRows(record.processSteps, record.processImages)
    },
    handleCancel() {
      this.visible = false
      this.model = {}
      this.stepRows = []
    },
    parseStepRows(processSteps, processImages) {
      const imageList = String(processImages || '').split(',').map(item => item.trim()).filter(Boolean)
      if (!processSteps) {
        return []
      }
      try {
        const list = JSON.parse(processSteps)
        if (!Array.isArray(list) || list.length === 0) {
          return []
        }
        return list.map((item, index) => ({
          stepNo: item.stepNo || index + 1,
          name: (item.name || item.stepName || '').trim(),
          content: (item.content || item.desc || item.description || '').trim(),
          checkPoint: (item.checkPoint || '').trim(),
          imageUrl: (item.imageUrl || imageList[index] || '').trim(),
          remark: (item.remark || '').trim()
        }))
      } catch (e) {
        return []
      }
    },
    formatDate(time) {
      if (time) {
        return moment(time).format('YYYY-MM-DD HH:mm:ss')
      }
      return '-'
    }
  }
}
</script>

<style scoped>
.step-list {
  max-height: 500px;
  overflow-y: auto;
}

.step-item {
  padding: 12px;
  margin-bottom: 12px;
  border: 1px solid #f0f0f0;
  border-radius: 4px;
  background: #fafafa;
}

.step-header {
  margin-bottom: 8px;
  font-size: 14px;
}

.step-name {
  margin-left: 8px;
  font-weight: 500;
}

.step-content {
  margin-top: 8px;
  font-size: 13px;
  line-height: 1.6;
}

.step-image {
  margin-top: 12px;
  text-align: center;
}

.step-image img {
  max-width: 100%;
  max-height: 300px;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
}
</style>
