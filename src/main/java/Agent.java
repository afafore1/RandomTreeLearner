import helper.Parser;
import tree.Node;
import tree.RandomTreeLearner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by Ayomitunde on 3/29/2017.
 */
public class Agent
{
    ArrayList<String[]> data = new ArrayList<>();
    private int sameSplitCount = 0;


    void readInFile(File dataFile)
    {
        try(Stream<String> stream = Files.lines(Paths.get(dataFile.getAbsolutePath())))
        {
            stream.forEach(s->{
                String [] sArr = s.split(",");
                data.add(sArr);
            });
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    int getRandom(int lim)
    {
        return (int)(Math.random() * lim);
    }

    ArrayList<Integer> getNextDataToUse(ArrayList<Integer> dataToUse, double splitValue, int feature, boolean isLeft)
    {
        ArrayList<Integer> nextDataToUse = new ArrayList<>();
        if(isLeft)
        {
            for(int i : dataToUse)
            {
                String [] currData = data.get(i);
                double compValue = Double.parseDouble(currData[feature]);
                if(compValue <= splitValue)
                {
                    nextDataToUse.add(i);
                }
            }
            return nextDataToUse;
        }
        for(int i : dataToUse)
        {
            String [] currData = data.get(i);
            double compValue = Double.parseDouble(currData[feature]);
            if(compValue > splitValue)
            {
                nextDataToUse.add(i);
            }
        }
        return nextDataToUse;
    }

    void createTree(RandomTreeLearner rtLearner, ArrayList<Integer> dataToUse, boolean isLeftChild)
    {
        int colSize = data.get(0).length;
        int randomRow1 = getRandom(dataToUse.size()); // random row to use
        int randomRow2 = getRandom(dataToUse.size());
        int feature = getRandom(colSize-1); // random feature to be used
        String [] randData1 = data.get(dataToUse.get(randomRow1));
        String [] randData2 = data.get(dataToUse.get(randomRow2));
        double splitValue = (Double.parseDouble(randData1[feature]) + Double.parseDouble(randData2[feature])) / 2;
        Node node = new Node(splitValue);
        if(dataToUse.size() <= 2)
        {
            int dataLen = randData1.length -1;
            double answer = (Double.parseDouble(randData1[dataLen]) + Double.parseDouble(randData2[dataLen])) / 2;
            node.setAsLeaf(answer);
        }else
        {
            node.setFeature(feature);
        }
        rtLearner.insert(node, isLeftChild);
        ArrayList<Integer> leftDataToUse = getNextDataToUse(dataToUse, splitValue, feature, true); // for left
        if(node.isLeaf())
        {
           return;
        }
        createTree(rtLearner, leftDataToUse, true);
        ArrayList<Integer> rightDataToUse = getNextDataToUse(dataToUse, splitValue, feature, false); // for right
        createTree(rtLearner, rightDataToUse, false);

        //createTree(rtLearner, rightDataToUse, false);
        System.out.println("\nFeature selected: "+feature+"\nRandom col 1: "+randomRow1+"\nRandom col 2: "+randomRow2);
    }

    double getResult(String [] queryData, RandomTreeLearner rtLearner)
    {
        Node root = rtLearner.getRoot();
        int feature = root.getFeature();
        double splitValue = Double.parseDouble(queryData[feature]);
        Node node = new Node(splitValue);
        return rtLearner.getAnswer(node);
    }

    public static void main(String [] args)
    {
        Agent agent = new Agent();
        RandomTreeLearner rtLearner = new RandomTreeLearner();
        File dataFile = Parser.getDataFile("simple.csv");
        agent.readInFile(dataFile);
        ArrayList<Integer> initialDataToUse = new ArrayList<>();
        int trainData = agent.data.size() * 70;
        trainData /= 100; // train with 70% of data
        for(int i = 0; i < trainData; i++)
        {
            initialDataToUse.add(i);
        }
        agent.createTree(rtLearner, initialDataToUse, false);
        // get answer
        for(int x = trainData; x < agent.data.size(); x++)
        {
            String [] queryData = agent.data.get(x);
            System.out.println(agent.getResult(queryData, rtLearner));
        }

        for(String [] d : agent.data)
        {
            System.out.println(Arrays.toString(d));
        }
    }
}
