package test;

public class UUIDThing
{
    private String id;
    private Thing thing;
        
    public String getId()
    {
        return id;
    }
    public void setId(String id)
    {
        this.id=id;
    }
    public Thing getThing()
    {
        return thing;
    }
    public void setThing(Thing otherThing)
    {
        this.thing=otherThing;
    }
}
