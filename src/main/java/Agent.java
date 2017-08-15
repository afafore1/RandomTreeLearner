import helper.Parser;
import net.minidev.json.JSONObject;
import tree.Node;
import tree.RandomTreeLearner;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
    static final int leaf_size = 1;
    public static JSONObject solution = new JSONObject();


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

    String [] removeNonASCIIChar(String [] currData)
    {
        String [] data = new String[currData.length];
        for(int i = 0; i < currData.length; i++)
        {
            String d = null;
            try {
                d = new String(currData[i].getBytes("ISO-8859-1"), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            data[i] = d;
        }
        return data;
    }

    ArrayList<Integer> getNextDataToUse(ArrayList<Integer> dataToUse, double splitValue, int feature, boolean isLeft)
    {
        ArrayList<Integer> nextDataToUse = new ArrayList<>();
        if(isLeft)
        {
            for(int i : dataToUse)
            {
                String [] currData = removeNonASCIIChar(data.get(i));
                String f = currData[feature];
                if(f.isEmpty())
                {
                    System.out.println(currData);
                    System.out.println(feature);
                    String s = currData[feature];
                    System.out.println(f);
                }
                double compValue = Double.parseDouble(f);
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
    Select two random rows (unique), if after three tries
    the random rows are the same then this should be used
    as a leaf node and answer can be selected from it.
     */
    ArrayList<String[]> getRandomData(ArrayList<Integer> dataToUse)
    {
        ArrayList<String[]> randomData = new ArrayList<>();
        int randomRow1 = getRandom(dataToUse.size()); // random row to use
        int randomRow2 = getRandom(dataToUse.size());
        int numberOfTries = 0;
        String [] randData1 = data.get(dataToUse.get(randomRow1));
        String [] randData2 = data.get(dataToUse.get(randomRow2));
        // allow up to ten tries to get random data
        while(Arrays.equals(randData1, randData2) && numberOfTries < 10)
        {
            randomRow1 = getRandom(dataToUse.size());
            randomRow2 = getRandom(dataToUse.size());
            randData1 = data.get(dataToUse.get(randomRow1));
            randData2 = data.get(dataToUse.get(randomRow2));
            numberOfTries++;
        }
        randomData.add(randData1);
        randomData.add(randData2);
        return  randomData;
    }

    double getMeanValue(ArrayList<Integer> dataToUse)
    {
        double mean = 0.0;
        for(int i : dataToUse)
        {
            String [] currentData =  data.get(i);
            mean += Double.parseDouble(currentData[currentData.length - 1]);
        }
        return mean/dataToUse.size();
    }

    /*
    This method creates the tree. The rtLearner is used to insert to the tree, currentNode is technically
    previous node created so we can add new created node to currentNode. dataToUse is the current data we
    selected based on if it's a rightDataToUse or leftDataToUse. isLeftChild determines where newly
    created node should be placed.
     */
    void createTree(RandomTreeLearner rtLearner, Node currentNode, ArrayList<Integer> dataToUse, boolean isLeftChild)
    {
        int colSize = data.get(0).length;
        int feature = getRandom(colSize-1); // random feature to be used
        if(dataToUse.size() == 0) // I don't think this should ever happen
        {
            return;
        }
        ArrayList<String[]> randomData = getRandomData(dataToUse);
        String [] randData1 = randomData.get(0);
        String [] randData2 = randomData.get(1);
        // Ensure that the size of current data is less than or equal to the leaf size given.
        if(dataToUse.size() <= leaf_size || Arrays.equals(randData1, randData2))
        {
            if(leaf_size > 1 || Arrays.equals(randData1, randData2))
            {
                // If greater than 1 then get the average of all the data here
                double answer = getMeanValue(dataToUse);
                Node node = new Node(answer);
                node.setAsLeaf();
                rtLearner.insert(currentNode, node, isLeftChild, solution);
                return;
            }
            double answer = (Double.parseDouble(randData1[randData1.length-1]));
            Node node = new Node(answer);
            node.setAsLeaf();
            rtLearner.insert(currentNode, node, isLeftChild, solution);
            return;
        }
        double splitValue = (Double.parseDouble(randData1[feature]) + Double.parseDouble(randData2[feature])) / 2;
        Node node = new Node(splitValue);
        node.setFeature(feature);
        rtLearner.insert(currentNode, node, isLeftChild, solution);
        ArrayList<Integer> leftDataToUse = getNextDataToUse(dataToUse, splitValue, feature, true); // for left
        createTree(rtLearner, node, leftDataToUse, true);
        ArrayList<Integer> rightDataToUse = getNextDataToUse(dataToUse, splitValue, feature, false); // for right
        createTree(rtLearner, node, rightDataToUse, false);
    }

    double getResult(String [] queryData, RandomTreeLearner rtLearner)
    {
        return rtLearner.getAnswer(queryData);
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
        double sumOfAnswers = 0;
        double sx = 0; double sy = 0; double sxx = 0; double syy = 0; double sxy = 0;
        for(int x : queryDataPoints)
        {
            String [] queryData = data.get(x);
            double answer = Double.parseDouble(queryData[queryData.length - 1]);
            double predictedAnswer = getResult(queryData, rtLearner);
            sumOfAnswers += Math.pow((answer - predictedAnswer), 2);
            sx+= answer; sy+= predictedAnswer; sxx += Math.pow(answer, 2); syy += Math.pow(predictedAnswer, 2);
            sxy += answer * predictedAnswer;

            if(answer == predictedAnswer)
            {
                grade++;
            }else
            {
                System.out.println("Answer off by "+Math.abs(answer - predictedAnswer));
            }
        }
        int dataLen = queryDataPoints.size();
        double covariation = sxy / dataLen - sx * sy/ dataLen /dataLen;
        // standard error of x
        double sigmaX = Math.sqrt(sxx/dataLen - Math.pow(sx, 2) / dataLen/dataLen);
        double sigmaY = Math.sqrt(syy/dataLen - Math.pow(sy, 2) / dataLen/dataLen);
        double correlation = covariation/sigmaX/sigmaY;
        sumOfAnswers /= queryDataPoints.size();
        System.out.println("Exact grade answer is "+grade/queryDataPoints.size());
        //System.out.println("Correlation is "+correlation);
        sumOfAnswers = Math.sqrt(sumOfAnswers);
        return correlation;
    }

    public static void main(String [] args)
    {
        Agent agent = new Agent();
        RandomTreeLearner rtLearner = new RandomTreeLearner();
        File dataFile = Parser.getDataFile("Istanbul.csv");
        agent.readInFile(dataFile);
        ArrayList<Integer> initialDataToUse = new ArrayList<>();
        int trainData = agent.data.size() * 60;
        trainData /= 100; // train with 60% of data
        for(int i = 0; i < trainData; i++)
        {
            initialDataToUse.add(i);
        }
        agent.createTree(rtLearner, null, initialDataToUse, false);
        System.out.println("Training Data");
        //agent.printData(initialDataToUse);
        System.out.println("Answers");
        ArrayList<Integer> queryDataPoints = new ArrayList<>();
        // get answer
        for(int x = trainData; x < agent.data.size(); x++) {
            String [] queryData = agent.data.get(x);
            System.out.println(agent.getResult(queryData, rtLearner));
            queryDataPoints.add(x);
        }
        System.out.println("Correlation for Out-Of-Sample is "+agent.grade(rtLearner, queryDataPoints));

        System.out.println("Out of Sample Query Data");
        agent.printData(queryDataPoints);

        System.out.println("In Sample Query Data");
        for(int x : initialDataToUse)
        {
            String [] inSampleData = agent.data.get(x);
            System.out.println(agent.getResult(inSampleData, rtLearner));
        }
        System.out.println("Correlation for In-Sample data is "+agent.grade(rtLearner, initialDataToUse));
        System.out.println(solution.toString());
        try {
            Parser.saveSolution(solution);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        System.out.println("Actual Data");
//        for(String [] d : agent.data)
//        {
//            System.out.println(Arrays.toString(d));
//        }
        //agent.printTree(rtLearner.getRoot());
    }
}
