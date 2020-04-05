package org.jeecg.modules.car.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.car.entity.Car;
import org.jeecg.modules.car.service.ICarService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;

 /**
 * @Description: 车型管理功能数据表
 * @Author: jeecg-boot & tanquan
 * @Date:   2020-04-03
 * @Version: V1.0
 */
@RestController
@RequestMapping("/carManage/car")
@Slf4j
public class CarController extends JeecgController<Car, ICarService>{
	@Autowired
	private ICarService carService;
	
	/**
	 * 分页列表查询
	 *
	 * @param car
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/rootList")
	public Result<?> queryPageList(Car car,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest request) {
		String parentId = car.getPid();
		if (oConvertUtils.isEmpty(parentId)) {
			parentId = "0";
		}
		car.setPid(null);
		QueryWrapper<Car> queryWrapper = QueryGenerator.initQueryWrapper(car, request.getParameterMap());
		// 下面这行代码等同于 slect * from test_demo_car
		// QueryWrapper<Car> wrapper = new QueryWrapper<>();
		// 设置查询条件为：where pid = 0
		queryWrapper.eq("pid", parentId);
		// and create_time >= '2020-04-02'
		// wrapper.ge("create_time", "2020-04-02");
		// 创建分页对象
		Page<Car> page = new Page<>(pageNo, pageSize);
		// 调用service层的分页方法，拿到分页数据
		IPage<Car> pageList = carService.page(page, queryWrapper);
		// 通过分页对象拿到车的数据
		List<Car> records = pageList.getRecords();
		// 定义图片路径前缀变量：需要改进优化，在配置文件中配置
		String prefix = "http://localhost:8080/jeecg-boot/upFiles/";
		// 循环遍历拿到的车数据，分别给logo图、type图字段加上前缀路径
		for (Car record : records) {
			String logoImg = record.getLogoImg();
			record.setLogoImg(prefix + logoImg);
			String typeImg = record.getTypeImg();
			record.setTypeImg(prefix + typeImg);
		}
		// 重新将处理完的数据设置到分页对象中
		pageList.setRecords(records);
		// 返回分页对象
		return Result.ok(pageList);
	}

	 /**
      * 获取子数据
      * @param car
      * @param req
      * @return
      */
	@GetMapping(value = "/childList")
	public Result<?> queryPageList(Car car,HttpServletRequest req) {
		QueryWrapper<Car> queryWrapper = QueryGenerator.initQueryWrapper(car, req.getParameterMap());
		List<Car> list = carService.list(queryWrapper);
		return Result.ok(list);
	}

	 /**
	  * 通过名称查询
	  * @param name
	  * @return
	  */
	 @AutoLog(value = "自己写的方法-通过name查询")
	 @GetMapping(value = "/queryByName")
	 public Result<?> queryByName(@RequestParam(name = "name", required = true) String name) {
		 Car car = carService.queryByName(name);
		 if (car == null) {
			 return Result.error("未找到对应数据");
		 }
		 return Result.ok(car);
	 }

	 /**
	  * 通过类型查询
	  * @param type
	  * @return
	  */
	 @AutoLog(value = "自己写的方法-通过type查询")
	 @GetMapping(value = "/queryByType")
	 public Result<?> queryByType(@RequestParam(name = "type", required = true) String type) {
		 Car car = carService.queryByType(type);
		 if (car == null) {
			 return Result.error("未找到对应数据");
		 }
		 return Result.ok(car);
	 }

	/**
	 *   添加
	 *
	 * @param car
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody Car car) {
		carService.addCar(car);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param car
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody Car car) {
		carService.updateCar(car);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		carService.deleteCar(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.carService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		Car car = carService.getById(id);
		if(car==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(car);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param car
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Car car) {
		return super.exportXls(request, car, Car.class, "车型管理功能数据表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		return super.importExcel(request, response, Car.class);
    }

}
