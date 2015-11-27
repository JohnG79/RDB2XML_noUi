package tree.schematree;

import Visitor.Visitor;

/**
 *
 * @author John
 */
public interface VisitorAcceptor
{

    /**
     *
     * @param visitor
     */
    void accept_visitor(Visitor visitor);
}
