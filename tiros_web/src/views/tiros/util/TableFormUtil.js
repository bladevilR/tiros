export default class TableFormUtil {
    constructor(that, refName, options = {}) {
        this.options = {
            attrs: []
        }
        Object.assign(this.options, options)

        if (!this.options.attrs.find(e => e.key === 'edit')) {
            this.options.attrs.push({
                key: 'edit'
            })
        }
        this.options.attrs.forEach(attr => {
            if (Object.hasOwnProperty.call(attr, 'key')) {
                this[attr.key] = false
            }
            if (!Object.hasOwnProperty.call(attr, 'judge')) {
                attr['judge'] = (e) => { return true }
            }
        });
        this.del = false
        this.nextTick = that.$nextTick
        this.refs = that.$refs
        this.confirm = that.$confirm
        this.message = that.$message
        this.refName = refName
        that.$nextTick(() => {
            this.vxeTable = that.$refs[refName]
        })
    }

    get editRow() {
        if (this.edit) {
            let records = this.vxeTable.getCheckboxRecords()
            return records.length ? records[0] : {}
        } else {
            return {}
        }
    }

    update() {
        this.nextTick(() => {
            if (!this.vxeTable) {
                this.vxeTable = this.refs[this.refName]
            }
            let records = this.vxeTable.getCheckboxRecords()
            this.del = records.length > 0
            this.options.attrs.forEach(attr => {
                for (const key in attr) {
                    if (Object.hasOwnProperty.call(attr, key)) {
                        if (records.length) {
                            this[attr.key] = records.length === 1 && attr.judge(records[0])
                        } else {
                            this[attr.key] = false
                        }
                    }
                }
            })
        })
    }

    async handleDel() {
        if (!this.vxeTable) {
            this.vxeTable = this.refs[this.refName]
        }
        return new Promise((resolve, reject) =>{
            let records = this.vxeTable.getCheckboxRecords()
            this.confirm({
                content: `是否删除选中${records.length}条数据？`,
                okText: '确定',
                cancelText: '取消',
                onOk: () => {
                    let delList = records.map((item) => {
                        return item.id
                    })
                    // let formData = new FormData()
                    // formData.append('ids', delList.join(','))
                    // axiosApi(formData).then((res) => {
                    //     if (res.success) {
                    //         this.message.success('删除成功')
                    //         this.findList()
                    //     } else {
                    //         this.message.error(res.message)
                    //     }
                    // })
                    resolve(delList.toString())
                },
                onCancel: () =>{
                    resolve(null)
                }
            })
        })
    }
}
