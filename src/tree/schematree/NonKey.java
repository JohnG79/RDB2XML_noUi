package tree.schematree;

import Visitor.Visitor;

/**
 *
 * @author John
 */
public class NonKey extends Attribute
{

    /**
     *
     * @param name
     */
    public NonKey( String name )
    {
        super(name);
    }

    /**
     *
     * @return
     */
    @Override
    public String get_name()
    {
        return name;
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
