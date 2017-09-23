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
        return (int)Math.floor((Math.random() * lim));
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

    double getMeanValue(int feature, List<Integer> dataToUse) {
        double mean = 0;
        for (Integer i : dataToUse) {
            String [] d = data.get(i);
            mean += Double.parseDouble(d[d.length - 1]);
        }
        return mean/dataToUse.size();
    }

    //feature, split value, left tree, right tree, left tree is just one step down
    void createTree(List<Integer> dataToUse) {
        String[] node = new String[4];
        int colSize = data.get(0).length;
        int feature = getRandom(colSize-1);
        if (dataToUse.size() == 0) return;
        List<String[]> randomData = getRandomData(dataToUse);
        String[] randD1 = randomData.get(0);
        String[] randD2 = randomData.get(1);
        if (dataToUse.size() <= leaf_size || isSame(dataToUse)) {
            double answer;
            answer = getMeanValue(feature, dataToUse);
            node[0] = "leaf";
            node[1] = String.valueOf(answer);
            node[2] = "N/A";
            node[3] = "N/A";
            tree.add(node);
            //System.out.println(node[0]+" "+node[1]+" "+node[2]+" "+node[3]+" is leaf ");
            return;
        }
        double splitVal = (Double.parseDouble(randD1[feature]) + Double.parseDouble(randD2[feature])) / 2;
        node[0] = String.valueOf(feature);
        node[1] = String.valueOf(splitVal);
        node[2] = "1";
        Map<String, List<Integer>> mapDataToUse = getDataToUse(dataToUse, splitVal, feature);
        List<Integer> leftDataToUse = getUniqueData(mapDataToUse.get("left"));
        List<Integer> rightDataToUse = getUniqueData(mapDataToUse.get("right"));
        int rightIndex = leftDataToUse.size() + 1;
        node[3] = String.valueOf(rightIndex);
        tree.add(node);
        createTree(leftDataToUse);
        createTree(rightDataToUse);
    }

    private boolean isSame(List<Integer> dataToUse) {
        String[] data1 = data.get(dataToUse.get(0));
        String[] c1 = Arrays.copyOf(data1, data1.length - 1);
        for (Integer i : dataToUse) {
            String[] currData = data.get(i);
            String[] c2 = Arrays.copyOf(currData, currData.length - 1);
            if (!Arrays.equals(c1, c2)) {
                return false;
            }
        }
        return true;
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

    private void correctTree() {
        for (int i = tree.size() -1; i >= 0; i--) {
            String[] current = tree.get(i);
            Integer leftTreeIndex;
            try {
                leftTreeIndex = Integer.parseInt(current[current.length - 1]);
                String[] child = tree.get(i+1);
                if (!child[child.length - 2].equals("N/A")) leftTreeIndex++;
                leftTreeIndex = getTreeLength(leftTreeIndex, i+1, child);
                current[current.length - 1] = String.valueOf(leftTreeIndex);
            } catch (Exception e) {

            }
        }
    }

    private int getTreeLength(int len, int index, String[] current) {
        if (current == null) return len;
        if (!current[current.length - 2].equals("N/A")) {
            String [] lChild = tree.get(index + 1);
            if (!lChild[lChild.length - 1].equals("N/A"))
                len++;
            getTreeLength(len, index + 2, lChild);
        }
        if (!current[current.length - 1].equals("N/A")) {
            int rti = Integer.parseInt(current[current.length - 1]) + index;
            String[] rChild = tree.get(rti);
            if (!rChild[rChild.length - 1].equals("N/A")) {
                len++;
            }
            getTreeLength(len, rti, rChild);
        }
        return len;
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
        agent.createTree(initialDataToUse);
        agent.correctTree();
        for (String [] d : agent.tree) {
            System.out.println(Arrays.toString(d));
        }
        int dataSize = agent.data.size();
        ArrayList<Integer> queryDataPoints = new ArrayList<>();
        for(int x = trainData; x < dataSize; x++) {
            queryDataPoints.add(x);
        }
        System.out.println("Correlation for Out-Of-Sample is "+agent.grade(queryDataPoints));
        System.out.println("Correlation for In-Sample data is "+agent.grade(initialDataToUse));
    }
}
