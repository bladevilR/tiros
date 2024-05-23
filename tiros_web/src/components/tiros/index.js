import NaSelect from './Na-select.vue'
import NaSplitter from './Na-splitter.vue'
import NaCard from './Na-card'
import NaCardLight from './Na-card-light'
import NaButtons from '@comp/tiros/Na-buttons'

const  components=[
  NaSelect,
  NaSplitter,
  NaCard,
  NaCardLight,
  NaButtons
]
const na_components = {
  install: function (Vue) {
    components.forEach(c => {
      Vue.component(c.name, c)
    })

  }
}
export default na_components;