package dataconnection;

import static dataconnection.ConnectionParameter.SCHEMA_FILE_NAME;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import static org.w3c.dom.Node.ELEMENT_NODE;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import tree.schematree.TreeDataStructure;

/**
 *
 * @author John
 */
public class XMLSchemaExtractor implements DatasourceConnector
{

    private Document document;
    private final DOMParser parser;
    private TreeDataStructure tree;

    /**
     *
     */
    public XMLSchemaExtractor()
    {
        parser = new DOMParser();
    }

    /**
     *
     * @param connectionParameters
     * @return
     */
    @Override
    public boolean connect( HashMap<ConnectionParameter, String> connectionParameters )
    {
        try
        {
            String full_file_path = connectionParameters.get(SCHEMA_FILE_NAME);
            parser.parse( full_file_path );
            document = parser.getDocument();
            return true;
        }
        catch ( SAXException | IOException ex )
        {
        }
        return false;
    }

    /**
     *
     * @param tree
     * @return
     */
    @Override
    public boolean extract_data( TreeDataStructure tree )
    {
        try
        {
            this.tree = tree;
            conduct_traversal_sequence();
            return true;
        }
        catch ( Exception e )
        {
        }
        return false;
    }

    private void conduct_traversal_sequence()
    {
        traverse_for_relation_schemata( document );
        traverse_for_primary_keys( document );
        traverse_for_foreign_keys( document );
    }

    private void traverse_for_relation_schemata( Node current_node )
    {
        if ( current_node.getNodeType() == ELEMENT_NODE )
        {
            String node_name = current_node.getNodeName();
            if ( node_name.equals( "xsd:element" ) )
            {
                NamedNodeMap node_attributes = current_node.getAttributes();
                if ( node_attributes.getNamedItem( "maxOccurs" ) != null && node_attributes.getNamedItem( "minOccurs" ) != null && node_attributes.getNamedItem( "name" ) != null )
                {
                    process_element_sub_tree( current_node );
                }
                else if ( node_attributes.getLength() == 1 && node_attributes.getNamedItem( "name" ) != null )
                {
                    String data_schema_name = node_attributes.getNamedItem( "name" ).getNodeValue();
                    tree.add_data_schema( data_schema_name.substring( 0 , data_schema_name.indexOf( "_database" ) ) );
                }
            }
        }
        NodeList child_nodes = current_node.getChildNodes();
        if ( child_nodes != null )
        {
            for ( int index = 0; index < child_nodes.getLength(); ++index )
            {
                traverse_for_relation_schemata( child_nodes.item( index ) );
            }
        }
    }

    private void process_element_sub_tree( Node element_sub_tree_root )
    {
        Node relation_schema = element_sub_tree_root.getAttributes().getNamedItem( "name" );
        String relation_schema_name = relation_schema.getNodeValue();
        tree.add_relation_schema( relation_schema_name );

        NodeList child_nodes = element_sub_tree_root.getChildNodes();
        ArrayList<String> attribute_names = new ArrayList<>();
        for ( int index = 0; index < child_nodes.getLength(); index++ )
        {
            traverse_relation_schema_descendants_for_attribute_elements( attribute_names, child_nodes.item( index ) );
        }
        for ( int index = 0; index < child_nodes.getLength(); index++ )
        {
            traverse_relation_schema_descendants_for_element_elements( attribute_names, child_nodes.item( index ) );
        }
        for(String attribute_name:attribute_names)
        {
            tree.add_non_key( relation_schema_name, attribute_name );
        }
    }

    private void traverse_relation_schema_descendants_for_attribute_elements( ArrayList<String> ATTRIBUTE_NAMES, Node relation_schema_descendant )
    {
        String ATTRIBUTE_NAME;

        if ( relation_schema_descendant.getNodeType() == ELEMENT_NODE )
        {
            String node_name = relation_schema_descendant.getNodeName();
            if ( node_name.equals( "xsd:attribute" ) )
            {
                NamedNodeMap node_attributes = relation_schema_descendant.getAttributes();
                ATTRIBUTE_NAME = node_attributes.getNamedItem( "name" ).getNodeValue();
                ATTRIBUTE_NAMES.add( ATTRIBUTE_NAME );
            }
        }
        NodeList child_nodes = relation_schema_descendant.getChildNodes();
        if ( child_nodes != null )
        {
            for ( int index = 0; index < child_nodes.getLength(); ++index )
            {
                traverse_relation_schema_descendants_for_attribute_elements( ATTRIBUTE_NAMES, child_nodes.item( index ) );
            }
        }
    }

    private void traverse_relation_schema_descendants_for_element_elements( ArrayList<String> SCHEMA_ATTRIBUTE_NAMES, Node relation_schema_descendant )
    {
        String SCHEMA_ATTRIBUTE_NAME;

        if ( relation_schema_descendant.getNodeType() == ELEMENT_NODE )
        {
            String node_name = relation_schema_descendant.getNodeName();
            if ( node_name.equals( "xsd:elements" ) )
            {
                NamedNodeMap node_attributes = relation_schema_descendant.getAttributes();
                SCHEMA_ATTRIBUTE_NAME = node_attributes.getNamedItem( "name" ).getNodeValue();
                SCHEMA_ATTRIBUTE_NAMES.add( SCHEMA_ATTRIBUTE_NAME );
            }
        }
        NodeList child_nodes = relation_schema_descendant.getChildNodes();
        if ( child_nodes != null )
        {
            for ( int index = 0; index < child_nodes.getLength(); ++index )
            {
                traverse_relation_schema_descendants_for_element_elements( SCHEMA_ATTRIBUTE_NAMES, child_nodes.item( index ) );
            }
        }
    }

