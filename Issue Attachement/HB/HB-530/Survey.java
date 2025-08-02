/**
 * Created by User: Sharenkov
 * Date: 01.10.2003
 * Time: 12:43:00
 */
package com.enterra.surv.po;

import com.enterra.surv.RootException;
import com.enterra.surv.po.enum.Language;
import com.enterra.surv.po.enum.ProtectionType;
import com.enterra.surv.po.enum.ResourceClass;
import com.enterra.surv.po.enum.ResourceType;
import com.enterra.surv.po.enum.SurveyState;
import com.enterra.surv.po.enum.SurveyType;
import com.enterra.surv.po.resource.SurveyTitleResourceCollectionImpl;
import com.enterra.surv.po.resource.ThankYouTextResourceCollectionImpl;
import com.enterra.surv.po.resource.WelcomeTextResourceCollectionImpl;
import com.enterra.surv.resource.ResourceCollection;
import com.enterra.surv.resource.TextResourceDelegator;
import com.enterra.surv.security.OwnedObject;
import com.enterra.surv.system.SessionContext;
import com.enterra.surv.system.TimeConvertor;
import net.sf.hibernate.HibernateException;

import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author Konstantin Sharenkov
 */
public class Survey extends Persistent implements OwnedObject
{
    private String name;

    private User owner;
    private Style style;

    private String description;
    private SurveyState state;
    private SurveyType type;

    private boolean showWelcomePage;
    private boolean showFinishPage;
    private boolean allowBackNavigation;
    private boolean library;

    private Timestamp launchMoment;
    private Timestamp finishMoment;

    private Timestamp created;
    private Timestamp modified;

    private List pages;
    private List interviews;
    private List reports;
    private List invitees;

    private Map titleResources;
    private Map welcomeTextResources;
    private Map thankYouTextResources;

    private TextResourceDelegator titleResourceDelegator;
    private TextResourceDelegator welcomeTextResourceDelegator;
    private TextResourceDelegator thankYouTextResourceDelegator;

    private WelcomeTextResourceCollectionImpl welcomeTextResourceCollection;
    private ThankYouTextResourceCollectionImpl thankYouTextResourceCollection;
    private SurveyTitleResourceCollectionImpl surveyTitleResourceCollection;

    private boolean enableIPProtection;
    private boolean enableCookieProtection;
    private boolean enableAuthProtection;
    private boolean enableTimeProtection;
    private ProtectionType protectionType;

    public Survey()
    {
        super();
        setState( SurveyState.New );

        Timestamp now = TimeConvertor.getInstance().getCurrentUTCTime();
        setCreated( now );
        setModified( now );

        setDescription( "" );
        setType( SurveyType.Public );

        setShowWelcomePage( true );
        setShowFinishPage( true );
        setAllowBackNavigation( true );

        setEnableAuthProtection( false );
        setEnableCookieProtection( false );
        setEnableIPProtection( false );
        setEnableTimeProtection( false );
        setProtectionType( ProtectionType.Prevent );
    }

    public boolean isAllowBackNavigation()
    {
        return allowBackNavigation;
    }

    public void setAllowBackNavigation( boolean allowBackNavigation )
    {
        this.allowBackNavigation = allowBackNavigation;
    }

    public Timestamp getCreated()
    {
        return created;
    }

    public void setCreated( Timestamp created )
    {
        this.created = created;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = Helper.normalizeStringField( description );
    }

    public Timestamp getFinishMoment()
    {
        return finishMoment;
    }

    public void setFinishMoment( Timestamp finishMoment )
    {
        this.finishMoment = finishMoment;
    }

    public Timestamp getLaunchMoment()
    {
        return launchMoment;
    }

    public void setLaunchMoment( Timestamp launchMoment )
    {
        this.launchMoment = launchMoment;
    }

    public Timestamp getModified()
    {
        return modified;
    }

    public void setModified( Timestamp modified )
    {
        this.modified = modified;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = Helper.normalizeStringField( name );
    }

    public User getOwner()
    {
        return owner;
    }

