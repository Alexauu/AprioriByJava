import java.io.*;
import java.util.*;
/**
 * @ClassName Apriori
 * @Description Apriori
 * @Author alexau
 * @Date 2019/10/24 18:22
 */

public class Apriori {

    private static final int MIN_SUPPORT = 2;
    private static List<String> dataList = new ArrayList<>();
    private static Map<String, Integer> frequentSet = new HashMap<>();

    public static void importData() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("data.dat"))));
        String data;
        while((data = bufferedReader.readLine()) != null){
            dataList.add(data);
            String[] is = data.split(" ");
            for (String i:is) {
                if (frequentSet.containsKey(i)){
                    frequentSet.put(i, frequentSet.get(i) + 1);
                }else frequentSet.put(i, 1);
            }
        }
        bufferedReader.close();
    }

    public static void startApriori() {
        int i = 1;
        do{
            System.out.println("第 " + i +" 级频繁项集为：");
            System.out.println("<项 集>:频率");
            for (Map.Entry<String, Integer> entry : frequentSet.entrySet()){
                System.out.println("< "+ entry.getKey() +" >:"+entry.getValue());
            }
            System.out.println("-----------------");
        }while ((frequentSet = getCandidateSet(frequentSet, ++i)).size() > 0);

    }

    /**
     * 通过连接步和剪枝步，产生候选k-项集
     */
    private static Map<String, Integer> getCandidateSet(Map<String, Integer> lastFrequentSet, int k){

        Map<String, Integer> candidateSet = new HashMap<>();

        Set<String> frequentSet = lastFrequentSet.keySet();

        //得到频繁集
        for (String L1: frequentSet){
            String linked = null;
            for (String L2 : frequentSet) {
                if (L1.equals(L2)) continue;
                if (canLink(L1, L2, k)){
                    linked = L1 + " " +L2.split(" ")[k-2];
                }
                //剪枝:第二次扫描不需要剪枝
                if (linked !=null && k == 2 || isFrequent(lastFrequentSet, linked)){
                    int support = computeSupport(linked);
                    if (support >= MIN_SUPPORT)
                        candidateSet.put(linked, computeSupport(linked));
                }
            }
        }
        return candidateSet;
    }

    /**
     * 判断是否两个项集可以连接
     */
    private static boolean canLink(String first, String secend, int k){
        if (k == 2) return (Integer.valueOf(first) < Integer.valueOf(secend));
        boolean result = true;
        String[] set1 = first.split(" ");
        String[] set2 = secend.split(" ");
        int length = k-2;
        for (int i=0;i<length;i++){
            if (set1[i].equals(set2[i]))
                continue;
            else {
                result = false;
                break;
            }
        }
        if (result) return Integer.valueOf(set1[set1.length-1]) < Integer.valueOf(set2[set2.length-1]);
        return false;
    }

    /**
     * 判断自然连接得到的集合是否为频繁集
     */
    private static boolean isFrequent(Map<String, Integer> lastFrequentSet, String linked){
        if (linked == null) return false;
        boolean result = true;
        String[] set = linked.split(" ");
        int length = set.length;
        //双重循环得到linked的真子集
        for (int i = 0; i < length; i++) {
            String str = "";
            for (int j = 0; j < length; j++) {
                if (i == j) continue;
                str += set[j] + " " ;
            }
            str = str.substring(0,str.length()-1);
            if (lastFrequentSet.containsKey(str))
                continue;
            else {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * 计算候补集支持度
     */
    private static int computeSupport(String subset){
        int support = 0;
        String[] strings = subset.split(" ");
        boolean isInclude;
        for (String set : dataList) {
            isInclude = true;
            for (String s : strings) {
                if (set.contains(s))
                    continue;
                else {
                    isInclude = false;
                    break;
                }
            }
            if (isInclude)
             support++;
        }
        return support;
    }
}
