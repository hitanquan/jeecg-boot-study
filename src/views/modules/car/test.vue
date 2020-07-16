<template>
  <a-card :bordered="true">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <img height="100px" src="http://localhost:8080/jeecg-boot/temp/logo_chr_1586329380343.png" width="100px">
      <img height="100px" src="http://localhost:8080/jeecg-boot/temp/CHR-PCnew_1586329387038.png" width="100px">
      <span>官方指导价</span>107800元起
    </div>


    <div class="table-page-search-wrapper" style="margin-top: 10px">
      <img height="100px" src="http://localhost:8080/jeecg-boot/temp/logo_chr_1586329380343.png" width="100px">
      <img height="100px" src="http://localhost:8080/jeecg-boot/temp/CHR-PCnew_1586329387038.png" width="100px">
      <span>官方指导价</span>107800元起
    </div>


    <!-- table区域-begin -->
    <div  slot="expandedRowRender"
          slot-scope="record"
          style="margin: 0">

      <a-row>
        <a-col :span="12">

          <span style="color: cornflowerblue; padding-right: 50px">
                  logo图
          </span>
          <img height="100px" src="http://localhost:8080/jeecg-boot/temp/logo_chr_1586329380343.png" width="100px"><br>

          <span style="color: cornflowerblue; padding-right: 50px">
                  类型图
          </span>
          <img height="100px" src="http://localhost:8080/jeecg-boot/temp/CHR-PCnew_1586329387038.png" width="100px"><br>

          <span style="color: cornflowerblue; padding-right: 50px">
                  最近修改信息
                </span>{{record.updateBy}} 于 {{record.updateTime}}<br>
        </a-col>
      </a-row>

    </div>

    <car-modal @ok="modalFormOk" ref="modalForm"></car-modal>
  </a-card>




</template>


<script>
/*  import ARow from "ant-design-vue/es/grid/Row";
  import ACol from "ant-design-vue/es/grid/Col";*/

</script>