    void setOwner( User owner )
    {
        this.owner = owner;
    }

    public boolean isShowFinishPage()
    {
        return showFinishPage;
    }

    public void setShowFinishPage( boolean showFinishPage )
    {
        this.showFinishPage = showFinishPage;
    }

    public boolean isShowWelcomePage()
    {
        return showWelcomePage;
    }

    public void setShowWelcomePage( boolean showWelcomePage )
    {
        this.showWelcomePage = showWelcomePage;
    }

    public SurveyState getState()
    {
        return state;
    }

    public void setState( SurveyState state )
    {
        this.state = state;
    }

// begin: title
    public String getTitle( Language language ) throws HibernateException
    {
        Resource titleResource = getTitleResourceDelegator().getResource( language );
        if( titleResource == null )
            return null;
        return titleResource.getText();
    }

    public String getTitle() throws HibernateException
    {
        SessionContext sctx = SessionContext.getInstance();
        return getTitle( sctx.getLanguage() );
    }

    public void setTitle( String title ) throws HibernateException
    {
        if( title == null )
            return;
        SessionContext sctx = SessionContext.getInstance();
        getTitleResourceDelegator().setText( sctx.getLanguage(), title );
    }
// end: end title

// begin: welcomeText
    public String getWelcomeText( Language language )
    {
        Resource welcomeTextResource = getWelcomeTextResourceDelegator().getResource( language );
        if( welcomeTextResource == null )
            return null;
        return welcomeTextResource.getText();
    }

    public String getWelcomeText()
    {
        SessionContext sctx = SessionContext.getInstance();
        return getWelcomeText( sctx.getLanguage() );
    }

    public void setWelcomeText( String welcomeText )
    {
        if( welcomeText == null )
            return;
        SessionContext sctx = SessionContext.getInstance();
        getWelcomeTextResourceDelegator().setText( sctx.getLanguage(), welcomeText );
    }

    private Map getWelcomeTextResources()
    {
        if( welcomeTextResources == null )
        {
            welcomeTextResources = getWelcomeTextResourceDelegator().getResources();
        }
        return welcomeTextResources;
    }


    private void setWelcomeTextResources( Map resources )
    {
        welcomeTextResources = resources;
    }

// end: welcomeText

// begin: thank you
    public String getThankYouText( Language language ) throws HibernateException
    {
        Resource thankYouResource = getThankYouResourceDelegator().getResource( language );
        if( thankYouResource == null )
            return null;
        return thankYouResource.getText();
    }

    public String getThankYouText() throws HibernateException
    {
        SessionContext sctx = SessionContext.getInstance();
        return getThankYouText( sctx.getLanguage() );
    }

    public void setThankYouText( String thankYouText ) throws HibernateException
    {
        if( thankYouText == null )
            return;
        SessionContext sctx = SessionContext.getInstance();
        getThankYouResourceDelegator().setText( sctx.getLanguage(), thankYouText );
    }
// end: end thank you


    private Map getThankYouTextResources()
    {
        if( thankYouTextResources == null )
        {
            thankYouTextResources = getThankYouResourceDelegator().getResources();
        }
        return thankYouTextResources;
    }

    private void setThankYouTextResources( Map resources )
    {
        thankYouTextResources = resources;
    }


    public TextResourceDelegator getWelcomeTextResourceDelegator()
    {
        if( welcomeTextResourceDelegator == null )
            welcomeTextResourceDelegator = new TextResourceDelegator( welcomeTextResources, ResourceClass.SurveyWelcome, getId() );
        return welcomeTextResourceDelegator;
    }

    public TextResourceDelegator getThankYouResourceDelegator()
    {
        if( thankYouTextResourceDelegator == null )
            thankYouTextResourceDelegator = new TextResourceDelegator( thankYouTextResources, ResourceClass.SurveyThankYou, getId() );
        return thankYouTextResourceDelegator;
    }

    public TextResourceDelegator getTitleResourceDelegator()
    {
        if( titleResourceDelegator == null )
            titleResourceDelegator = new TextResourceDelegator( titleResources, ResourceClass.SurveyTitle, getId() );
        return titleResourceDelegator;
    }

