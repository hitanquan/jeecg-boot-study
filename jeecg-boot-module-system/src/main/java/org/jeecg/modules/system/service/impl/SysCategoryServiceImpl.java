package org.jeecg.modules.system.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jeecg.common.constant.FillRuleConstant;
import org.jeecg.common.util.FillRuleUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.SysCategory;
import org.jeecg.modules.system.mapper.SysCategoryMapper;
import org.jeecg.modules.system.model.TreeSelectModel;
import org.jeecg.modules.system.service.ISysCategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.system.vo.SysCategoryVO;

/**
 * @Description: 分类字典
 * @Author: tanquan
 * @Date: 2020年7月14日
 * @Version: V1.0
 */
@Service
public class SysCategoryServiceImpl extends ServiceImpl<SysCategoryMapper, SysCategory> implements ISysCategoryService {
    private String tempName;

    /**
     * 分类字典 json 字符串生成，用于前台树形结构预览
     *
     * @param categoryName
     * @return String
     */
    @Override
    public String generateJsonData(String categoryName) {
        List<SysCategoryVO> categoryList = split(categoryName);
        List<SysCategoryVO> jsonObjStructure = convertJsonObjStructure(categoryList);
        String json = JSON.toJSONString(jsonObjStructure);
        return json;
    }

