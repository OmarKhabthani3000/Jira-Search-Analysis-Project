/**
 * Ticket2Rock ist die Beispielanwendung des Buchs "EJB 3.1 professionell"
 * (dpunkt). Es implementiert eine einfache Webanwendung zur Onlinebuchung von
 * Tickets für Rockkonzerten.
 *
 * Copyright (C) 2006-2011 Holisticon AG
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package de.ejb3buch.ticket2rock.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

import de.ejb3buch.ticket2rock.entity.primarykey.VeranstalterPK;
import jakarta.persistence.Basic;
import jakarta.persistence.Embedded;
import java.io.Serializable;

/**
 * Ein Veranstalter organisiert Konzerte.
 */
@Entity
public class Veranstalter implements Serializable {

    private static final long serialVersionUID = -3926155354962395307L;

    private String name;

    @EmbeddedId
    @Basic
    @AttributeOverrides({
        @AttributeOverride(name = "handelsregisternummer", column = @Column(name = "HR_NR")),
        @AttributeOverride(name = "amtsgericht", column = @Column(name = "AMTSGERICHT"))})
    private VeranstalterPK pk;

    public VeranstalterPK getPk() {
        return pk;
    }

    public void setPk(VeranstalterPK pk) {
        this.pk = pk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
