package tree.schematree;

import Visitor.Visitor;
import java.util.ArrayList;

/**
 *
 * @author John
 */
public class DataSchema implements DataObjectContainer
{
    ArrayList<RelationSchema> relation_schemata;
    String name;
    private DataSchema(){}

    /**
     *
     * @param name
     */
    public DataSchema(String name)
    {
        relation_schemata = new ArrayList<>();
        this.name = name;
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
     * @param relation_schema
     */
    public void add_relation_schema(RelationSchema relation_schema)
    {
        relation_schemata.add( relation_schema );
    }
    
    /**
     *
     * @return
     */
    public ArrayList<RelationSchema> get_relation_schemata()
    {
        return relation_schemata;
    }

    /**
     *
     * @param relation_schema_name
     * @return
     */
    public RelationSchema get_relation_schema( String relation_schema_name )
    {
        for(RelationSchema relation_schema : relation_schemata )
        {
            if(relation_schema.get_name().equals(relation_schema_name))
            {
                return relation_schema;
            }
        }
        return null;
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
