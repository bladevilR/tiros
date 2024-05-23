<template>
    <div>
        <div class="descriptionBox">
            <a-descriptions bordered :column="6">
                <a-descriptions-item :span="2" label="线路">{{formCopy.lineName}}</a-descriptions-item>
                <a-descriptions-item :span="2" label="车辆">{{formCopy.trainNo}}</a-descriptions-item>
                <a-descriptions-item :span="2" label="走行公里数">{{formCopy.acceptMileage}}</a-descriptions-item>
                <a-descriptions-item :span="2" label="修程次数">{{formCopy.itemNo}}</a-descriptions-item>
                <a-descriptions-item :span="2" label="计划模板">{{formCopy.planTempName}}</a-descriptions-item>
                <a-descriptions-item :span="2" label="修程">{{formCopy.programName}}</a-descriptions-item>
                <a-descriptions-item :span="2" label="接车日期">{{formCopy.acceptDate}}</a-descriptions-item>
                <a-descriptions-item :span="2" label="计划交车日期">{{formCopy.planFinishDate}}</a-descriptions-item>
                <a-descriptions-item :span="2" label="总列次">{{formCopy.trainIndex}}</a-descriptions-item>
                <a-descriptions-item :span="2" label="交付部门">{{formCopy.sendDeptName}}</a-descriptions-item>
                <a-descriptions-item :span="2" label="交付人">{{formCopy.sendUserName}}</a-descriptions-item>
                <a-descriptions-item :span="2" label="备注">{{formCopy.remark}}</a-descriptions-item>
            </a-descriptions>
        </div>
    </div>
</template>

<script>
import all from './mixins/common.js'
export default {
    name: 'ReceiveCarView',
    mixins: [all],
    data() {
        return {
            formCopy: {},
        }
    },
}
</script>

<style lang="less" scoped>
.descriptionBox {
    /deep/.ant-descriptions-item-label {
        width: 10%;
        text-align: right;
    }
    /deep/.ant-descriptions-item-content {
        width: 20%;
    }
}
</style>