    /**
     * 判断分类字典是否已存在，用于新增一级分类节点、导入下级节点以及编辑功能 新增一级分类节点，pid 为 0，查询判断 pid 为 0
     * 的一级节点是否已存在当前添加节点 新增子节点分类，pid 为前台传来的当前添加节点父级节点 pid，查询该节点下是否已存在当前添加节点
     * 编辑分类字典，pid 为前台传来的当前编辑节点父级节点 pid，查询该节点下是否已存在当前编辑节点
     *
     * @param sysCategory
     * @return boolean
     */
    @Override
    public boolean isExist(SysCategory sysCategory) {
        String categoryPid = ISysCategoryService.ROOT_PID_VALUE;
        String pid = sysCategory.getPid();
        List<TreeSelectModel> list;
        // 若 pid 不为空，说明是添加子级分类字典或修改分类字典
        if (oConvertUtils.isNotEmpty(pid)) {
            list = baseMapper.queryListByPid(pid, null);
            // 否则，说明是添加一级节点
        } else {
            list = baseMapper.queryListByPid(categoryPid, null);
        }
        // 循环判断是否重复添加
        for (TreeSelectModel tree : list) {
            String title = tree.getTitle();
            if (title.equals(sysCategory.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断批量导入分类字典是否存在重复，存在返回true，不存在返回false
     *
     * @param treeSelectModels
     * @param name
     * @return boolean
     */
    @Override
    public boolean isRepeatBatchAdd(List<TreeSelectModel> treeSelectModels, String name) {
        for (TreeSelectModel tree : treeSelectModels) {
            String title = tree.getTitle();
            // 若存在名称相等的，说明存在，返回true
            if (title.equals(name)) {
                tempName = title;
                return true;
            }
        }
        return false;
    }

    /**
     * 此方法返回重复导入的字典名称，用于前台页面信息提示
     *
     * @return String
     */
    @Override
    public String returnName() {
        return tempName;
    }

    /**
     * 批量添加分类字典 成功添加需要保证以下条件：
     * 1.主键 id 不能重复，因此每次添加完一条数据都再 new 一个分类字典对象
     * 2.分类编码不能重复，因此每次添加一条数据都根据其 pid 重新生成分类编码
     * <p>
     * 注： SysCategoryVO 对象用于辅助完成批量导入功能，具体用于存储前台批量导入的分类字典名称、空格数量以及当前所添加节点的父节点数据
     *
     * @param sysCategory
     * @return boolean
     * @throws Exception
     */
    @Override
    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean batchAddSysCategory(SysCategory sysCategory) throws Exception {
        // 当前分类字典 pid
        String pid = "";
        // 根节点 id
        String rootId = "";
        // 当前分类字典编码
        String categoryCode;
        // 当前所添加子节点的父级节点分类字典对象
        SysCategoryVO father;
        // 统计字典入库数量
        int count = 0;
        // 判断是否重复标识变量
        boolean flag = false;

        if (sysCategory != null) {
            // 获取前台传来的根节点 pid
            pid = sysCategory.getPid();
            // 保存一份该 pid，后面会用到
            rootId = pid;
        }
        // 更新当前所导入节点父节点的 has_child 字段值为1
        this.existChildNode(pid);
        // 按空格切割分类字典名称
        List<SysCategoryVO> categoryList = split(sysCategory.getName());
        JSONObject formData = new JSONObject();
        // 遍历分类字典集合，获取分类字典对象，按空格数分层级入库
        for (int i = 0; i < categoryList.size(); i++) {
            // 获取当前分类字典对象
            SysCategoryVO current = categoryList.get(i);
            // 获取当前 VO 分类字典对象的分类字典名称，保存到实际分类对象的 name 属性
            sysCategory.setName(current.getName());
            // 获取当前节点的空格数
            int spaceCount = current.getSpaceCount();
            // 让当前要添加的节点往上找最近的父节点
            for (int j = i - 1; j >= 0; j--) {
                // 获取当前节点的父节点
                father = categoryList.get(j);
                // 若父节点不为空，并且空格数等于当前节点空格数减 2，说明当前节点是其子节点
                if (father != null && (father.getSpaceCount() == spaceCount - 2)) {
                    // 获取父节点 id 作为当前节点 pid
                    pid = father.getId();
                    break;
                }
                // 若空格数为0 ，说明当前节点是一级节点
                if (spaceCount == 0) {
                    // 让其 pid 为根节点 id
                    pid = rootId;
                    break;
                }
            }
            // 查询数据表节点数据
            List<TreeSelectModel> treeSelectModels = baseMapper.queryListByPid(pid, null);
            // 判断是否导入了已存在的分类字典
            flag = isRepeatBatchAdd(treeSelectModels, current.getName());
            if (flag) {
                // 若出现重复导入情况，手动回滚事务，撤销此前插入的数据
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;
            }
            formData.put("pid", pid);
            // 根据 pid 生成分类编码
            categoryCode = (String) FillRuleUtil.executeRule(FillRuleConstant.CATEGORY, formData);
            // set 当前节点的 pid、分类编码
            sysCategory.setPid(pid);
            sysCategory.setCode(categoryCode);
            // 排序字段值默认设置为当前节点所有子节点中排序值最大值的基础上加1
            double maxSortValue = this.getMaxSortValue(treeSelectModels);
            sysCategory.setSortNo(maxSortValue + 1);
            this.existChildNode(pid);
            int insert = baseMapper.insert(sysCategory);
            // 每成功插入一条数据，count加1
            count += insert;
            // 创建新 SysCategoryVO 对象，确保 categoryList 集合能存储所有的分类字典对象
            father = new SysCategoryVO();
            // 拷贝一份当前分类字典对象数据到 father 对象
            BeanUtils.copyProperties(sysCategory, father);
            father.setSpaceCount(current.getSpaceCount());
            // 重新添加 father 对象到集合
            categoryList.set(i, father);
            // 创建新的 SysCategory 对象，确保 id 主键不重复
            sysCategory = new SysCategory();
        }
        // 分类字典数据全部入库，并且没有重复，说明导入成功，返回true
        if (count > 0 && count == categoryList.size() && flag != true) {
            return true;
        } else {
            throw new Exception("分类字典批量导入失败，事务回滚！");
        }
    }

    /**
     * 用于批量导入分类字典时，判断并更新父节点的 hasChild 字段值为1 若当前节点的 pid 不为空并且不为 0，说明当前节点是子节点，
     * 因此需要设置其父节点的 hasChild 字段值为1，代表该父节点存在子节点
     *
     * @param categoryPid
     */
    public void existChildNode(String categoryPid) {
        if (oConvertUtils.isNotEmpty(categoryPid)) {
            if (!ISysCategoryService.ROOT_PID_VALUE.equals(categoryPid)) {
                SysCategory parent = baseMapper.selectById(categoryPid);
                if (parent != null && !"1".equals(parent.getHasChild())) {
                    parent.setHasChild("1");
                    baseMapper.updateById(parent);
                }
            }
        }
    }

    /**
     * 用于删除节点后，判断并更新当前节点父节点 hasChild 值为0 当某节点没有了子节点，即子节点全部被删除了时，需要设置其父节点的 hasChild
     * 字段值为0，代表该父节点不存在子节点
     *
     * @param categoryPid
     */
    @Override
    public void notExistChildNode(String categoryPid) {
        if (oConvertUtils.isNotEmpty(categoryPid)) {
            // 查询当前 pid 下是否存在子节点
            // List<TreeSelectModel> treeSelectModels = this.getAllChildNode(categoryPid);
            boolean flag = this.isHasChild(categoryPid);
            // 若返回结果为false，说明不存在
            if (!flag) {
                // 获取父节点分类字典对象
                SysCategory parent = baseMapper.selectById(categoryPid);
                // 设置其 hasChild 为 0
                if (parent != null && "1".equals(parent.getHasChild())) {
                    parent.setHasChild("0");
                    baseMapper.updateById(parent);
                }
            }
        }
    }

    /**
     * 判断某节点下是否有子节点
     *
     * @param categoryPid
     * @return boolean
     */
    @Override
    public boolean isHasChild(String categoryPid) {
        if (oConvertUtils.isNotEmpty(categoryPid)) {
            // 查询当前 pid 下是否存在子节点
            List<TreeSelectModel> treeSelectModels = this.getAllChildNode(categoryPid);
            if (treeSelectModels.size() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 批量删除分类字典 用于删除某节点，该节点存在一个或多个子节点的时候
     *
     * @param childList
     */
    @Override
    public void batchDeleteSysCategory(List<TreeSelectModel> childList) {
        for (TreeSelectModel tree : childList) {
            String newId = tree.getKey();
            this.removeById(newId);
            // 判断当前节点下是否还有子节点
            boolean flag = this.isHasChild(newId);
            if (flag) {
                // 根据当前分类字典 id 查询其子节点，返回分类字典集合
                childList = this.getAllChildNode(newId);
                // 递归调用
                this.batchDeleteSysCategory(childList);
            } else {
                continue;
            }
        }
    }

    /**
     * 根据id获取当前节点所有子节点
     *
     * @param id
     * @return
     */
    @Override
    public List<TreeSelectModel> getAllChildNode(String id) {
        return baseMapper.queryListByPid(id, null);
    }

    /**
     * 分类字典名称文本切割、去空格处理
     *
     * @param categoryName
     * @return List<SysCategoryVO>
     */
    public List<SysCategoryVO> split(String categoryName) {
        // 按换行符切割成单行字符串放入数组
        String[] split = categoryName.split("\n");
        // 正则匹配规则：出现 0 或多个空格
        Pattern space = Pattern.compile("\\s{0,}");
        List<SysCategoryVO> list = new ArrayList<>();
        // 遍历数组，统计字符串空格数量，去除空格添加到list集合
        int i = 0;
        for (String str : split) {
            Matcher matcher = space.matcher(str);
            if (matcher.find()) {
                int spaceCount = matcher.end() - matcher.start();
                // 分类对象添加到集合之前名称需要去除前面的空格
                list.add(new SysCategoryVO().setTitle(str.trim()).setName(str.trim()).setSpaceCount(spaceCount)
                        .setKey(i + ""));
                i++;
            }
        }
        return list;
    }

    /**
     * 遍历每一个元素，找父节点。 1. 如果当前元素的空格数是 0，说明是它根节点之一（一级节点）; 2.
     * 否则，找最近的父节点，将自己设置成父节点的子节点之一（List<children>）
     *
     * @param list
     * @return List<SysCategoryVO>
     */
    public List<SysCategoryVO> convertJsonObjStructure(List<SysCategoryVO> list) {
        // 该集合存储所有的「一级节点」
        List<SysCategoryVO> resultStack = new ArrayList<>();
        // 生成树形结构
        // 循环遍历集合中的节点
        for (int i = 0; i < list.size(); i++) {
            // 获取当前节点
            SysCategoryVO current = list.get(i);
            // 获取当前节点空格数
            int spaceCount = current.getSpaceCount();
            // 空格数为0说明是一级节点
            if (0 == spaceCount) {
                resultStack.add(current);
                continue;
            }
            // 让子节点往上找最近的父节点
            for (int j = i - 1; j >= 0; j--) {
                SysCategoryVO father = list.get(j);
                if (father.getSpaceCount() == spaceCount - 2) {
                    List<SysCategoryVO> children = father.getChildren();
                    if (null == children)
                        children = new ArrayList<>();
                        children.add(current);
                        father.setChildren(children);
                        break;
                }
            }
        }
        return resultStack;
    }

    /**
     * 添加分类字典
     *
     * @param sysCategory
     */
    @Override
    public void addSysCategory(SysCategory sysCategory) {
        String categoryCode = "";
        String categoryPid = ISysCategoryService.ROOT_PID_VALUE;
        if (oConvertUtils.isNotEmpty(sysCategory.getPid())) {
            categoryPid = sysCategory.getPid();

            // PID 不为0，说明当前添加节点是子节点 需要设置父节点 hasChild 为1
            if (!ISysCategoryService.ROOT_PID_VALUE.equals(categoryPid)) {
                SysCategory parent = baseMapper.selectById(categoryPid);
                if (parent != null && !"1".equals(parent.getHasChild())) {
                    parent.setHasChild("1");
                    baseMapper.updateById(parent);
                }
            }
        }
        // update-begin--Author:baihailong Date:20191209 for：分类字典编码规则生成器做成公用配置
        JSONObject formData = new JSONObject();
        formData.put("pid", categoryPid);
        categoryCode = (String) FillRuleUtil.executeRule(FillRuleConstant.CATEGORY, formData);
        // update-end--Author:baihailong Date:20191209 for：分类字典编码规则生成器做成公用配置
        sysCategory.setCode(categoryCode);
        sysCategory.setPid(categoryPid);
        baseMapper.insert(sysCategory);
    }

    /**
     * 更新分类字典
     *
     * @param sysCategory
     */
    @Override
    public void updateSysCategory(SysCategory sysCategory) {
        if (oConvertUtils.isEmpty(sysCategory.getPid())) {
            sysCategory.setPid(ISysCategoryService.ROOT_PID_VALUE);
        } else {
            // 如果当前节点父 ID 不为空 则设置父节点的 hasChild 字段值为1
            SysCategory parent = baseMapper.selectById(sysCategory.getPid());
            if (parent != null && !"1".equals(parent.getHasChild())) {
                parent.setHasChild("1");
                baseMapper.updateById(parent);
            }
        }
        baseMapper.updateById(sysCategory);
        // 更新当前节点后需要根据其 pid 查询父节点是否还存在子节点
        // 不存在则将父节点 has_child 字段值改为0
        String pid = sysCategory.getPid();
        if (!pid.equals("0")) {
            this.notExistChildNode(pid);
        }
    }

    /**
     * 获取最大排序值，用于添加下级分类字典时，获取子节点的的最大排序值
     *
     * @param treeSelectModels
     * @return double
     */
    @Override
    public double getMaxSortValue(List<TreeSelectModel> treeSelectModels) {
        double maxSortNo = 0;
        for (TreeSelectModel tree : treeSelectModels) {
            String id = tree.getKey();
            SysCategory sysCategory = this.getById(id);
            double sortNo = sysCategory.getSortNo();
            if (sortNo > maxSortNo) {
                maxSortNo = sortNo;
            }
        }
        return maxSortNo;
    }

    @Override
    public List<TreeSelectModel> queryListByCode(String pcode) throws Exception {
        String pid = ROOT_PID_VALUE;
        if (oConvertUtils.isNotEmpty(pcode)) {
            List<SysCategory> list = baseMapper
                    .selectList(new LambdaQueryWrapper<SysCategory>().eq(SysCategory::getCode, pcode));
            if (list == null || list.size() == 0) {
                throw new Exception("该编码【" + pcode + "】不存在，请核实!");
            }
            if (list.size() > 1) {
                throw new Exception("该编码【" + pcode + "】存在多个，请核实!");
            }
            pid = list.get(0).getId();
        }
        return baseMapper.queryListByPid(pid, null);
    }

    @Override
    public List<TreeSelectModel> queryListByPid(String pid) {
        if (oConvertUtils.isEmpty(pid)) {
            pid = ROOT_PID_VALUE;
        }
        return baseMapper.queryListByPid(pid, null);
    }

    @Override
    public List<TreeSelectModel> queryListByPid(String pid, Map<String, String> condition) {
        if (oConvertUtils.isEmpty(pid)) {
            pid = ROOT_PID_VALUE;
        }
        return baseMapper.queryListByPid(pid, condition);
    }

    @Override
    public String queryIdByCode(String code) {
        return baseMapper.queryIdByCode(code);
    }

}
