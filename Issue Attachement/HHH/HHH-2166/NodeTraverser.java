package org.hibernate.hql.ast.util;

import antlr.collections.AST;
import java.util.Map;
import java.util.HashMap;

/**
 * A visitor for traversing an AST tree.
 *
 * @author Steve Ebersole
 * @author Philip R. "Pib" Burns.	Replaced recursion in tree traversal
 *	with iteration.
 */

public class NodeTraverser {
	public static interface VisitationStrategy {
		public void visit(AST node);
	}

	private final VisitationStrategy strategy;

	public NodeTraverser(VisitationStrategy strategy) {
		this.strategy = strategy;
	}

	/**	Traverse the AST tree depth first.
	 *
	 *	@param ast Root node of subtree to traverse.
	 *
	 *	<p>
	 *	Note that the AST passed in is not visited itself.  Visitation
	 *	starts with its children.
	 *	</p>
	 *
	 *	<p>
	 *	This method originally called a recursive method visitDepthFirst
	 *	which performed a recursive traversal of the tree rooted at the
	 *	node specified by the ast parameter.  The original code looked
	 *	like this:
	 *	</p>
	 *	<code>
	 *	<pre>
	 *	private void visitDepthFirst(AST ast) {
	 *		if ( ast == null ) {
	 *			return;
	 *		}
	 *		strategy.visit( ast );
	 *		visitDepthFirst( ast.getFirstChild() );
	 *		visitDepthFirst( ast.getNextSibling() );
	 *	}
	 *	</pre>
	 *	</code>
	 *	</p>
	 *
	 *	<p>
	 *	The current code for traverseDepthFirst uses iteration to
	 *	walk the tree.  This corrects stack overflow problems for
	 *	constructs such as "x in (:x)" where ":x" specifies a large number
	 *	of items.
	 *	</p>
	 */

	public void traverseDepthFirst( AST ast )
	{
								//	Root AST node cannot be null or
								//	traversal of its subtree is impossible.
		if ( ast == null )
		{
			throw new IllegalArgumentException(
				"node to traverse cannot be null!" );
		}
								//	Map to hold parents of each
								//	AST node.  Unfortunately the AST
								//	interface does not provide a method
								//	for finding the parent of a node, so
								//	we use the Map to save them.

		Map parentNodes	= new HashMap();

								//	Start tree traversal with first child
								//	of the specified root AST node.

		AST currentNode	= ast.getFirstChild();

								//	Remember parent of first child.

		parentNodes.put( currentNode , ast );

								//	Iterate through nodes, simulating
								//	recursive tree traversal, and add them
								//	to queue in proper order for later
								//	linear traversal.  This "flattens" the
								//	into a linear list of nodes which can
								//	be visited non-recursively.

		while ( currentNode != null )
		{
								//	Visit the current node.

			strategy.visit( currentNode );

								//	Move down to current node's first child
								//	if it exists.

			AST childNode	= currentNode.getFirstChild();

								//	If the child is not null, make it
								//	the current node.

			if ( childNode != null )
			{
								//	Remember parent of the child.

				parentNodes.put( childNode , currentNode );

								//	Make child the current node.

				currentNode	= childNode;

				continue;
			}

			while ( currentNode != null )
			{
								//	Move to next sibling if any.

				AST siblingNode	= currentNode.getNextSibling();

				if ( siblingNode != null )
				{
								//	Get current node's parent.
								//	This is also the parent of the
								//	sibling node.

					AST parentNode	= (AST)parentNodes.get( currentNode );

								//	Remember parent of sibling.

					parentNodes.put( siblingNode , parentNode );

								//	Make sibling the current node.

					currentNode		= siblingNode;

					break;
				}
								//	Move up to parent if no sibling.
								//	If parent is root node, we're done.

				currentNode	= (AST)parentNodes.get( currentNode );

				if ( currentNode.equals( ast ) )
				{
					currentNode = null;
				}
			}
		}
	}
}

