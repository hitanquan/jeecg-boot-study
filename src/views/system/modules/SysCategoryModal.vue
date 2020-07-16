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
    <!-- <a-spin :spinning="confirmLoading"> -->
      <a-form :form="form">
        <a-form-item label="父级节点" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-tree-select
           :disabled="disabled"
            ref="treeSelect"
            placeholder="请选择父级节点"
            v-decorator="['pid', validatorRules.pid]"
            dict="sys_category,name,id"
            pidField="pid"
            pidValue="0">
          </j-tree-select>
        </a-form-item>

        <a-form-item label="分类名称" :labelCol="labelCol" :wrapperCol="wrapperCol" hasFeedback>
          <a-input v-decorator="[ 'name', validatorRules.name]" placeholder="请输入分类名称"></a-input>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="排序">
          <a-input-number placeholder="请输入分类排序" style="width: 100%" v-decorator="[ 'sortNo',validatorRules.sortNo]"/>
        </a-form-item>

        <!-- <a-form-item label="类型编码" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'code', validatorRules.code]" placeholder="请输入类型编码"></a-input>
        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol">
          <span style="font-size: 12px;color:red" slot="label">编码规则(注)</span>
          <span style="font-size: 12px;color:red">
            编码值前缀需和父节点保持一致,比如父级节点编码是A01则当前编码必须以A01开头
          </span>
        </a-form-item> -->
      </a-form>
    <!-- </a-spin> -->
  </a-modal>
</template>

<script>

import { httpAction, getAction } from '@/api/manage'
import pick from 'lodash.pick'
import JTreeSelect from '@/components/jeecg/JTreeSelect'
import { axios } from '@/utils/request'
import Utils from '@/utils/util2'

export default {
  name: 'SysCategoryModal',
  components: {
    JTreeSelect
  },
  data () {
    return {
      form: this.$form.createForm(this),
      title: '操作',
      width: 800,
      visible: false,
      model: {},
      disabled: false,
      sortValue: 0,
      tempId: 0,
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
      },
      confirmLoading: false,
      url: {
        // 调整Controller接口
        list: '/sys/category/rootList',
        add: '/sys/category/add',
        edit: '/sys/category/edit',
        checkCode: '/sys/category/checkCode',
        getMaxSortValue: '/sys/category/getMaxSortValue'
      },
      expandedRowKeys: [],
      pidField: 'pid',
      subExpandedKeys: []
    }
  },
  computed: {
    validatorRules: function() {
      return {
        name: { rules: [{ required: true, message: '请输入分类名称!' }] },
        sortNo: { initialValue: this.sortValue }
      }
    }
  },
  created () {
  },
  mounted() {
    var that = this
    Utils.$on('sort', function (id) {
      that.getMaxSortValue(id)
    })
    Utils.$on('sort2', function (id) {
      that.getInitSortValue()
    })
  },
  methods: {
    getInitSortValue() {
      this.sortValue = 1
    },
    getMaxSortValue(id) {
      axios.get(this.url.getMaxSortValue, {
        params: {
          id: id
        }
      })
        .then(resp => {
          console.log('后台返回结果：' + resp.result)
          this.sortValue = resp.result + 1
          // alert("sortNo:"+this.sortValue)
        }).catch(err => {
          console.log('请求失败：' + err.message + ',' + err.code)
        })
    },
    add () {
      this.addSysCategory({})
    },
    addSysCategory (record) {
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.disabled = false
      this.$nextTick(() => {
        setTimeout(() => {
          this.form.setFieldsValue(pick(this.model, 'pid', 'name', 'code', 'sortNo'))
        })
      })
    },
    edit (record) {
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.disabled = true
      this.$nextTick(() => {
        setTimeout(() => {
          this.form.setFieldsValue(pick(this.model, 'pid', 'name', 'code', 'sortNo'))
        })
      })
    },
    close () {
      this.$emit('close')
      this.visible = false
    },
    handleOk () {
      const that = this
      // 触发表单验证
      this.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let httpurl = ''
          let method = ''
          if (!this.model.id){
            httpurl += this.url.add
            method = 'post'
          } else {
            httpurl += this.url.edit
            method = 'put'
          }
          let formData = Object.assign(this.model, values)
          console.log('表单提交数据', formData)
          const record = this.model
          httpAction(httpurl, formData, method).then((res) => {
            if (res.success){
              if (record.pid !== null && record.pid === '') {
                // alert(record.pid)
                that.$message.success(res.message)
                that.submitSuccess(formData)
                that.close()
              } else {
                that.$message.success(res.message)
                that.submitSuccess(formData)
                that.close()
              }
            } else {
              that.$message.warning(res.message)
            }
          }).finally(() => {
            that.confirmLoading = false
          })
        }
      })
    },
    handleCancel () {
      this.close()
    },
    popupCallback(row){
      this.form.setFieldsValue(pick(row, 'pid', 'name', 'code'))
    },
    submitSuccess(formData){
      if (!formData.id){
        let treeData = this.$refs.treeSelect.getCurrTreeData()
        this.expandedRowKeys = []
        this.getExpandKeysByPid(formData[this.pidField], treeData, treeData)
        if (formData.pid && this.expandedRowKeys.length === 0){
          this.expandedRowKeys = this.subExpandedKeys
        }
        this.$emit('ok', formData, this.expandedRowKeys.reverse())
      } else {
        this.$emit('ok', formData)
      }
    },
    getExpandKeysByPid(pid, arr, all){
      if (pid && arr && arr.length > 0){
        for (let i = 0; i < arr.length; i++){
          if (arr[i].key === pid){
            this.expandedRowKeys.push(arr[i].key)
            this.getExpandKeysByPid(arr[i]['parentId'], all, all)
          } else {
            this.getExpandKeysByPid(pid, arr[i].children, all)
          }
        }
      }
    },
    validateMyCode(rule, value, callback){
      let params = {
        pid: this.form.getFieldValue('pid'),
        code: value
      }
      getAction(this.url.checkCode, params).then((res) => {
        if (res.success) {
          callback()
        } else {
          callback(res.message)
        }
      })
    }
  }
}
</script>
