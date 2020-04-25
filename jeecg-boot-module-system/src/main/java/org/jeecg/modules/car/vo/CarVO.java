package org.jeecg.modules.car.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * @Author tanquan
 * @Description 该类是数据表和前端页面数据交互的中间类
 * @Date 2020年4月25日
 */
@Data
public class CarVO implements Serializable {
    private static final long serialVersionUID = 1L;

	private String id;
	private Integer serialNumber;
	private String name;
	private String alias;
	private String title;
	private String  identificationCode;
	@Dict(dicCode = "type")
	private Integer type;
	private BigDecimal suggestPrice;
    private String logoImg;
	private String typeImg;
	private String link;
	private Boolean isNew;
    private String createBy;
	private Date createTime;
    private String updateBy;
	private Date updateTime;
	// 非数据库字段，只用于前台展示
	// logo图
	private String pageLogoImg;
	// 类型图
	private String pageTypeImg;
	// 指导价
	private BigDecimal guidePrice;
	// 入库时间
	private Date storageIime;

}
