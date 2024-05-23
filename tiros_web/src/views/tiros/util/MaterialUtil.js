export class MaterialUtil{
  joinDistributionInfo (tableData) {
    if (!tableData || tableData.length < 1) {
      return
    }

    Array.from(tableData,((item)=>{
      item.sourceLocationAndPalletName = '';
      Array.from(item.assignDetailList,((e)=>{
        let source = '';
        source += `${e.ebsWhCode}=>${e.ebsWhChildCode} | ${e.amount}`;
        if(e.palletName){
          source += (' | ' + e.amount);
        }
        if(e.tradeNo){
          source += ` ｜ 第${e.tradeNo}批次`;
        }
        item.sourceLocationAndPalletName += source +" \n"
      }))
    }))
  }
}

export default new MaterialUtil()