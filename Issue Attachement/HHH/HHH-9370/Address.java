/*******************************************************************************
 * Copyright(c) 2005-2012 Huawei Tech. Co., Ltd.
 * All rights reserved.
 * 
 * Author: l00196402
 * Date  : 2014年8月20日
 *******************************************************************************/
package org.hibernate.test.onetoone;

import java.math.BigDecimal;

/**
 * TODO 添加类注释
 */
public class Address
{
    private BigDecimal id;
    
    public BigDecimal getId()
    {
        return id;
    }
    
    public void setId(BigDecimal id)
    {
        this.id = id;
    }
    
    private String name;
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
}
