package org.jeecg.modules.car.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.car.entity.Car;
import org.jeecg.modules.car.mapper.CarMapper;
import org.jeecg.modules.car.service.ICarService;
import org.jeecg.modules.car.vo.CarVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: 车型管理功能数据表
 * @Author: jeecg-boot
 * @Date: 2020-04-03
 * @Version: V1.0
 */
@Service
public class CarServiceImpl extends ServiceImpl<CarMapper, Car> implements ICarService {

    @Autowired
    private CarMapper carMapper;

    @Override
    public void addCar(Car car) {
        carMapper.insert(car);
    }

    @Override
    public void updateCar(Car car) {
        carMapper.updateById(car);
    }

    @Override
    public void deleteCar(String id) throws JeecgBootException {
        Car car = this.getById(id);
        if (car == null) {
            throw new JeecgBootException("未找到对应实体");
        }
        carMapper.deleteById(id);
    }

    @Override
    public void updateCar2(CarVO car) {
        Car carEntity = getById(car.getId());
        carEntity.setName(car.getName());
        carEntity.setAlias(car.getAlias());
        carEntity.setIdentificationCode(car.getIdentificationCode());
        carEntity.setSuggestPrice(Integer.parseInt(car.getSuggestPrice()));
        carEntity.setType(car.getType());
        carEntity.setLogoImg(car.getLogoImg());
        carEntity.setTypeImg(car.getTypeImg());
        carEntity.setLink(car.getLink());
        carEntity.setIsNew(car.getIsNew());
        boolean success = updateById(carEntity);
        // 方式2
        // UpdateWrapper<Car> updateWrapper = new UpdateWrapper<>();
        // updateWrapper
        //        .set().set().set()
        //        .eq().eq();
        //boolean update = update(updateWrapper);

    }

}
