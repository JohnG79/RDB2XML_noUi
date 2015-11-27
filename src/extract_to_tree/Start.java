package extract_to_tree;

import Visitor.DOMDataBuilder;
import Visitor.DOMSchemaBuilder;
import dataconnection.ConnectionParameter;
import static dataconnection.ConnectionParameter.DATABASE_NAME;
import static dataconnection.ConnectionParameter.HOST;
import static dataconnection.ConnectionParameter.PASSWORD;
import static dataconnection.ConnectionParameter.PORT;
import static dataconnection.ConnectionParameter.USER_NAME;
import static dataconnection.DatasourceType.MYSQL_DATA;
import static dataconnection.DatasourceType.MYSQL_SCHEMA;
import static dataconnection.PersistenceLayer.DATA_connect;
import static dataconnection.PersistenceLayer.DATA_extract;
import static dataconnection.PersistenceLayer.SCHEMA_connect;
import static dataconnection.PersistenceLayer.SCHEMA_extract;
import static dataconnection.PersistenceLayer.set_DATA_source_type;
import static dataconnection.PersistenceLayer.set_SCHEMA_source_type;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import tree.schematree.DataSchema;
import tree.schematree.TreeDataStructure;


public class Start
{

    public static void main( String[] args ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ParserConfigurationException
    {
        set_SCHEMA_source_type(MYSQL_SCHEMA);
        set_DATA_source_type(MYSQL_DATA);

        HashMap<ConnectionParameter, String> connection_parameters = new HashMap<>();


        /**
         *
         *   MySQL Database CONNECTION PARAMETERS
         *
         **/
        connection_parameters.put(HOST, "localhost" );
        connection_parameters.put(PORT, "3306" );
        connection_parameters.put(USER_NAME, "root" );
        connection_parameters.put(PASSWORD, "root" );
        connection_parameters.put(DATABASE_NAME, "university" );


        SCHEMA_connect( connection_parameters );
        DATA_connect( connection_parameters );

        TreeDataStructure tree = new TreeDataStructure();
        
        SCHEMA_extract( tree );
        DATA_extract( tree );

        DataSchema data_schema = tree.get_data_schema();

        DOMSchemaBuilder dom_schema_builder = new DOMSchemaBuilder();
        DOMDataBuilder dom_data_builder = new DOMDataBuilder();

        data_schema.accept_visitor( dom_schema_builder );
        data_schema.accept_visitor( dom_data_builder );


        /**
        *
        *   OUTPUT LOCATION
        *
        **/
        dom_schema_builder.printToFile( "C:\\OneDrive\\Documents\\Java\\mySQL_to_XML\\xml_stuff\\out\\university.xsd" );
        dom_data_builder.printToFile( "C:\\OneDrive\\Documents\\Java\\mySQL_to_XML\\xml_stuff\\out\\university.xml" );
    }
}
