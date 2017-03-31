package tree;

/**
 * Created by Ayomitunde on 3/12/2017.
 */
public class RandomTreeLearner
{
    private Node root = null;

    public Node getRoot()
    {
        return this.root;
    }

    public void insert(Node currentNode, Node node, boolean isLeftChild)
    {
        if(currentNode == null)
        {
            root = node;
            return;
        }
        if(isLeftChild)
        {
            currentNode.setLeftChild(node);
        }else
        {
            currentNode.setRightChild(node);
        }
        node.setParent(currentNode);
//        if(value >= node.getValue())
//        {
//            if(node.getRightChild() == null)
//            {
//                node.setRightChild(child);
//            }else
//            {
//                insert(node.getRightChild(), value);
//            }
//        }else
//        {
//            if(node.getLeftChild() == null)
//            {
//                node.setLeftChild(child);
//            }else
//            {
//                insert(node.getLeftChild(), value);
//            }
//        }
    }

    public double getAnswer(Node node)
    {
        return getAnswer(root, node);
    }

    double getAnswer(Node node, Node queryNode)
    {
        if(node == null)
        {
            System.out.println();
        }
        if(!node.isLeaf())
        {
            if(queryNode.getSplit_value() <= node.getSplit_value())
            {
                return getAnswer(node.getLeftChild(), queryNode);
            }
            return getAnswer(node.getRightChild(), queryNode);
        }
        return node.getSplit_value();
    }

//    Tree.Node find(int value)
//    {
//        return find(root, value);
//    }
//
//    Tree.Node find(Tree.Node node, int value)
//    {
//        if(node.getValue() == value) return node;
//        if(value >= node.getValue())
//        {
//            return find(node.getRightChild(), value);
//        }else
//        {
//            return find(node.getLeftChild(), value);
//        }
//    }

//    void remove(int value)
//    {
//        /*
//        1. Find node
//        2. Get it's parent
//        3. check if its right or left node and set to null.
//        4. Remove
//            1. If it has no children - set to null.
//            2. If it has one child - Insert the child to where current node is.
//            3. If it has two children -
//                If root, replace with left child
//                else
//                    if right child is not null replace with right child
//                    if left child is not null but right child is null then recurv
//                    return node
//         */
//    }
}