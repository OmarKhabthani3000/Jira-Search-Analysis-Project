package hibernatebug;

import java.util.*;

public class Entity
{
    Long id;
    int version;
    Map<String,Object> key;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Map<String, Object> getKey() {
        return key;
    }

    public void setKey(Map<String, Object> key) {
        this.key = key;
    }
}
