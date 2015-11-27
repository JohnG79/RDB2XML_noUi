package tree.schematree;

import Visitor.Visitor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author John
 */
public class RelationSchema implements DataObjectContainer
{
    private final ArrayList<Attribute> attributes;
    private final ArrayList<Tuple> tuples;
    private final String name;

    /**
     *
     * @param name
     */
    public RelationSchema( String name )
    {
        attributes = new ArrayList<>();
        tuples = new ArrayList<>();
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
     * @return
     */
    public ArrayList<Attribute> get_attributes()
    {
        return attributes;
    }

    /**
     *
     * @param attribute_name
     * @return
     */
    public Attribute get_attribute( String attribute_name )
    {
        for ( Attribute attribute : attributes )
        {
            if ( attribute.get_name().equals( attribute_name ) )
            {
                return attribute;
            }
        }
        return null;
    }

    /**
     *
     * @param key_name
     */
    public void remove_attribute( String key_name )
    {
        for ( int index = 0; index < attributes.size(); index++ )
        {
            if ( attributes.get( index ).get_name().equals( key_name ) )
            {
                attributes.remove( index );
                return;
            }
        }
    }

    /**
     *
     * @param attribute
     */
    public void add_attribute( Attribute attribute )
    {
        attributes.add( attribute );
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
     * @param variables_and_data
     */
    public void add_tuple( HashMap<String, String> variables_and_data )
    {
        Tuple new_tuple = new Tuple( name );

        HashMap<Attribute, String> attributes_and_data = new HashMap<>();

        Set<String> variable_names = variables_and_data.keySet();
        for ( String variable_name : variable_names )
        {
            for ( Attribute attribute : attributes )
            {
                String attribute_name = attribute.get_name();
                if(attribute_name.equals(variable_name))
                {
                    attributes_and_data.put( attribute, variables_and_data.get( attribute_name ) );
                    break;
                }
            }
        }
        new_tuple.add_data( attributes_and_data );
        tuples.add( new_tuple );
    }

    /**
     *
     * @return
     */
    public ArrayList< Tuple > get_tuples()
    {
        return tuples;
    }

    /**
     *
     * @return
     */
    public ArrayList<String> get_variable_names()
    {
        ArrayList<String> variable_names = new ArrayList<>();
        for(Attribute attribute : attributes )
        {
            variable_names.add( attribute.get_name() );
        }
        return variable_names;
    }
}