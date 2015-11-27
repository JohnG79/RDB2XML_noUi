package Visitor;

import tree.schematree.CombinedKey;
import tree.schematree.DataSchema;
import tree.schematree.ForeignKey;
import tree.schematree.Key;
import tree.schematree.NonKey;
import tree.schematree.RelationSchema;
import tree.schematree.Tuple;

/**
 *
 * @author John
 */
public interface Visitor
{

    /**
     *
     * @param data_schema
     */
    void visit(DataSchema data_schema);
    
    /**
     *
     * @param relation_schema
     */
    void visit(RelationSchema relation_schema);
    
    /**
     *
     * @param non_key
     */
    void visit(NonKey non_key);

    /**
     *
     * @param key
     */
    void visit(Key key);

    /**
     *
     * @param foreign_key
     */
    void visit(ForeignKey foreign_key);

    /**
     *
     * @param combined_key
     */
    void visit(CombinedKey combined_key);
    
    /**
     *
     * @param tuple
     */
    void visit(Tuple tuple);
}
