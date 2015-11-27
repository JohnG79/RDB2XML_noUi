package tree.schematree;

import Visitor.Visitor;

/**
 *
 * @author John
 */
public class CombinedKey extends ForeignKey
{

    /**
     *
     * @param name
     * @param constraint_name
     */
    public CombinedKey( String name, String constraint_name )
    {
        super( name, constraint_name );
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
