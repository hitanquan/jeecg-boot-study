package org.jeecg.modules.help.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.help.entity.Apply;
import org.jeecg.modules.help.mapper.ApplyMapper;
import org.jeecg.modules.help.service.IApplyService;
import org.springframework.stereotype.Service;

/**
 * @Description: 低保申请service层接口实现
 * @Author: tanquan
 * @Date:  2020年7月15日 15点54分
 * @Version: V1.0
 */
@Service
public class ApplyServiceImpl extends ServiceImpl<ApplyMapper, Apply> implements IApplyService {
}