    private void traverse_for_primary_keys( Node current_node )
    {
        if ( current_node.getNodeType() == ELEMENT_NODE )
        {
            String node_name = current_node.getNodeName();
            if ( node_name.equals( "xsd:key" ) )
            {
                process_key_sub_tree( current_node );
            }
        }
        NodeList child_nodes = current_node.getChildNodes();
        if ( child_nodes != null )
        {
            for ( int index = 0; index < child_nodes.getLength(); ++index )
            {
                traverse_for_primary_keys( child_nodes.item( index ) );
            }
        }
    }

    private void process_key_sub_tree( Node key_sub_tree_root )
    {
        String CONSTRAINT_NAME;
        ArrayList<String> RELATION_SCHEMA_NAME = new ArrayList<>();
        ArrayList<String> KEY_NAMES = new ArrayList<>();
        NamedNodeMap node_map = key_sub_tree_root.getAttributes();
        CONSTRAINT_NAME = node_map.getNamedItem( "name" ).getNodeValue();

        NodeList child_nodes = key_sub_tree_root.getChildNodes();
        for ( int i = 0; i < child_nodes.getLength(); i++ )
        {
            if ( child_nodes.item( i ).getNodeType() == ELEMENT_NODE )
            {
                process_key_descendants( RELATION_SCHEMA_NAME, KEY_NAMES, child_nodes.item( i ) );
            }
        }
        for(String KEY_NAME:KEY_NAMES)
        {
            tree.add_primary_key( RELATION_SCHEMA_NAME.get( 0 ), KEY_NAME, CONSTRAINT_NAME );
        }

    }

    private void process_key_descendants( ArrayList<String> RELATION_SCHEMA_NAME, ArrayList<String> KEY_NAMES, Node key_descendant )
    {
        String KEY_NAME;

        String node_name = key_descendant.getNodeName();
        if ( node_name.equals( "xsd:selector" ) && key_descendant.getAttributes().getNamedItem( "xpath" ) != null )
        {
            Node node = key_descendant.getAttributes().getNamedItem( "xpath" );
            RELATION_SCHEMA_NAME.add( node.getNodeValue().substring( node.getNodeValue().lastIndexOf('/') + 1, node.getNodeValue().length() ) );
        }
        else if ( node_name.equals( "xsd:field" ) && key_descendant.getAttributes().getNamedItem( "xpath" ) != null )
        {
            Node node = key_descendant.getAttributes().getNamedItem( "xpath" );
            KEY_NAME = node.getNodeValue().substring( node.getNodeValue().indexOf('@') + 1, node.getNodeValue().length() );
            KEY_NAMES.add( KEY_NAME );
        }
    }

    private void traverse_for_foreign_keys( Node current_node )
    {
        if ( current_node.getNodeType() == ELEMENT_NODE )
        {
            String node_name = current_node.getNodeName();
            if ( node_name.equals( "xsd:keyref" ) )
            {
                process_keyref_sub_tree( current_node );
            }
        }
        NodeList child_nodes = current_node.getChildNodes();
        if ( child_nodes != null )
        {
            for ( int index = 0; index < child_nodes.getLength(); index++ )
            {
                traverse_for_foreign_keys( child_nodes.item( index ) );
            }
        }
    }

    private void process_keyref_sub_tree( Node keyref_subtree_root )
    {
        String CONSTRAINT_NAME;
        String REFERENCED_CONSTRAINT_NAME;
        ArrayList<String> RELATION_SCHEMA_NAME = new ArrayList<>();
        ArrayList<String> KEY_NAMES = new ArrayList<>();
        NamedNodeMap node_map = keyref_subtree_root.getAttributes();

        CONSTRAINT_NAME = node_map.getNamedItem( "name" ).getNodeValue();
        int index = node_map.getNamedItem( "refer" ).getNodeValue().indexOf(':');
        REFERENCED_CONSTRAINT_NAME = node_map.getNamedItem( "refer" ).getNodeValue().substring( index + 1, node_map.getNamedItem( "refer" ).getNodeValue().length() );

        NodeList node_list = keyref_subtree_root.getChildNodes();
        for ( int i = 0; i < node_list.getLength(); i++ )
        {
            if ( node_list.item( i ).getNodeType() == ELEMENT_NODE )
            {
                process_key_descendants( RELATION_SCHEMA_NAME, KEY_NAMES, node_list.item( i ) );
            }
        }
        for(String KEY_NAME:KEY_NAMES)
        {
            tree.add_foreign_key( RELATION_SCHEMA_NAME.get( 0 ), KEY_NAME, CONSTRAINT_NAME, REFERENCED_CONSTRAINT_NAME );
        }
    }

}
