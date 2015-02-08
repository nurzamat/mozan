package trade.mozan.model;

/**
 * Created by nurzamat on 1/20/15.
 */
public class Category
{
    private String id;
    private String name;
    private String parent = null;

    public Category()
    {
    }

    public Category(String _id, String _name, String _parent)
    {
        this.id = _id;
        this.name = _name;
        this.parent = _parent;
    }
    public String getId() {
        return id;
    }

    public void setId(String _id) {
        this.id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String _name) {
        this.name = _name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String _parent) {
        this.parent = _parent;
    }
}