    public SurveyType getType()
    {
        return type;
    }

    public void setType( SurveyType type )
    {
        this.type = type;
    }

    public boolean isLibrary()
    {
        return library;
    }

    public void setLibrary( boolean library )
    {
        this.library = library;
    }

    public Style getStyle()
    {
        return style;
    }

    public void setStyle( Style style )
    {
        this.style = style;
    }

    private Map getTitleResources()
    {
        if( titleResources == null )
        {
            titleResources = getTitleResourceDelegator().getResources();
        }
        return titleResources;
    }

    private void setTitleResources( Map resources )
    {
        titleResources = resources;
    }

    public boolean isEnableIPProtection()
    {
        return enableIPProtection;
    }

    public void setEnableIPProtection( boolean enableIPProtection )
    {
        this.enableIPProtection = enableIPProtection;
    }

    public boolean isEnableCookieProtection()
    {
        return enableCookieProtection;
    }

    public void setEnableCookieProtection( boolean enableCookieProtection )
    {
        this.enableCookieProtection = enableCookieProtection;
    }

    public boolean isEnableAuthProtection()
    {
        return enableAuthProtection;
    }

    public void setEnableAuthProtection( boolean enableAuthProtection )
    {
        this.enableAuthProtection = enableAuthProtection;
    }

    public boolean isEnableTimeProtection()
    {
        return enableTimeProtection;
    }

    public void setEnableTimeProtection( boolean enableTimeProtection )
    {
        this.enableTimeProtection = enableTimeProtection;
    }

    public ProtectionType getProtectionType()
    {
        return protectionType;
    }

    public void setProtectionType( ProtectionType protectionType )
    {
        this.protectionType = protectionType;
    }


    /**
     * Operations with pages
     */
    public List getPages()
    {
        if( pages == null )
            pages = new ArrayList();
        return pages;
    }

    void setPages( List pages )
    {
        this.pages = pages;
    }

    public void addPage( Page page ) throws RootException
    {
        List pages = getPages();
        page.setOrder( Helper.nextOrder() );
        page.setSurvey( this );
        pages.add( page );
    }

    public void removePage( Page page )
    {
        List pages = getPages();
        if( !pages.contains( page ) ) return;

        page.setArchived( true );
        pages.remove( page );
    }

    public void movePagePrev( Page page )
    {
        List pages = getPages();
        if( !pages.contains( page ) ) return;

        int idx = pages.indexOf( page );
        if( idx > 0 )
        {
            Page other = (Page) pages.get( idx - 1 );
            other.swapOrderWith( page );
            Collections.swap( pages, idx - 1, idx );
        }
    }

    public void movePageNext( Page page )
    {
        List pages = getPages();
        if( !pages.contains( page ) ) return;

        int idx = pages.indexOf( page );
        if( idx < pages.size() - 1 )
        {
            Page other = (Page) pages.get( idx + 1 );
            other.swapOrderWith( page );
            Collections.swap( pages, idx + 1, idx );
        }
    }

    /**
     * Operations with interviews
     */
    public List getInterviews()
    {
        if( interviews == null )
            interviews = new ArrayList();
        return interviews;
    }

    void setInterviews( List interviews )
    {
        this.interviews = interviews;
    }

    public void addInterview( Interview interview )
    {
        List interviews = getInterviews();
        interview.setSurvey( this );
        interviews.add( interview );
    }

    public void removeInterview( Interview interview )
    {
        List interviews = getInterviews();
        if( !interviews.contains( interview ) ) return;

        interview.setArchived( true );
        interviews.remove( interview );
    }

    /**
     * Operations with reports
     */
    public List getReports()
    {
        if( reports == null )
            reports = new ArrayList();
        return reports;
    }

    void setReports( List reports )
    {
        this.reports = reports;
    }

    public void addReport( Report report )
    {
        List reports = getReports();
        report.setSurvey( this );
        reports.add( report );
    }

