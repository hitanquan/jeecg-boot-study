package org.jeecg.modules.system.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.ShiroUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.SysCategory;
import org.jeecg.modules.system.model.TreeSelectModel;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.system.service.ISysCategoryService;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 分类字典控制器
 * @Author: tanquan
 * @Date: 2020年7月14日 12点50分
 * @Version: V1.0
 */
@RestController
@RequestMapping("/sys/category")
@Slf4j
public class SysCategoryController {
    @Autowired
    private ISysCategoryService sysCategoryService;

    /**
     * 分页列表查询
     *
     * @param sysCategory 分类对象
     * @param pageNo 页码
     * @param pageSize 每页数据记录数
     * @return Result<IPage<SysCategory>> 分页对象
     */
    @GetMapping(value = "/rootList")
    public Result<IPage<SysCategory>> queryPageList(SysCategory sysCategory,
                                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        if (oConvertUtils.isEmpty(sysCategory.getPid())) {
            sysCategory.setPid("0");
        }
        Result<IPage<SysCategory>> result = new Result<>();
        QueryWrapper<SysCategory> queryWrapper = new QueryWrapper<>();
        // 根据 pid 查询分类字典数据
        queryWrapper.eq("pid", sysCategory.getPid());
        queryWrapper.orderByAsc("sort_no");
        Page<SysCategory> page = new Page<>(pageNo, pageSize);
        IPage<SysCategory> pageList = sysCategoryService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 查询子节点数据
     *
     * @param sysCategory 分类对象
     * @return Result<List<SysCategory>> 子节点结果集
     */
    @GetMapping(value = "/childList")
    public Result<List<SysCategory>> queryPageList(SysCategory sysCategory) {
        Result<List<SysCategory>> result = new Result<>();
        QueryWrapper<SysCategory> queryWrapper = new QueryWrapper<>();
        if (oConvertUtils.isNotEmpty(sysCategory.getPid())) {
            queryWrapper.eq("pid", sysCategory.getPid());
            queryWrapper.orderByAsc("sort_no");
        }
        List<SysCategory> list = sysCategoryService.list(queryWrapper);
        result.setSuccess(true);
        result.setResult(list);
        return result;
    }

    /**
     * 分类字典 json 字符串前端回显
     *
     * @param categoryName 分类名称
     * @return Result<String> json 字符串
     */
    @GetMapping(value = "/json")
    public Result<String> json(@RequestParam(name = "categoryName", required = true) String categoryName) {
        Result<String> result = new Result<>();
        try {
            String json = sysCategoryService.generateJsonData(categoryName);
            result.success("添加成功！");
            result.setResult(json);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败！");
        }
        return result;
    }

    /**
     * 批量导入分类字典
     *
     * @param sysCategory 分类对象
     * @return Result<String> 导入提示信息
     */
    @PostMapping(value = "/batchAdd")
    public Result<String> batchAdd(@RequestBody SysCategory sysCategory) {
        Result<String> result = new Result<>();
        if (oConvertUtils.isNotEmpty(sysCategory)) {
            try {
                boolean flag = sysCategoryService.batchAddSysCategory(sysCategory);
                if (flag) {
                    result.success("导入成功!");
                } else {
                    String name = sysCategoryService.returnName();
                    result.error500("分类名称‘" + name + "’已存在，请不要重复导入!");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                result.error500("导入失败!");
            }
        } else {
            result.error500("未找到对应实体!");
        }
        return result;
    }

    /**
     * 添加分类字典
     *
     * @param sysCategory 分类对象
     * @return Result<String> 添加提示信息
     */
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody SysCategory sysCategory) {
        Result<String> result = new Result<>();
        if (oConvertUtils.isNotEmpty(sysCategory)) {
            try {
                boolean flag = sysCategoryService.isExist(sysCategory);
                if (flag) {
                    result.error500("当前分类名称已存在！");
                    result.setSuccess(false);
                } else {
                    sysCategoryService.addSysCategory(sysCategory);
                    result.success("添加成功！");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                result.error500("添加失败！");
            }
        } else {
            result.error500("未找到对应实体!");
        }
        return result;
    }

    /**
     * 编辑分类字典
     *
     * @param sysCategory 分类对象
     * @return Result<String> 编辑提示信息
     */
    @PutMapping(value = "/edit")
    public Result<String> edit(@RequestBody SysCategory sysCategory) {
        Result<String> result = new Result<>();
        SysCategory sysCategoryEntity = sysCategoryService.getById(sysCategory.getId());
        if (sysCategoryEntity == null) {
            result.error500("未找到对应实体!");
        }
        boolean flag = sysCategoryService.isExist(sysCategory);
        // 当分类名称、排序值都没更改才提示当前分类已存在，否则允许用户编辑分类字典
        if (flag == true && sysCategoryEntity.getSortNo().equals(sysCategory.getSortNo())) {
            result.error500("当前分类名称已存在！");
        } else {
            sysCategoryService.updateSysCategory(sysCategory);
            result.success("修改成功!");
        }
        return result;
    }

    /**
     * 删除分类字典
     *
     * @param id 分类id
     * @return Result<String> 删除提示信息
     */
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        Result<String> result = new Result<>();
        SysCategory sysCategory = sysCategoryService.getById(id);
        if (oConvertUtils.isEmpty(sysCategory)) {
            result.error500("未找到对应实体!");
        }
        // 判断当前分类是否有子节点
        boolean flag = sysCategoryService.isHasChild(id);
        // 若没有直接执行删除代码
        if (!flag) {
            boolean ok = sysCategoryService.removeById(id);
            if (ok) {
                result.success("删除成功!");
            }
        // 否则，需要拿到所有分类字典的id，批量删除
        } else {
            // 先删除所有子节点
            List<TreeSelectModel> childList = sysCategoryService.getAllChildNode(id);
            sysCategoryService.batchDeleteSysCategory(childList);
            // 子节点删完后，删除当前节点
            sysCategoryService.removeById(id);
            result.success("删除成功！");
        }
        // 删除当前节点后需要根据其 pid 查询父节点是否还存在子节点
        // 不存在则将父节点 has_child 字段值改为0
        String pid = sysCategory.getPid();
        sysCategoryService.notExistChildNode(pid);
        return result;
    }

    /**
     * 获取最大排序值
     * 用于添加下级节点时拿到所有分类子节点中的最大排序值，回显在表单排序控件，减少用户手动调整排序值操作
     * 
     * @param id 分类id
     * @return Result<Double> 分类节点最大排序值
     */
    @GetMapping(value = "/getMaxSortValue")
    public Result<Double> getMaxSortValue(@RequestParam(name = "id", required = true) String id) {
        Result<Double> result = new Result<>();
        if (oConvertUtils.isNotEmpty(id)) {
            boolean flag = sysCategoryService.isHasChild(id);
            if (flag) {
                List<TreeSelectModel> childNode = sysCategoryService.getAllChildNode(id);
                double maxSortValue = sysCategoryService.getMaxSortValue(childNode);
                result.setResult(maxSortValue);
            }
        }
        return result;
    }

    /**
     * 判断当前节点下是否存在子节点
     *
     * @param id 分类id
     * @return Result<Boolean> 是否存在子节点
     */
    @GetMapping(value = "/isHasChild")
    public Result<Boolean> isHasChild(@RequestParam(name = "id", required = true) String id) {
        Result<Boolean> result = new Result<>();
        if (oConvertUtils.isNotEmpty(id)) {
            boolean flag = sysCategoryService.isHasChild(id);
            if (flag) {
                result.setResult(true);
            } else {
                result.setResult(false);
            }
        }
        return result;
    }

    /**
     * 批量删除分类字典
     *
     * @param ids 分类id
     * @return Result<String> 删除提示信息
     */
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<String> result = new Result<>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.sysCategoryService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 通过 id 查询分类字典
     *
     * @param id 分类id
     * @return Result<SysCategory> 分类对象
     */
    @GetMapping(value = "/queryById")
    public Result<SysCategory> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<SysCategory> result = new Result<>();
        SysCategory sysCategory = sysCategoryService.getById(id);
        if (sysCategory == null) {
            result.error500("未找到对应实体！");
        } else {
            result.setResult(sysCategory);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 导出excel
     * @param request http请求对象
     * @param sysCategory 分类对象
     * @return ModelAndView
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SysCategory sysCategory) {
        // Step.1 组装查询条件查询数据
        QueryWrapper<SysCategory> queryWrapper = QueryGenerator.initQueryWrapper(sysCategory,
                request.getParameterMap());
        List<SysCategory> pageList = sysCategoryService.list(queryWrapper);
        // Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        // 过滤选中数据
        String selections = request.getParameter("selections");
        if (oConvertUtils.isEmpty(selections)) {
            mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        } else {
            List<String> selectionList = Arrays.asList(selections.split(","));
            List<SysCategory> exportList = pageList.stream().filter(item -> selectionList.contains(item.getId()))
                    .collect(Collectors.toList());
            mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        }
        // 导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "分类字典列表");
        mv.addObject(NormalExcelConstants.CLASS, SysCategory.class);
        LoginUser user = ShiroUtils.getUser();
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("分类字典列表数据", "导出人:" + user.getRealname(), "导出信息"));
        return mv;
    }

