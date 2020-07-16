<template>
  <a-modal
    :title="title"
    :width="width"
    :centered="centered"
    :visible="visible"
    @cancel="handleCancel"
    :destroyOnClose="true"
    :footer="null"
  >
    <div v-show="layoutName === 'importView'">
      <a-form :form="form">
        <a-form-item class="a-form-item-header" :labelCol="labelCol" :wrapperCol="wrapperCol" >
          <h4>请按如下要求输入分类字典名称：</h4>
          <p>1.每行请填写一个分类字典;</p>
          <p>2.子级节点相对父级节点使用两个空格缩进。</p>
        </a-form-item>

        <a-form-item
          class="a-form-item-select"
          label="3.父级节点:"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
        >

          <j-tree-select
            ref="treeSelect"
            placeholder="请选择父级节点"
            v-decorator="['pid', validatorRules.pid, validateTrigger=true]"
            dict="sys_category,name,id"
            pidField="pid"
            pidValue="0"
          ></j-tree-select>
        </a-form-item>

        <a-form-item>
          <a-textarea
            v-model="inputValue"
            v-decorator="['name', validatorRules.name]"
            placeholder="请输入要批量导入的分类字典名称, 示例：
一级节点1
  二级节点1
    三级节点1
  二级节点2
    三级节点2
一级节点2
  二级节点3
    三级节点3
  二级节点4
    三级节点4"
            :auto-size="{ minRows: 10, maxRows: 15 }"
          />
        </a-form-item>

        <div class="a-form-item-button">
          <a-button
            :type="layoutName === 'importView' ? 'primary' : ''"
            @click="previewView('previewView')"
          >下一步
          </a-button>
          <a-button class="a-form-item-button-margin" @click="handleCancel">取消</a-button>
        </div>
      </a-form>
    </div>

  <div>
    <a-spin tip="正在导入..." :spinning="confirmLoading">
      <div v-show="layoutName === 'previewView'">
        <h4>导入预览</h4>
        <a-spin :spinning="spinning" tip="加载中...">
          <div style="min-height:400px;">
            <a-directory-tree
              multiple
              v-if="treeData.length > 0"
              :default-expand-all="true"
              :tree-data="treeData"
            >
            </a-directory-tree>
          </div>
        </a-spin>

        <div class="a-form-item-button">
          <a-button
            :type="layoutName === 'previewView' ? 'primary' : ''"
            @click="inputView('importView')"
          >上一步
          </a-button>
          <a-button class="a-form-item-button-margin" @click="handleOk" type="primary">确定</a-button>
          <a-button class="a-form-item-button-margin" @click="handleCancel">取消</a-button>
        </div>
      </div>
    </a-spin>
  </div>
  </a-modal>
</template>

<script>
import { httpAction, getAction } from '@/api/manage'
import pick from 'lodash.pick'
import JTreeSelect from '@/components/jeecg/JTreeSelect'
import { axios } from '@/utils/request'

