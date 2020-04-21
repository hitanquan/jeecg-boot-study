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
    // 以毫秒表示的时间
    // private static final long MINUTE_IN_MILLIS = 60 * 1000;
    // private static final long HOUR_IN_MILLIS = 3600 * 1000;
    // private static final long DAY_IN_MILLIS = 24 * 3600 * 1000;
    // 数字太大，内存溢出
    // private static final long MONTH_IN_MILLIS = 30 * 24 * 3600 * 1000L;


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
    public Result<?> queryPageList(CarVO car,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   @RequestParam(name = "sorterName", defaultValue = "createTime") String sorterName,
                                   @RequestParam(name = "sorterRule", defaultValue = "descend") String sorterRule) {
        // 从 car 对象获取作为查询条件的属性值：id、类型、名称
        String id = car.getId();
        String type = car.getType();
        String name = car.getName();
        // 创建 mybatis plus 的查询包装器对象
        QueryWrapper<Car> queryWrapper = new QueryWrapper<>();
        // 手动指定查询数据表哪些字段
        QueryWrapper<Car> select = queryWrapper.select("id", "name", "serial_number", "alias", "title", "type", "identification_code",
                "suggest_price", "logo_img", "type_img", "link", "create_by", "create_time", "update_by", "update_time");
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
        Page<Car> page = new Page<>(pageNo, pageSize);
        IPage<Car> pageList = carService.page(page, queryWrapper);
        List<Car> records = pageList.getRecords();

        // 对图片路径进行拼接，供前台访问，对指导价格字段进行格式化，对入库时间进行换算
        // 该集合用来存储 CarVO 对象
        List<CarVO> voList = new ArrayList<>();
        for (Car record : records) {
            CarVO newCar = new CarVO();
            // 拷贝 car 所有数据到 newCar，除了数据类型改变的指导价和创建时间
            BeanUtils.copyProperties(record, newCar);

            // 处理图片路径
            String logoImg = record.getLogoImg();
            newCar.setLogoImg(imgPath + logoImg);
            String typeImg = record.getTypeImg();
            newCar.setTypeImg(imgPath + typeImg);

            // 前端需要展示的非数据表字段，用 CarVO 类的相关属性代替
            // 入库时间
            // newCar.setStorageIime(record.getCreateTime());

            // 指导价和创建时间
            Integer suggestPrice = record.getSuggestPrice();
            newCar.setSuggestPrice(suggestPrice.toString());
            Date createTime = record.getCreateTime();
            newCar.setCreateTime(createTime.toString());

            // 指导价格格式化
            String price = newCar.getSuggestPrice();
            if (!StringUtils.isEmpty(price) && price.length() > 3) {
                price = new StringBuffer(price).insert(price.length() - 3, ',').toString();
                newCar.setSuggestPrice(price);
            }

            // 获取当前时间
//            Calendar currentCalendar = DateUtils.getCalendar();
//            Calendar createTimeCalendar = null;
//
//            try {
//                createTimeCalendar = DateUtils.parseCalendar(record.getCreateTime().toString(),"yyyy-MM-dd HH:mm:ss");
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            int d = DateUtils.dateDiff('d', currentCalendar, createTimeCalendar);
//            newCar.setCreateTime(d+"天之前");
//            System.out.println(d);

            // 入库时间换算
            // 获取当前时间，当前时间毫秒数 - 入库时间毫秒数 = 需要显示的时间差的毫秒数


//            long currentTimeMillis = System.currentTimeMillis();
//            long createTimeMillis = DateUtils.getMillis(record.getCreateTime());
//            long l = currentTimeMillis - createTimeMillis;
//            // 对时间毫秒数差进行换算
//            // 换算成分钟
//            long min = l / MINUTE_IN_MILLIS;
//            // 换算成小时
//            long hour = l / HOUR_IN_MILLIS;
//            // 换算成天
//            long day = l / DAY_IN_MILLIS;
//            // 换算成月
//            long month = l / MONTH_IN_MILLIS;
//
//            // 若分钟小于60，则返回分钟数
//            if (min == 0) {
//                newCar.setCreateTime("刚刚");
//            }
//            if (min < 60) {
//                newCar.setCreateTime(min + "分钟之前");
//                // 若小时小于24，则返回小时数
//            } else if (hour < 24) {
//                newCar.setCreateTime(hour + "小时之前");
//                //若天数小于30，则返回天数
//            } else if (day < 30) {
//                newCar.setCreateTime(day + "天之前");
//            }
//            // 若月数小于12，则返回月数
//            // TODO 这里有BUG,需要修改
//            else if (month < 12) {
//                newCar.setCreateTime(month + "月之前");
//            }

            // 入库时间换算处理
            String time = DateUtils.fromToday(record.getCreateTime());
            newCar.setCreateTime(time);

            // 入库记录、修改记录前台显示判断、拼接处理
            if (record.getCreateBy() != "" && record.getCreateTime() != null) {
                newCar.setStorageRecord(record.getCreateBy() + " 于 " + record.getCreateTime());
            } else {
                newCar.setStorageRecord("没有入库记录");
            }

            if (record.getUpdateBy() != "" && record.getUpdateTime() != null) {
                newCar.setUpdateRecord(record.getUpdateBy() + " 于 " + record.getUpdateTime());
            } else {
                newCar.setUpdateRecord("暂无修改记录");
            }
            voList.add(newCar);
        }
        // 新的分页对象，存储所有经过处理的和前台需要的数据
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
    public Result<?> edit(@RequestBody CarVO car, HttpServletRequest request) {
        // 编辑、修改同样需要获取当前操作用户名称
        userName = JwtUtil.getUserNameByToken(request);
        car.setUpdateBy(userName);
        carService.updateCar2(car);
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