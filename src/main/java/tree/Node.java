package tree;

/**
 * Created by Ayomitunde on 3/29/2017.
 */
public class Node
{
    private Node parent;
    private Node rightChild;
    private Node leftChild;
    private int feature;
    private double split_value;
    private boolean isLeaf = false;

    public Node(double splitValue)
    {
        this.split_value = splitValue;
        parent = null;
        rightChild = null;
        leftChild = null;
    }

    int getFeature()
    {
        return feature;
    }

    Node getRightChild()
    {
        return rightChild;
    }

    Node getLeftChild()
    {
        return leftChild;
    }

    void setRightChild(Node node)
    {
        rightChild = node;
    }

    void setLeftChild(Node node)
    {
        leftChild = node;
    }

    void setParent(Node node)
    {
        parent = node;
    }

    public double getSplit_value() {
        return split_value;
    }

    public void setSplit_value(int split_value) {
        this.split_value = split_value;
    }

    public void setFeature(int feature) {
        this.feature = feature;
    }

    public void setAsLeaf()
    {
        isLeaf = true;
    }

    public boolean isLeaf()
    {
        return isLeaf;
    }
}

