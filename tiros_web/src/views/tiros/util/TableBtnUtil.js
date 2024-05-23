/***
 * 使用过滤条件方法
 * 
 * options ={
 *  attrs：[
 *    {
        key: 'beforeFlow',
        judge: (e) => e.wfStatus === '未发起',
      },
      {
        key: 'flowEnd',
        judge: (e) => e.wfStatus === '已结束',
      },
 *  ]
 * 
 * }
 */

export default class TableBtnUtil {
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
                        if (attr['isDel']) {
                            if (records.length) {
                                this[attr.key] = records.length > 0 && attr.judge(records[0])
                            } else {
                                this[attr.key] = false
                            }
                        } else {
                            if (records.length) {
                                this[attr.key] = records.length === 1 && attr.judge(records[0])
                            } else {
                                this[attr.key] = false
                            }
                        }
                    }
                }
            })
        })
    }

}

// import TableBtnUtil from '@views/tiros/util/TableBtnUtil'
// btnStatus: new TableBtnUtil(this, 'listTable'),
// :disabled="!btnStatus.edit"
// this.btnStatus.update()
// handleEdit(btnStatus.editRow)
// @checkbox-change="btnStatus.update()"
// @checkbox-all="btnStatus.update()"

// btnStatus: new TableBtnUtil(this, 'listTable', {
//     attrs: [
//       {
//         key: 'beforeFlow',
//         judge: (e) => e.wfStatus === '未发起',
//       },
//       {
//         key: 'flowEnd',
//         judge: (e) => e.wfStatus === '已结束',
//       },
//     ],
//   }),