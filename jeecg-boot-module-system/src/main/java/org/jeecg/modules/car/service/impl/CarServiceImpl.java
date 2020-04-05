package org.jeecg.modules.car.service.impl;

import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.car.entity.Car;
import org.jeecg.modules.car.mapper.CarMapper;
import org.jeecg.modules.car.service.ICarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 车型管理功能数据表
 * @Author: jeecg-boot
 * @Date:   2020-04-03
 * @Version: V1.0
 */
@Service
public class CarServiceImpl extends ServiceImpl<CarMapper, Car> implements ICarService {

	@Autowired
	private CarMapper carMapper;

	@Override
	public void addCar(Car car) {
		if(oConvertUtils.isEmpty(car.getPid())){
			car.setPid(ICarService.ROOT_PID_VALUE);
		}else{
			//如果当前节点父ID不为空 则设置父节点的hasChildren 为1
			Car parent = baseMapper.selectById(car.getPid());
			if(parent!=null && !"1".equals(parent.getHasChild())){
				parent.setHasChild("1");
				baseMapper.updateById(parent);
			}
		}
		baseMapper.insert(car);
	}
	
	@Override
	public void updateCar(Car car) {
		Car entity = this.getById(car.getId());
		if(entity==null) {
			throw new JeecgBootException("未找到对应实体");
		}
		String old_pid = entity.getPid();
		String new_pid = car.getPid();
		if(!old_pid.equals(new_pid)) {
			updateOldParentNode(old_pid);
			if(oConvertUtils.isEmpty(new_pid)){
				car.setPid(ICarService.ROOT_PID_VALUE);
			}
			if(!ICarService.ROOT_PID_VALUE.equals(car.getPid())) {
				baseMapper.updateTreeNodeStatus(car.getPid(), ICarService.HASCHILD);
			}
		}
		baseMapper.updateById(car);
	}
	
	@Override
	public void deleteCar(String id) throws JeecgBootException {
		Car car = this.getById(id);
		if(car==null) {
			throw new JeecgBootException("未找到对应实体");
		}
		updateOldParentNode(car.getPid());
		baseMapper.deleteById(id);
	}
	
	
	
	/**
	 * 根据所传pid查询旧的父级节点的子节点并修改相应状态值
	 * @param pid
	 */
	private void updateOldParentNode(String pid) {
		if(!ICarService.ROOT_PID_VALUE.equals(pid)) {
			Integer count = baseMapper.selectCount(new QueryWrapper<Car>().eq("pid", pid));
			if(count==null || count<=1) {
				baseMapper.updateTreeNodeStatus(pid, ICarService.NOCHILD);
			}
		}
	}

	/**
	 * 根据名称查询Car
	 * @param name
	 * @return
	 */
	@Override
	public Car queryByName(String name) {
		return carMapper.selectByName(name);
	}


	/**
	 * 根据类型查询Car
	 * @param type
	 * @return
	 */
	@Override
	public Car queryByType(String type) {
		return carMapper.selectByType(type);
	}

}
