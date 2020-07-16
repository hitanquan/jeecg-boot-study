<template>
  <a-card :bordered="false">
    <div>
      <!--采用栅格重新布局，使新建按钮、查询和视图切换控件显示在一行-->
      <a-row>
        <!--新建和批量删除按钮-->
        <a-col :span="12">
          <div class="table-operator">
            <a-button :disabled="isDisabledAuth('car:disable')" @click="handleAdd" icon="plus" style="margin-top: 0px"
                      type="primary">新建
            </a-button>

            <a-dropdown style="margin-top: 0px" v-if="selectedRowKeys.length > 0">
              <a-menu slot="overlay">
                <a-menu-item :disabled="isDisabledAuth('car:disable')" @click="batchDel" key="1">
                  <a-icon type="delete"/>
                  删除
                </a-menu-item>
              </a-menu>
              <a-button>
                批量操作
                <a-icon type="down"/>
              </a-button>
            </a-dropdown>
          </div>
        </a-col>

        <!--查询、视图切换部分-->
        <a-col :span="12">
          <!-- 查询区域 -->
          <div class="table-page-search-wrapper">
            <a-form @keyup.enter.native="searchQuery" layout="inline">
              <a-row :gutter="1">
                <a-col :md="16" :sm="24">
                  <a-form-item>
                    <a-input-group compact>
                      <!--<j-dict-select-tag dict-code="type" style="width:40%" :triggerChange="true" @change="handleTableChange"  v-model="queryParam.type"/>-->
                      <a-select style="width: 40%" :allowClear="true"  placeholder="请选择车型" @change="handleTableChange" v-model="queryParam.type">
                        <a-icon slot="suffixIcon" type="caret-down" style="icon: auto"/>
                        <a-select-option value="1">轿车</a-select-option>
                        <a-select-option value="2">新能源</a-select-option>
                        <a-select-option value="3">SUV/MPV</a-select-option>
                        <a-select-option value="4">油电混合</a-select-option>
                      </a-select>
                      <a-input placeholder="请输入车名称" style="width:60%" v-model="queryParam.name"/>
                    </a-input-group>
                  </a-form-item>
                </a-col>

                <a-col :md="8" :sm="24">
                  <span class="table-page-search-submitButtons">
                    <a-button @click="searchQuery"  icon="search" type="primary">查询</a-button>
                    <!--网格、列表视图切换控件-->
                    <a-button-group>
                      <a-button :type="layoutName === 'grid' ? 'primary' : ''" @click="changeView('grid')" icon="table"
                                style="margin-left: 10px"/>
                      <a-button :type="layoutName === 'list' ? 'primary' : ''" @click="changeView('list')" icon="bars"
                                id="bars"/>
                    </a-button-group>
                  </span>
                </a-col>
              </a-row>
            </a-form>
          </div>
        </a-col>
      </a-row>

    </div>

    <!-- table视图区域-begin -->
    <div v-if="layoutName === 'grid'">
      <a-table
        :columns="columns"
        :dataSource="dataSource"
        :expandedRowKeys="expandedRowKeys"
        :loading="loading"
        :pagination="false"
        @change="handleTableChange"
        @expand="handleExpand"
        ref="table"
        rowKey="id"
        size="middle"
        v-bind="tableProps">

        <div
          slot="expandedRowRender"
          slot-scope="record"
          style="margin: 10px">
          <a-row>
            <a-col :span="12">
                <span style="color: #779ecb; padding-right: 70px">
                  ID
                </span>{{record.id}}<br>
              <span style="color: #779ecb; padding-right: 70px">
                  别名
                </span>{{record.alias}}<br>
              <span style="color: #779ecb; padding-right: 50px">
                  logo图
                </span><img :src="record.pageLogoImg" height="70px" width="100px"><br>
              <span style="color: #779ecb; padding-right: 50px">
                  入库记录
              </span><span
              v-if="record.createBy && record.createTime">{{record.createBy}} 于 {{record.createTime}}</span><span
              v-else>暂无入库记录</span>
            </a-col>

            <a-col :span="12">
                <span style="color: #779ecb; padding-right: 70px">
                  标题
                </span>{{record.title}}<br>
              <span style="color: #779ecb; padding-right: 70px">
                  识别码
                </span>{{record.identificationCode}}<br>
              <span style="color: #779ecb; padding-right: 50px">
                  车型图
                </span><img :src="record.pageTypeImg" height="70px" width="100px"><br>
              <span style="color: #779ecb; padding-right: 50px">
                  最近修改信息
              </span><span
              v-if="record.updateBy && record.updateTime">{{record.updateBy}} 于 {{record.updateTime}}</span><span
              v-else>暂无修改记录</span>
            </a-col>
          </a-row>
        </div>

        <div slot="action" slot-scope="text, record">
          <span :disabled="isDisabledAuth('car:disable')" @click="handleEdit(record)"><a-icon style="padding-right: 5px"
                                                                                              type="edit"/>编辑</span>
          <a-divider type="vertical"/>

          <span @click="showDeleteConfirm(record)" :disabled="isDisabledAuth('car:disable')"><a-icon style="padding-right: 5px" type="close"/>删除</span>

        </div>
      </a-table>
      <a-modal @ok="doDelete" cancelText="取消" okText="确认" title="删除确认" v-model="deleteModalVisible">
        <p>
          <a-icon style="size: legal; padding-right: 5px" type="warning"/>
          此操作将永久删除该车型(<span style="color: red">{{currentRecord.name}}</span>),是否继续?
        </p>
      </a-modal>
    </div>
    <!-- table视图区域-end -->

    <div @change="handleTableChange" v-if="layoutName === 'list'">
      <!--列表视图区域-begin-->
      <a-list :dataSource="dataSource" class="table-page-search-wrapper">
        <a-list-item class="listContent" slot="renderItem" slot-scope="record">
          <a-row :gutter="16 + 8 * 10" style="margin-left: 5px; padding-top: 5px">
            <a-col :span="6">
              <img :src="record.pageLogoImg">
            </a-col>
            <a-col :span="6">
              <img :src="record.pageTypeImg">
            </a-col>
            <a-col :span="6" style="margin-top: 10px">
              官方指导价<br><span>{{record.suggestPrice}}</span><br>元起
            </a-col>
            <a-col :span="6" style="margin-top: 20px">
              <a-row>
                <a-col :span="3">
                  <a-button><a :href="record.link" target="_blank">查看车型</a></a-button>
                </a-col>
                <a-col :span="3" style="margin-left: 80px">
                  <a-button><a :href="record.link" target="_blank">咨询底价</a></a-button>
                </a-col>
              </a-row>
            </a-col>
          </a-row>
        </a-list-item>
      </a-list>
      <!--列表视图区域-end-->
    </div>

    <div style="padding-top: 15px; float: right">
      <a-pagination :defaultCurrent="1" :defaultPageSize="10" :pageSizeOptions="['10', '20', '30', '40']"
                    :showQuickJumper="true"
                    :showSizeChanger="true" :showTotal="total => `共 ${total} 条`"
                    :total="ipagination.total" @change="onPageChange"
                    @showSizeChange="onPageSizeChange"/>
    </div>
    <car-modal @ok="modalFormOk" ref="modalForm"></car-modal>

  </a-card>
