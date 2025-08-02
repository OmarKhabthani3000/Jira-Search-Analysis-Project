package net.ema.titan.base;

import java.beans.Introspector;
import java.util.List;

import org.hibernate.cfg.reveng.DelegatingReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategyUtil;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.util.StringHelper;

public class TitanStrategy extends DelegatingReverseEngineeringStrategy {
    private ReverseEngineeringSettings settings;

    public TitanStrategy(ReverseEngineeringStrategy delegate) {
        super(delegate);
        settings = new ReverseEngineeringSettings();
    }

    /**
     * 根据数据库表名生成类名
     */
    public String tableToClassName(TableIdentifier ti) {
        String className = ti.getName();
        
        //删除表名前面的"T_XX_". XX代表模块名
        int index = className.indexOf("_"); className = className.substring(index+1);
        index = className.indexOf("_"); className = className.substring(index+1);

        String pkgName = settings.getDefaultPackageName();
        className = toUpperCamelCase(className) + "Model";

        if(pkgName.length() > 0)
            return StringHelper.qualify(pkgName, className);
        else
            return className;
    }

    /**
     * 用于manytoone关系的属性名产生
     */
    public String foreignKeyToEntityName(String keyname, TableIdentifier fromTable, List fromColumnNames, 
            TableIdentifier referencedTable, List referencedColumnNames, boolean uniqueReference) {
        String propertyName = Introspector.decapitalize(StringHelper.unqualify(tableToClassName(referencedTable)));
        if(!uniqueReference)
            if(fromColumnNames != null && fromColumnNames.size() == 1)
            {
                String columnName = ((Column)fromColumnNames.get(0)).getName();
                propertyName = propertyName + "By" + toUpperCamelCase(columnName);
            } else
            {
                propertyName = propertyName + "By" + toUpperCamelCase(keyname);
            }
        return propertyName;
    }

    /**
     * 用于manytomany关系的属性名产生
     */
    public String foreignKeyToManyToManyName(ForeignKey fromKey, TableIdentifier middleTable, ForeignKey toKey, boolean uniqueReference) {
        String propertyName = Introspector.decapitalize(StringHelper.unqualify(tableToClassName(TableIdentifier.create(toKey.getReferencedTable()))));
        propertyName = pluralize(propertyName);
        if(!uniqueReference)
            if(toKey.getColumns() != null && toKey.getColumns().size() == 1)
            {
                String columnName = ((Column)toKey.getColumns().get(0)).getName();
                propertyName = propertyName + "For" + toUpperCamelCase(columnName);
            } else
            {
                propertyName = propertyName + "For" + toUpperCamelCase(toKey.getName());
            }
        return propertyName;
    }

    protected String toUpperCamelCase(String s) {
        return ReverseEngineeringStrategyUtil.toUpperCamelCase(s);
    }

    protected String pluralize(String singular) {
        return ReverseEngineeringStrategyUtil.simplePluralize(singular);
    }

    public void setSettings(ReverseEngineeringSettings settings) {
        this.settings = settings;
    }
}
