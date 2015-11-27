package dataconnection;

import static dataconnection.ConnectionParameter.DATA_FILE_NAME;
import java.io.IOException;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import static javax.xml.parsers.DocumentBuilderFactory.newInstance;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
public class XMLDataExtractor implements DatasourceConnector
{

    private Element root_element;

    /**
     *
     */
    public XMLDataExtractor()
    {
        DOMParser dom_parser = new DOMParser();
    }

    /**
     *
     * @param connection_parameters
     * @return
     */
    @Override
    public boolean connect( HashMap<ConnectionParameter, String> connection_parameters )
    {
        String full_file_path = connection_parameters.get(DATA_FILE_NAME);

        try
        {
            DocumentBuilderFactory dbf = newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            Document dom_document = db.parse(full_file_path);
            root_element = dom_document.getDocumentElement();
            return true;
        }
        catch ( SAXException | IOException | ParserConfigurationException ex )
        {
            return false;
        }
        
    }

    /**
     *
     * @param tree
     * @return
     */
    @Override
    public boolean extract_data( TreeDataStructure tree )
    {
        NodeList child_nodes = root_element.getChildNodes();
        for ( int i = 0; i < child_nodes.getLength(); i++ )
        {
            Node current_node = child_nodes.item( i );
            NodeList child_nodes2 = current_node.getChildNodes();
            for ( int k = 0; k < child_nodes2.getLength(); k++ )
            {
                Node current_node2 = child_nodes2.item( k );
                String relation_name = current_node2.getNodeName();
                if ( current_node2.getNodeType() == ELEMENT_NODE )
                {
                    HashMap<String, String> tuple = new HashMap<>();
                    NamedNodeMap attributes = current_node2.getAttributes();
                    for ( int j = 0; j < attributes.getLength(); j++ )
                    {
                        Node attribute = attributes.item( j );
                        tuple.put( attribute.getNodeName(), attribute.getNodeValue() );
                    }
                    NodeList child_nodes3 = current_node2.getChildNodes();
                    for ( int j = 0; j < child_nodes3.getLength(); j++ )
                    {
                        Node current_node3 = child_nodes3.item( j );
                        tuple.put( current_node3.getNodeName(), current_node3.getNodeValue() );
                    }
                    tree.add_tuple( relation_name, tuple );
                }
            }
        }
        return true;
    }
}
