package tree.schematree;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author John
 */
public class TreeDataStructure
{

    private DataSchema data_schema;

    /**
     *
     */
    public TreeDataStructure()
    {
    }

    /**
     *
     * @param data_schema_name
     */
    public void add_data_schema( String data_schema_name )
    {
        data_schema = new DataSchema( data_schema_name );
    }

    /**
     *
     * @param relation_schema_name
     */
    public void add_relation_schema( String relation_schema_name )
    {
        data_schema.add_relation_schema( new RelationSchema( relation_schema_name ) );
    }

    /**
     *
     * @return
     */
    public DataSchema get_data_schema()
    {
        return data_schema;
    }

    /**
     *
     * @param relation_schema_name
     * @param key_name
     * @param constraint_name
     */
    public void add_primary_key( String relation_schema_name, String key_name, String constraint_name )
    {
        RelationSchema relation_schema;
        if ( ( relation_schema = data_schema.get_relation_schema( relation_schema_name ) ) != null )
        {
            if ( relation_schema.get_attribute( key_name ) != null )
            {
                relation_schema.remove_attribute( key_name );
            }
            Key new_primary_key = new PrimaryKey( key_name, constraint_name );
            new_primary_key.set_parent_relation_schema( relation_schema );
            relation_schema.add_attribute( new_primary_key );
        }
        else
        {
            throw new IllegalStateException();
        }
    }

    /**
     *
     * @param relation_schema_name
     * @param key_name
     * @param constraint_name
     * @param referenced_constraint_name
     */
    public void add_foreign_key( String relation_schema_name, String key_name, String constraint_name, String referenced_constraint_name )
    {
        RelationSchema relation_schema = data_schema.get_relation_schema( relation_schema_name );

        if ( relation_schema != null )
        {
            Attribute attribute = relation_schema.get_attribute( key_name );
            Attribute key_attribute;
            if ( attribute == null )
            {
                key_attribute = new ForeignKey( key_name, constraint_name );
            }
            else if ( attribute instanceof NonKey )
            {
                relation_schema.remove_attribute( key_name );
                key_attribute = new ForeignKey( key_name, constraint_name );
            }
            else
            {
                relation_schema.remove_attribute( key_name );
                key_attribute = new CombinedKey( key_name, constraint_name );
            }
            (( Key )key_attribute).set_parent_relation_schema( relation_schema );
            set_referenced_primary_key( ( ForeignKey ) key_attribute, referenced_constraint_name );
            relation_schema.add_attribute( key_attribute );
        }
        else
        {
            throw new IllegalStateException();
        }
    }

    private void set_referenced_primary_key( ForeignKey foreign_key, String referenced_constraint_name )
    {
        ArrayList<RelationSchema> relation_schemata;
        if ( !( relation_schemata = data_schema.get_relation_schemata() ).isEmpty() )
        {
            for ( RelationSchema relation_schema : relation_schemata )
            {
                ArrayList<Attribute> attributes = relation_schema.get_attributes();
                for ( Attribute attribute : attributes )
                {
                    if ( attribute instanceof Key && ( ( Key ) attribute ).get_constraint_name().equals( referenced_constraint_name ) )
                    {
                        foreign_key.set_referenced_primary_key( ( ( PrimaryKey ) attribute ) );
                        return;
                    }
                }
            }
        }
        else
        {
            throw new IllegalStateException();
        }
    }

    /**
     *
     * @param relation_schema_name
     * @param attribute_name
     */
    public void add_non_key( String relation_schema_name, String attribute_name )
    {
        RelationSchema relation_schema;
        if ( ( relation_schema = data_schema.get_relation_schema( relation_schema_name ) ) != null && relation_schema.get_attribute( attribute_name ) == null )
        {
            Attribute attribute = new NonKey( attribute_name );
            relation_schema.add_attribute( attribute );
        }
        else
        {
            throw new IllegalStateException();
        }
    }
    
    /**
     *
     * @param relation_name
     * @param variable_names_and_data
     */
    public void add_tuple( String relation_name, HashMap<String, String> variable_names_and_data )
    {
        data_schema.get_relation_schema( relation_name ).add_tuple( variable_names_and_data );
    }
}
