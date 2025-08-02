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
import java.util.Set;

import javax.persistence.*;

@Entity(access = AccessType.PROPERTY)
@Table(name = "rights_groups")
public class Group implements Serializable {
    private static final long serialVersionUID = 6426211378636499343L;

    private int id;

    private String name;

    private Set<User> users;

    @Id(generate = GeneratorType.AUTO)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToMany(mappedBy = "groups")
    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

}