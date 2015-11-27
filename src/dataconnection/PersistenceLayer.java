package dataconnection;

import static dataconnection.ConnectorFactory.getConnector;
import java.util.HashMap;
import tree.schematree.TreeDataStructure;

/**
 *
 * @author John
 */
public class PersistenceLayer
{

    private static DatasourceConnector SCHEMA_connector;
    private static DatasourceConnector DATA_connector;

    /**
     *
     * @param SCHEMA_source_type
     */
    public static void set_SCHEMA_source_type( DatasourceType SCHEMA_source_type)
    {
        SCHEMA_connector = getConnector( SCHEMA_source_type );
    }

    /**
     *
     * @param DATA_source_type
     */
    public static void set_DATA_source_type( DatasourceType DATA_source_type)
    {
        DATA_connector = getConnector( DATA_source_type );
    }

    /**
     *
     * @param connection_parameters
     */
    public static void connect( HashMap<ConnectionParameter, String> connection_parameters)
    {
        SCHEMA_connector.connect(connection_parameters );
        DATA_connector.connect(connection_parameters );
    }

    /**
     *
     * @param connection_parameters
     */
    public static void SCHEMA_connect( HashMap<ConnectionParameter, String> connection_parameters)
    {
        SCHEMA_connector.connect(connection_parameters );
    }

    /**
     *
     * @param connection_parameters
     */
    public static void DATA_connect( HashMap<ConnectionParameter, String> connection_parameters)
    {
        DATA_connector.connect(connection_parameters );
    }

    /**
     *
     * @param tree
     */
    public static void extract( TreeDataStructure tree)
    {
        SCHEMA_connector.extract_data( tree );
        DATA_connector.extract_data( tree );
    }

    /**
     *
     * @param tree
     */
    public static void SCHEMA_extract( TreeDataStructure tree)
    {
        SCHEMA_connector.extract_data( tree );
    }

    /**
     *
     * @param tree
     */
    public static void DATA_extract( TreeDataStructure tree)
    {
        DATA_connector.extract_data( tree );
    }

    /**
     *
     * @param SCHEMA_source_type
     * @param DATA_source_type
     */
    private PersistenceLayer( DatasourceType SCHEMA_source_type, DatasourceType DATA_source_type)
    {
        SCHEMA_connector = getConnector( SCHEMA_source_type );
        DATA_connector = getConnector( DATA_source_type );
    }
}
