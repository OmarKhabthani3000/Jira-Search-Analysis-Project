import java.util.HashSet;
import java.util.Set;

/**
 * Class description.
 *
 * @author jcattell
 */
public class SimpleObject {

    private Long _id;
    private String _name;
    private Set<String> _tags = new HashSet<String>();

    public SimpleObject() {
    }

    public SimpleObject(final String name) {
        this._name = name;
    }

    public Long getId() {
        return _id;
    }

    public void setId(final Long id) {
        this._id = id;
    }

    public String getName() {
        return _name;
    }

    public void setName(final String name) {
        this._name = name;
    }

    public Set<String> getTags() {
        return _tags;
    }

    public void setTags(final Set<String> tags) {
        this._tags = tags;
    }

    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final SimpleObject that = (SimpleObject) o;

        if (!_name.equals(that._name)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = _name.hashCode();
        return result;
    }

    public String toString() {
        return "SimpleObject{" + "_id=" + _id + ", _name='" + _name + '\'' + ", _tags=" +
                _tags + '}';
    }
}
