package org.jeecg.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.system.entity.SysCategory;
import org.jeecg.modules.system.entity.SysCategory;
import org.jeecg.modules.system.model.TreeSelectModel;


import java.util.List;
import java.util.Map;

/**
 * @Description: 分类字典
 * @Author: tanquan
 * @Date:   2020年7月14日
 * @Version: V1.0
 */
 public interface ISysCategoryService extends IService<SysCategory> {

	 /**根节点父ID的值*/
	 String ROOT_PID_VALUE = "0";

    /**
     * 批量导入分类字典是否存在重复，和 isExist() 方法代码逻辑不同
     * @param treeSelectModels
     * @param name
     * @return boolean
     */
    boolean isRepeatBatchAdd(List<TreeSelectModel> treeSelectModels, String name);

    /**
     * 判断分类字典是否已存在
     * @return boolean
     */
    boolean isExist(SysCategory sysCategory);

    /**
     * 返回重复导入的分类字典名称
     * @return String
     */
    String returnName();

    /**
     * 判断当前分类否有子节点
     * @param categoryPid
     * @return boolean
     */
    boolean isHasChild(String categoryPid);

    /**
     * 处理批量导入的分类字典，返回json字符串给前台，用于树形结构预览
     * @param categoryName
     * @return String
     */
    String generateJsonData(String categoryName);

    /**
     * 批量添加分类字典
     * @param sysCategory
     */
    boolean batchAddSysCategory(SysCategory sysCategory) throws Exception;

    /**
     * 添加分类字典
     * @param sysCategory
     */
    void addSysCategory(SysCategory sysCategory);

    /**
     * 批量删除分类节点
     * @param childList
     */
    void batchDeleteSysCategory(List<TreeSelectModel> childList);

    /**
     * 更新分类字典
     * @param sysCategory
     */
    void updateSysCategory(SysCategory sysCategory);

    /**
     * 批量导入分类字典时，修改其父节点 has_child 字段值为1
     * @param categoryPid
     */
    void existChildNode(String categoryPid);

    /**
     * 删除完子节点后，修改其父节点 has_child 字段值为0
     * @param categoryPid
     */
    void notExistChildNode(String categoryPid);

    /**
     * 根据id获取当前节点的所有子节点
     * @param id
     * @return List<TreeSelectModel>
     */
    List<TreeSelectModel> getAllChildNode(String id);

    /**
     * 根据父级编码加载分类字典的数据
     * @param pcode
     * @return List<TreeSelectModel>
     */
    List<TreeSelectModel> queryListByCode(String pcode) throws Exception;

    /**
     * 根据pid查询子节点集合
     * @param pid
     * @return List<TreeSelectModel>
     */
    List<TreeSelectModel> queryListByPid(String pid);

    /**
     * 根据pid查询子节点集合,支持查询条件
     * @param pid
     * @param condition
     * @return List<TreeSelectModel>
     */
    List<TreeSelectModel> queryListByPid(String pid, Map<String, String> condition);

    /**
     * 根据code查询id
     * @param code
     * @return String
     */
    String queryIdByCode(String code);

    /**
     * 获取子节点最大排序值
     * @param treeSelectModels
     * @return Double
     */
    double getMaxSortValue(List<TreeSelectModel> treeSelectModels);
}
