import moment from 'moment';

export const WebuiMixin = {
  data(){
    return {
      deLayout:{
        labelCol: {
          xs: { span: 24 },
          sm: { span: 6 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 18 },
        },
        bodyHeight: 0,
      },
      defaultDateRange:  [moment().subtract(1,'months'), moment()]
    }
  },
  beforeMount() {
    let h = document.documentElement.clientHeight || document.body.clientHeight
    if (this.deLayout) {
      this.deLayout.bodyHeight = h
    }

  }
}