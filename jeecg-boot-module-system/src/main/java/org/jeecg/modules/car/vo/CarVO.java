package org.jeecg.modules.car.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * @Author tanquan
 * @Description 该类是数据表和前端页面数据交互的中间类
 */
@Data
public class CarVO implements Serializable {
    private static final long serialVersionUID = 1L;

	private String id;
	private Integer serialNumber;
	private String name;
	private String alias;
	private String title;
	private String type;
	// 数据表中是 int 类型，前端页面需要字符串
	private String identificationCode;
	private String suggestPrice;
	private String logoImg;
	private String typeImg;
	private String link;
	private Integer isNew;
	// 数据表中是 Date 类型， 前端页面需要字符串
	private String createTime;
	private Date updateTime;
}
