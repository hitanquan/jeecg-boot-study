package org.jeecg.modules.help.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 低保申请实体类
 * @Author: tanquan
 * @Date:   2020年7月15日 15点41分
 * @Version: V1.0
 */
@Data
@TableName("test_demo_help")
public class Apply implements Serializable {
    private static final long serialVersionUID = 1L;
    /**主键*/
    @TableId(type = IdType.ID_WORKER_STR)
    private String id;
    /**街道*/
    private String street;
    /**社区*/
    private String community;
    /**姓名*/
    private String name;
    /**身份证号码*/
    private String idCardNo;
    /**家庭人口数*/
    private int populace;
    /**办理时限*/
    private int limitTime;
    /**办理状态*/
    private int status;
    /**救助金*/
    private double salvageMoney;
    /**创建人*/
    @Excel(name = "创建人", width = 15)
    private String createBy;
    /**创建日期*/
    @Excel(name = "创建日期", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**更新人*/
    @Excel(name = "更新人", width = 15)
    private String updateBy;
    /**更新日期*/
    @Excel(name = "更新日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
