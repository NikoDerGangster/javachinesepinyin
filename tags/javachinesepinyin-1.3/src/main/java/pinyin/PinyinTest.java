/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pinyin;

import hmm.HmmResult;
import hmm.ObserveListException;
import hmm.Viterbi;
import java.io.*;
import java.util.*;
import pinyin.util.Pair;

/**
 *
 * @author ray
 */
public class PinyinTest {

    PinyinToWord ptw = new PinyinToWord();
    int entryCount = 1;
    int correctCount = 0;

    public PinyinTest() {
        ptw.load("ptw.m");
        System.out.println("ptw load completed.");
        ptw.init();
        System.out.println("ptw init completed.");
    }

    public void testFile(File file) {
        System.out.println(file.getName());
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                    new FileInputStream(file), "UTF-8"));
            List<Pair<String, String>> sentence = readSentence(br);
            while (null != sentence) {
                entryCount += sentence.size();
                correctCount += getCorrectCount(sentence);
                sentence = readSentence(br);
            }
            br.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void printTestResult() {
        System.out.println("Test set size: " + entryCount);
        System.out.println("Correct count: " + correctCount);
        System.out.println("Precise rate: " + (double)correctCount / (double)entryCount);
    }

    private List<Pair<String, String>> readSentence(BufferedReader br) throws IOException {
        List<Pair<String, String>> ret = null;

        String line = br.readLine();
        while (line != null) {
            if (null == ret) {
                ret = new ArrayList<Pair<String, String>>();
            }

            if (line.startsWith("c") || "".equals(line.trim())) {
                break;
            } else {
                String[] pair = line.split("\\s");

                if (null != pair && pair.length == 2) {
                    Pair<String, String> p = new Pair<String, String>(pair[1], pair[0].replace("Head", ""));
                    ret.add(p);
                }
            }

            line = br.readLine();
        }

        return ret;
    }

    private int getCorrectCount(List<Pair<String, String>> testSet) {
        int c = 0;

        String[] pinyin = new String[testSet.size()];
        String[] words = new String[testSet.size()];
        for (int i = 0; i < testSet.size(); i++) {
            Pair<String, String> pair = testSet.get(i);
            pinyin[i] = pair.getKey();
            words[i] = pair.getValue();
        }

        String[] ret = pinyinToWord(pinyin);

        if (null != ret && ret.length == pinyin.length) {
            for (int i = 0; i < ret.length; i++) {
                if (ret[i].equals(words[i])) {
                    c++;
                }
                System.out.println(words[i] + "\t" + ret[i]);
            }
        }

        return c;
    }

    private String[] pinyinToWord(String[] o) {
        HmmResult ret = null;
        try {
            ret = ptw.viterbi(o);
        } catch (ObserveListException ex) {
            System.out.println(ex.getMessage());
        }
        Map<Double, String> results = new HashMap<Double, String>();
        if (null != ret && ret.states != null) {
            for (int pos = 0; pos < ret.states[o.length - 1].length; pos++) {
                StringBuilder sb = new StringBuilder();
                int[] statePath = Viterbi.getStatePath(ret.states, ret.psai, o.length - 1, o.length, pos);
                for (int state : statePath) {
                    Character name = ptw.getStateBank().get(state).getName();
                    sb.append(name).append(" ");
                }
                results.put(ret.delta[o.length - 1][pos], sb.toString());
            }
            List<Double> list = new ArrayList<Double>(results.keySet());
            Collections.sort(list);
            Collections.reverse(list);
            return results.get(list.get(0)).trim().split(" ");
        }
        return null;
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            PinyinTest t = new PinyinTest();
            File dir = new File(args[0]);
            if (dir.isDirectory() && dir.exists()) {
                for (File f : dir.listFiles()) {
                    String fname = f.getName();
                    if (fname.endsWith(".txt")) {
                        t.testFile(f);
                    }
                }
                t.printTestResult();
            }
        } else {
            System.out.println("Please input directory of corpus:\n\t/>./test.sh ../corpus/LCMC");
        }
    }
}
