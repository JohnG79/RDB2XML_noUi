package dataconnection;

import static dataconnection.ConnectionParameter.DATABASE_NAME;
import static dataconnection.ConnectionParameter.HOST;
import static dataconnection.ConnectionParameter.PASSWORD;
import static dataconnection.ConnectionParameter.PORT;
import static dataconnection.ConnectionParameter.USER_NAME;
import static java.lang.Class.forName;
import static java.lang.Integer.parseInt;
import static java.lang.Thread.currentThread;
import static java.sql.DriverManager.getConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import tree.schematree.TreeDataStructure;

/**
 *
 * @author John
 */
public class MySQLDataExtractor implements DatasourceConnector
{
    private java.sql.Connection connection;
    private String database_name;
    private TreeDataStructure tree;

    /**
     *
     */
    public MySQLDataExtractor()
    {
        try
        {
            forName( "com.mysql.jdbc.Driver" ).newInstance();
        }
        catch ( ClassNotFoundException | InstantiationException | IllegalAccessException ex )
        {

        }
    }

    /**
     *
     * @param connectionParameters
     * @return
     */
    @Override
    public boolean connect( HashMap<ConnectionParameter, String> connectionParameters )
    {
        String host = connectionParameters.get(HOST);
        String port = connectionParameters.get(PORT);
        String user_name = connectionParameters.get(USER_NAME);
        String password = connectionParameters.get(PASSWORD);
        database_name = connectionParameters.get(DATABASE_NAME);
        try
        {
            connection = getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + database_name + "?user=" + user_name + "&password=" + password
            );
            return true;
        }
        catch ( SQLException ex )
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
        this.tree = tree;
        conduct_extraction_sequence();
        return true;
    }

    private void conduct_extraction_sequence()
    {

        ArrayList<String> relation_names = get_relation_names();
        for(String relation_name: relation_names )
        {
            ArrayList<String> variable_names = get_variable_names( relation_name );
            int row_count = get_row_count( relation_name );
            for ( int row_number = 0; row_number < row_count; row_number++ )
            {
                HashMap<String, String> tuple = get_tuple( relation_name, variable_names, row_number );
                tree.add_tuple( relation_name, tuple );
            }
        }
    }

    /**
     *
     * @return
     */
    public ArrayList<String> get_relation_names()
    {
        try
        {
            PreparedStatement prepared_statement = connection.prepareStatement( "select distinct table_name from information_schema.key_column_usage where table_schema = ?" );
            prepared_statement.setString( 1, database_name );
            return parse_result_set( prepared_statement.executeQuery() );
        }
        catch ( SQLException e )
        {
            return new ArrayList<>();
        }
    }

    /**
     *
     * @param relation_name
     * @return
     */
    public ArrayList<String> get_variable_names( String relation_name )
    {
        try
        {
            PreparedStatement prepared_statement = connection.prepareStatement( "select distinct column_name from information_schema.columns where table_name = ? and table_schema = ?" );
            prepared_statement.setString( 1, relation_name );
            prepared_statement.setString( 2, database_name );
            return parse_result_set( prepared_statement.executeQuery() );
        }
        catch ( SQLException e )
        {
            return new ArrayList<>();
        }
    }

    /**
     *
     * @param relation_name
     * @return
     */
    public int get_row_count( String relation_name )
    {
        try
        {
            String query = "select count(*) from " + relation_name;
            Statement statement = connection.createStatement();
            ResultSet result_set = statement.executeQuery( query );
            return parseInt( get_first_result( result_set ) );
        }
        catch ( SQLException e )
        {
            return -1;
        }
    }

    /**
     *
     * @param relation_name
     * @param variable_names
     * @param row_number
     * @return
     */
    public HashMap<String, String> get_tuple( String relation_name, ArrayList<String> variable_names, int row_number )
    {
        try
        {
            String query = "SELECT * FROM " + relation_name + " LIMIT " + row_number + ", 1";
            Statement statement = connection.createStatement();
            ResultSet result_set = statement.executeQuery( query );
            HashMap<String, String> row = new HashMap<>();
            if ( result_set.next() )
            {
                for ( String variable_name : variable_names )
                {
                    row.put( variable_name, result_set.getString( variable_name ) );
                }
            }
            return row;
        }
        catch ( SQLException e )
        {
            return new HashMap<>();
        }
    }

    /**
     *
     * @param relation_name
     * @param variable_name
     * @return
     */
    public ArrayList<String> get_variable_values( String relation_name, String variable_name )
    {
        try
        {
            String query = "select " + variable_name + " from " + relation_name + ";";
            Statement statement = connection.createStatement();
            return parse_result_set( statement.executeQuery( query ) );
        }
        catch ( SQLException e )
        {
            return new ArrayList<>();
        }
    }

    private ArrayList<String> parse_result_set( ResultSet resultSet )
    {
        ArrayList<String> results = new ArrayList<>();
        try
        {
            while ( resultSet.next() )
            {
                results.add( resultSet.getString( 1 ) );
            }
        }
        catch ( SQLException ex )
        {
            currentThread().getStackTrace();
        }
        return results;
    }

    private String get_first_result( ResultSet resultSet )
    {
        try
        {
            if ( resultSet.next() )
            {
                return resultSet.getString( 1 );
            }
        }
        catch ( SQLException ex )
        {
            currentThread().getStackTrace();
        }
        return "";
    }

}
