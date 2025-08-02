package com.ludus.server.domain.stock;

import com.ludus.server.domain.ExternalArticle;
import com.ludus.server.domain.base.HibernateEntity;
import com.ludus.server.domain.purchase.ShippingDocumentLine;
import com.ludus.server.domain.sales.SalesShippingDocument;
import com.ludus.server.domain.sales.SalesShippingDocumentLine;
import com.ludus.shared.rest.erp.stock.stocklocationlist.stocklocation.StockLocationDto;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MapKey;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "STOCK_LOCATION",
        indexes = {@javax.persistence.Index(name = "DELETED", columnList = "DELETED")})
public class StockLocation2 extends HibernateEntity
{
    @OneToMany(fetch = FetchType.EAGER.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "mStockLocation")
    @MapKeyJoinColumn(name="EXTERNALARTICLE_ID")
    @LazyCollection(LazyCollectionOption.EXTRA)
    private Map<ExternalArticle, ExternalArticleStockLocation> mExternalArticleStockLocationMap = new LinkedHashMap();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public StockLocation2()
    {
        super();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ExternalArticleStockLocation
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Map<ExternalArticle, ExternalArticleStockLocation> getExternalArticleStockLocationMap()
    {
        // Bug fix!
        setExternalArticleStockLocationMap(mExternalArticleStockLocationMap);

        return mExternalArticleStockLocationMap;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void setExternalArticleStockLocationMap(Map<ExternalArticle, ExternalArticleStockLocation> pExternalArticleStockLocationMap)
    {
        mExternalArticleStockLocationMap = pExternalArticleStockLocationMap;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void addShippingDocumentLineStockLocation(ExternalArticleStockLocation pExternalArticleStockLocation)
    {
        Map<ExternalArticle, ExternalArticleStockLocation> lExternalArticleStockLocationMap = getExternalArticleStockLocationMap();
        ExternalArticle lExternalArticle = pExternalArticleStockLocation.getExternalArticle();
        lExternalArticleStockLocationMap.put(lExternalArticle, pExternalArticleStockLocation);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void removeShippingDocumentLineStockLocation(ExternalArticleStockLocation pExternalArticleStockLocation)
    {
        Map<ExternalArticle, ExternalArticleStockLocation> lExternalArticleStockLocationMap = getExternalArticleStockLocationMap();
        lExternalArticleStockLocationMap.remove(pExternalArticleStockLocation);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private ExternalArticleStockLocation createExternalArticleStockLocation(ExternalArticle pExternalArticle, Integer pActualStockLevel)
    {
        ExternalArticleStockLocation lExternalArticleStockLocation = new ExternalArticleStockLocation(pExternalArticle, this, pActualStockLevel);
        return lExternalArticleStockLocation;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Set<ExternalArticle> getExternalArticleSet()
    {
        Map<ExternalArticle, ExternalArticleStockLocation> lExternalArticleStockLocationMap = getExternalArticleStockLocationMap();
        Set<ExternalArticle> lExternalArticleSet = lExternalArticleStockLocationMap.keySet();

        return lExternalArticleSet;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean hasExternalArticle(ExternalArticle pExternalArticle)
    {
        Map<ExternalArticle, ExternalArticleStockLocation> lExternalArticleStockLocationMap = getExternalArticleStockLocationMap();
        boolean lHasExternalArticle = lExternalArticleStockLocationMap.containsKey(pExternalArticle);

        return lHasExternalArticle;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean hasExternalArticle()
    {
        Map<ExternalArticle, ExternalArticleStockLocation> lExternalArticleStockLocationMap = getExternalArticleStockLocationMap();
        boolean lHasExternalArticle = (!lExternalArticleStockLocationMap.isEmpty());

        return lHasExternalArticle;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Remove returned orphan ExternalArticleStockLocation after removeStockLocation()
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public ExternalArticleStockLocation removeExternalArticle(ExternalArticle pExternalArticle)
    {
        ExternalArticleStockLocation lExternalArticleStockLocation = null;

        if (hasExternalArticle(pExternalArticle))
        {
            Map<ExternalArticle, ExternalArticleStockLocation> lExternalArticleStockLocationMap = getExternalArticleStockLocationMap();
            lExternalArticleStockLocation = lExternalArticleStockLocationMap.get(pExternalArticle);
            lExternalArticleStockLocation.remove();
        }

        return lExternalArticleStockLocation;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Integer getActualStockLevel(ExternalArticle pExternalArticle)
    {
        Integer lActualStockLevel = null;

        if (hasExternalArticle(pExternalArticle))
        {
            Map<ExternalArticle, ExternalArticleStockLocation> lExternalArticleStockLocationMap = getExternalArticleStockLocationMap();
            ExternalArticleStockLocation lExternalArticleStockLocation = lExternalArticleStockLocationMap.get(pExternalArticle);
            lActualStockLevel = lExternalArticleStockLocation.getActualStockLevel();
        }

        return lActualStockLevel;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void setActualStockLevel(ExternalArticle pExternalArticle, Integer pActualStockLevel)
    {
        if (hasExternalArticle(pExternalArticle))
        {
            Map<ExternalArticle, ExternalArticleStockLocation> lExternalArticleStockLocationMap = getExternalArticleStockLocationMap();
            ExternalArticleStockLocation lExternalArticleStockLocation = lExternalArticleStockLocationMap.get(pExternalArticle);
            lExternalArticleStockLocation.setActualStockLevel(pActualStockLevel);
        }
        else
        {
            createExternalArticleStockLocation(pExternalArticle, pActualStockLevel);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Integer getActualStockLevel()
    {
        Map<ExternalArticle, ExternalArticleStockLocation> lExternalArticleStockLocationMap = getExternalArticleStockLocationMap();
        Collection<ExternalArticleStockLocation> lExternalArticleStockLocationCollection = lExternalArticleStockLocationMap.values();
        Integer lActualStockLevel = 0;

        for (ExternalArticleStockLocation lExternalArticleStockLocation : lExternalArticleStockLocationCollection)
        {
            lActualStockLevel += lExternalArticleStockLocation.getActualStockLevel();
        }

        return lActualStockLevel;
    }
}
