package test;

public class Thing
{
    private String id;
    private IntThing intThing;
    private UUIDThing UUIDThing;

    public String getId()
    {
        return id;
    }
    public void setId(String id)
    {
            this.id=id;
    }
    public IntThing getIntThing()
    {
        return intThing;
    }
    public void setIntThing(IntThing intThing)
    {
        this.intThing=intThing;
    }
    public UUIDThing getUUIDThing()
    {
        return UUIDThing;
    }
    public void setUUIDThing(UUIDThing UUIDThing)
    {
        this.UUIDThing=UUIDThing;
    }
    
}
