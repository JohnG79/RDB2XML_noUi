package dataconnection;

import static dataconnection.ConnectionParameter.DATABASE_NAME;
import static dataconnection.ConnectionParameter.HOST;
import static dataconnection.ConnectionParameter.PASSWORD;
import static dataconnection.ConnectionParameter.PORT;
import static dataconnection.ConnectionParameter.USER_NAME;
import static java.lang.Class.forName;
import static java.sql.DriverManager.getConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
import tree.schematree.TreeDataStructure;

/**
 *
 * @author John
 */
public class MySQLSchemaExtractor implements DatasourceConnector
{

    private Connection connection;
    private String database_name;
    private TreeDataStructure tree;

    /**
     *
     */
    public MySQLSchemaExtractor()
    {
        try
        {
            forName( "com.mysql.jdbc.Driver" ).newInstance();
        }
        catch ( ClassNotFoundException | InstantiationException | IllegalAccessException ex )
        {
			System.err.println(ex.getMessage());
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
			System.err.println(ex.getMessage());
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
        tree.add_data_schema( database_name );
        ArrayList<String> relation_schema_names = get_relation_schema_names( database_name );


        for(String relation_schema_name: relation_schema_names)
        {
            get_primary_key_names( relation_schema_name );
        }
        for(String relation_schema_name : relation_schema_names)
        {
            get_foreign_key_names( relation_schema_name );
        }
        for(String relation_schema_name: relation_schema_names)
        {
            try
            {
                get_non_key_names( relation_schema_name );
            }
            catch ( Exception ex )
            {
            }
        }
        return true;
    }

    private ArrayList<String> get_relation_schema_names( String database_name )
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement( "select distinct table_name from information_schema.key_column_usage where table_schema = ?" );
            statement.setString( 1, database_name );
            ArrayList<String> relation_schema_names = parse_result_set( statement.executeQuery() );


            for( String relation_schema_name : relation_schema_names )
            {
                tree.add_relation_schema( relation_schema_name );
            }
            return relation_schema_names;
        }
        catch ( SQLException ex )
        {
        }
        return new ArrayList<>();
    }

    private void get_primary_key_names( String relation_schema_name )
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement( "select column_name from information_schema.columns where table_name = ? and column_key = 'PRI' and table_schema = ?" );
            statement.setString( 1, relation_schema_name );
            statement.setString( 2, database_name );
            ArrayList<String> primary_key_names = parse_result_set( statement.executeQuery() );
            for( String primary_key_name: primary_key_names )
            {
                tree.add_primary_key( relation_schema_name, primary_key_name, "PK" + relation_schema_name );
            }
        }
        catch ( SQLException ex )
        {
        }
    }

    private void get_foreign_key_names( String relation_schema_name )
    {
        try
        {
            PreparedStatement prepared_statement = connection.prepareStatement( "select column_name from information_schema.key_column_usage where table_name = ? and constraint_name != 'PRIMARY' and constraint_name != column_name and table_schema = ?" );
            prepared_statement.setString( 1, relation_schema_name );
            prepared_statement.setString( 2, database_name );
            ArrayList<String> foreign_key_names = parse_result_set( prepared_statement.executeQuery() );
            for ( String foreign_key_name : foreign_key_names )
            {
                prepared_statement = connection.prepareStatement( "select referenced_table_name from information_schema.key_column_usage where table_name = ? and constraint_name != 'PRIMARY' and constraint_name != column_name and column_name = ? and table_schema = ?" );
                prepared_statement.setString( 1, relation_schema_name );
                prepared_statement.setString( 2, foreign_key_name );
                prepared_statement.setString( 3, database_name );
                String referenced_table_name = get_first_result( prepared_statement.executeQuery() );
                tree.add_foreign_key( relation_schema_name, foreign_key_name, "FK" + relation_schema_name + "_" + foreign_key_name, "PK" + referenced_table_name );
            }
        }
        catch ( SQLException ex )
        {
        }
    }

    private void get_non_key_names( String relation_schema_name )
    {
        try
        {
            PreparedStatement prepared_statement = connection.prepareStatement( "select column_name from information_schema.columns where table_name = ? and column_key != 'PRI' and column_key != 'MUL' and table_schema = ?" );
            prepared_statement.setString( 1, relation_schema_name );
            prepared_statement.setString( 2, database_name );
            ArrayList<String> non_key_names = parse_result_set( prepared_statement.executeQuery() );
            for( String non_key_name : non_key_names)
            {
                tree.add_non_key( relation_schema_name, non_key_name );
            }
        }
        catch ( SQLException ex )
        {
        }
    }

    private ArrayList<String> parse_result_set( ResultSet resultSet )
    {
        try
        {
            ArrayList<String> results = new ArrayList<>();
            while ( resultSet.next() )
            {
                results.add( resultSet.getString( 1 ) );
            }
            return results;
        }
        catch ( SQLException ex )
        {
        }
        return new ArrayList<>();
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
        }
        return "";
    }
}
