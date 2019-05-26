package com.example.demo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;

@Data
@TableName("qlt_service")
public class QltService {
    @TableId(type = IdType.AUTO)
    @Field("id")
    private String qlInnerCode;
    @Field("QL_NAME")
    private String qlName;
    @Field("DEPT_NAME")
    private String deptName;
    @Field("BELONGXIAQUCODE")
    private Double belongxiaqucode;
}
