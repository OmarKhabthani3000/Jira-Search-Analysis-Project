/**
 * Created by User: Sharenkov
 * Date: 01.10.2003
 * Time: 11:09:24
 */
package com.enterra.surv.po;

import com.enterra.surv.po.enum.Language;
import com.enterra.surv.po.enum.UserState;
import com.enterra.surv.po.enum.UserType;
import com.enterra.surv.security.OwnedObject;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 *
 * @author Konstantin Sharenkov
 */
public class User extends Persistent implements OwnedObject
{
    private String name;
    private String mail;
    private String password;
    private UserType type;
    private UserState state;
    private Language language;
    private List surveys;

    public User()
    {
        super();
        setPassword( "" );
        setType( UserType.Respondent );
        setState( UserState.Active );
        setLanguage( Language.English );
    }

    public String getMail()
    {
        return mail;
    }

    public void setMail( String mail )
    {
        if( mail==null )
            this.mail = null;
        else
            this.mail = Helper.normalizeStringField(mail.toLowerCase());
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = Helper.normalizeStringField(name);
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

    public UserState getState()
    {
        return state;
    }

    public void setState( UserState state )
    {
        this.state = state;
    }

    public UserType getType()
    {
        return type;
    }

    public void setType( UserType type )
    {
        this.type = type;
    }

    public Language getLanguage()
    {
        return language;
    }

    public void setLanguage( Language language )
    {
        this.language = language;
    }

    /**
     * Operations with surveys
     */
    public List getSurveys()
    {
        if( surveys == null )
            surveys = new ArrayList();
        return surveys;
    }

    void setSurveys( List surveys )
    {
        this.surveys = surveys;
    }

    public void addSurvey( Survey survey )
    {
        List surveys = getSurveys();
        survey.setOwner( this );
        surveys.add( survey );
    }

    public void removeSurevy( Survey survey )
    {
        List surveys = getSurveys();
        if( !surveys.contains( survey ) ) return;

        survey.setArchived( true );
        surveys.remove( survey );
    }

    public void remove()
    {
        setArchived( true );
    }

    public User getOwner()
    {
        return this;
    }
}
