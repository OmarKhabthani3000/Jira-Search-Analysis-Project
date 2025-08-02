{\rtf1\ansi\ansicpg1252\cocoartf1504\cocoasubrtf600
{\fonttbl\f0\fnil\fcharset0 Menlo-Bold;\f1\fnil\fcharset0 Menlo-Regular;\f2\fnil\fcharset0 Menlo-BoldItalic;
\f3\fnil\fcharset0 Menlo-Italic;}
{\colortbl;\red255\green255\blue255;\red0\green0\blue109;\red109\green111\blue5;\red15\green112\blue3;
\red221\green220\blue255;\red82\green0\blue103;\red109\green109\blue109;\red0\green0\blue254;}
{\*\expandedcolortbl;\csgray\c100000;\csgenericrgb\c0\c0\c42745;\csgenericrgb\c42745\c43529\c1961;\csgenericrgb\c5882\c43922\c1176;
\csgenericrgb\c86667\c86275\c100000;\csgenericrgb\c32157\c0\c40392;\csgenericrgb\c42745\c42745\c42745;\csgenericrgb\c0\c0\c99608;}
\paperw11900\paperh16840\margl1440\margr1440\vieww10800\viewh8400\viewkind0
\pard\tx560\tx1120\tx1680\tx2240\tx2800\tx3360\tx3920\tx4480\tx5040\tx5600\tx6160\tx6720\pardirnatural\partightenfactor0

\f0\b\fs24 \cf2 package 
\f1\b0 \cf0 com.ludus.server.domain;\uc0\u8232 \u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 com.ludus.server.domain.base.HibernateEntity;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 com.ludus.server.domain.financial.VatCode;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 com.ludus.server.domain.purchase.PurchaseOrderLine;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 com.ludus.server.domain.sales.SalesOrderLine;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 com.ludus.server.domain.stock.ExternalArticleStockLocation;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 com.ludus.server.domain.stock.ExternalArticleStockLocationAdministration;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 com.ludus.server.domain.stock.StockLocation;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 com.ludus.server.domain.stock.StockTransaction;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 com.ludus.shared.rest.erp.externalarticlelist.externalarticle.ExternalArticleDto;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 org.hibernate.annotations.\cf3 Filter\cf0 ;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 org.hibernate.annotations.\cf3 LazyCollection\cf0 ;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 org.hibernate.annotations.LazyCollectionOption;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 org.hibernate.annotations.\cf3 LazyToOne\cf0 ;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 org.hibernate.annotations.LazyToOneOption;\uc0\u8232 \u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 javax.persistence.CascadeType;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 javax.persistence.\cf3 Column\cf0 ;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 javax.persistence.\cf3 Entity\cf0 ;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 javax.persistence.FetchType;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 javax.persistence.\cf3 ForeignKey\cf0 ;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 javax.persistence.\cf3 JoinColumn\cf0 ;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 javax.persistence.\cf3 ManyToOne\cf0 ;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 javax.persistence.\cf3 MapKey\cf0 ;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 javax.persistence.\cf3 MapKeyJoinColumn\cf0 ;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 javax.persistence.\cf3 OneToMany\cf0 ;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 javax.persistence.\cf3 OneToOne\cf0 ;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 javax.persistence.\cf3 OrderColumn\cf0 ;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 javax.persistence.\cf3 Table\cf0 ;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 javax.persistence.\cf3 Transient\cf0 ;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 java.util.ArrayList;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 java.util.Collection;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 java.util.Date;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 java.util.HashMap;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 java.util.LinkedHashMap;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 java.util.List;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 java.util.Map;\uc0\u8232 
\f0\b \cf2 import 
\f1\b0 \cf0 java.util.Set;\uc0\u8232 \u8232 \cf3 @Entity\uc0\u8232 @Table\cf0 (name = 
\f0\b \cf4 "EXTERNAL_ARTICLE"
\f1\b0 \cf0 ,\uc0\u8232         indexes = \{\cf3 @javax.persistence.Index\cf0 (name = 
\f0\b \cf4 "DELETED"
\f1\b0 \cf0 , columnList = 
\f0\b \cf4 "DELETED"
\f1\b0 \cf0 )\})\uc0\u8232 \u8232 
\f0\b \cf2 public class 
\f1\b0 \cf0 \cb5 ExternalArticle\cb1  
\f0\b \cf2 extends 
\f1\b0 \cf0 HibernateEntity\uc0\u8232 \{\u8232     \cf3 @OneToMany\cf0 (fetch = FetchType.
\f2\i\b \cf6 LAZY
\f1\i0\b0 \cf0 , cascade = \{CascadeType.
\f2\i\b \cf6 PERSIST
\f1\i0\b0 \cf0 , CascadeType.
\f2\i\b \cf6 MERGE
\f1\i0\b0 \cf0 \}, mappedBy = 
\f0\b \cf4 "mExternalArticle"
\f1\b0 \cf0 )\uc0\u8232     \cf3 @MapKeyJoinColumn\cf0 (name=
\f0\b \cf4 "STOCKLOCATION_ID"
\f1\b0 \cf0 )\uc0\u8232     \cf3 @LazyCollection\cf0 (LazyCollectionOption.
\f2\i\b \cf6 EXTRA
\f1\i0\b0 \cf0 )\uc0\u8232     
\f0\b \cf2 private 
\f1\b0 \cf0 Map<StockLocation, ExternalArticleStockLocation> 
\f0\b \cf6 mExternalArticleStockLocationMap 
\f1\b0 \cf0 = 
\f0\b \cf2 new 
\f1\b0 \cf0 LinkedHashMap();\uc0\u8232 \u8232     
\f3\i \cf7 ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\uc0\u8232     
\f0\i0\b \cf2 public 
\f1\b0 \cf0 ExternalArticle()\uc0\u8232     \{\u8232     \}\u8232 \u8232     
\f3\i \cf7 ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\uc0\u8232     
\f0\i0\b \cf2 private 
\f1\b0 \cf0 Map<StockLocation, ExternalArticleStockLocation> getExternalArticleStockLocationMap()\uc0\u8232     \{\u8232         
\f3\i \cf7 // Bug fix!\uc0\u8232         
\f1\i0 \cf0 setExternalArticleStockLocationMap(
\f0\b \cf6 mExternalArticleStockLocationMap
\f1\b0 \cf0 );\uc0\u8232 \u8232         
\f0\b \cf2 return \cf6 mExternalArticleStockLocationMap
\f1\b0 \cf0 ;\uc0\u8232     \}\u8232 \u8232     
\f3\i \cf7 ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\uc0\u8232     
\f0\i0\b \cf2 public void 
\f1\b0 \cf0 setExternalArticleStockLocationMap(Map<StockLocation, ExternalArticleStockLocation> pExternalArticleStockLocationMap)\uc0\u8232     \{\u8232         
\f0\b \cf6 mExternalArticleStockLocationMap 
\f1\b0 \cf0 = pExternalArticleStockLocationMap;\uc0\u8232     \}\u8232 \u8232     
\f3\i \cf7 ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\uc0\u8232     
\f0\i0\b \cf2 public void 
\f1\b0 \cf0 add(ExternalArticleStockLocation pExternalArticleStockLocation)\uc0\u8232     \{\u8232         Map<StockLocation, ExternalArticleStockLocation> lExternalArticleStockLocationMap = getExternalArticleStockLocationMap();\u8232         StockLocation lStockLocation = pExternalArticleStockLocation.getStockLocation();\u8232         lExternalArticleStockLocationMap.put(lStockLocation, pExternalArticleStockLocation);\u8232     \}\u8232 \u8232     
\f3\i \cf7 ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\uc0\u8232     
\f0\i0\b \cf2 public void 
\f1\b0 \cf0 remove(ExternalArticleStockLocation pExternalArticleStockLocation)\uc0\u8232     \{\u8232         Map<StockLocation, ExternalArticleStockLocation> lExternalArticleStockLocationMap = getExternalArticleStockLocationMap();\u8232         lExternalArticleStockLocationMap.remove(pExternalArticleStockLocation);\u8232     \}\u8232 \u8232     
\f3\i \cf7 ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\uc0\u8232     
\f0\i0\b \cf2 private 
\f1\b0 \cf0 ExternalArticleStockLocation createExternalArticleStockLocation(StockLocation pStockLocation, Integer pActualStockLevel)\uc0\u8232     \{\u8232         ExternalArticleStockLocation lExternalArticleStockLocation = 
\f0\b \cf2 new 
\f1\b0 \cf0 ExternalArticleStockLocation(
\f0\b \cf2 this
\f1\b0 \cf0 , pStockLocation, pActualStockLevel);\uc0\u8232         
\f0\b \cf2 return 
\f1\b0 \cf0 lExternalArticleStockLocation;\uc0\u8232     \}\u8232 \u8232     
\f3\i \cf7 ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\uc0\u8232     
\f0\i0\b \cf2 public 
\f1\b0 \cf0 Set<StockLocation> getStockLocationSet()\uc0\u8232     \{\u8232         Map<StockLocation, ExternalArticleStockLocation> lExternalArticleStockLocationMap = getExternalArticleStockLocationMap();\u8232         Set<StockLocation> lStockLocationSet = lExternalArticleStockLocationMap.keySet();\u8232 \u8232         
\f0\b \cf2 return 
\f1\b0 \cf0 lStockLocationSet;\uc0\u8232     \}\u8232 \u8232     
\f3\i \cf7 ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\uc0\u8232     
\f0\i0\b \cf2 public boolean 
\f1\b0 \cf0 hasStockLocation(StockLocation pStockLocation)\uc0\u8232     \{\u8232         Map<StockLocation, ExternalArticleStockLocation> lExternalArticleStockLocationMap = getExternalArticleStockLocationMap();\u8232         
\f0\b \cf2 boolean 
\f1\b0 \cf0 lHasStockLocation = lExternalArticleStockLocationMap.containsKey(pStockLocation);\uc0\u8232 \u8232         
\f0\b \cf2 return 
\f1\b0 \cf0 lHasStockLocation;\uc0\u8232     \}\u8232 \u8232     
\f3\i \cf7 ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\uc0\u8232     
\f0\i0\b \cf2 public boolean 
\f1\b0 \cf0 hasStockLocation()\uc0\u8232     \{\u8232         Map<StockLocation, ExternalArticleStockLocation> lExternalArticleStockLocationMap = getExternalArticleStockLocationMap();\u8232         
\f0\b \cf2 boolean 
\f1\b0 \cf0 lHasExternalArticle = (!lExternalArticleStockLocationMap.isEmpty());\uc0\u8232 \u8232         
\f0\b \cf2 return 
\f1\b0 \cf0 lHasExternalArticle;\uc0\u8232     \}\u8232 \u8232     
\f3\i \cf7 ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\uc0\u8232     // Remove returned orphan ExternalArticleStockLocation after removeStockLocation()\u8232     ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\u8232     
\f0\i0\b \cf2 public 
\f1\b0 \cf0 ExternalArticleStockLocation removeStockLocation(StockLocation pStockLocation)\uc0\u8232     \{\u8232         ExternalArticleStockLocation lExternalArticleStockLocation = 
\f0\b \cf2 null
\f1\b0 \cf0 ;\uc0\u8232 \u8232         
\f0\b \cf2 if 
\f1\b0 \cf0 (hasStockLocation(pStockLocation))\uc0\u8232         \{\u8232             Map<StockLocation, ExternalArticleStockLocation> lExternalArticleStockLocationMap = getExternalArticleStockLocationMap();\u8232             lExternalArticleStockLocation = lExternalArticleStockLocationMap.get(pStockLocation);\u8232             lExternalArticleStockLocation.remove();\u8232         \}\u8232 \u8232         
\f0\b \cf2 return 
\f1\b0 \cf0 lExternalArticleStockLocation;\uc0\u8232     \}\u8232 \u8232     
\f3\i \cf7 ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\uc0\u8232     
\f0\i0\b \cf2 public 
\f1\b0 \cf0 Integer getActualStockLevel(StockLocation pStockLocation)\uc0\u8232     \{\u8232         Integer lActualStockLevel = 
\f0\b \cf2 null
\f1\b0 \cf0 ;\uc0\u8232 \u8232         
\f0\b \cf2 if 
\f1\b0 \cf0 (hasStockLocation(pStockLocation))\uc0\u8232         \{\u8232             Map<StockLocation, ExternalArticleStockLocation> lExternalArticleStockLocationMap = getExternalArticleStockLocationMap();\u8232             ExternalArticleStockLocation lExternalArticleStockLocation = lExternalArticleStockLocationMap.get(pStockLocation);\u8232             lActualStockLevel = lExternalArticleStockLocation.getActualStockLevel();\u8232         \}\u8232 \u8232         
\f0\b \cf2 return 
\f1\b0 \cf0 lActualStockLevel;\uc0\u8232     \}\u8232 \u8232     
\f3\i \cf7 ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\uc0\u8232     
\f0\i0\b \cf2 public void 
\f1\b0 \cf0 setActualStockLevel(StockLocation pStockLocation, Integer pActualStockLevel)\uc0\u8232     \{\u8232         
\f0\b \cf2 if 
\f1\b0 \cf0 (hasStockLocation(pStockLocation))\uc0\u8232         \{\u8232             Map<StockLocation, ExternalArticleStockLocation> lExternalArticleStockLocationMap = getExternalArticleStockLocationMap();\u8232             ExternalArticleStockLocation lExternalArticleStockLocation = lExternalArticleStockLocationMap.get(pStockLocation);\u8232             lExternalArticleStockLocation.setActualStockLevel(pActualStockLevel);\u8232         \}\u8232         
\f0\b \cf2 else\uc0\u8232         
\f1\b0 \cf0 \{\uc0\u8232             createExternalArticleStockLocation(pStockLocation, pActualStockLevel);\u8232         \}\u8232     \}\u8232 \u8232     
\f3\i \cf7 ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\uc0\u8232     
\f0\i0\b \cf2 public 
\f1\b0 \cf0 Integer getActualStockLevel()\uc0\u8232     \{\u8232         Map<StockLocation, ExternalArticleStockLocation> lExternalArticleStockLocationMap = getExternalArticleStockLocationMap();\u8232         Collection<ExternalArticleStockLocation> lExternalArticleStockLocationCollection = lExternalArticleStockLocationMap.values();\u8232         Integer lActualStockLevel = \cf8 0\cf0 ;\uc0\u8232 \u8232         
\f0\b \cf2 for 
\f1\b0 \cf0 (ExternalArticleStockLocation lExternalArticleStockLocation : lExternalArticleStockLocationCollection)\uc0\u8232         \{\u8232             lActualStockLevel += lExternalArticleStockLocation.getActualStockLevel();\u8232         \}\u8232 \u8232         
\f0\b \cf2 return 
\f1\b0 \cf0 lActualStockLevel;\uc0\u8232     \}\u8232 \}\u8232 \
}