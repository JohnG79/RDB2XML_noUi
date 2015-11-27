package tree.schematree;

import Visitor.Visitor;

/**
 *
 * @author John
 */
public class Key extends Attribute
{
    private final String constraint_name;
    private RelationSchema parent_relation_schema;

    /**
     *
     * @param name
     * @param constraint_name
     */
    public Key(String name, String constraint_name)
    {
        super(name);
        this.constraint_name = constraint_name;
        
    }

    /**
     *
     * @return
     */
    public String get_constraint_name()
    {
        return constraint_name;
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
     * @param parent_relation_schema
     */
    public void set_parent_relation_schema(RelationSchema parent_relation_schema )
    {
        this.parent_relation_schema = parent_relation_schema;
    }

    /**
     *
     * @return
     */
    public RelationSchema get_parent_relation_schema( )
    {
        return parent_relation_schema;
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
