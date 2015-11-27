package dataconnection;

import java.util.HashMap;
import tree.schematree.TreeDataStructure;

/**
 *
 * @author John
 */
public interface DatasourceConnector
{

    /**
     *
     * @param connectionParameters
     * @return
     */
    boolean connect(HashMap<ConnectionParameter, String> connectionParameters);

    /**
     *
     * @param tree
     * @return
     */
    boolean extract_data(TreeDataStructure tree);
}
