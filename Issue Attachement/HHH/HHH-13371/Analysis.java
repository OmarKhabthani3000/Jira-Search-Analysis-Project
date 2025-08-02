// execution: de.psi.metals.plugins:suite-mda-plugin:3.1.0-SNAPSHOT:mda {execution: default}
// generator: de.psi.mda.generator.entity.EntityBeanClassGenerator
// date     : Tue Apr 16 16:23:11 CEST 2019
// model    : 4P MDA Base Model + PSIsuite MES Persistence Model + 4P Suite MFM Profile + HHC Server Model

package de.suite4p.hhc.production.pojo;

import de.psi.persistence.EntityBaseLegacyMappedSuperclass;
import de.psi.persistence.PrimitiveAttribute;
import de.suite4p.hhc.material.pojo.HHCMetalMaterial;
import de.suite4p.hhc.mom.pojo.HHCBatch;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Generated;
import javax.persistence.*;

@Entity
@Access( AccessType.FIELD )
@Table( name = "ANALYSIS" )
@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
@DiscriminatorColumn( name = "DISCRIMINATOR", discriminatorType = DiscriminatorType.STRING, length = 255 )
@DiscriminatorValue( "ANALYSIS" )
@NamedQuery( name = "Analysis.findAll", query = "from de.suite4p.hhc.production.pojo.Analysis" )
@Generated( value = "de.psi.mda.generator.entity.EntityBeanClassGenerator", date = "2019-04-16T16:23:11.036+02:00" )
public class Analysis
    extends EntityBaseLegacyMappedSuperclass
{
    private static final long serialVersionUID = 1L;


    // ----- members for attribute "name" -----
    public static final String attributeName = "name";

    /**
     * State for attribute "name". 
    * Analysenummer
     */
    @Column( name = "NAME" )
    private String name;

    /**
     * Get attribute "name". 
    * Analysenummer
     * @return value of attribute "name".
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set attribute "name". 
    * Analysenummer
     * @param newName new value for attribute "name".
     */
    public void setName( String newName )
    {
        this.name = newName;
    }



    // ----- members for attribute "activationtime" -----
    public static final String attributeActivationtime = "activationtime";

    /**
     * State for attribute "activationtime". 
     */
    @Column( name = "ACTIVATIONTIME" )
    private Timestamp activationtime;

    /**
     * Get attribute "activationtime". 
     * @return value of attribute "activationtime".
     */
    public Timestamp getActivationtime()
    {
        return activationtime;
    }

    /**
     * Set attribute "activationtime". 
     * @param newActivationtime new value for attribute "activationtime".
     */
    public void setActivationtime( Timestamp newActivationtime )
    {
        this.activationtime = newActivationtime;
    }



    // ----- members for attribute "active" -----
    public static final String attributeActive = "active";

    /**
     * State for attribute "active". 
     */
    @Column( name = "ACTIVE" )
    private Boolean active = true;

    /**
     * Get attribute "active". 
     * @return value of attribute "active".
     */
    public boolean getActive()
    {
        return PrimitiveAttribute.nullSafeGet( active, true );
    }

    /**
     * Set attribute "active". 
     * @param newActive new value for attribute "active".
     */
    public void setActive( Boolean newActive )
    {
        this.active = PrimitiveAttribute.nullSafeGet( newActive, true );
    }

    /**
     * Additional setter for primitive type.
     * Set attribute "active". 
     * @param newActive new value for attribute "active".
     */
    public void setActive( boolean newActive )
    {
       this.active = newActive;
    }



    // ----- members for attribute "analysisNo" -----
    public static final String attributeAnalysisNo = "analysisNo";

    /**
     * State for attribute "analysisNo". 
     */
    @Column( name = "ANALYSISNO" )
    private Integer analysisNo;

    /**
     * Get attribute "analysisNo". 
     * @return value of attribute "analysisNo".
     */
    public int getAnalysisNo()
    {
        return PrimitiveAttribute.nullSafeGet( analysisNo );
    }

    /**
     * Set attribute "analysisNo". 
     * @param newAnalysisNo new value for attribute "analysisNo".
     */
    public void setAnalysisNo( Integer newAnalysisNo )
    {
        this.analysisNo = PrimitiveAttribute.nullSafeGet( newAnalysisNo );
    }

    /**
     * Additional setter for primitive type.
     * Set attribute "analysisNo". 
     * @param newAnalysisNo new value for attribute "analysisNo".
     */
    public void setAnalysisNo( int newAnalysisNo )
    {
       this.analysisNo = newAnalysisNo;
    }



    // ----- members for attribute "chargeID" -----
    public static final String attributeChargeID = "chargeID";

    /**
     * State for attribute "chargeID". 
     */
    @Column( name = "CHARGEID" )
    private String chargeID;

    /**
     * Get attribute "chargeID". 
     * @return value of attribute "chargeID".
     */
    public String getChargeID()
    {
        return chargeID;
    }

    /**
     * Set attribute "chargeID". 
     * @param newChargeID new value for attribute "chargeID".
     */
    public void setChargeID( String newChargeID )
    {
        this.chargeID = newChargeID;
    }



    // ----- members for attribute "analysisOK" -----
    public static final String attributeAnalysisOK = "analysisOK";

    /**
     * State for attribute "analysisOK". 
     */
    @Column( name = "ANALYSISOK" )
    private String analysisOK;

    /**
     * Get attribute "analysisOK". 
     * @return value of attribute "analysisOK".
     */
    public String getAnalysisOK()
    {
        return analysisOK;
    }

    /**
     * Set attribute "analysisOK". 
     * @param newAnalysisOK new value for attribute "analysisOK".
     */
    public void setAnalysisOK( String newAnalysisOK )
    {
        this.analysisOK = newAnalysisOK;
    }



    // ----- members for attribute "instrumentType" -----
    public static final String attributeInstrumentType = "instrumentType";

    /**
     * State for attribute "instrumentType". 
     */
    @Column( name = "INSTRUMENTTYPE" )
    private String instrumentType;

    /**
     * Get attribute "instrumentType". 
     * @return value of attribute "instrumentType".
     */
    public String getInstrumentType()
    {
        return instrumentType;
    }

    /**
     * Set attribute "instrumentType". 
     * @param newInstrumentType new value for attribute "instrumentType".
     */
    public void setInstrumentType( String newInstrumentType )
    {
        this.instrumentType = newInstrumentType;
    }



    // ----- members for attribute "probenArt" -----
    public static final String attributeProbenArt = "probenArt";

    /**
     * State for attribute "probenArt". 
     */
    @Column( name = "PROBENART" )
    private String probenArt;

    /**
     * Get attribute "probenArt". 
     * @return value of attribute "probenArt".
     */
    public String getProbenArt()
    {
        return probenArt;
    }

    /**
     * Set attribute "probenArt". 
     * @param newProbenArt new value for attribute "probenArt".
     */
    public void setProbenArt( String newProbenArt )
    {
        this.probenArt = newProbenArt;
    }



    // ----- members for attribute "probenNr" -----
    public static final String attributeProbenNr = "probenNr";

    /**
     * State for attribute "probenNr". 
     */
    @Column( name = "PROBENNR" )
    private String probenNr;

    /**
     * Get attribute "probenNr". 
     * @return value of attribute "probenNr".
     */
    public String getProbenNr()
    {
        return probenNr;
    }

    /**
     * Set attribute "probenNr". 
     * @param newProbenNr new value for attribute "probenNr".
     */
    public void setProbenNr( String newProbenNr )
    {
        this.probenNr = newProbenNr;
    }



    // ----- members for attribute "produkt" -----
    public static final String attributeProdukt = "produkt";

    /**
     * State for attribute "produkt". 
     */
    @Column( name = "PRODUKT" )
    private String produkt;

    /**
     * Get attribute "produkt". 
     * @return value of attribute "produkt".
     */
    public String getProdukt()
    {
        return produkt;
    }

    /**
     * Set attribute "produkt". 
     * @param newProdukt new value for attribute "produkt".
     */
    public void setProdukt( String newProdukt )
    {
        this.produkt = newProdukt;
    }



    // ----- members for attribute "programName" -----
    public static final String attributeProgramName = "programName";

    /**
     * State for attribute "programName". 
     */
    @Column( name = "PROGRAMNAME" )
    private String programName;

    /**
     * Get attribute "programName". 
     * @return value of attribute "programName".
     */
    public String getProgramName()
    {
        return programName;
    }

    /**
     * Set attribute "programName". 
     * @param newProgramName new value for attribute "programName".
     */
    public void setProgramName( String newProgramName )
    {
        this.programName = newProgramName;
    }



    // ----- members for attribute "evaluationType" -----
    public static final String attributeEvaluationType = "evaluationType";

    /**
     * State for attribute "evaluationType". 
     */
    @Column( name = "EVALUATIONTYPE" )
    private String evaluationType = "LABOR";

    /**
     * Get attribute "evaluationType". 
     * @return value of attribute "evaluationType".
     */
    public String getEvaluationType()
    {
        return evaluationType;
    }

    /**
     * Set attribute "evaluationType". 
     * @param newEvaluationType new value for attribute "evaluationType".
     */
    public void setEvaluationType( String newEvaluationType )
    {
        this.evaluationType = newEvaluationType;
    }



    // ----- members for attribute "isMasked" -----
    public static final String attributeIsMasked = "isMasked";

    /**
     * State for attribute "isMasked". 
     */
    @Column( name = "ISMASKED" )
    private Boolean isMasked = false;

    /**
     * Get attribute "isMasked". 
     * @return value of attribute "isMasked".
     */
    public Boolean getIsMasked()
    {
        return isMasked;
    }

    /**
     * Set attribute "isMasked". 
     * @param newIsMasked new value for attribute "isMasked".
     */
    public void setIsMasked( Boolean newIsMasked )
    {
        this.isMasked = newIsMasked;
    }



    // ----- members for attribute "maskReason" -----
    public static final String attributeMaskReason = "maskReason";

    /**
     * State for attribute "maskReason". 
     */
    @Column( name = "MASKREASON" )
    private String maskReason;

    /**
     * Get attribute "maskReason". 
     * @return value of attribute "maskReason".
     */
    public String getMaskReason()
    {
        return maskReason;
    }

    /**
     * Set attribute "maskReason". 
     * @param newMaskReason new value for attribute "maskReason".
     */
    public void setMaskReason( String newMaskReason )
    {
        this.maskReason = newMaskReason;
    }



    // ----- members for attribute "deliveryNr" -----
    public static final String attributeDeliveryNr = "deliveryNr";

    /**
     * State for attribute "deliveryNr". 
    * The deliver-number received from LaborReceiverService.
    * The first part of attribue 'sampleno' separated by first character '-'.
     */
    @Column( name = "DELIVERYNR" )
    private String deliveryNr;

    /**
     * Get attribute "deliveryNr". 
    * The deliver-number received from LaborReceiverService.
    * The first part of attribue 'sampleno' separated by first character '-'.
     * @return value of attribute "deliveryNr".
     */
    public String getDeliveryNr()
    {
        return deliveryNr;
    }

    /**
     * Set attribute "deliveryNr". 
    * The deliver-number received from LaborReceiverService.
    * The first part of attribue 'sampleno' separated by first character '-'.
     * @param newDeliveryNr new value for attribute "deliveryNr".
     */
    public void setDeliveryNr( String newDeliveryNr )
    {
        this.deliveryNr = newDeliveryNr;
    }



    // ----- members for attribute "weightingOfMeanValue" -----
    public static final String attributeWeightingOfMeanValue = "weightingOfMeanValue";

    /**
     * State for attribute "weightingOfMeanValue". 
     */
    @Column( name = "WEIGHTINGOFMEANVALUE" )
    private Double weightingOfMeanValue = 1.;

    /**
     * Get attribute "weightingOfMeanValue". 
     * @return value of attribute "weightingOfMeanValue".
     */
    public double getWeightingOfMeanValue()
    {
        return PrimitiveAttribute.nullSafeGet( weightingOfMeanValue, 1. );
    }

    /**
     * Set attribute "weightingOfMeanValue". 
     * @param newWeightingOfMeanValue new value for attribute "weightingOfMeanValue".
     */
    public void setWeightingOfMeanValue( Double newWeightingOfMeanValue )
    {
        this.weightingOfMeanValue = PrimitiveAttribute.nullSafeGet( newWeightingOfMeanValue, 1. );
    }

    /**
     * Additional setter for primitive type.
     * Set attribute "weightingOfMeanValue". 
     * @param newWeightingOfMeanValue new value for attribute "weightingOfMeanValue".
     */
    public void setWeightingOfMeanValue( double newWeightingOfMeanValue )
    {
       this.weightingOfMeanValue = newWeightingOfMeanValue;
    }



    // ----- members for attribute "supplierId" -----
    public static final String attributeSupplierId = "supplierId";

    /**
     * State for attribute "supplierId". 
    * The supplier id received from LaborReceiverService.
    * The second part of attribue 'sampleno' separated by first character '-'.
     */
    @Column( name = "SUPPLIERID" )
    private String supplierId;

    /**
     * Get attribute "supplierId". 
    * The supplier id received from LaborReceiverService.
    * The second part of attribue 'sampleno' separated by first character '-'.
     * @return value of attribute "supplierId".
     */
    public String getSupplierId()
    {
        return supplierId;
    }

    /**
     * Set attribute "supplierId". 
    * The supplier id received from LaborReceiverService.
    * The second part of attribue 'sampleno' separated by first character '-'.
     * @param newSupplierId new value for attribute "supplierId".
     */
    public void setSupplierId( String newSupplierId )
    {
        this.supplierId = newSupplierId;
    }



    // ----- members for attribute "isCaptured" -----
    public static final String attributeIsCaptured = "isCaptured";

    /**
     * State for attribute "isCaptured". 
     */
    @Column( name = "ISCAPTURED" )
    private Boolean isCaptured = false;

    /**
     * Get attribute "isCaptured". 
     * @return value of attribute "isCaptured".
     */
    public boolean getIsCaptured()
    {
        return PrimitiveAttribute.nullSafeGet( isCaptured, false );
    }

    /**
     * Set attribute "isCaptured". 
     * @param newIsCaptured new value for attribute "isCaptured".
     */
    public void setIsCaptured( Boolean newIsCaptured )
    {
        this.isCaptured = PrimitiveAttribute.nullSafeGet( newIsCaptured, false );
    }

    /**
     * Additional setter for primitive type.
     * Set attribute "isCaptured". 
     * @param newIsCaptured new value for attribute "isCaptured".
     */
    public void setIsCaptured( boolean newIsCaptured )
    {
       this.isCaptured = newIsCaptured;
    }



    // ----- members for attribute "averageContributors" -----
    public static final String attributeAverageContributors = "averageContributors";

    /**
     * State for attribute "averageContributors". 
    * Anzahl Analysen, die für die Mittelwertbildung verwendet wurden.
     */
    @Column( name = "AVERAGECONTRIBUTORS" )
    private Integer averageContributors = 0;

    /**
     * Get attribute "averageContributors". 
    * Anzahl Analysen, die für die Mittelwertbildung verwendet wurden.
     * @return value of attribute "averageContributors".
     */
    public int getAverageContributors()
    {
        return PrimitiveAttribute.nullSafeGet( averageContributors, 0 );
    }

    /**
     * Set attribute "averageContributors". 
    * Anzahl Analysen, die für die Mittelwertbildung verwendet wurden.
     * @param newAverageContributors new value for attribute "averageContributors".
     */
    public void setAverageContributors( Integer newAverageContributors )
    {
        this.averageContributors = PrimitiveAttribute.nullSafeGet( newAverageContributors, 0 );
    }

    /**
     * Additional setter for primitive type.
     * Set attribute "averageContributors". 
    * Anzahl Analysen, die für die Mittelwertbildung verwendet wurden.
     * @param newAverageContributors new value for attribute "averageContributors".
     */
    public void setAverageContributors( int newAverageContributors )
    {
       this.averageContributors = newAverageContributors;
    }


    // ----- members for bidirectional many-to-many association "AnalysisElements" -----

    /**
     * State for association "AnalysisElements".
     * We are on the left-side ("Analysis") of an unordered many-to-many association.
     * This field holds a Set of reference to the associated "Elements" instances.
     * It is subject to lazy initialization.
     */
    @ManyToMany
    @JoinTable( name = "ANALYSIS_ELEMENTS",
        joinColumns = @JoinColumn( name = "ANALYSISOID" ),
        inverseJoinColumns = @JoinColumn( name = "ELEMENTSOID" ) )
    private Set<AnalysisElement> elements;

    /**
     * Obtain set of "Elements" instances.
     * @return a set holding associated "Elements" instances
     */
    public Set<AnalysisElement> getElements()
    {
        // lazy evaluation; the field might still be null
        if( elements == null )
        {
           elements = new HashSet<AnalysisElement>();
        }
        return elements;
    }

    /**
     * Set attribute "elements".
     *
     * @param newElements 
     *             new value for attribute "elements"
     */
    @SuppressWarnings( "unused" ) // only for ERM access.
    private void setElements( final Set<AnalysisElement> newElements )
    {
        this.elements = newElements;
    }

    /**
     * Check if some "Elements" instance is contained in the
     * association "AnalysisElements".
     * @param analysisElement "Elements" instance to check
     * @return true if instance is member of the association
     */
    public boolean containsInElements( AnalysisElement analysisElement )
    {
        return this.elements != null && this.elements.contains( analysisElement );
    }

    /**
     * Number of associated "Elements" instances in
     * association "AnalysisElements".
     * @return number of associated instances
     */
    public int numberOfElements()
    {
        // handle uninitialized set, but do not initialize it now
        return elements == null ? 0 : elements.size();
    }

    /**
     * Flush association "AnalysisElements" by removing all
     * instances of "Elements". Instances on the other
     * side are updated correctly.
     */
    public void flushElements()
    {
       if( elements != null )
       {
          // avoid ConcurrentModificationException
          for( AnalysisElement c : new ArrayList<AnalysisElement>( elements ))
          {
             // a list may contain null values
             if( c != null )
             {
                removeFromElements( c );
             }
          }
          elements = null;
       }
    }

    /**
     * Add a "Elements" instance to the "AnalysisElements" association.
     * Will update the other side of the association.
     * @param analysisElement the instance to add
     */
    public void addToElements( AnalysisElement analysisElement )
    {
        getElements().add( analysisElement );
        analysisElement.oneSided_addToAnalysis( this );
    }

    /**
     * Remove a "Elements" instance from the "AnalysisElements" association.
     * Will update the other side of the association.
     * @param analysisElement the instance to add
     */
    public void removeFromElements( AnalysisElement analysisElement )
    {
        getElements().remove( analysisElement );
        analysisElement.oneSided_removeFromAnalysis( this );
    }

    /**
     * Add a "Elements" instance to the "AnalysisElements" association.
     * Will not update the other side of the association.
     * @param analysisElement the instance to add
     */
    public void oneSided_addToElements( AnalysisElement analysisElement )
    {
        getElements().add( analysisElement );
    }

    /**
     * Remove a "Elements" instance from the "AnalysisElements" association.
     * Will not update the other side of the association.
     * @param analysisElement the instance to add
     */
    public void oneSided_removeFromElements( AnalysisElement analysisElement )
    {
        getElements().remove( analysisElement );
    }


    // ----- members for bidirectional many-to-one association "MaterialAnalysis" -----

    /**
     * State for association "MaterialAnalysis".
     * We are on the many-side ("Analysis") of an unordered one-to-many association.
     * This field holds a reference to the associated "Material" instance.
     */
    @ManyToOne( optional = false )
    @JoinColumn( name = "MATERIALOID" )
    private HHCMetalMaterial material;


    /**
     * Obtain reference to "Material" instance.
     * @return associated "Material" instance
     *   or null if not associated
     */
    public HHCMetalMaterial getMaterial()
    {
        return material;
    }

    /**
     * Set reference to "Material" instance.
     * Automatic update of the other sides references.
     * @param newMaterial "Material" instance to associate
     *   or null to delete association
     */
    public void setMaterial( HHCMetalMaterial newMaterial )
    {
        HHCMetalMaterial oldMaterial = this.material;
        this.material = newMaterial;
        if( oldMaterial != newMaterial )
        {
           if( oldMaterial != null )
           {
              oldMaterial.oneSided_removeFromAnalysis( this );
           }
           if( newMaterial != null )
           {
              newMaterial.oneSided_addToAnalysis( this );
           }
        }
    }

    /**
     * Set reference to "Material" instance.
     * No automatic update of the other sides references.
     * @param newMaterial "Material" instance to associate
     *   or null to delete association
     */
    public void oneSided_setMaterial( HHCMetalMaterial newMaterial )
    {
        this.material = newMaterial;
    }


    // ----- members for bidirectional many-to-one association "Assoc_analysis_batch" -----

    /**
     * State for association "Assoc_analysis_batch".
     * We are on the many-side ("Analysis") of an unordered one-to-many association.
     * This field holds a reference to the associated "Batch" instance.
     */
    @ManyToOne( optional = false )
    @JoinColumn( name = "BATCHOID" )
    private HHCBatch batch;


    /**
     * Obtain reference to "Batch" instance.
     * @return associated "Batch" instance
     *   or null if not associated
     */
    public HHCBatch getBatch()
    {
        return batch;
    }

    /**
     * Set reference to "Batch" instance.
     * Automatic update of the other sides references.
     * @param newBatch "Batch" instance to associate
     *   or null to delete association
     */
    public void setBatch( HHCBatch newBatch )
    {
        HHCBatch oldBatch = this.batch;
        this.batch = newBatch;
        if( oldBatch != newBatch )
        {
           if( oldBatch != null )
           {
              oldBatch.oneSided_removeFromAnalysis( this );
           }
           if( newBatch != null )
           {
              newBatch.oneSided_addToAnalysis( this );
           }
        }
    }

    /**
     * Set reference to "Batch" instance.
     * No automatic update of the other sides references.
     * @param newBatch "Batch" instance to associate
     *   or null to delete association
     */
    public void oneSided_setBatch( HHCBatch newBatch )
    {
        this.batch = newBatch;
    }


    // ----- members for bidirectional many-to-many association "Assoc_capturedMaterial_inputAnalysis" -----

    /**
     * State for association "Assoc_capturedMaterial_inputAnalysis".
     * We are on the left-side ("InputAnalysis") of an unordered many-to-many association.
     * This field holds a Set of reference to the associated "CapturedMaterial" instances.
     * It is subject to lazy initialization.
     */
    @ManyToMany( mappedBy = "inputAnalysis" )
    private Set<HHCMetalMaterial> capturedMaterial;

    /**
     * Obtain set of "CapturedMaterial" instances.
     * @return a set holding associated "CapturedMaterial" instances
     */
    public Set<HHCMetalMaterial> getCapturedMaterial()
    {
        // lazy evaluation; the field might still be null
        if( capturedMaterial == null )
        {
           capturedMaterial = new HashSet<HHCMetalMaterial>();
        }
        return capturedMaterial;
    }

    /**
     * Set attribute "capturedMaterial".
     *
     * @param newCapturedMaterial 
     *             new value for attribute "capturedMaterial"
     */
    @SuppressWarnings( "unused" ) // only for ERM access.
    private void setCapturedMaterial( final Set<HHCMetalMaterial> newCapturedMaterial )
    {
        this.capturedMaterial = newCapturedMaterial;
    }

    /**
     * Check if some "CapturedMaterial" instance is contained in the
     * association "Assoc_capturedMaterial_inputAnalysis".
     * @param hHCMetalMaterial "CapturedMaterial" instance to check
     * @return true if instance is member of the association
     */
    public boolean containsInCapturedMaterial( HHCMetalMaterial hHCMetalMaterial )
    {
        return this.capturedMaterial != null && this.capturedMaterial.contains( hHCMetalMaterial );
    }

    /**
     * Number of associated "CapturedMaterial" instances in
     * association "Assoc_capturedMaterial_inputAnalysis".
     * @return number of associated instances
     */
    public int numberOfCapturedMaterial()
    {
        // handle uninitialized set, but do not initialize it now
        return capturedMaterial == null ? 0 : capturedMaterial.size();
    }

    /**
     * Flush association "Assoc_capturedMaterial_inputAnalysis" by removing all
     * instances of "CapturedMaterial". Instances on the other
     * side are updated correctly.
     */
    public void flushCapturedMaterial()
    {
       if( capturedMaterial != null )
       {
          // avoid ConcurrentModificationException
          for( HHCMetalMaterial c : new ArrayList<HHCMetalMaterial>( capturedMaterial ))
          {
             // a list may contain null values
             if( c != null )
             {
                removeFromCapturedMaterial( c );
             }
          }
          capturedMaterial = null;
       }
    }

    /**
     * Add a "CapturedMaterial" instance to the "Assoc_capturedMaterial_inputAnalysis" association.
     * Will update the other side of the association.
     * @param hHCMetalMaterial the instance to add
     */
    public void addToCapturedMaterial( HHCMetalMaterial hHCMetalMaterial )
    {
        getCapturedMaterial().add( hHCMetalMaterial );
        hHCMetalMaterial.oneSided_addToInputAnalysis( this );
    }

    /**
     * Remove a "CapturedMaterial" instance from the "Assoc_capturedMaterial_inputAnalysis" association.
     * Will update the other side of the association.
     * @param hHCMetalMaterial the instance to add
     */
    public void removeFromCapturedMaterial( HHCMetalMaterial hHCMetalMaterial )
    {
        getCapturedMaterial().remove( hHCMetalMaterial );
        hHCMetalMaterial.oneSided_removeFromInputAnalysis( this );
    }

    /**
     * Add a "CapturedMaterial" instance to the "Assoc_capturedMaterial_inputAnalysis" association.
     * Will not update the other side of the association.
     * @param hHCMetalMaterial the instance to add
     */
    public void oneSided_addToCapturedMaterial( HHCMetalMaterial hHCMetalMaterial )
    {
        getCapturedMaterial().add( hHCMetalMaterial );
    }

    /**
     * Remove a "CapturedMaterial" instance from the "Assoc_capturedMaterial_inputAnalysis" association.
     * Will not update the other side of the association.
     * @param hHCMetalMaterial the instance to add
     */
    public void oneSided_removeFromCapturedMaterial( HHCMetalMaterial hHCMetalMaterial )
    {
        getCapturedMaterial().remove( hHCMetalMaterial );
    }


    // ----- members for bidirectional one-to-many association "AnalysisHistories" -----

    /**
     * State for association "AnalysisHistories".
     * We are on the one-side of an unordered one-to-many association.
     * This field holds a Set of reference to the associated "Histories" instances.
     * It is subject to lazy initialization.
     */
    @OneToMany( mappedBy = "analysis" )
    private Set<AnalysisHistory> histories;

    /**
     * Obtain set of "Histories" instances.
     *
     * @return a set holding associated "Histories" instances
     */
    public Set<AnalysisHistory> getHistories()
    {
        // lazy evaluation; the field might still be null
        if( histories == null )
        {
           histories = new HashSet<AnalysisHistory>();
        }
        return histories;
    }

    /**
     * Set attribute "histories".
     *
     * @param newHistories 
     *             new value for attribute "histories"
     */
    @SuppressWarnings( "unused" ) // only for ERM access.
    private void setHistories( final Set<AnalysisHistory> newHistories )
    {
        this.histories = newHistories;
    }
    
    /**
     * Check if some "Histories" instance is contained in the
     * association "AnalysisHistories".
     * @param analysisHistory "Histories" instance to check
     * @return true iff instance is member of the association
     */
    public boolean containsInHistories( final AnalysisHistory analysisHistory )
    {
        return this.histories != null && this.histories.contains( analysisHistory );
    }

    /**
     * Number of associated "Histories" instances in
     * association "AnalysisHistories".
     * @return number of associated instances
     */
    public int numberOfHistories()
    {
        // handle uninitialized set, but do not initialize it now
        return histories == null ? 0 : histories.size();
    }

    /**
     * Flush association "AnalysisHistories" by removing all
     * instances of "Histories". Instances on the other
     * side are updated correctly.
     */
    public void flushHistories()
    {
       if( histories != null )
       {
          // avoid ConcurrentModificationException
          for( AnalysisHistory c : new ArrayList<AnalysisHistory>( histories ))
          {
             // a list may contain null values
             if( c != null )
             {
                c.oneSided_setAnalysis( null );
             }
          }
 	      histories.clear();
          histories = null;
       }
    }

    /**
     * Add a "Histories" instance to the "AnalysisHistories" association.
     * Will update the other side of the association.
     * @param analysisHistory the instance to add
     */
    public void addToHistories( final AnalysisHistory analysisHistory )
    {
        getHistories().add( analysisHistory );
        Analysis oldAnalysis = analysisHistory.getAnalysis();
        if( oldAnalysis != this && oldAnalysis != null )
        {
           oldAnalysis.oneSided_removeFromHistories( analysisHistory );
        }
        analysisHistory.oneSided_setAnalysis( this );
    }

    /**
     * Remove a "Histories" instance from the "AnalysisHistories" association.
     * Will update the other side of the association.
     * @param analysisHistory the instance to add
     */
    public void removeFromHistories( final AnalysisHistory analysisHistory )
    {
        getHistories().remove( analysisHistory );
        analysisHistory.oneSided_setAnalysis( null );
    }

    /**
     * Add a "Histories" instance to the "AnalysisHistories" association.
     * Will not update the other side of the association.
     * @param analysisHistory the instance to add
     */
    public void oneSided_addToHistories( AnalysisHistory analysisHistory )
    {
        getHistories().add( analysisHistory );
    }

    /**
     * Remove a "Histories" instance from the "AnalysisHistories" association.
     * Will not update the other side of the association.
     * @param analysisHistory the instance to add
     */
    public void oneSided_removeFromHistories( AnalysisHistory analysisHistory )
    {
        getHistories().remove( analysisHistory );
    }


    // ----- lifecycle support -----

    @Override
    public void removeAllAggregations()
    {
        super.removeAllAggregations();
        flushElements();
        setMaterial( null );
        setBatch( null );
        flushCapturedMaterial();
        flushHistories();
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
        attrMap.put( "name", name );
        attrMap.put( "activationtime", activationtime );
        attrMap.put( "active", active );
        attrMap.put( "analysisNo", analysisNo );
        attrMap.put( "chargeID", chargeID );
        attrMap.put( "analysisOK", analysisOK );
        attrMap.put( "instrumentType", instrumentType );
        attrMap.put( "probenArt", probenArt );
        attrMap.put( "probenNr", probenNr );
        attrMap.put( "produkt", produkt );
        attrMap.put( "programName", programName );
        attrMap.put( "evaluationType", evaluationType );
        attrMap.put( "isMasked", isMasked );
        attrMap.put( "maskReason", maskReason );
        attrMap.put( "deliveryNr", deliveryNr );
        attrMap.put( "weightingOfMeanValue", weightingOfMeanValue );
        attrMap.put( "supplierId", supplierId );
        attrMap.put( "isCaptured", isCaptured );
        attrMap.put( "averageContributors", averageContributors );
    }
}

