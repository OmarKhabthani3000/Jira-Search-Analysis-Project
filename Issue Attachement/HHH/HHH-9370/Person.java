/*******************************************************************************
 * Copyright(c) 2005-2012 Huawei Tech. Co., Ltd.
 * All rights reserved.
 * 
 * Author: l00196402
 * Date  : 2014年8月20日
 *******************************************************************************/
package org.hibernate.test.onetoone;

import java.math.BigDecimal;
import java.util.List;

/**
 * TODO 添加类注释
 */
public class Person
{
    private BigDecimal id;
    
    private String name;
    
    private List<Address> address;
    
    public BigDecimal getId()
    {
        return id;
    }
    
    public void setId(BigDecimal id)
    {
        this.id = id;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public List<Address> getAddress()
    {
        return address;
    }
    
    public void setAddress(List<Address> address)
    {
        this.address = address;
    }
    
}
