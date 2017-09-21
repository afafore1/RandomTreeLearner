import helper.Parser;
import tree.RandomTreeLearner;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by Ayomitunde on 3/29/2017.
 */
public class Agent
{
    ArrayList<String[]> data = new ArrayList<>();
    private List<String[]> tree = new ArrayList<>();
    static final int leaf_size = 1;


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
    ArrayList<String[]> getRandomData(List<Integer> dataToUse)
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

    double getMeanValue(List<Integer> dataToUse)
    {
        double mean = 0.0;
        for(int i : dataToUse)
        {
            String [] currentData =  data.get(i);
            mean += Double.parseDouble(currentData[currentData.length - 1]);
        }
        return mean/dataToUse.size();
    }

    //feature, split value, left tree, right tree, left tree is just one step down
    void createTree(List<Integer> dataToUse, boolean isLeft) {
        String[] node = new String[4];
        int colSize = data.get(0).length;
        int feature = getRandom(colSize - 1);
        node[0] = String.valueOf(feature);
        if (dataToUse.size() == 0) return;
        List<String[]> randomData = getRandomData(dataToUse);
        String[] randD1 = randomData.get(0);
        String[] randD2 = randomData.get(1);
        if (dataToUse.size() <= leaf_size || Arrays.equals(randD1, randD2)) {
            double answer;
            if (leaf_size > 1 || Arrays.equals(randD1, randD2)) {
                answer = getMeanValue(dataToUse);
            } else {
                answer = (Double.parseDouble(randD1[randD1.length - 1]));
            }
            node[0] = "leaf";
            node[1] = String.valueOf(answer);
            node[2] = "N/A";
            node[3] = "N/A";
            tree.add(node);
            return;
        }
        double splitVal = (Double.parseDouble(randD1[feature]) + Double.parseDouble(randD2[feature])) / 2;
        node[1] = String.valueOf(splitVal);
        node[2] = "1";
        Map<String, List<Integer>> mapDataToUse = getDataToUse(dataToUse, splitVal, feature);
        List<Integer> leftDataToUse = getUniqueData(mapDataToUse.get("left"));
        List<Integer> rightDataToUse = getUniqueData(mapDataToUse.get("right"));
        node[3] = String.valueOf(rightDataToUse.size() + 1);
        tree.add(node);
        createTree(leftDataToUse, true);
        createTree(rightDataToUse, false);
    }

    private List<Integer> getUniqueData(List<Integer> rightDataToUse) {
        List<Integer> rightData = new ArrayList<>();
        List<String[]> datas = new ArrayList<>();
        for (Integer index : rightDataToUse) {
            String[] dataToUse = data.get(index);
            if (!listContains(datas, dataToUse)) {
                datas.add(dataToUse);
                rightData.add(index);
            }
        }
        return rightData;
    }

    private boolean listContains(List<String[]> datas, String[] find) {
        for (String[] d : datas) {
            boolean isSame = false;
            for (int i = 0; i < d.length; i++) {
                if (d[i].equals(find[i])) {
                    isSame = true;
                } else {
                    break;
                }
            }
            if (isSame) return true;
        }
        return false;
    }

    private Map<String, List<Integer>> getDataToUse(List<Integer> dataToUse, double splitValue, int feature) {
        Map<String, List<Integer>> mapDataToUse = new HashMap<>();
        mapDataToUse.put("left", new ArrayList<>());
        mapDataToUse.put("right", new ArrayList<>());
        for (Integer i : dataToUse) {
            String [] currentData = data.get(i);
            double currentSplit = Double.parseDouble(currentData[feature]);
            List<Integer> leftData = mapDataToUse.get("left");
            List<Integer> rightData = mapDataToUse.get("right");
            if (currentSplit <= splitValue) {
                leftData.add(i);
                mapDataToUse.put("left", leftData);
            } else {
                rightData.add(i);
                mapDataToUse.put("right", rightData);
            }
        }
        return mapDataToUse;
    }
    /*
    This method creates the tree. The rtLearner is used to insert to the tree, currentNode is technically
    previous node created so we can add new created node to currentNode. dataToUse is the current data we
    selected based on if it's a rightDataToUse or leftDataToUse. isLeftChild determines where newly
    created node should be placed.
     */
//    void createTree(RandomTreeLearner rtLearner, Node currentNode, ArrayList<Integer> dataToUse, boolean isLeftChild)
//    {
//        int colSize = data.get(0).length;
//        int feature = getRandom(colSize-1); // random feature to be used
//        if(dataToUse.size() == 0) // I don't think this should ever happen
//        {
//            return;
//        }
//        ArrayList<String[]> randomData = getRandomData(dataToUse);
//        String [] randData1 = randomData.get(0);
//        String [] randData2 = randomData.get(1);
//        // Ensure that the size of current data is less than or equal to the leaf size given.
//        if(dataToUse.size() <= leaf_size || Arrays.equals(randData1, randData2))
//        {
//            if(leaf_size > 1 || Arrays.equals(randData1, randData2))
//            {
//                // If greater than 1 then get the average of all the data here
//                double answer = getMeanValue(dataToUse);
//                Node node = new Node(answer);
//                node.setAsLeaf();
//                rtLearner.insert(currentNode, node, isLeftChild);
//                return;
//            }
//            double answer = (Double.parseDouble(randData1[randData1.length-1]));
//            Node node = new Node(answer);
//            node.setAsLeaf();
//            rtLearner.insert(currentNode, node, isLeftChild);
//            return;
//        }
//        double splitValue = (Double.parseDouble(randData1[feature]) + Double.parseDouble(randData2[feature])) / 2;
//        Node node = new Node(splitValue);
//        node.setFeature(feature);
//        rtLearner.insert(currentNode, node, isLeftChild);
//        ArrayList<Integer> leftDataToUse = getNextDataToUse(dataToUse, splitValue, feature, true); // for left
//        createTree(rtLearner, node, leftDataToUse, true);
//        ArrayList<Integer> rightDataToUse = getNextDataToUse(dataToUse, splitValue, feature, false); // for right
//        createTree(rtLearner, node, rightDataToUse, false);
//    }

