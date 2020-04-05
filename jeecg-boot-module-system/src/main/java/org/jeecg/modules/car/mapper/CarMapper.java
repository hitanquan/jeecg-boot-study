package org.jeecg.modules.car.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.car.entity.Car;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 车型管理功能数据表
 * @Author: jeecg-boot
 * @Date:   2020-04-03
 * @Version: V1.0
 */
public interface CarMapper extends BaseMapper<Car> {

	/**
	 * 编辑节点状态
	 * @param id
	 * @param status
	 */
	void updateTreeNodeStatus(@Param("id") String id, @Param("status") String status);

	/**
	 * 根据名称查询car
	 *
	 * @param name
	 * @return
	 */
	Car selectByName(String name);

	/**
	 * 通过类型查询Car
	 * @param type
	 * @return
	 */
	Car selectByType(String type);

}