export default {
  name: 'sysCategoryBatchModal',
  components: {
    JTreeSelect
  },
  data() {
    return {
      form: this.$form.createForm(this),
      title: '操作',
      width: 500,
      centered: true,
      visible: false,
      model: '',
      layoutName: 'importView',
      inputValue: '',
      treeData: [],
      tempArr: [],
      tempArr2: [],
      spaceCount: 0,
      flag: false,
      allIsSpace: false,
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
      },
      loading: false,
      confirmLoading: false,
      spinning: false,
      validatorRules: {
        name: {
          rules: [
            { validator: this.checkCategoryName }
          ]
        }
      },
      url: {
        // 调整Controller接口
        isRepeatAdd: '/sys/category/isRepeatAdd',
        json: '/sys/category/json',
        add: '/sys/category/batchAdd',
        edit: '/sys/category/edit',
        checkCode: '/sys/category/checkCode'
      },
      expandedRowKeys: [],
      pidField: 'pid',
      subExpandedKeys: []
    }
  },
  created() {},
  methods: {
    // 该方法用于预览分类字典树形结构效果
    previewView: function(view) {
      this.form.validateFields(['name'])
      if (this.flag === true) {
        this.getJsonData()
        if (this.treeData !== null) {
          this.layoutName = view
        }
      } else {
        this.layoutName = 'importView'
      }
    },
    // 分类字典输入框校验
    checkCategoryName(rule, value, callback) {
      if (value === '' || value === undefined) {
        callback('分类字典名称不能为空!')
        this.flag = false
      }
      this.handleAllSpace(value)
      if (this.allIsSpace === true) {
        callback('首行不需要空格缩进!')
        this.flag = false
      }
      this.handleTextData(value)
      if (this.spaceCount > 0 && this.spaceCount % 2 !== 0) {
        callback('子级节点相对父级节点使用两个空格缩进，节点末尾不要存在多余空格!')
        this.flag = false
        this.tempArr = []
        this.tempArr2 = []
      } else {
        this.flag = true
      }
    },
    // 该方法用于统计空格数量，控制父子节点以2个空格缩进
    handleTextData(str) {
      // 根据换行符切割字符串存入数组
      this.spaceCount = 0
      this.tempArr = str.split('\n')
      // 遍历数组
      for (var i = 0; i < this.tempArr.length; i++) {
        // 按空字符切割字符串，统计空格数量
        this.tempArr2 = this.tempArr[i].split('')
        for (var j = 0; j < this.tempArr2.length; j++) {
          if (this.tempArr2[j] === ' ') {
            this.spaceCount++
          }
        }
      }
    },
    // 该方法请求后台生成 json 格式字符串接口，获取分类字典树形结构预览数据
    getJsonData() {
      this.spinning = true
      axios.get(this.url.json, {
        // 请求参数：文本域输入的分类字典名称
        params: {
          categoryName: this.inputValue
        }
      })
        .then(resp => {
          console.log('后台响应json数据：' + resp.result)
          // 解析 json 字符串为对象
          let treeData = JSON.parse(resp.result)
          let treeValue = this.$refs['treeSelect'].treeValue
          let parentName = ''
          if (Array.isArray(treeValue)) {
            parentName = treeValue[0].label
          } else {
            parentName = treeValue.label
          }
          // console.log('parentName:', parentName)
          treeData = [{
            'title': parentName,
            'key': '-1',
            children: treeData
          }]
          this.treeData = treeData
          this.spinning = false
        }).catch(err => {
          this.spinning = false
          console.log('请求失败：' + err.message + ',' + err.code)
        })
    },
    inputView: function(view) {
      this.layoutName = view
      this.treeData = []
    },
    // 分类字典名称不能全是空格
    handleAllSpace(str) {
      this.tempArr = str.split(' ')
      let count = 0
      for (var i = 0; i < this.tempArr.length; i++) {
        if (this.tempArr[i] === '') {
          count++
          if (count === this.tempArr.length) {
            this.allIsSpace = true
          } else {
            this.allIsSpace = false
          }
        } else {
          this.allIsSpace = false
        }
      }
    },
    onSelect(keys, event) {
      console.log('Trigger Select', keys, event)
    },
    onExpand() {
      console.log('Trigger Expand')
    },
    add() {
      this.edit({})
    },
    edit(record) {
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.disabled = true
      this.$nextTick(() => {
        setTimeout(() => {
          this.form.setFieldsValue(pick(this.model, 'pid', 'name', 'code', 'id'))
        })
      })
    },
    close() {
      this.$emit('close')
      this.visible = false
    },
    handleOk() {
      let categoryData = {
        name: this.inputValue
      }
      const that = this
      that.confirmLoading = true
      let httpurl = ''
      let method = ''
      if (!this.model.id) {
        httpurl += this.url.add
        method = 'post'
      } else {
        httpurl += this.url.edit
        method = 'put'
      }
      let formData = Object.assign(this.model, categoryData)
      console.log('表单提交数据', formData)
      httpAction(httpurl, formData, method)
        .then(res => {
          if (res.success) {
            that.$message.success(res.message)
            that.submitSuccess(formData)
            that.inputValue = ''
            that.confirmLoading = false
            that.layoutName = 'importView'
            that.close()
          } else {
            that.$message.warning(res.message)
            that.confirmLoading = false
            that.layoutName = 'importView'
          }
        })
        // .finally(() => {
        //   that.$message.warning(res.message)
        //   that.confirmLoading = false
        //   that.layoutName = 'importView'
        // })
    },
    handleCancel() {
      this.close()
      this.treeData = []
      this.layoutName = 'importView'
    },
    popupCallback(row) {
      this.form.setFieldsValue(pick(row, 'pid', 'name', 'code'))
    },
    submitSuccess(formData) {
      if (!formData.id) {
        let treeData = this.$refs.treeSelect.getCurrTreeData()
        this.expandedRowKeys = []
        this.getExpandKeysByPid(formData[this.pidField], treeData, treeData)
        if (formData.pid && this.expandedRowKeys.length === 0) {
          this.expandedRowKeys = this.subExpandedKeys
        }
        this.$emit('ok', formData, this.expandedRowKeys.reverse())
      } else {
        this.$emit('ok', formData)
      }
    },
    getExpandKeysByPid(pid, arr, all) {
      if (pid && arr && arr.length > 0) {
        for (let i = 0; i < arr.length; i++) {
          if (arr[i].key === pid) {
            this.expandedRowKeys.push(arr[i].key)
            this.getExpandKeysByPid(arr[i]['parentId'], all, all)
          } else {
            this.getExpandKeysByPid(pid, arr[i].children, all)
          }
        }
      }
    },
    validateMyCode(rule, value, callback) {
      let params = {
        pid: this.form.getFieldValue('pid'),
        code: value
      }
      getAction(this.url.checkCode, params).then(res => {
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
<style scoped>
/* @import '~@assets/less/common.less' */

h4 {
  color: #4F5F6F;
  font-weight: bold;
  margin-top: 0px;
  margin: 0;
}

.a-form-item-header {
  margin: 0px;
}

.a-form-item-header p {
  margin: 0px;
}

.a-form-item-select {
  margin-bottom: 5px;
  width: 85%;
}

.a-form-item-button {
  margin-top: 10px;
  margin-left: 130px;
}

.a-form-item-button-margin {
  margin-top: 10px;
  margin-left: 10px;
}
</style>
