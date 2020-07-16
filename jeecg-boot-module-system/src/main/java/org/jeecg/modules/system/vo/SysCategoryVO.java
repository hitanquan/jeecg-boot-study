package org.jeecg.modules.system.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
public class SysCategoryVO {

    private String id;
    private String pid;
    private String name;
    private String code;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;
    private String sysOrgCode;
    private String hasChild;
    // 以下非数据库表字段用于辅助实现分类字典相关功能
    private String title;
    private String key;
    private int spaceCount;
    private List<SysCategoryVO> children;
}
