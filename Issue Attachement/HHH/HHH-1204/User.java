/*
 * Copyright 2001-2005 Fizteh-Center Lab., MIPT, Russia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created on 25.11.2005
 */
package ru.arptek.arpsite.data.usergroup;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

@Entity(access = AccessType.PROPERTY)
@Table(name = "rights_users")
@NamedQueries(@NamedQuery(name = "User.findByGroupRealm", queryString = "SELECT users "
        + "FROM User users "
        + "WHERE ? IN (users.groups) AND users.realm=?"))
public class User implements Serializable {
    private static final long serialVersionUID = -4521371190428882063L;

    private Set<Group> groups = new HashSet<Group>();

    private int id = -1;

    private String name;

    private int realm;

    @ManyToMany
    @JoinTable(table = @Table(name = "rights_user2group"), joinColumns = @JoinColumn(name = "userid"), inverseJoinColumns = @JoinColumn(name = "grp"))
    public Set<Group> getGroups() {
        return groups;
    }

    @Id(generate = GeneratorType.AUTO)
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getRealm() {
        return realm;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRealm(int realm) {
        this.realm = realm;
    }
}