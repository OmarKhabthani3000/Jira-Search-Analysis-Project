import net.java.dev.springannotation.annotation.Bean;

import org.apache.commons.lang.StringUtils;
import org.hibernate.jmx.StatisticsService;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

import persistence.hibernate.HibernateUtil;

@ManagedResource(objectName = "bean:name=secondLevelCacheStatisticsMBean", description = "Hibernate Second Level Cache Statistics MBean")
@Bean(name = "secondLevelCacheStatisticsMBean")
public class SecondLevelCacheStatisticsMBean {
    /**
     * Returns the Hibernate second level cache statistics for all regions. 
     */
    @ManagedOperation(description = "Gets the Hibernate second level cache statistics for the all regions")
    @ManagedOperationParameters({
      @ManagedOperationParameter(name = "ignored", description = "This parameter is not used.")
      })
    public String getSecondLevelCacheStatisticsForAllRegions(String ignored) {
        StringBuffer buf = new StringBuffer(1024);
        
        StatisticsService statisticsService = new StatisticsService();
        statisticsService.setSessionFactory(HibernateUtil.getSession().getSessionFactory());
        statisticsService.setStatisticsEnabled(true);
        
        String[] regionNames = statisticsService.getSecondLevelCacheRegionNames();
        for (int i=0, numberOfRegions=regionNames.length; i < numberOfRegions; i++) {
            SecondLevelCacheStatistics cacheStatistics = statisticsService.getSecondLevelCacheStatistics(regionNames[i]);
            if (cacheStatistics != null) {
                buf.append(regionNames[i] + " " + cacheStatistics.toString()).append("\n");
            }
        }

        return buf.toString();
    }

    /**
     * Returns the Hibernate second level cache statistics for a given region. 
     */
    @ManagedOperation(description = "Gets the Hibernate second level cache statistics for the specified region")
    @ManagedOperationParameters({
      @ManagedOperationParameter(name = "regionName", description = "The region name")
      })
    public String getSecondLevelCacheStatistics(String regionName) {
        if (StringUtils.isBlank(regionName)) {
            return "Illegal Argument: regionName is required";
        }
        
        StatisticsService statisticsService = new StatisticsService();
        statisticsService.setSessionFactory(HibernateUtil.getSession().getSessionFactory());
        statisticsService.setStatisticsEnabled(true);
        
        SecondLevelCacheStatistics cacheStatistics = statisticsService.getSecondLevelCacheStatistics(regionName);
        if (cacheStatistics != null) {
            return cacheStatistics.toString();
        }
        else {
            return "Region not found: " + regionName;
        }
    }
}