    public void removeReport( Report report )
    {
        List reports = getReports();
        if( !reports.contains( report ) ) return;

        report.setArchived( true );
        reports.remove( report );
    }

    /**
     * Operations with invitees
     */
    public List getInvitees()
    {
        if( invitees == null )
            invitees = new ArrayList();
        return invitees;
    }

    void setInvitees( List invitees )
    {
        this.invitees = invitees;
    }

    public void addInvitation( User user )
    {
        List invitees = getInvitees();
        invitees.add( user );
    }

    public void removeInvitation( User user )
    {
        List invitees = getInvitees();
        if( !invitees.contains( user ) ) return;

        invitees.remove( user );
    }

    /**
     *  Other operations
     * @return
     */
    public Survey dublicate() throws RootException, HibernateException
    {
        Survey result = new Survey();

        result.setAllowBackNavigation( isAllowBackNavigation() );
        result.setShowFinishPage( isShowFinishPage() );
        result.setShowWelcomePage( isShowWelcomePage() );

        result.setLibrary( false );

        result.setName( getName() + "(copy)" );

        result.setTitleResources( new HashMap( getTitleResources() ) );
        result.setWelcomeTextResources( new HashMap( getWelcomeTextResources() ) );
        result.setThankYouTextResources( new HashMap( getThankYouTextResources() ) );
        result.setInvitees( new ArrayList( getInvitees() ) );

        result.setDescription( getDescription() );
        result.setStyle( getStyle() );

        result.setType( getType() );
        result.setState( getState() );
        result.setCreated( TimeConvertor.getInstance().getCurrentUTCTime() );


        for( int i = 0; i < getPages().size(); i++ )
            result.addPage( ((Page) getPages().get( i )).dublicate() );

        return result;
    }

    public ResourceCollection getTitleResourceCollection()
    {
        if( surveyTitleResourceCollection == null )
            surveyTitleResourceCollection = new SurveyTitleResourceCollectionImpl( getId(), ResourceType.Text, ResourceClass.SurveyTitle );
        return surveyTitleResourceCollection;
    }

    public com.enterra.surv.resource.ResourceCollection getThankYouTextResourceCollection()
    {
        if( thankYouTextResourceCollection == null )
            thankYouTextResourceCollection = new ThankYouTextResourceCollectionImpl( getId(), ResourceType.Text, ResourceClass.SurveyThankYou );
        return thankYouTextResourceCollection;
    }

    public ResourceCollection getWelcomeTextResourceCollection()
    {
        if( welcomeTextResourceCollection == null )
            welcomeTextResourceCollection = new WelcomeTextResourceCollectionImpl( getId(), ResourceType.Text, ResourceClass.SurveyWelcome );
        return welcomeTextResourceCollection;
    }


    public void launch() throws RootException, MessagingException, IOException, HibernateException
    {
        if( getState() != SurveyState.New )
            throw new RootException( "can't launch survey '" + this + "' with state '" + getState() + "'" );
        setState( SurveyState.Active );
        setLaunchMoment( TimeConvertor.getInstance().getCurrentUTCTime()  );
    }

    public void suspend() throws RootException
    {
        if( getState() != SurveyState.Active )
            throw new RootException( "can't suspend survey '" + this + "' with state '" + getState() + "'" );

        setState( SurveyState.Suspended );
    }

    public void resume() throws RootException
    {
        if( getState() != SurveyState.Suspended )
            throw new RootException( "can't resume survey '" + this + "' with state '" + getState() + "'" );

        setState( SurveyState.Active );
    }

    public void finish() throws RootException
    {
        if( getState() != SurveyState.Active && getState() != SurveyState.Suspended )
            throw new RootException( "can't finish survey '" + this + "' with state '" + getState() + "'" );

        setState( SurveyState.Finished );
        setFinishMoment( TimeConvertor.getInstance().getCurrentUTCTime()  );
    }

    public boolean isUserInvited( User user )
    {
        return getInvitees().contains( user );
    }

    public void setModifedNow()
    {
        setModified( TimeConvertor.getInstance().getCurrentUTCTime() );
    }
}
