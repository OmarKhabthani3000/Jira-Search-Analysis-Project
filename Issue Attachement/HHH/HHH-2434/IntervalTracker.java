/*
 * ========================================================================
 *
 * Copyright (c) 2007 Unpublished Work of Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS AN UNPUBLISHED WORK AND CONTAINS CONFIDENTIAL,
 * PROPRIETARY AND TRADE SECRET INFORMATION OF NOVELL, INC. ACCESS TO
 * THIS WORK IS RESTRICTED TO (I) NOVELL, INC. EMPLOYEES WHO HAVE A NEED
 * TO KNOW HOW TO PERFORM TASKS WITHIN THE SCOPE OF THEIR ASSIGNMENTS AND
 * (II) ENTITIES OTHER THAN NOVELL, INC. WHO HAVE ENTERED INTO
 * APPROPRIATE LICENSE AGREEMENTS. NO PART OF THIS WORK MAY BE USED,
 * PRACTICED, PERFORMED, COPIED, DISTRIBUTED, REVISED, MODIFIED,
 * TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED, COMPILED,
 * LINKED, RECAST, TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN
 * CONSENT OF NOVELL, INC. ANY USE OR EXPLOITATION OF THIS WORK WITHOUT
 * AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL
 * LIABILITY.
 *
 * ========================================================================
 */

package com.novell.soa.persist;

import java.util.Calendar;
import java.util.Date;

/**
 * This object will be used to test various Hibernate issues.
 * <p/>
 * Issue 1: calculating date differences, and querying date differences
 */
public class IntervalTracker
{
    private long m_id;
    private Calendar m_startCalendar;
    private Calendar m_endCalendar;
    private Date m_startDate;
    private Date m_endDate;


    /** Constructs a new IntervalTracker. */
    private IntervalTracker()
    {
    }

    /**
     * Constructs a new IntervalTracker with values.
     *
     * @param startCalendar starting time (as a Calendar)
     * @param endCalendar   ending time
     */
    public IntervalTracker(final Calendar startCalendar, final Calendar endCalendar)
    {
        this.m_startCalendar = startCalendar;
        this.m_endCalendar = endCalendar;
        this.m_startDate = startCalendar.getTime();
        this.m_endDate = endCalendar.getTime();
    }

    /**
     * Getter for property 'startCalendar'.
     *
     * @return Value for property 'startCalendar'.
     */
    public Calendar getStartCalendar()
    {
        return m_startCalendar;
    }

    /**
     * Setter for property 'startCalendar'.
     *
     * @param startCalendar Value to set for property 'startCalendar'.
     */
    private void setStartCalendar(final Calendar startCalendar)
    {
        m_startCalendar = startCalendar;
    }

    /**
     * Getter for property 'endCalendar'.
     *
     * @return Value for property 'endCalendar'.
     */
    public Calendar getEndCalendar()
    {
        return m_endCalendar;
    }

    /**
     * Setter for property 'endCalendar'.
     *
     * @param endCalendar Value to set for property 'endCalendar'.
     */
    private void setEndCalendar(final Calendar endCalendar)
    {
        m_endCalendar = endCalendar;
    }

    /**
     * Getter for property 'startDate'.
     *
     * @return Value for property 'startDate'.
     */
    public Date getStartDate()
    {
        return m_startDate;
    }

    /**
     * Setter for property 'startDate'.
     *
     * @param startDate Value to set for property 'startDate'.
     */
    private void setStartDate(final Date startDate)
    {
        m_startDate = startDate;
    }

    /**
     * Getter for property 'endDate'.
     *
     * @return Value for property 'endDate'.
     */
    public Date getEndDate()
    {
        return m_endDate;
    }

    /**
     * Setter for property 'endDate'.
     *
     * @param endDate Value to set for property 'endDate'.
     */
    private void setEndDate(final Date endDate)
    {
        m_endDate = endDate;
    }

    /**
     * Getter for property 'm_id'.
     *
     * @return Value for property 'm_id'.
     */
    public long getId()
    {
        return m_id;
    }

    /**
     * Setter for property 'm_id'.
     *
     * @param id Value to set for property 'm_id'.
     */
    public void setId(final long id)
    {
        this.m_id = id;
    }

    /** {@inheritDoc} */
    public boolean equals(final Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        IntervalTracker that = (IntervalTracker) o;

        if (m_id != that.m_id) {
            return false;
        }
        if (!m_endCalendar.equals(that.m_endCalendar)) {
            return false;
        }
        if (!m_endDate.equals(that.m_endDate)) {
            return false;
        }
        if (!m_startCalendar.equals(that.m_startCalendar)) {
            return false;
        }
        if (!m_startDate.equals(that.m_startDate)) {
            return false;
        }

        return true;
    }

    /** {@inheritDoc} */
    public int hashCode()
    {
        int result;
        result = (int) (m_id ^ (m_id >>> 32));
        result = 31 * result + m_startCalendar.hashCode();
        result = 31 * result + m_endCalendar.hashCode();
        result = 31 * result + m_startDate.hashCode();
        result = 31 * result + m_endDate.hashCode();
        return result;
    }
}
