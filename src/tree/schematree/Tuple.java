package tree.schematree;

import Visitor.Visitor;
import java.util.HashMap;

/**
 *
 * @author John
 */
public class Tuple implements DataObjectContainer
{

    private String parent_relation_schema_name;
    HashMap<Attribute, String> data;

    /**
     *
     */
    public Tuple()
    {

    }

    /**
     *
     * @param parent_relation_schema_name
     */
    public Tuple( String parent_relation_schema_name )
    {
        this.parent_relation_schema_name = parent_relation_schema_name;
        data = new HashMap<>();
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

    /**
     *
     * @param data
     */
    public void add_data( HashMap<Attribute, String> data )
    {
        this.data = data;
    }

    /**
     *
     * @return
     */
    public HashMap<Attribute, String> get_data()
    {
        return data;
    }

    /**
     *
     * @return
     */
    @Override
    public String get_name()
    {
        return parent_relation_schema_name;
    }

}
