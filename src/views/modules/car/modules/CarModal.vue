<template>
  <a-modal
    :confirmLoading="confirmLoading"
    :destroyOnClose="true"
    :title="title"
    :visible="visible"
    :width="width"
    @cancel="handleCancel"
    @ok="handleOk"
    cancelText="关闭"
    okText="确定">
    <a-spin :spinning="confirmLoading">
      <a-form :form="form" :layout="formLayout">
        <!--新增数据页表单-->
        <a-row span="24">
          <a-col>
            <a-form-item :labelCol="{span: 2, offset: 1}" :wrapperCol="wrapperCol" label="名称">
              <a-input placeholder="请输入车型名" style="width: 125%; height: 35px"
                       v-decorator="[ 'name', validatorRules.name]"/>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row span="24">
          <a-col :span="12">
            <a-form-item :labelCol="{span: 4, offset: 2}" :wrapperCol="wrapperCol" label="别名">
              <a-input placeholder="请输入别名" style="height: 35px" v-decorator="[ 'alias', validatorRules.alias]"/>
            </a-form-item>
          </a-col>

          <a-col :span="12">
            <a-form-item :labelCol="{span: 4, offset: 2}" :wrapperCol="wrapperCol" label="识别码">
              <a-input placeholder="请输入识别码" style="height: 35px"
                       v-decorator="[ 'identificationCode', validatorRules.identificationCode]"/>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row span="24">
          <a-col :span="12">
            <a-form-item :labelCol="{span: 4, offset: 2}" :wrapperCol="wrapperCol" label="指导价">
              <a-input placeholder="请输入指导价" style="height: 35px"
                       v-decorator="[ 'suggestPrice', validatorRules.suggestPrice]"/>
            </a-form-item>
          </a-col>

          <a-col :span="12">
            <a-form-item :labelCol="{span: 4, offset: 2}" :wrapperCol="wrapperCol" label="类型">
              <!-- <j-dict-select-tag :triggerChange="true" dict-code="type" v-decorator="[ 'type', validatorRules.type]"/> -->
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
            <a-form-item :labelCol="{span: 4, offset: 2}" :wrapperCol="wrapperCol" label="logo图">
              <!--<a-button :style="{ backgroundColor: '#28B16E', border: '1px solid #28B16E' }" size="large" shape="circle">
                <a-icon :style="right" type="upload"/>
              </a-button>-->
              <j-image-upload :trigger-change="true" style="margin-left: 30px"
                              v-decorator="['logoImg', validatorRules.logoImg]"></j-image-upload>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item :labelCol="{span: 4, offset: 2}" :wrapperCol="wrapperCol" label="类型图">
              <j-image-upload :trigger-change="true" style="margin-left: 30px"
                              v-decorator="['typeImg', validatorRules.typeImg]"></j-image-upload>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :span="24">
          <a-col>
            <a-form-item :labelCol="{span: 2, offset: 1}" :wrapperCol="wrapperCol" label="链接">
              <a-input placeholder="请输入链接" style="width: 125%; height: 35px"
                       v-decorator="[ 'link', validatorRules.link]"></a-input>
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item :labelCol="{span: 2, offset: 1}" :wrapperCol="wrapperCol" label="是否新品">
          <a-switch @change="onChange" checkedChildren="是" style="margin-left: 5px"
                    unCheckedChildren="否" v-decorator="['isNew', validatorRules.isNew]"/>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>

  import {httpAction} from '@/api/manage'
  import pick from 'lodash.pick'
  import JDate from '@/components/jeecg/JDate'
  import JImageUpload from '@/components/jeecg/JImageUpload'
  import JTreeSelect from '@/components/jeecg/JTreeSelect'
  import ARow from "ant-design-vue/es/grid/Row";
  import ACol from "ant-design-vue/es/grid/Col";

  export default {
    name: "CarModal",
    components: {
      ACol,
      ARow,
      JDate,
      JImageUpload,
      JTreeSelect
    },
    data() {
      return {
        form: this.$form.createForm(this),
        title: "操作",
        width: 800,
        height: 600,
        visible: false,
        model: {},
        formLayout: 'horizontal',
        labelCol: {
          xs: {span: 24},
          sm: {span: 5},
        },
        wrapperCol: {
          xs: {span: 24},
          sm: {span: 16},
        },
        confirmLoading: false,

        // 表单数据检验
        validatorRules: {
          serialNumber: {
            rules: []
          },
          name: {
            rules: [
              {required: true, message: '请输入车名!'},
            ]
          },
          alias: {
            rules: [
              {required: true, message: '请输入别名!'},
            ]
          },
          type: {
            rules: [
              {required: true, message: '请输入车类型!'},
            ]
          },
          identificationCode: {
            rules: [
              {required: true, message: '请输入识别码!'},
              {pattern: /^.{6,16}$/, message: '请输入6到16位任意字符!'},
            ]
          },
          suggestPrice: {
            rules: [
              {required: true, message: '请输入指导价!'},
              {pattern:/^-?\d+\.?\d*$/, message: '请输入数字!'},
            ]
          },
          logoImg: {
            rules: []
          },
          typeImg: {
            rules: []
          },
          link: {
            rules: [
              {
                pattern: /^(?:([A-Za-z]+):)?(\/{0,3})([0-9.\-A-Za-z]+)(?::(\d+))?(?:\/([^?#]*))?(?:\?([^#]*))?(?:#(.*))?$/,
                message: '请输入正确的网址!'
              },
            ]
          },
          // 其他需要校验的字段，也是按以上方式补充
          // .......
        },
        url: {
          add: "/carManage/car/add",
          edit: "/carManage/car/edit",
        },
      }
    },


    methods: {

      onChange(checked) {
        console.log(`a-switch to ${checked}`);
        // TODO 是否新品传值逻辑
        //this.loadData();
      },

      // 新增
      add() {
        this.edit({});
      },

      // 编辑
      edit(record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          // 回显表单数据
          this.form.setFieldsValue(pick(this.model, 'name', 'alias', 'type', 'identificationCode', 'suggestPrice', 'logoImg', 'typeImg', 'link', 'isNew'))
        })
      },

      // 关闭弹窗
      close() {
        this.$emit('close');
        this.visible = false;
      },

      // 表单新增、编辑数据处理
      handleOk() {
        const that = this;
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true;
            let httpUrl = '';
            let method = '';
            if (!this.model.id) {
              // 发起新增请求
              httpUrl += this.url.add;
              method = 'post';
            } else {
              // 发起编辑请求
              httpUrl += this.url.edit;
              method = 'put';
            }
            let formData = Object.assign(this.model, values);
            console.log("表单提交数据", formData);
            httpAction(httpUrl, formData, method).then((res) => {
              if (res.success) {
                that.$message.success(res.message);
                that.submitSuccess(formData)
              } else {
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
              that.close();
            })
          }
        })
      },

      // 取消新增关闭弹窗
      handleCancel() {
        this.close()
      },

      // 提交成功刷新页面
      submitSuccess(formData) {
        this.$emit('ok', formData);
      }
    }
  }
</script>