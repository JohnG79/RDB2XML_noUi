package Visitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import static javax.xml.parsers.DocumentBuilderFactory.newInstance;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import tree.schematree.Attribute;
import tree.schematree.CombinedKey;
import tree.schematree.DataSchema;
import tree.schematree.ForeignKey;
import tree.schematree.Key;
import tree.schematree.NonKey;
import tree.schematree.PrimaryKey;
import tree.schematree.RelationSchema;
import tree.schematree.Tuple;

/**
 *
 * @author John
 */
public class DOMDataBuilder implements Visitor
{
    private DOMImplementation implementation = null;
    private Element database_root;
    private Element current_relation_root;
    private Document document;

    private final String target_namespace_prefix_declation;
    private final String target_namespace_prefix;
    private final String target_namespace;
    
    public DOMDataBuilder()
    {
        target_namespace_prefix = "un";
        target_namespace = "http://www.example.com/";
        target_namespace_prefix_declation = "xmlns:un";
        
        DocumentBuilderFactory factory = newInstance();
        DocumentBuilder builder;
        try
        {
            builder = factory.newDocumentBuilder();
            implementation = builder.getDOMImplementation();
        }
        catch ( ParserConfigurationException ex )
        {
            Logger.getLogger( DOMDataBuilder.class.getName() ).log( Level.SEVERE, null, ex );
        }
    }

    private Element append_database_to_document( String database_name )
    {        
        String full_target_namespace = target_namespace + database_name;
        String prefixed_document_tag_name = target_namespace_prefix + ":" + database_name;       
        document = implementation.createDocument( full_target_namespace, prefixed_document_tag_name, null );
        Element root_element = document.getDocumentElement();
        String xmlns_namespace = "http://www.w3.org/2000/xmlns/";
        String xsd_instance_namespace = "http://www.w3.org/2001/XMLSchema-instance";
        String xsd_instance_namespace_prefix_declation = "xmlns:xsi";
        root_element.setAttributeNS(xmlns_namespace, xsd_instance_namespace_prefix_declation, xsd_instance_namespace);
        root_element.setAttribute( "xsi:schemaLocation", full_target_namespace + " " + database_name + ".xsd");
        root_element.setAttribute( target_namespace_prefix_declation, full_target_namespace);
        return root_element;
    }

    private Element append_relation_to_database( Element parent_element, String relation_name )
    {
        Element relation_element = document.createElement( relation_name + "_relation" );
        parent_element.appendChild( relation_element );
        return relation_element;
    }

    private Element add_variable_and_datum_as_element( Element parent_element, String variable_name, String datum )
    {
        Element variable_element = document.createElement( variable_name );
        parent_element.appendChild( variable_element );
        Text text = document.createTextNode( datum );
        variable_element.appendChild( text );
        return variable_element;
    }

    /**
     *
     * @param full_file_path
     */
    public void printToFile(String full_file_path )
    {
        try
        {
            OutputFormat format = new OutputFormat( document );
            format.setIndenting( true );

            XMLSerializer serializer = new XMLSerializer(
                    new FileOutputStream( new File( full_file_path) ), format );

            serializer.serialize( document );

        }
        catch ( IOException ie )
        {
        }
    }

    /**
     *
     * @param data_schema
     */
    @Override
    public void visit( DataSchema data_schema )
    {
        database_root = append_database_to_document( data_schema.get_name() );
        for(RelationSchema relation_schema: data_schema.get_relation_schemata())
        {
            relation_schema.accept_visitor( this );
        }
    }

    /**
     *
     * @param relation_schema
     */
    @Override
    public void visit( RelationSchema relation_schema )
    {       
        current_relation_root = append_relation_to_database( database_root, relation_schema.get_name() );
        for(Tuple tuple : relation_schema.get_tuples())
        {
            tuple.accept_visitor( this );
        }
    }

    /**
     *
     * @param tuple
     */
    @Override
    public void visit( Tuple tuple )
    {
        Element tuple_element = document.createElement( tuple.get_name() );
        current_relation_root.appendChild( tuple_element );

        HashMap<Attribute, String> attributes_and_data = tuple.get_data();
        Set<Attribute> attributes = attributes_and_data.keySet();
        for(Attribute attribute : attributes )
        {
            if(attribute instanceof PrimaryKey || attribute instanceof CombinedKey )
            {
                tuple_element.setAttribute( attribute.get_name() ,attributes_and_data.get( attribute ) );
            }
            else
            {
                add_variable_and_datum_as_element( tuple_element, attribute.get_name(), attributes_and_data.get( attribute ) );
            }
        }
    }

    /**
     *
     * @param non_key
     */
    @Override
    public void visit( NonKey non_key )
    {
    }

    /**
     *
     * @param key
     */
    @Override
    public void visit( Key key )
    {
    }

    /**
     *
     * @param foreign_key
     */
    @Override
    public void visit( ForeignKey foreign_key )
    {
    }

    /**
     *
     * @param combined_key
     */
    @Override
    public void visit( CombinedKey combined_key )
    {
    }
}