</template>

<script>
  import STable from '@/components/table/';
  import {deleteAction, getAction} from '@/api/manage';
  import {JeecgListMixin} from '@/mixins/JeecgListMixin';
  import CarModal from './modules/CarModal';
  import ImagPreview from "@/views/jeecg/ImagPreview";
  // 单列数据权限过滤器
  import {colAuthFilter} from "@/utils/authFilter";
  // 页面控件禁用依赖
  import {DisabledAuthFilterMixin} from '@/mixins/DisabledAuthFilterMixin';
  import ARow from "ant-design-vue/es/grid/Row";
  import ACol from "ant-design-vue/es/grid/Col";


  export default {
    name: "CarList",
    mixins: [JeecgListMixin, DisabledAuthFilterMixin],
    components: {
      ACol,
      ARow,
      ImagPreview,
      STable,
      CarModal,
    },
    data() {
      return {
        description: '车型管理数据操作页面',
        picUrl: false,
        layoutName: 'grid',
        showGrid: true,
        showList: false,
        sorter2: {},
        pageSize: 10,
        size: 10,
        storageTime: '',
        deleteModalVisible: false,
        carDictOptions: [],

        // 表头
        columns: [
          {
            title: '序号',
            align: "left",
            dataIndex: 'serialNumber'
          },
          {
            title: '名称',
            align: "left",
            dataIndex: 'name'
          },
          {
            title: '类型',
            align: "right",
            key: 'type',
            dataIndex: 'type_dictText',
            sorter: true
          },
          {
            title: '指导价',
            align: "right",
            key: 'guidePrice',
            dataIndex: 'guidePrice',
            sorter: true
          },
          {
            title: '入库时间',
            align: "right",
            key: 'createTime',
            dataIndex: 'createTime',
            sorter: true
          },
          {
            title: '操作',
            dataIndex: 'action',
            align: "center",
            scopedSlots: {customRender: 'action'},
          }
        ],
        // 调用后台对应接口
        url: {
          list: "/carManage/car/rootList",
          childList: "/carManage/car/childList",
          delete: "/carManage/car/delete",
          deleteBatch: "/carManage/car/deleteBatch",
        },
        // 点击“+”和“-”控件拓展显示数据
        length: 0,
        expandedRowKeys: [],
        currentRecord: {}
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
      // 指导价格处理
      toThousandsNoZero(num) {
        return num ? ((isNaN(parseFloat(num.toString().replace(/,/g, ""))) ? 1 : parseFloat(num.toString().replace(/,/g, "")))).toFixed(2).toString().replace(/(\d)(?=(\d{3})+\.)/g, function ($0, $1) {
          return $1 + ",";
        }) : "";
      },
      // 入库时间格式化处理
      formatCreateTime(time) {
        let storageTime = new Date(time);
        let nowTime = new Date;
        let dt = nowTime - storageTime;
        let s = dt / 1000;
        let m = s / 60;
        let h = m / 60;
        if (s < 60) {
          return '刚刚';
        } else if (s < 3600) {
          return parseInt(s / 60) + '分钟前';
        } else if (s < 86400) {
          return parseInt(s / 60 / 60) + '小时前';
        } else if (s < 604800) {//在一周内
          return parseInt(s / 60 / 60 / 24) + '天前';
        } else if (s < 2592000) {
          return parseInt(s / 60 / 60 / 24 / 7) + "周前"
        } else if (s < 2592000 && s > 604800) {//超过一周
          return parseInt(s / 60 / 60 / 24) + '天前';
        } else if (s < 31104000) {
          return parseInt(s / 60 / 60 / 24 / 30) + '个月前';
        } else if (s < 311040000) {
          return parseInt(s / 60 / 60 / 24 / 30 / 12) + '年前';
        }
      },
      // 数据条数变化后的回调
      onPageSizeChange(page, pageSize) {
        this.loadData(page, pageSize)
      },
      // 页码变化后的回调
      onPageChange(current, size) {
        console.log("current:", current)
        console.log("size:", size)
        this.loadData(current, size)
      },
      changeView(view) {
        // console.log("视图切换------id：", view)
        this.layoutName = view
      },
      created() {
      },
      // 分页、筛选、排序
      handleTableChange(pagination, filters, sorter) {
        console.log("pagination", pagination);
        console.log("filters", filters);
        console.log("sorter", sorter);
        this.sorter2 = sorter
        this.loadData(1);
      },
      // 加载数据
      // args 是通用参数，传入什么就代表什么
      loadData(args, size) {
        console.log("args：", args);
        console.log("size：", size);
        if (undefined !== args) {
          this.ipagination.current = args
        }
        if (undefined !== size) {
          this.ipagination.pageSize = size
        }
        this.loading = true;
        this.expandedRowKeys = [];
        // 获取查询参数，
        let params = this.getQueryParams();
        // this.loading = true;
        if (null != this.sorter2) {
          params.sorterName = this.sorter2.field;
          params.sorterRule = this.sorter2.order;
        }
        return new Promise((resolve) => {
          console.log("params:", params);
          getAction(this.url.list, params).then(res => {
            console.log("res:", res);
            if (res.success) {
              let result = res.result;
              result.records.forEach(item => {
                // console.log("createTime:", result.records.createTime);
                // 入库时间、指导价格格式化
                item.createTime = this.formatCreateTime(item.createTime);
                item.guidePrice = this.toThousandsNoZero(item.guidePrice);
                this.dataSource.push(item);
              });
              if (Number(result.total) > 0) {
                this.ipagination.total = Number(result.total);
                this.dataSource = this.getDataByResult(res.result.records);
                resolve()
              } else {
                this.ipagination.total = 0;
                this.dataSource = []
              }
            } else {
              this.$message.warning(res.message)
            }
            this.loading = false
          })
        })
      },
      handleAdd: function () {
        this.$refs.modalForm.add();
        this.$refs.modalForm.title = "新建车型";
        this.$refs.modalForm.disableSubmit = false;
      },
      handleEdit: function (record) {
        this.$refs.modalForm.edit(record);
        this.$refs.modalForm.title = "编辑车型";
        this.$refs.modalForm.disableSubmit = false;
      },
      async doDelete() {
        const record = this.currentRecord
        const res = await deleteAction(this.url.delete, {id: record.id})
        if (res.success) {
          this.$message.success(res.message);
          this.loadData();
        } else {
          this.$message.warning(res.message);
        }
        this.deleteModalVisible = false;
      },
      confirm() {
        this.$confirm({
          title: '删除确认',
          content: '此操作将永久删除该车型是否继续?',
          okText: '确认',
          cancelText: '取消',
        });
      },
      showDeleteConfirm(record) {
        this.currentRecord = record
        this.deleteModalVisible = true
      },
      getDataByResult(result) {
        return result.map(item => {
          //判断是否标记了带有子节点
          if (item[this.hasChildrenField] == '1') {
            let loadChild = {id: item.id + '_loadChild', name: 'loading...', isLoading: true}
            item.children = [loadChild]
          }
          return item
        })
      },
      handleExpand(expanded, record) {
        //判断是否是展开状态
        if (expanded) {
          this.expandedRowKeys.push(record.id)
          if (record.children.length > 0 && record.children[0].isLoading === true) {
            //查询条件
            let params = this.getQueryParams();
            params[this.pidField] = record.id
            getAction(this.url.childList, params).then((res) => {
              if (res.success) {
                if (res.result && res.result.length > 0) {
                  record.children = this.getDataByResult(res.result)
                  this.dataSource = [...this.dataSource]
                } else {
                  record.children = ''
                  record.hasChildrenField = '0'
                }
              } else {
                this.$message.warning(res.message)
              }
            })
          }
        } else {
          let keyIndex = this.expandedRowKeys.indexOf(record.id)
          if (keyIndex >= 0) {
            this.expandedRowKeys.splice(keyIndex, 1);
          }
        }
      },
      modalFormOk(formData, arr) {
        if (!formData.id) {
          this.addOk(formData, arr)
        } else {
          if (!arr) {
            this.loadData()
          } else {
            this.editOk(formData, this.dataSource)
            this.dataSource = [...this.dataSource]
          }
        }
      },
      editOk(formData, arr) {
        if (arr && arr.length > 0) {
          for (let i = 0; i < arr.length; i++) {
            if (arr[i].id == formData.id) {
              arr[i] = formData
              break
            } else {
              this.editOk(formData, arr[i].children)
            }
          }
        }
      },
      async addOk(formData, arr) {
        console.log("formData", formData);
        console.log("arr", arr);
        if (!formData[this.pidField]) {
          this.loadData()
        } else {
          this.expandedRowKeys = []
          for (let i of arr) {
            await this.expandTreeNode(i)
          }
        }
      },
      getFormDataById(id, arr) {
        if (arr && arr.length > 0) {
          for (let i = 0; i < arr.length; i++) {
            if (arr[i].id == id) {
              this.parentFormData = arr[i]
            } else {
              this.getFormDataById(id, arr[i].children)
            }
          }
        }
      },
    },
    // TODO 尝试使用过滤器实现将入库时间、指导价格式化
    filters: {
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
  @import "car.css";
</style>