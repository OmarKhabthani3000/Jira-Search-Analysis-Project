// execution: de.psi.metals.plugins:suite-mda-plugin:3.1.0-SNAPSHOT:mda {execution: default}
// generator: de.psi.mda.generator.entity.EntityBeanClassGenerator
// date     : Tue Apr 16 16:23:11 CEST 2019
// model    : 4P MDA Base Model + PSIsuite MES Persistence Model + 4P Suite MFM Profile + HHC Server Model

package de.suite4p.hhc.production.pojo;

import de.suite4p.hhc.material.pojo.AlloyElement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Generated;
import javax.persistence.*;

@Entity
@Access( AccessType.FIELD )
@DiscriminatorValue( "ANALYSISELEMENT" )
@NamedQuery( name = "AnalysisElement.findAll", query = "from de.suite4p.hhc.production.pojo.AnalysisElement" )
@Generated( value = "de.psi.mda.generator.entity.EntityBeanClassGenerator", date = "2019-04-16T16:23:11.028+02:00" )
public class AnalysisElement
    extends AlloyElement
{
    private static final long serialVersionUID = 1L;


    // ----- members for attribute "standardValue" -----
    public static final String attributeStandardValue = "standardValue";

    /**
     * State for attribute "standardValue". 
     */
    @Column( name = "STANDARDVALUE" )
    private Double standardValue;

    /**
     * Get attribute "standardValue". 
     * @return value of attribute "standardValue".
     */
    public Double getStandardValue()
    {
        return standardValue;
    }

    /**
     * Set attribute "standardValue". 
     * @param newStandardValue new value for attribute "standardValue".
     */
    public void setStandardValue( Double newStandardValue )
    {
        this.standardValue = newStandardValue;
    }



    // ----- members for attribute "calibrationFlag" -----
    public static final String attributeCalibrationFlag = "calibrationFlag";

    /**
     * State for attribute "calibrationFlag". 
     */
    @Column( name = "CALIBRATIONFLAG" )
    private String calibrationFlag;

    /**
     * Get attribute "calibrationFlag". 
     * @return value of attribute "calibrationFlag".
     */
    public String getCalibrationFlag()
    {
        return calibrationFlag;
    }

    /**
     * Set attribute "calibrationFlag". 
     * @param newCalibrationFlag new value for attribute "calibrationFlag".
     */
    public void setCalibrationFlag( String newCalibrationFlag )
    {
        this.calibrationFlag = newCalibrationFlag;
    }



    // ----- members for attribute "itemType" -----
    public static final String attributeItemType = "itemType";

    /**
     * State for attribute "itemType". 
     */
    @Column( name = "ITEMTYPE" )
    private String itemType;

    /**
     * Get attribute "itemType". 
     * @return value of attribute "itemType".
     */
    public String getItemType()
    {
        return itemType;
    }

    /**
     * Set attribute "itemType". 
     * @param newItemType new value for attribute "itemType".
     */
    public void setItemType( String newItemType )
    {
        this.itemType = newItemType;
    }


    // ----- members for bidirectional many-to-many association "AnalysisElements" -----

    /**
     * State for association "AnalysisElements".
     * We are on the left-side ("Elements") of an unordered many-to-many association.
     * This field holds a Set of reference to the associated "Analysis" instances.
     * It is subject to lazy initialization.
     */
    @ManyToMany( mappedBy = "elements" )
    private Set<Analysis> analysis;

    /**
     * Obtain set of "Analysis" instances.
     * @return a set holding associated "Analysis" instances
     */
    public Set<Analysis> getAnalysis()
    {
        // lazy evaluation; the field might still be null
        if( analysis == null )
        {
           analysis = new HashSet<Analysis>();
        }
        return analysis;
    }

    /**
     * Set attribute "analysis".
     *
     * @param newAnalysis 
     *             new value for attribute "analysis"
     */
    @SuppressWarnings( "unused" ) // only for ERM access.
    private void setAnalysis( final Set<Analysis> newAnalysis )
    {
        this.analysis = newAnalysis;
    }

    /**
     * Check if some "Analysis" instance is contained in the
     * association "AnalysisElements".
     * @param analysis "Analysis" instance to check
     * @return true if instance is member of the association
     */
    public boolean containsInAnalysis( Analysis analysis )
    {
        return this.analysis != null && this.analysis.contains( analysis );
    }

    /**
     * Number of associated "Analysis" instances in
     * association "AnalysisElements".
     * @return number of associated instances
     */
    public int numberOfAnalysis()
    {
        // handle uninitialized set, but do not initialize it now
        return analysis == null ? 0 : analysis.size();
    }

    /**
     * Flush association "AnalysisElements" by removing all
     * instances of "Analysis". Instances on the other
     * side are updated correctly.
     */
    public void flushAnalysis()
    {
       if( analysis != null )
       {
          // avoid ConcurrentModificationException
          for( Analysis c : new ArrayList<Analysis>( analysis ))
          {
             // a list may contain null values
             if( c != null )
             {
                removeFromAnalysis( c );
             }
          }
          analysis = null;
       }
    }

    /**
     * Add a "Analysis" instance to the "AnalysisElements" association.
     * Will update the other side of the association.
     * @param analysis the instance to add
     */
    public void addToAnalysis( Analysis analysis )
    {
        getAnalysis().add( analysis );
        analysis.oneSided_addToElements( this );
    }

    /**
     * Remove a "Analysis" instance from the "AnalysisElements" association.
     * Will update the other side of the association.
     * @param analysis the instance to add
     */
    public void removeFromAnalysis( Analysis analysis )
    {
        getAnalysis().remove( analysis );
        analysis.oneSided_removeFromElements( this );
    }

    /**
     * Add a "Analysis" instance to the "AnalysisElements" association.
     * Will not update the other side of the association.
     * @param analysis the instance to add
     */
    public void oneSided_addToAnalysis( Analysis analysis )
    {
        getAnalysis().add( analysis );
    }

    /**
     * Remove a "Analysis" instance from the "AnalysisElements" association.
     * Will not update the other side of the association.
     * @param analysis the instance to add
     */
    public void oneSided_removeFromAnalysis( Analysis analysis )
    {
        getAnalysis().remove( analysis );
    }


    // ----- lifecycle support -----

    @Override
    public void removeAllAggregations()
    {
        super.removeAllAggregations();
        flushAnalysis();
    }
    
    @Override
    public void removeAllCompositions()
    {
        super.removeAllCompositions();
    }

    // ----- member utils -----

    /**
     * Collect field names and values.
     * @param attrMap map object to be filled with field names and values
     */
    @Override
    public void collectValues( Map<String, Object> attrMap )
    {
        super.collectValues( attrMap );
        attrMap.put( "standardValue", standardValue );
        attrMap.put( "calibrationFlag", calibrationFlag );
        attrMap.put( "itemType", itemType );
    }
}

