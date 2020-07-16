<template>
  <a-radio-group v-if="tagType=='radio'" @change="handleInput" :value="getValueSting" :disabled="disabled">
    <a-radio v-for="(item, key) in dictOptions" :key="key" :value="item.value">{{ item.text }}</a-radio>
  </a-radio-group>

  <a-select v-else-if="tagType=='select'" :getPopupContainer = "(target) => target.parentNode"  :disabled="disabled" :triggerChange="true" :value="getValueSting" :allowClear="true" >
    <a-select-option :value="undefined">请选择车型</a-select-option>
    <a-select-option v-for="(item, key) in dictOptions" :key="key" :value="item.value">
      <span style="display: inline-block;width: 100%" :title=" item.text || item.label ">
        {{ item.text || item.label }}
      </span>
    </a-select-option>
  </a-select>
</template>

<script>
  import {ajaxGetDictItems} from '@/api/api'

  export default {
    name: "JDictSelectTag",
    props: {
      dictCode: String,
      placeholder: String,
      triggerChange: Boolean,
      disabled: Boolean,
      value: [String, Number],
      type: String
    },
    data() {
      return {
        dictOptions: [],
        tagType:""
      }
    },
    watch:{
      dictCode:{
        immediate:true,
        handler() {
          this.initDictData()
        },
      }
    },
    created() {
      // console.log(this.dictCode);
      if(!this.type || this.type==="list"){
        this.tagType = "select"
      }else{
        this.tagType = this.type
      }
      //获取字典数据
      // this.initDictData();
    },
    computed: {
      getValueSting(){
        return this.value ? this.value.toString() : null;
      },
    },
    methods: {
      initDictData() {
        //根据字典Code, 初始化字典数组
        ajaxGetDictItems(this.dictCode, null).then((res) => {
          if (res.success) {
//                console.log(res.result);
            this.dictOptions = res.result;
          }
        })
      },
      handleInput(e) {
        let val;
        if(this.tagType=="radio"){
          val = e.target.value
        }else{
          val = e
        }
        console.log("下拉选择的参数值：", val);
        if(this.triggerChange){
          console.log("进入了方法没测试")
          this.$emit('change', val);
        }else{
          this.$emit('input', val);
        }
      },
      setCurrentDictOptions(dictOptions){
        this.dictOptions = dictOptions
      },
      getCurrentDictOptions(){
        return this.dictOptions
      }
    }
  }
</script>

<style scoped>
</style>