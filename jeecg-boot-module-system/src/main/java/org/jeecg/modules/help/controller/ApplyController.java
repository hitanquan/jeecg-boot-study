package org.jeecg.modules.help.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.help.entity.Apply;
import org.jeecg.modules.help.service.IApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;


/**
 * @Description: 低保申请控制器
 * @Author: tanquan
 * @Date:   2020年7月15日 15点52分
 * @Version: V1.0
 */
@RestController
@RequestMapping("/help/apply")
@Slf4j
public class ApplyController extends JeecgController<Apply, IApplyService> {
    @Autowired
    private IApplyService applyService;

    @GetMapping(value = "/rootList")
    public Result<?> queryPageList(Apply apply,
                                   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                   HttpServletRequest req) {
        Result<IPage<Apply>> result = new Result<>();
        QueryWrapper<Apply> queryWrapper = new QueryWrapper<>();
        Page<Apply> page = new Page<>(pageNo, pageSize);
        IPage<Apply> pageList = applyService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }
}
