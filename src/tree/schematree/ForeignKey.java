package tree.schematree;

import Visitor.Visitor;

/**
 *
 * @author John
 */
public class ForeignKey extends Key
{

    /**
     *
     */
    protected PrimaryKey referenced_primary_key;
    
    /**
     *
     * @param name
     * @param constraint_name
     */
    public ForeignKey(String name, String constraint_name )
    {
        super(name,constraint_name);
    }

    /**
     *
     * @return
     */
    public PrimaryKey get_referenced_primary_key()
    {
        return referenced_primary_key;
    }

    /**
     *
     * @param referenced_primary_key
     */
    public void set_referenced_primary_key(PrimaryKey referenced_primary_key)
    {
        this.referenced_primary_key = referenced_primary_key;
    }

    /**
     *
     * @param visitor
     */
    @Override
    public void accept_visitor( Visitor visitor )
    {
        visitor.visit( this );
    }
}
