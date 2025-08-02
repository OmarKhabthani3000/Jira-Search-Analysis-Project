package com.ludus.server.domain;

import com.ludus.server.domain.base.HibernateEntity;
import com.ludus.server.domain.financial.VatCode;
import com.ludus.server.domain.purchase.PurchaseOrderLine;
import com.ludus.server.domain.sales.SalesOrderLine;
import com.ludus.server.domain.stock.ExternalArticleStockLocation;
import com.ludus.server.domain.stock.ExternalArticleStockLocationAdministration;
import com.ludus.server.domain.stock.StockLocation;
import com.ludus.server.domain.stock.StockTransaction;
import com.ludus.shared.rest.erp.externalarticlelist.externalarticle.ExternalArticleDto;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "EXTERNAL_ARTICLE",
        indexes = {@javax.persistence.Index(name = "DELETED", columnList = "DELETED")})

public class ExternalArticle2 extends HibernateEntity
{
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "mExternalArticle")
    @MapKeyJoinColumn(name="STOCKLOCATION_ID")
    @LazyCollection(LazyCollectionOption.EXTRA)
    private Map<StockLocation, ExternalArticleStockLocation> mExternalArticleStockLocationMap = new LinkedHashMap();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public ExternalArticle2()
    {
        super();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ExternalArticleStockLocation
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private Map<StockLocation, ExternalArticleStockLocation> getExternalArticleStockLocationMap()
    {
        // Bug fix!
        setExternalArticleStockLocationMap(mExternalArticleStockLocationMap);

        return mExternalArticleStockLocationMap;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void setExternalArticleStockLocationMap(Map<StockLocation, ExternalArticleStockLocation> pExternalArticleStockLocationMap)
    {
        mExternalArticleStockLocationMap = pExternalArticleStockLocationMap;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void add(ExternalArticleStockLocation pExternalArticleStockLocation)
    {
        Map<StockLocation, ExternalArticleStockLocation> lExternalArticleStockLocationMap = getExternalArticleStockLocationMap();
        StockLocation lStockLocation = pExternalArticleStockLocation.getStockLocation();
        lExternalArticleStockLocationMap.put(lStockLocation, pExternalArticleStockLocation);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void remove(ExternalArticleStockLocation pExternalArticleStockLocation)
    {
        Map<StockLocation, ExternalArticleStockLocation> lExternalArticleStockLocationMap = getExternalArticleStockLocationMap();
        lExternalArticleStockLocationMap.remove(pExternalArticleStockLocation);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private ExternalArticleStockLocation createExternalArticleStockLocation(StockLocation pStockLocation, Integer pActualStockLevel)
    {
        ExternalArticleStockLocation lExternalArticleStockLocation = new ExternalArticleStockLocation(this, pStockLocation, pActualStockLevel);
        return lExternalArticleStockLocation;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Set<StockLocation> getStockLocationSet()
    {
        Map<StockLocation, ExternalArticleStockLocation> lExternalArticleStockLocationMap = getExternalArticleStockLocationMap();
        Set<StockLocation> lStockLocationSet = lExternalArticleStockLocationMap.keySet();

        return lStockLocationSet;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean hasStockLocation(StockLocation pStockLocation)
    {
        Map<StockLocation, ExternalArticleStockLocation> lExternalArticleStockLocationMap = getExternalArticleStockLocationMap();
        boolean lHasStockLocation = lExternalArticleStockLocationMap.containsKey(pStockLocation);

        return lHasStockLocation;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean hasStockLocation()
    {
        Map<StockLocation, ExternalArticleStockLocation> lExternalArticleStockLocationMap = getExternalArticleStockLocationMap();
        boolean lHasExternalArticle = (!lExternalArticleStockLocationMap.isEmpty());

        return lHasExternalArticle;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Remove returned orphan ExternalArticleStockLocation after removeStockLocation()
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public ExternalArticleStockLocation removeStockLocation(StockLocation pStockLocation)
    {
        ExternalArticleStockLocation lExternalArticleStockLocation = null;

        if (hasStockLocation(pStockLocation))
        {
            Map<StockLocation, ExternalArticleStockLocation> lExternalArticleStockLocationMap = getExternalArticleStockLocationMap();
            lExternalArticleStockLocation = lExternalArticleStockLocationMap.get(pStockLocation);
            lExternalArticleStockLocation.remove();
        }

        return lExternalArticleStockLocation;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Integer getActualStockLevel(StockLocation pStockLocation)
    {
        Integer lActualStockLevel = null;

        if (hasStockLocation(pStockLocation))
        {
            Map<StockLocation, ExternalArticleStockLocation> lExternalArticleStockLocationMap = getExternalArticleStockLocationMap();
            ExternalArticleStockLocation lExternalArticleStockLocation = lExternalArticleStockLocationMap.get(pStockLocation);
            lActualStockLevel = lExternalArticleStockLocation.getActualStockLevel();
        }

        return lActualStockLevel;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void setActualStockLevel(StockLocation pStockLocation, Integer pActualStockLevel)
    {
        if (hasStockLocation(pStockLocation))
        {
            Map<StockLocation, ExternalArticleStockLocation> lExternalArticleStockLocationMap = getExternalArticleStockLocationMap();
            ExternalArticleStockLocation lExternalArticleStockLocation = lExternalArticleStockLocationMap.get(pStockLocation);
            lExternalArticleStockLocation.setActualStockLevel(pActualStockLevel);
        }
        else
        {
            createExternalArticleStockLocation(pStockLocation, pActualStockLevel);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Integer getActualStockLevel()
    {
        Map<StockLocation, ExternalArticleStockLocation> lExternalArticleStockLocationMap = getExternalArticleStockLocationMap();
        Collection<ExternalArticleStockLocation> lExternalArticleStockLocationCollection = lExternalArticleStockLocationMap.values();
        Integer lActualStockLevel = 0;

        for (ExternalArticleStockLocation lExternalArticleStockLocation : lExternalArticleStockLocationCollection)
        {
            lActualStockLevel += lExternalArticleStockLocation.getActualStockLevel();
        }

        return lActualStockLevel;
    }
}
