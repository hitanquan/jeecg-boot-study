package org.jeecg.modules.car.service;

import org.jeecg.modules.car.entity.Car;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.car.entity.Car;
import org.jeecg.modules.car.vo.CarVO;

/**
 * @Description: 车型管理功能数据表
 * @Author: jeecg-boot
 * @Date:   2020-04-03
 * @Version: V1.0
 */
public interface ICarService extends IService<Car> {

	/**根节点父ID的值*/
	public static final String ROOT_PID_VALUE = "0";
	
	/**树节点有子节点状态值*/
	public static final String HASCHILD = "1";
	
	/**树节点无子节点状态值*/
	public static final String NOCHILD = "0";

	/**新增节点*/
	void addCar(Car car);
	
	/**修改节点*/
	void updateCar(Car car) throws JeecgBootException;
	
	/**删除节点*/
	void deleteCar(String id) throws JeecgBootException;

	void updateCar2(Car car);

	/**通过名称查询*//*
	Car queryByName(String name);

	*//*通过类别查询*//*
	Car queryByType(String type);*/

}
