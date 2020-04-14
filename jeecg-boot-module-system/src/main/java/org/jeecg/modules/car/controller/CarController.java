package org.jeecg.modules.car.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.modules.car.entity.Car;
import org.jeecg.modules.car.service.ICarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 车型管理功能数据表
 * @Author: jeecg-boot & tanquan
 * @Date: 2020-04-03
 * @Version: V1.0
 */
@RestController
@RequestMapping("/carManage/car")
@Slf4j
public class CarController extends JeecgController<Car, ICarService> {
    @Autowired
    private ICarService carService;
    private String userName;
    // 图片前缀路径
    @Value(value = "${jeecg.path.img}")
    private String imgpath;

    /**
     * 查询接口：实现了前台所有查询条件
     *
     * @param car        车对象
     * @param pageNo     页码
     * @param pageSize   当前页记录数
     * @param sorterName 排序字段
     * @param sorterRule 排序规则：升序、降序
     * @return
     */
    @GetMapping(value = "/rootList")
    public Result<?> queryPageList(Car car,
                           @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                           @RequestParam(name = "sorterName", defaultValue = "") String sorterName,
                           @RequestParam(name = "sorterRule", defaultValue = "") String sorterRule) {

        // 从 car 对象获取作为查询条件的属性值：id、类型、名称
        String id = car.getId();
        String type = car.getType();
        String name = car.getName();
        // 创建 mybatis plus 的查询包装器对象
        QueryWrapper<Car> queryWrapper = new QueryWrapper<>();
        // 手动指定查询数据表哪些字段
        QueryWrapper<Car> select = queryWrapper.select("id", "name", "serial_number", "alias", "type", "identification_code",
                "suggest_price", "logo_img", "type_img", "link", "create_by", "create_time", "update_by", "update_time");
        // 对 id、type、name 做非空判断，并设为查询条件
        if (!StringUtils.isEmpty(id)) {
            queryWrapper.eq("id", id);
        }
        if (!StringUtils.isEmpty(type)) {
            queryWrapper.eq("type", type);
        }
        if (!StringUtils.isEmpty(name)) {
            // queryWrapper.eq("name", name);
            // 名称支持模糊查询
            queryWrapper.like("name", name);
        }
        // 排序逻辑
        if (!StringUtils.isEmpty(sorterName)) {
            // 根据创建时间排序
            if (sorterName.equals("createTime")) {
                if ("ascend".equalsIgnoreCase(sorterRule)) {
                    queryWrapper.orderByAsc("create_time");
                } else if ("descend".equalsIgnoreCase(sorterRule)) {
                    queryWrapper.orderByDesc("create_time");
                }
            }
            // 根据指导价格排序
            if (sorterName.equals("suggestPrice")) {
                if ("ascend".equalsIgnoreCase(sorterRule)) {
                    queryWrapper.orderByAsc("suggest_price");
                } else if ("descend".equalsIgnoreCase(sorterRule)) {
                    queryWrapper.orderByDesc("suggest_price");
                }
            }
        }
        Page<Car> page = new Page<>(pageNo, pageSize);
        IPage<Car> pageList = carService.page(page, queryWrapper);
        List<Car> records = pageList.getRecords();
        // 已改成在配置文件中配置
        // String prefix = "http://localhost:8080/jeecg-boot/";
        // 对图片路径进行拼接，供前台访问
        for (Car record : records) {
            String logoImg = record.getLogoImg();
            record.setLogoImg(imgpath + logoImg);
            String typeImg = record.getTypeImg();
            record.setTypeImg(imgpath + typeImg);
        }
        pageList.setRecords(records);
        return Result.ok(pageList);
    }

    /**
     * 添加
     * @param car
     * @param request
     * @return
     */
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody Car car, HttpServletRequest request) {
        // 在做数据插入时，需要将当前操作用户信息填入
        // 获取当前登录用户名，绑定到car对象
        // 1.使用session域对象
        // String loginUser = (String) session.getAttribute("loginUser");
        // car.setCreateBy(loginUser);
        // 2.使用现有的工具类JwtUtil
        userName = JwtUtil.getUserNameByToken(request);
        car.setCreateBy(userName);
        carService.addCar(car);
        // queryPageList(car, pageNo, pageSize, sorterName, sorterRule);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param car
     * @param request
     * @return
     */
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody Car car, HttpServletRequest request) {
        // 编辑修改同样需要获取当前操作用户名称
        userName = JwtUtil.getUserNameByToken(request);
        car.setUpdateBy(userName);
        carService.updateCar(car);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     * @param id
     * @return
     */
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        carService.deleteCar(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.carService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }
}