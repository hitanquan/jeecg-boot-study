<template>
  <a-modal
    :title="title"
    :width="width"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    :destroyOnClose="true"
    cancelText="关闭">
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <!--新增数据页表单-->
        <a-form-item label="名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'name', validatorRules.name]" placeholder="请输入车型名"/>
        </a-form-item>

        <a-row>
          <a-col :span="12">
            <a-form-item label="别名" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="[ 'alias', validatorRules.alias]" placeholder="请输入别名"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="识别码" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="[ 'identificationCode', validatorRules.identificationCode]" placeholder="请输入识别码"/>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="12">
            <a-form-item label="指导价" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="[ 'suggestPrice', validatorRules.suggestPrice]" placeholder="请输入指导价" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-select placeholder="请选择车型" v-decorator="[ 'type', validatorRules.type]" default-value="0">
                <a-select-option value="轿车">轿车</a-select-option>
                <a-select-option value="新能源">新能源</a-select-option>
                <a-select-option value="SUV/MPV">SUV/MPV</a-select-option>
                <a-select-option value="油电混合">油电混合</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="12">
            <a-form-item label="logo图" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-image-upload v-decorator="['logoImg', validatorRules.logoImg]" :trigger-change="true"></j-image-upload>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="类型图" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-image-upload v-decorator="['typeImg', validatorRules.typeImg]" :trigger-change="true"></j-image-upload>
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item label="链接" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'link', validatorRules.link]" placeholder="请输入链接"></a-input>
        </a-form-item>

        <a-form-item label="是否新品" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <!--<span>是否新品</span>-->
          <a-switch  defaultChecked @change="onChange" checkedChildren="是" unCheckedChildren="否"/>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>

  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import { validate} from '@/utils/util'
  import JDate from '@/components/jeecg/JDate'
  //import JUpload from '@/components/jeecg/JUpload'
  import JImageUpload from '@/components/jeecg/JImageUpload'
  import JTreeSelect from '@/components/jeecg/JTreeSelect'

  export default {
    name: "CarModal2",
    components: {
      JDate,
      JImageUpload,
      //JUpload,
      JTreeSelect
    },
    data () {
      return {
        form: this.$form.createForm(this),
        title:"操作",
        width:800,
        visible:false,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },

        confirmLoading: false,
        // 表单数据检验
        validatorRules: {
          serialNumber: {rules: [
          ]},
          name: {rules: [
            {required: true, message: '请输入车名!'},
          ]},
          alias: {rules: [
            {required: true, message: '请输入别名!'},
          ]},
          type: {rules: [
            {required: true, message: '请输入车类型!'},
          ]},
          identificationCode: {rules: [
            {required: true, message: '请输入识别码!'},
           {pattern:/^.{6,16}$/, message: '请输入6到16位任意字符!'},
          ]},
          suggestPrice: {rules: [
            {required: true, message: '请输入指导价!'},
           {pattern:/^-?\d+\.?\d*$/, message: '请输入数字!'},
          ]},
          logoImg: {rules: [
          ]},
          typeImg: {rules: [
          ]},
          link: {rules: [
            {pattern:/^(?:([A-Za-z]+):)?(\/{0,3})([0-9.\-A-Za-z]+)(?::(\d+))?(?:\/([^?#]*))?(?:\?([^#]*))?(?:#(.*))?$/, message: '请输入正确的网址!'},
          ]},
          isNew: {rules: [
            {pattern:/^-?\d+$/, message: '请输入整数!'},
          ]},
          createBy: {rules: [
          ]},
          createTime: {rules: [
          ]},
          updateBy: {rules: [
          ]},
          updateTime: {rules: [
          ]},
          pid: {rules: [
          ]},
        },
        url: {
          add: "/carManage/car/add",
          edit: "/carManage/car/edit",
        },
        expandedRowKeys:[],
        pidField:"pid"

      }
    },
   /* created () {
    },*/
    methods: {
      onChange(checked) {
        console.log(`a-switch to ${checked}`);
        // TODO 是否新品传值逻辑
        this.loadData();
      },

      add () {
        this.edit({});
      },

      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'name','alias','type','identificationCode','suggestPrice','logoImg','typeImg','link','isNew'))
        })
      },

      close () {
        this.$emit('close');
        this.visible = false;
      },

      handleOk () {
        const that = this;
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true;
            let httpurl = '';
            let method = '';
            if(!this.model.id){
              httpurl+=this.url.add;
              method = 'post';
            }else{
              httpurl+=this.url.edit;
               method = 'put';
            }
            // let old_pid = this.model[this.pidField]
            let formData = Object.assign(this.model, values);
            // let new_pid = this.model[this.pidField]
            console.log("表单提交数据",formData)
            httpAction(httpurl,formData,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.submitSuccess(formData,old_pid==new_pid)
              }else{
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
              that.close();
            })
          }
        })
      },

      handleCancel () {
        this.close()
      },

      popupCallback(row){
        this.form.setFieldsValue(pick(row,'serialNumber','name','alias','type','identificationCode','suggestPrice','logoImg','typeImg','link','isNew','createBy','createTime','updateBy','updateTime','pid'))
      },

      submitSuccess(formData,flag){
        if(!formData.id){
          let treeData = this.$refs.treeSelect.getCurrTreeData()
          this.expandedRowKeys=[]
          this.getExpandKeysByPid(formData[this.pidField],treeData,treeData)
          this.$emit('ok',formData,this.expandedRowKeys.reverse());
        }else{
          this.$emit('ok',formData,flag);
        }
      },

      getExpandKeysByPid(pid,arr,all){
        if(pid && arr && arr.length>0){
          for(let i=0;i<arr.length;i++){
            if(arr[i].key==pid){
              this.expandedRowKeys.push(arr[i].key)
              this.getExpandKeysByPid(arr[i]['parentId'],all,all)
              this.getExpandKeysByPid(arr[i]['parentId'],all,all)
            }else{
              this.getExpandKeysByPid(pid,arr[i].children,all)
            }
          }
        }
      }
    }
  }
</script>