    /**
     * 通过excel导入数据
     *
     * @param request http请求对象
     * @param response http响应对象
     * @return Result<?>
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<SysCategory> listSysCategorys = ExcelImportUtil.importExcel(file.getInputStream(),
                        SysCategory.class, params);
                for (SysCategory sysCategoryExcel : listSysCategorys) {
                    sysCategoryService.save(sysCategoryExcel);
                }
                return Result.ok("文件导入成功！数据行数：" + listSysCategorys.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败：" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.error("文件导入失败！");
    }

    /**
     * 加载单个数据 用于回显
     *
     * @param field
     * @param val
     * @return Result<SysCategory>
     */
    @RequestMapping(value = "/loadOne", method = RequestMethod.GET)
    public Result<SysCategory> loadOne(@RequestParam(name = "field") String field,
            @RequestParam(name = "val") String val) {
        Result<SysCategory> result = new Result<>();
        try {
            QueryWrapper<SysCategory> query = new QueryWrapper<>();
            query.eq(field, val);
            List<SysCategory> ls = this.sysCategoryService.list(query);
            if (ls == null || ls.size() == 0) {
                result.setMessage("查询无果");
                result.setSuccess(false);
            } else if (ls.size() > 1) {
                result.setMessage("查询数据异常,[" + field + "]存在多个值:" + val);
                result.setSuccess(false);
            } else {
                result.setSuccess(true);
                result.setResult(ls.get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage(e.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 加载节点的子数据
     *
     * @param pid
     * @return Result<List < TreeSelectModel>>
     */
    @RequestMapping(value = "/loadTreeChildren", method = RequestMethod.GET)
    public Result<List<TreeSelectModel>> loadTreeChildren(@RequestParam(name = "pid") String pid) {
        Result<List<TreeSelectModel>> result = new Result<>();
        try {
            List<TreeSelectModel> ls = this.sysCategoryService.queryListByPid(pid);
            result.setResult(ls);
            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage(e.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 加载一级节点/如果是同步 则所有数据
     *
     * @param async
     * @param pcode
     * @return Result<List < TreeSelectModel>>
     */
    @RequestMapping(value = "/loadTreeRoot", method = RequestMethod.GET)
    public Result<List<TreeSelectModel>> loadTreeRoot(@RequestParam(name = "async") Boolean async,
            @RequestParam(name = "pcode") String pcode) {
        Result<List<TreeSelectModel>> result = new Result<>();
        try {
            List<TreeSelectModel> ls = this.sysCategoryService.queryListByCode(pcode);
            if (!async) {
                loadAllCategoryChildren(ls);
            }
            result.setResult(ls);
            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage(e.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 递归求子节点 同步加载用到
     *
     * @param ls
     */
    private void loadAllCategoryChildren(List<TreeSelectModel> ls) {
        for (TreeSelectModel tsm : ls) {
            List<TreeSelectModel> temp = this.sysCategoryService.queryListByPid(tsm.getKey());
            if (temp != null && temp.size() > 0) {
                tsm.setChildren(temp);
                loadAllCategoryChildren(temp);
            }
        }
    }

    /**
     * 校验编码
     *
     * @param pid
     * @param code
     * @return Result<?>
     */
    @GetMapping(value = "/checkCode")
    public Result<?> checkCode(@RequestParam(name = "pid", required = false) String pid,
            @RequestParam(name = "code", required = false) String code) {
        if (oConvertUtils.isEmpty(code)) {
            return Result.error("错误,类型编码为空!");
        }
        if (oConvertUtils.isEmpty(pid)) {
            return Result.ok();
        }
        SysCategory parent = this.sysCategoryService.getById(pid);
        if (code.startsWith(parent.getCode())) {
            return Result.ok();
        } else {
            return Result.error("编码不符合规范,须以\"" + parent.getCode() + "\"开头!");
        }

    }

    /**
     * 分类字典树控件 加载节点
     *
     * @param pid
     * @param pcode
     * @param condition
     * @return Result<List < TreeSelectModel>>
     */
    @RequestMapping(value = "/loadTreeData", method = RequestMethod.GET)
    public Result<List<TreeSelectModel>> loadDict(@RequestParam(name = "pid", required = false) String pid,
            @RequestParam(name = "pcode", required = false) String pcode,
            @RequestParam(name = "condition", required = false) String condition) {
        Result<List<TreeSelectModel>> result = new Result<>();
        // pid如果传值了 就忽略pcode的作用
        if (oConvertUtils.isEmpty(pid)) {
            if (oConvertUtils.isEmpty(pcode)) {
                result.setSuccess(false);
                result.setMessage("加载分类字典树参数有误.[null]!");
                return result;
            } else {
                if (ISysCategoryService.ROOT_PID_VALUE.equals(pcode)) {
                    pid = ISysCategoryService.ROOT_PID_VALUE;
                } else {
                    pid = this.sysCategoryService.queryIdByCode(pcode);
                }
                if (oConvertUtils.isEmpty(pid)) {
                    result.setSuccess(false);
                    result.setMessage("加载分类字典树参数有误.[code]!");
                    return result;
                }
            }
        }
        Map<String, String> query = null;
        if (oConvertUtils.isNotEmpty(condition)) {
            query = JSON.parseObject(condition, Map.class);
        }
        List<TreeSelectModel> ls = sysCategoryService.queryListByPid(pid, query);
        result.setSuccess(true);
        result.setResult(ls);
        return result;
    }

    /**
     * 分类字典控件数据回显[表单页面]
     *
     * @param ids
     * @return Result<List < String>>
     */
    @RequestMapping(value = "/loadDictItem", method = RequestMethod.GET)
    public Result<List<String>> loadDictItem(@RequestParam(name = "ids") String ids) {
        Result<List<String>> result = new Result<>();
        LambdaQueryWrapper<SysCategory> query = new LambdaQueryWrapper<SysCategory>().in(SysCategory::getId, ids);
        List<SysCategory> list = this.sysCategoryService.list(query);
        List<String> textList = new ArrayList<>();
        for (String id : ids.split(",")) {
            for (SysCategory c : list) {
                if (id.equals(c.getId())) {
                    textList.add(c.getName());
                    break;
                }
            }
        }
        result.setSuccess(true);
        result.setResult(textList);
        return result;
    }

    /**
     * [列表页面]加载分类字典数据 用于值的替换
     *
     * @param code
     * @return Result<List < DictModel>>
     */
    @RequestMapping(value = "/loadAllData", method = RequestMethod.GET)
    public Result<List<DictModel>> loadAllData(@RequestParam(name = "code", required = true) String code) {
        Result<List<DictModel>> result = new Result<>();
        LambdaQueryWrapper<SysCategory> query = new LambdaQueryWrapper<>();
        if (oConvertUtils.isNotEmpty(code) && !"0".equals(code)) {
            query.likeRight(SysCategory::getCode, code);
        }
        List<SysCategory> list = this.sysCategoryService.list(query);
        if (list == null || list.size() == 0) {
            result.setMessage("无数据,参数有误.[code]");
            result.setSuccess(false);
            return result;
        }
        List<DictModel> rdList = new ArrayList<DictModel>();
        for (SysCategory c : list) {
            rdList.add(new DictModel(c.getId(), c.getName()));
        }
        result.setSuccess(true);
        result.setResult(rdList);
        return result;
    }
}
