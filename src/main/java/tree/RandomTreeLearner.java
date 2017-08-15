package tree;

import helper.Parser;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import java.io.File;
import java.io.IOException;

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

    public void insert(Node currentNode, Node node, boolean isLeftChild, JSONObject solution) {
        if(currentNode == null) {
            root = node;
            System.out.println("Root has "+node.getSplit_value()+" with feature "+node.getFeature());
            return;
        }

        JSONObject obj;
        if (solution.get(String.valueOf(currentNode.getSplit_value())) == null) {
            obj = new JSONObject();
            solution.put(String.valueOf(currentNode.getSplit_value()), obj);
        } else {
            obj = (JSONObject) solution.get(String.valueOf(currentNode.getSplit_value()));
        }
        obj.put("feature", currentNode.getFeature());
        solution.put(String.valueOf(currentNode.getSplit_value()), obj);
        if(isLeftChild) {
            currentNode.setLeftChild(node);
            System.out.println("Left Child of "+currentNode.getSplit_value()+" "+" has "+node.getSplit_value()+" with feature "+node.getFeature());
            obj.put("leftChild", String.valueOf(node.getSplit_value()));
        } else {
            currentNode.setRightChild(node);
            System.out.println("Right Child of "+currentNode.getSplit_value()+" "+" has "+node.getSplit_value()+" with feature "+node.getFeature());
            obj.put("rightChild", String.valueOf(node.getSplit_value()));
        }
        if(node.isLeaf()) {
            System.out.println("Leaf has answer "+node.getSplit_value());
            obj.put("leaf", String.valueOf(node.getSplit_value()));
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

    public double getAnswer(String [] queryData)
    {
        try {
            return getAnswer(root, queryData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Double.MIN_VALUE;
    }

    double getAnswer(Node node, String [] queryData) throws Exception {
        int feature = node.getFeature();
        double splitValue = Double.parseDouble(queryData[feature]);
        Node queryNode = new Node(splitValue);
        if(!node.isLeaf())
        {
            if(queryNode.getSplit_value() <= node.getSplit_value())
            {
                if(node.getLeftChild() != null) // this should not happen
                {
                    return getAnswer(node.getLeftChild(), queryData);
                }
            }
            if(node.getRightChild() != null)
            {
                return getAnswer(node.getRightChild(), queryData);
            }
        }
        return node.getSplit_value();
    }

//    double getAnswer(Node node, Node queryNode)
//    {
//        if(node == null)
//        {
//            System.out.println();
//        }
//        if(!node.isLeaf())
//        {
//            if(queryNode.getSplit_value() <= node.getSplit_value())
//            {
//                int leftFeature = node.getLeftChild().getFeature();
//
//                return getAnswer(node.getLeftChild(), queryNode);
//            }
//            return getAnswer(node.getRightChild(), queryNode);
//        }
//        return node.getSplit_value();
//    }

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