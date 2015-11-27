package dataconnection;

/**
 *
 * @author John
 */
public class ConnectorFactory
{

    /**
     *
     * @param data_source_type
     * @return
     */
    public static DatasourceConnector getConnector(DatasourceType data_source_type)
    {
        switch(data_source_type)
        {
            case MYSQL_SCHEMA:
            {
                return new MySQLSchemaExtractor();
            }
            case MYSQL_DATA:
            {
                return new MySQLDataExtractor();
            }
            case ORACLE_:
            {
                throw new UnsupportedOperationException(">> Oracle Connector not implemented yet");
            }
            case XML_DATA:
            {
                return new XMLDataExtractor();
            }
            case XML_SCHEMA:
            {
                return new XMLSchemaExtractor();
            }
            default:
            {
                throw new UnsupportedOperationException(">> Invalid connector_id passed to ConnectorFactory.getConnector(int connector_id) method");
            }
        }
    }

    private ConnectorFactory()
    {
    }
}