    double getResult(List<String[]> tree, Integer index, String [] queryData) {
        String [] current = tree.get(index);
        Integer feature = null;
        try {
            feature = Integer.parseInt(current[0]);
        } catch (Exception e) {
            return Double.parseDouble(current[1]);
        }
        double splitValue = Double.parseDouble(current[1]);
        double compare = Double.parseDouble(queryData[feature]);
        if (compare <= splitValue) {
            index += Integer.parseInt(current[2]);
        } else {
            index += Integer.parseInt(current[3]);
        }
        return getResult(tree, index, queryData);
    }

    void printData(ArrayList<Integer> dataPoints) {
        for(Integer i : dataPoints)
        {
            System.out.println(Arrays.toString(data.get(i)));
        }
    }

    double grade(ArrayList<Integer> queryDataPoints)
    {
        double grade = 0;
        double sumOfAnswers = 0;
        double sx = 0; double sy = 0; double sxx = 0; double syy = 0; double sxy = 0;
        for(int x : queryDataPoints)
        {
            String [] queryData = data.get(x);
            double answer = Double.parseDouble(queryData[queryData.length - 1]);
            double predictedAnswer = getResult(tree, 0, queryData);
            sumOfAnswers += Math.pow((answer - predictedAnswer), 2);
            sx+= answer; sy+= predictedAnswer; sxx += Math.pow(answer, 2); syy += Math.pow(predictedAnswer, 2);
            sxy += answer * predictedAnswer;

            if(answer == predictedAnswer) {
                grade++;
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
        File dataFile = Parser.getDataFile("simple.csv");
        agent.readInFile(dataFile);
        ArrayList<Integer> initialDataToUse = new ArrayList<>();
        int trainData = agent.data.size() * 60;
        trainData /= 100; // train with 60% of data
        for(int i = 0; i < trainData; i++)
        {
            initialDataToUse.add(i);
        }
        //agent.createTree(rtLearner, null, initialDataToUse, false);
        agent.createTree(initialDataToUse, false);
        for (String [] d : agent.tree) {
            System.out.println(Arrays.toString(d));
        }
//        System.out.println("Training Data");
//        agent.printData(initialDataToUse);
//        System.out.println("Answers");
        int dataSize = agent.data.size();
        ArrayList<Integer> queryDataPoints = new ArrayList<>();
        // get answer
        for(int x = trainData; x < dataSize; x++)
        {
            String [] queryData = agent.data.get(x);
            //System.out.println(agent.getResult(agent.tree, 0, queryData));
            queryDataPoints.add(x);
        }
        System.out.println("Correlation for Out-Of-Sample is "+agent.grade(queryDataPoints));
//
//        System.out.println("Out of Sample Query Data");
//        agent.printData(queryDataPoints);
//
        System.out.println("In Sample Query Data");
        for(int x = 0; x < trainData; x++)
        {
            String [] inSampleData = agent.data.get(x);
            //System.out.println(agent.getResult(agent.tree, 0, inSampleData));
        }
        System.out.println("Correlation for In-Sample data is "+agent.grade(initialDataToUse));

//        System.out.println("Actual Data");
//        for(String [] d : agent.data)
//        {
//            System.out.println(Arrays.toString(d));
//        }
        //agent.printTree(rtLearner.getRoot());
    }
}
