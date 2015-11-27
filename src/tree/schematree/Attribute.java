package tree.schematree;

/**
 *
 * @author John
 */
public abstract class Attribute implements DataObject
{

    /**
     *
     */
    protected String name;

    /**
     *
     * @param name
     */
    public Attribute( String name )
    {
        this.name = name;
    }
}
