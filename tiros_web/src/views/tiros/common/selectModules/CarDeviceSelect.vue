<template>
    <div>
        <!--  车辆具体设备选择 -->
        <a-modal
            title="车辆设备选择"
            width="80%"
            :bodyStyle="{ height: '60vh' }"
            :visible="visible"
            :confirmLoading="confirmLoading"
            :closable="true"
            @ok="handleOk"
            @cancel="close"
            :destroyOnClose="true"
        >
            <!-- 查询区域 -->
            <div class="table-page-search-wrapper">
                <a-form layout="inline" @keyup.enter.native="searchQuery">
                    <a-row :gutter="24">
                        <a-col :md="5" :sm="24">
                            <a-form-item label="名称">
                                <a-input placeholder="名称或编码" v-model="queryParam.title"></a-input>
                            </a-form-item>
                        </a-col>
                        <a-col :md="10" :sm="24">
                            <span
                                style="float: left; overflow: hidden"
                                class="table-page-search-submitButtons"
                            >
                                <a-space>
                                    <a-button @click="searchQuery">查询</a-button>
                                </a-space>
                            </span>
                        </a-col>
                    </a-row>
                </a-form>
            </div>
            <div class="tableHeight">
                <vxe-table
                    border
                    resizable
                    row-id="id"
                    ref="listTable"
                    :align="allAlign"
                    height="auto"
                    :data="tableData"
                    show-overflow="tooltip"
                    max-height="100%"
                    :radio-config="{trigger: !multiple ? 'row' : 'default',highlight: true,checkMethod: checkRadioMethod}"
                    :checkbox-config="{trigger: multiple ? 'row' : 'default', highlight: true,checkStrictly: true,checkMethod: checkRadioMethod}"
                    :tree-config="{ lazy: true, children: 'children', hasChild: 'hasChildren', loadMethod: loadChildrenMethod }"
                >
                    <vxe-table-column v-if="!multiple" type="radio" width="40" fixed="left" />
                    <vxe-table-column v-else type="checkbox" width="40" fixed="left"></vxe-table-column>
                    <vxe-table-column
                        field="assetName"
                        title="名称"
                        tree-node
                        align="left"
                        header-align="center"
                    ></vxe-table-column>
                    <vxe-table-column align="left" header-align="center" field="assetNo" title="编码" width="12%"></vxe-table-column>
                    <vxe-table-column field="status_dictText" title="状态" width="100"></vxe-table-column>
                    <vxe-table-column field="structType_dictText" title="类型" width="10%"></vxe-table-column>
                    <vxe-table-column
                        field="assetTypeName"
                        title="设备类型"
                        width="13%"
                        align="left"
                        header-align="center"
                    ></vxe-table-column>
                </vxe-table>
            </div>
        </a-modal>
    </div>
</template>

<script>
import { getTrainAssetList } from '@/api/tirosApi'

export default {
    props: {
        // 是否多选
        multiple: {
            type: Boolean,
            default: true,
        },
        trainNo: {
            // type: [Number, String],
            required: true,
        },
        // 1 系统 2子系统 3 部件 4 子部件
        canSelectType: {
            type: Array,
            default: () => {
                return [1, 2, 3, 4]
            },
        },
    },
    data() {
        return {
            allAlign: 'center',
            tableData: [],
            visible: false,
            confirmLoading: false,
            queryParam: {
                code: '',
                title: '',
            },
        }
    },
    created() {},
    methods: {
        checkRadioMethod({ row }) {
            //   1 系统 2子系统 3 部件 4 子部件  5 车辆
            if (this.canSelectType.indexOf(row.structType) > -1) {
                return true
            } else {
                return false
            }
        },
        searchQuery() {
            this.findList()
        },
        loadChildrenMethod({ row }) {
            let param = {
                code: this.trainNo,
                id: row.id,
                searchText: this.queryParam.searchText,
            }
            return new Promise((resolve) => {
                getTrainAssetList(param).then((res) => {
                    if (res.success) {
                        resolve(res.result)
                    } else {
                        this.$message.error(`${res.msg}`)
                    }
                })
            })
        },
        handleOk() {
            let selectRecords = []
            if (this.multiple) {
                selectRecords = this.$refs.listTable.getCheckboxRecords()
            } else {
                if (this.$refs.listTable.getRadioRecord()) {
                    selectRecords.push(this.$refs.listTable.getRadioRecord())
                }
            }
            console.log(selectRecords)
            this.$emit('ok', selectRecords)
            this.visible = false
        },
        showModal() {
            this.visible = true
            this.queryParam.code = this.trainNo
            this.findList()
        },
        close() {
            this.visible = false
        },
        findList() {
            this.loading = true
            getTrainAssetList(this.queryParam).then((res) => {
                if (res.success) {
                    this.loading = false
                    this.tableData = res.result
                }
            })
        },
    },
}
</script>

<style lang="less" scoped>
.tableHeight {
    // min-height: calc(100vh - 255px);
    height: calc(100% - 68px);
    // overflow-y: auto;
}
</style>