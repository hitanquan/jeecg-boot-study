<template>
  <div>
    <!-- 卡片一 start -->
    <a-card title="申请待办列表" style="width: 100%" :bordered="false">
      <a href="#" slot="extra">more</a>
      <!-- 操作按钮区域 start -->
      <div>
        <a-row>
            <a-col :span="12">
              <a-button  @click="handleAdd" icon="plus" style="margin-bottom: 16px" type="primary">
                 录入申请
              </a-button>

              <a-dropdown v-if="selectedRowKeys.length > 0">
              <a-menu slot="overlay">
                <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
              </a-menu>
              <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
              </a-dropdown>
            </a-col>
        </a-row>
      </div>
      <!-- 操作按钮区域 end -->

      <!-- 表格数据区域 start -->
      <div>
        <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
          <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
          <a style="margin-left: 24px" @click="onClearSelected">清空</a>
        </div>

        <a-table
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        rowKey="id"
        v-bind="tableProps">
        
        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical" />
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
            <a>删除</a>
          </a-popconfirm>
        </span>

        </a-table>
      </div>
      <!-- 表格数据区域 end -->
    </a-card>
    <!-- 卡片一 end -->


    <!-- 卡片二 start -->
    <br />
    <a-card size="small" title="已办列表" style="width: 300px">
      <a href="#" slot="extra">more</a>
      <p>card content</p>
      <p>card content</p>
      <p>card content</p>
    </a-card>
    <!-- 卡片二 end -->
  </div>
</template>
i
<script>
  import { getAction } from '@/api/manage'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: "apply",
    mixins:[JeecgListMixin],
    components: {
    },
    data () {
      return {
        description: '低保申请页面',
        // 表头
        columns: [
          {
            title:'街道',
            dataIndex: 'street'
          },
          {
            title:'社区',
            dataIndex: 'community'
          },
          {
            title:'姓名',
            dataIndex: 'name'
          },
          {
            title:'身份证号码',
            dataIndex: 'idCardNo'
          },
          {
            title:'家庭人口数',
            dataIndex: 'populace'
          },
          {
            title:'申请日期',
            dataIndex: 'createTime'
          },
          {
            title:'办理时限',
            dataIndex: 'limitTime'
          },
          {
            title:'办理状态',
            dataIndex: 'status'
          },
          {
            title:'救助金',
            dataIndex: 'salvageMoney'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
        url: {
          list: "/help/apply/rootList",
          childList: "/help/apply/childList",
          delete: "/help/apply/delete",
          deleteBatch: "/help/apply/deleteBatch",
          exportXlsUrl: "/help/apply/exportXls",
          importExcelUrl: "/help/apply/importExcel",
        },
        expandedRowKeys:[],
        hasChildrenField:"hasChild",
        pidField:"pid",
        dictOptions:{
        } 
      }
    },
    computed: {
      tableProps() {
        let _this = this
        return {
          // 列表项是否可选择
          rowSelection: {
            selectedRowKeys: _this.selectedRowKeys,
            onChange: (selectedRowKeys) => _this.selectedRowKeys = selectedRowKeys
          }
        }
      }
    },
    methods: {
      loadData(arg) {
        if(arg === 1) {
          this.ipagination.current = 1
        }
        this.loading = true
        let params = this.getQueryParams()
        return new Promise((resolve) => {
          getAction(this.url.list, params).then(res=>{
            if(res.success) {
              let result = res.result
              if(Number(result.total) > 0) {
                this.ipagination.total = Number(result.total)
                this.dataSource = this.getDataByResult(res.result.records)
                resolve()
              }else {
                this.ipagination.total=0
                this.dataSource=[]
              }
            }else {
              this.$message.warning(res.message)
            }
            this.loading = false
          })
        })
      },
      getDataByResult(result) {
        return result.map(item=>{
          //判断是否标记了带有子节点
          if(item[this.hasChildrenField]=='1'){
            let loadChild = { id: item.id+'_loadChild', name: 'loading...', isLoading: true }
            item.children = [loadChild]
          }
          return item
        })
      },
    }
  }
</script>

<style scoped>

</style>