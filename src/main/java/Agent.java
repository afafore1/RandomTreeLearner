import helper.Parser;
import tree.Node;
import tree.RandomTreeLearner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
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
    /*
    The random data being picked should be unique,
     */
    ArrayList<String[]> getRandomData(ArrayList<Integer> dataToUse)
    {
        ArrayList<String[]> randomData = new ArrayList<>();
        int randomRow1 = getRandom(dataToUse.size()); // random row to use
        int randomRow2 = getRandom(dataToUse.size());
        int numberOfTries = 0; // allow up to three
        while(randomRow1 == randomRow2 && numberOfTries < 3)
        {
            randomRow1 = getRandom(dataToUse.size());
            randomRow2 = getRandom(dataToUse.size());
            numberOfTries++;
        }
        String [] randData1 = data.get(dataToUse.get(randomRow1));
        String [] randData2 = data.get(dataToUse.get(randomRow2));
        randomData.add(randData1);
        randomData.add(randData2);
        return  randomData;
    }

    void createTree(RandomTreeLearner rtLearner, Node currentNode, ArrayList<Integer> dataToUse, boolean isLeftChild)
    {
        int colSize = data.get(0).length;
        int feature = getRandom(colSize-1); // random feature to be used
        if(dataToUse.size() == 0)
        {
            return;
        }
        ArrayList<String[]> randomData = getRandomData(dataToUse);
        String [] randData1 = randomData.get(0);
        String [] randData2 = randomData.get(1);
        double splitValue = (Double.parseDouble(randData1[feature]) + Double.parseDouble(randData2[feature])) / 2;
        Node node = new Node(splitValue);
        if(dataToUse.size() <= 2)
        {
            int dataLen = randData1.length - 1;
            double answer = (Double.parseDouble(randData1[dataLen]) + Double.parseDouble(randData2[dataLen])) / 2;
            node.setAsLeaf(answer);
            rtLearner.insert(currentNode, node, isLeftChild);
            return;
        }
        node.setFeature(feature);
        rtLearner.insert(currentNode, node, isLeftChild);
        ArrayList<Integer> leftDataToUse = getNextDataToUse(dataToUse, splitValue, feature, true); // for left
        //if(leftDataToUse.isEmpty()) return;
        createTree(rtLearner, node, leftDataToUse, true);
        ArrayList<Integer> rightDataToUse = getNextDataToUse(dataToUse, splitValue, feature, false); // for right
        //if(rightDataToUse.isEmpty()) return;
        createTree(rtLearner, node, rightDataToUse, false);

        //createTree(rtLearner, rightDataToUse, false);
        //System.out.println("\nFeature selected: "+feature+"\nRandom col 1: "+randomRow1+"\nRandom col 2: "+randomRow2);
    }

    double getResult(String [] queryData, RandomTreeLearner rtLearner)
    {
        Node root = rtLearner.getRoot();
        int feature = root.getFeature();
        double splitValue = Double.parseDouble(queryData[feature]);
        Node node = new Node(splitValue);
        return rtLearner.getAnswer(node);
    }

    void printData(ArrayList<Integer> dataPoints)
    {
        for(Integer i : dataPoints)
        {
            System.out.println(Arrays.toString(data.get(i)));
        }
    }

    double grade(RandomTreeLearner rtLearner, ArrayList<Integer> queryDataPoints)
    {
        double grade = 0;
        for(int x : queryDataPoints)
        {
            String [] queryData = data.get(x);
            double answer = Double.parseDouble(queryData[queryData.length - 1]);
            double predictedAnswer = getResult(queryData, rtLearner);
            if(answer == predictedAnswer) grade++;
        }
        return grade/queryDataPoints.size();
    }

    public static void main(String [] args)
    {
        Agent agent = new Agent();
        RandomTreeLearner rtLearner = new RandomTreeLearner();
        File dataFile = Parser.getDataFile("3_groups.csv");
        agent.readInFile(dataFile);
        ArrayList<Integer> initialDataToUse = new ArrayList<>();
        int trainData = agent.data.size() * 90;
        trainData /= 100; // train with 70% of data
        for(int i = 0; i < trainData; i++)
        {
            initialDataToUse.add(i);
        }
        agent.createTree(rtLearner, null, initialDataToUse, false);
        System.out.println("Training Data");
        agent.printData(initialDataToUse);
        System.out.println("Answers");
        ArrayList<Integer> queryDataPoints = new ArrayList<>();
        // get answer
        for(int x = trainData; x < agent.data.size(); x++)
        {
            String [] queryData = agent.data.get(x);
            System.out.println(agent.getResult(queryData, rtLearner));
            queryDataPoints.add(x);
        }

        System.out.println("Query Data");
        agent.printData(queryDataPoints);
        System.out.println("Actual Data");
        for(String [] d : agent.data)
        {
            System.out.println(Arrays.toString(d));
        }

        System.out.println("Final grade is "+agent.grade(rtLearner, queryDataPoints));
    }
}
