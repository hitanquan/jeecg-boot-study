package org.jeecg.modules.system.controller;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * JeecgBoot开发入门demo
 * @author tanquan
 * @date 2020年3月27日
 */
@RestController
@RequestMapping("/test/jeecgDemo")
@Slf4j
public class MyJeecgDemoController {

    /**
     * hello world
     * @return
     */
    @GetMapping("/hello")
    public Result<String> hello() {
        Result<String> result = new Result<String>();
        result.setResult( "Hello,World!" );
        result.setSuccess( true );
        return result;
    }
}
