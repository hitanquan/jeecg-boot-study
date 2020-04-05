package org.jeecg.modules.car.entity;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;

/**
 * @Description: 车型管理功能数据表
 * @Author: jeecg-boot
 * @Date:   2020-04-03
 * @Version: V1.0
 */
@Data
@TableName("test_demo_car")
public class Car implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ID_WORKER_STR)
	private String id;
	/**序号*/
	@Excel(name = "序号", width = 15)
	private Integer serialNumber;
	/**车名*/
	@Excel(name = "车名", width = 15)
	private String name;
	/**别名*/
	@Excel(name = "别名", width = 15)
	private String alias;
	/**车类型*/
	@Excel(name = "车类型", width = 15)
	private String type;
	/**识别码*/
	@Excel(name = "识别码", width = 15)
	private String identificationCode;
	/**指导价*/
	@Excel(name = "指导价", width = 15)
	private Integer suggestPrice;
	/**logo图*/
	@Excel(name = "logo图", width = 15)
	private String logoImg;
	/**类型图*/
	@Excel(name = "类型图", width = 15)
	private String typeImg;
	/**链接*/
	@Excel(name = "链接", width = 15)
	private String link;
	/**是否新品，1新品，0非新品*/
	@Excel(name = "是否新品，1新品，0非新品", width = 15)
	private Integer isNew;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
	private String createBy;
	/**创建日期*/
	@Excel(name = "创建日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	/**更新人*/
	@Excel(name = "更新人", width = 15)
	private String updateBy;
	/**更新日期*/
	@Excel(name = "更新日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	/**父级节点*/
	@Excel(name = "父级节点", width = 15)
	private String pid;
	/**是否有子节点*/
	@Excel(name = "是否有子节点", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
	private String hasChild;
}
