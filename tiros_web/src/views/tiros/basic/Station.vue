 <template>
  <a-row type="flex" :gutter="16">
    <a-col :md="5" :sm="24">
      <left-page v-model="typeId" :detail="detail" @select="getDetail"/>
    </a-col>
    <a-col :md="24-5" :sm="24">
      <right-page v-model="typeId" :detail="detail"/>
    </a-col>
  </a-row>
</template>
<script>
  import LeftPage from './modules/workStation/LeftPage'
  import RightPage from './modules/workStation/RightPage'

  export default {
    name: 'Station',
    components: { LeftPage ,RightPage},
    data() {
      return {
        typeId: '',
        detail:{}
      }
    },

    methods: {
      getDetail(data){
        this.detail = data
      }
    }
  }
</script>
