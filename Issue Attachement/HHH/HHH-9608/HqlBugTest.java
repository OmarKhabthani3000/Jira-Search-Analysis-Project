/*
 * Copyright ©2015 Iacm.megadatatech.com All Rights Reserved
 *
 * 類名：com.megadatatech.iacm.HqlBugTest.class
 * 
 * 當前版本： 1.0
 *
 * 創建時間：2015年2月10日 下午6:25:11  
 * 
 * 修訂歷史：
 *
 *    日期　　　　　　　　修訂人　　　　　　　　版本　　　　　　　　描述
 * ------------------------------------------------------------------
 * 2015年2月10日　　　　Wii　　　　　　　　1.0　　　　　　　初始版本
 */
package com.megadatatech.iacm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.megadatatech.iacm.lfb.app.domain.PriceCatalogue;
import com.megadatatech.iacm.lfb.init.config.app.AppConfig;

/**
 * 基本描述：HqlBugTest
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class})
public class HqlBugTest {

	@Autowired
	private EntityManager entityManager;

	@Test
	public void bugTest() {
		String hql = "select priceCatalogue from PriceCatalogue priceCatalogue where priceCatalogue.status = ?1 and locate(?2,concat(concat(?3,priceCatalogue.businessTypes),?4))-1 >= ?5";
//		String hql = "select priceCatalogue from PriceCatalogue priceCatalogue where priceCatalogue.status = :status and locate(:input,concat(concat(:prefix,priceCatalogue.businessTypes),:subfix))-1 >= :compare";
		TypedQuery<PriceCatalogue> query = entityManager.createQuery(hql, PriceCatalogue.class);
		query.setParameter(1, Boolean.TRUE);
		query.setParameter(2, ",B401,");
		query.setParameter(3, ",");
		query.setParameter(4, ",");
		query.setParameter(5, 0);
//		query.setParameter("status", Boolean.TRUE);
//		query.setParameter("input", ",B401,");
//		query.setParameter("prefix", ",");
//		query.setParameter("subfix", ",");
//		query.setParameter("compare", 0);
		List<PriceCatalogue> result = query.getResultList();
		for(PriceCatalogue p : result){
			System.out.println(p.getBusinessTypes());
		}
	}
}
