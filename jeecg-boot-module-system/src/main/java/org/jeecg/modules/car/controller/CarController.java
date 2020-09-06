package org.jeecg.modules.car.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.car.entity.Car;
import org.jeecg.modules.car.service.ICarService;
import org.jeecg.modules.car.vo.CarVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
    private String imgPath;

    /**
     * 查询接口：一个接口实现前台所有查询功能
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
           @RequestParam(name = "sorterName", defaultValue = "createTime") String sorterName,
           @RequestParam(name = "sorterRule", defaultValue = "descend") String sorterRule) {
        String id = null;
        Integer type = null;
        String name = null;
        // 从 car 对象获取作为查询条件的属性值：id、类型、名称
        if (!StringUtils.isEmpty(car)) {
            id = car.getId();
            type = car.getType();
            name = car.getName();
        }
        // 创建 mybatis plus 的查询包装器对象
        QueryWrapper<Car> queryWrapper = new QueryWrapper<>();
        // 手动指定查询数据表哪些字段
        QueryWrapper<Car> select = queryWrapper.select("id", "name", "serial_number", "alias", "title", "type", "identification_code",
                "suggest_price", "logo_img", "type_img","is_new", "link", "create_by", "create_time", "update_by", "update_time");
        // 对 id、type、name 做非空判断，并设为查询条件
        if (!StringUtils.isEmpty(id)) {
            queryWrapper.eq("id", id);
        }
        if (!StringUtils.isEmpty(type)) {
            queryWrapper.eq("type", type);
        }
        if (!StringUtils.isEmpty(name)) {
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
        // 创建分页对象
        Page<Car> page = new Page<>(pageNo, pageSize);
        // 查询分页列表
        IPage<Car> pageList = carService.page(page, queryWrapper);
        // 获取分页对象记录
        List<Car> records = pageList.getRecords();
        // 该集合用来存储 CarVO 对象
        List<CarVO> voList = new ArrayList<>();
        for (Car record : records) {
            CarVO newCar = new CarVO();
            // 拷贝 car 所有数据到 newCar
            BeanUtils.copyProperties(record, newCar);
            // 这两个字段只用于做前台展示
            newCar.setGuidePrice(record.getSuggestPrice());
            newCar.setStorageIime(record.getCreateTime());
            // 对图片路径进行拼接，供前台访问
            String logoImg = record.getLogoImg();
            newCar.setPageLogoImg(imgPath + logoImg);
            String typeImg = record.getTypeImg();
            newCar.setPageTypeImg(imgPath + typeImg);
            voList.add(newCar);
        }
        // 新的分页对象，返回的是 CarVO，而非 Car
        Page<CarVO> newPage = new Page<>(pageNo, pageSize);
        newPage.setTotal(pageList.getTotal());
        newPage.setCurrent(pageList.getCurrent());
        newPage.setRecords(voList);
        return Result.ok(newPage);
    }

    /**
     * 添加
     *
     * @param car
     * @param request
     * @return
     */
    @PostMapping(value = "/add")
    // @RequiresRoles("admin")
    // @RequiresPermissions("car:add")
    public Result<?> add(@RequestBody Car car, HttpServletRequest request) {
        // 在做数据插入时，需要将当前操作用户信息填入对应字段
        // 获取当前登录用户名，绑定到 car 对象
        // 1.使用 session 域对象
        // String loginUser = (String) session.getAttribute("loginUser");
        // car.setCreateBy(loginUser);
        // 2.使用现有的工具类 JwtUtil
        userName = JwtUtil.getUserNameByToken(request);
        car.setCreateBy(userName);
        carService.addCar(car);
        // 添加完一条数据重新发起查询列表请求
        // Result<?> pageList = queryPageList(car, 1, 10, "", "");
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
    // @RequiresPermissions("car:edit")
    public Result<?> edit(@RequestBody Car car, HttpServletRequest request) {
        // 编辑、修改同样需要获取当前操作用户名称
        userName = JwtUtil.getUserNameByToken(request);
        car.setUpdateBy(userName);
        carService.updateCar(car);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = "/delete")
    // @RequiresPermissions("car:delete")
    public Result<?> delete(@RequestParam(name = "id") String id) {
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
    public Result<?> deleteBatch(@RequestParam(name = "ids") String ids) {
        this.carService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功！");
    }
}