<!--<script>-->
  //import STable from '@/components/table/';
  // {getAction} from '@/api/manage';
  import {JeecgListMixin} from '@/mixins/JeecgListMixin';
  import CarModal from './modules/CarModal';
  // import ImagPreview from "@/views/jeecg/ImagPreview";
  // import ImgDragSort from "@/views/jeecg/ImgDragSort";
  import ImgTurnPage from "@/views/jeecg/ImgTurnPage";
  // import JImageUpload from '@/components/jeecg/JImageUpload';
  import ARow from "ant-design-vue/es/grid/Row";
  import ACol from "ant-design-vue/es/grid/Col";

  // export default {
  //   name: "CarList",
  //   mixins: [JeecgListMixin],
  //   components: {
  //     ImgTurnPage,
  //     // JImageUpload,
  //     // ImgDragSort,
  //     // ImagPreview,
  //     ARow,
  //     ACol,
  //     STable,
  //     CarModal
  //   },
  //   data() {
  //     return {
  //       description: '车型管理功能数据表管理页面',
  //       picUrl:false,
  //       // 表头
  //       columns: [
  //         {
  //           title: '序号',
  //           align: "left",
  //           dataIndex: 'serialNumber'
  //         },
  //         {
  //           title: '名称',
  //           align: "left",
  //           dataIndex: 'name'
  //         },
  //         /*{
  //           title:'别名',
  //           align:"left",
  //           dataIndex: 'alias'
  //         },*/
  //         {
  //           title: '类型',
  //           align: "right",
  //           key: 'type',
  //           dataIndex: 'type',
  //           // 默认排序方式
  //           // defaultSortOrder: 'descend',
  //           // 根据字段长度排序
  //           sorter: true
  //         },
  //         /*{
  //           title:'识别码',
  //           align:"left",
  //           dataIndex: 'identificationCode'
  //         },*/
  //         {
  //           title: '指导价',
  //           align: "right",
  //           key: 'suggestPrice',
  //           dataIndex: 'suggestPrice',
  //           // defaultSortOrder: 'descend',
  //           sorter: true
  //         },
  //         {
  //           title: '入库时间',
  //           align: "right",
  //           key: 'createTime',
  //           dataIndex: 'createTime',
  //           // defaultSortOrder: 'descend',
  //           sorter: true
  //         },
  //         /*{
  //           title:'logo图',
  //           align:"left",
  //           dataIndex: 'logoImg',
  //           scopedSlots: {customRender: 'imgSlot'}
  //         },
  //         {
  //           title:'类型图',
  //           align:"left",
  //           dataIndex: 'typeImg',
  //           scopedSlots: {customRender: 'imgSlot'}
  //         },*/
  //         /*{
  //           title:'链接',
  //           align:"left",
  //           dataIndex: 'link'
  //         },
  //         {
  //           title:'是否新品',
  //           align:"left",
  //           dataIndex: 'isNew'
  //         },
  //         {
  //           title:'创建人',
  //           align:"left",
  //           dataIndex: 'createBy'
  //         },
  //         {
  //           title:'创建日期',
  //           align:"left",
  //           dataIndex: 'createTime'
  //         },
  //         {
  //           title:'更新人',
  //           align:"left",
  //           dataIndex: 'updateBy'
  //         },
  //         {
  //           title:'更新日期',
  //           align:"left",
  //           dataIndex: 'updateTime'
  //         },*/
  //         {
  //           title: '操作',
  //           // style: 'font-weight:bold',
  //           dataIndex: 'action',
  //           align: "center",
  //           scopedSlots: {customRender: 'action'},
  //         }
  //       ],
  //       // 调用后台对应接口
  //       url: {
  //         list: "/carManage/car/rootList",
  //         childList: "/carManage/car/childList",
  //         delete: "/carManage/car/delete",
  //         deleteBatch: "/carManage/car/deleteBatch",
  //         exportXlsUrl: "/carManage/car/exportXls",
  //         importExcelUrl: "carManage/car/importExcel",
  //       },
  //       expandedRowKeys: [],
  //       hasChildrenField: "hasChild",
  //       pidField: "Pid",
  //       dictOptions: {}
  //     }
  //   },
  //   computed: {
  //     importExcelUrl() {
  //       return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
  //     },
  //     tableProps() {
  //       // sortDirections: ['descend', 'ascend']
  //       let _this = this
  //       return {
  //         // 列表项是否可选择
  //         rowSelection: {
  //           selectedRowKeys: _this.selectedRowKeys,
  //           onChange: (selectedRowKeys) => _this.selectedRowKeys = selectedRowKeys
  //         }
  //       }
  //     }
  //   },
  //   methods: {
  //
  //
  //     handleTableChange(pagination, filters, sorter, value) {
  //       console.log("pagination", pagination);
  //       console.log("filters", filters);
  //       console.log("sorter", sorter);
  //       // 参数 {currentDataSource},
  //       // console.log("currentDataSource", currentDataSource);
  //       console.log("value:",value)
  //       this.loadData(pagination.current, sorter, value);
  //     },
  //
  //     loadData(args, sorter, value) {
  //       console.log("args：", args);
  //       // if (undefined !== args) {
  //       //   this.ipagination.current = args
  //       // }
  //       if (args === 1) {
  //         this.ipagination.current = args
  //       }
  //       this.loading = true;
  //       this.expandedRowKeys = [];
  //       // 获取查询参数，
  //       let params = this.getQueryParams();
  //       if (null != sorter) {
  //         params.sorterName = sorter.field;
  //         params.sorterRule = sorter.order;
  //       }
  //
  //       return new Promise((resolve) => {
  //         console.log("params:", params);
  //         getAction(this.url.list, params).then(res => {
  //           console.log("res:", res);
  //           if (res.success) {
  //             let result = res.result;
  //             if (Number(result.total) > 0) {
  //               this.ipagination.total = Number(result.total);
  //               this.dataSource = this.getDataByResult(res.result.records);
  //               resolve()
  //             } else {
  //               this.ipagination.total = 0;
  //               this.dataSource = []
  //             }
  //           } else {
  //             this.$message.warning(res.message)
  //           }
  //           this.loading = false
  //         })
  //       })
  //     },
  //     getDataByResult(result) {
  //       return result.map(item => {
  //         //判断是否标记了带有子节点
  //         if (item[this.hasChildrenField] == '1') {
  //           let loadChild = {id: item.id + '_loadChild', name: 'loading...', isLoading: true}
  //           item.children = [loadChild]
  //         }
  //         return item
  //       })
  //     },
  //     handleExpand(expanded, record) {
  //       // 判断是否是展开状态
  //       if (expanded) {
  //         this.expandedRowKeys.push(record.id)
  //         if (record.children.length > 0 && record.children[0].isLoading === true) {
  //           let params = this.getQueryParams();//查询条件
  //           params[this.pidField] = record.id
  //           getAction(this.url.childList, params).then((res) => {
  //             if (res.success) {
  //               if (res.result && res.result.length > 0) {
  //                 record.children = this.getDataByResult(res.result)
  //                 this.dataSource = [...this.dataSource]
  //               } else {
  //                 record.children = ''
  //                 record.hasChildrenField = '0'
  //               }
  //             } else {
  //               this.$message.warning(res.message)
  //             }
  //           })
  //         }
  //       } else {
  //         let keyIndex = this.expandedRowKeys.indexOf(record.id)
  //         if (keyIndex >= 0) {
  //           this.expandedRowKeys.splice(keyIndex, 1);
  //         }
  //       }
  //     },
  //     initDictConfig() {
  //     },
  //     modalFormOk(formData, arr) {
  //       if (!formData.id) {
  //         this.addOk(formData, arr)
  //       } else {
  //         if (!arr) {
  //           this.loadData()
  //         } else {
  //           this.editOk(formData, this.dataSource)
  //           this.dataSource = [...this.dataSource]
  //         }
  //       }
  //     },
  //     editOk(formData, arr) {
  //       if (arr && arr.length > 0) {
  //         for (let i = 0; i < arr.length; i++) {
  //           if (arr[i].id == formData.id) {
  //             arr[i] = formData
  //             break
  //           } else {
  //             this.editOk(formData, arr[i].children)
  //           }
  //         }
  //       }
  //     },
  //     async addOk(formData, arr) {
  //       if (!formData[this.pidField]) {
  //         this.loadData()
  //       } else {
  //         this.expandedRowKeys = []
  //         for (let i of arr) {
  //           await this.expandTreeNode(i)
  //         }
  //       }
  //     },
  //     expandTreeNode(nodeId) {
  //       return new Promise((resolve, reject) => {
  //         this.getFormDataById(nodeId, this.dataSource)
  //         let row = this.parentFormData
  //         this.expandedRowKeys.push(nodeId)
  //         let params = this.getQueryParams();//查询条件
  //         params[this.pidField] = nodeId
  //         getAction(this.url.childList, params).then((res) => {
  //           if (res.success) {
  //             if (res.result && res.result.length > 0) {
  //               row.children = this.getDataByResult(res.result)
  //               this.dataSource = [...this.dataSource]
  //               resolve()
  //             } else {
  //               reject()
  //             }
  //           } else {
  //             reject()
  //           }
  //         })
  //       })
  //     },
  //     getFormDataById(id, arr) {
  //       if (arr && arr.length > 0) {
  //         for (let i = 0; i < arr.length; i++) {
  //           if (arr[i].id == id) {
  //             this.parentFormData = arr[i]
  //           } else {
  //             this.getFormDataById(id, arr[i].children)
  //           }
  //         }
  //       }
  //     },
  //   }
  // }
<!--</script>-->
<style scoped>
  @import '~@assets/less/common.less';
</style>
