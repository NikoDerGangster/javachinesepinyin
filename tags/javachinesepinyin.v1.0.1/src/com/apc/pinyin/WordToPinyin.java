/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apc.pinyin;

import com.apc.util.Pair;
import com.apc.util.Window;
import hmm.*;
import hmm.ngram.*;
import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author ray
 */
public class WordToPinyin {

    Viterbi<String, Character> viterbi = new Viterbi<String, Character>();
    //
    Map<Integer, Integer> pii = new HashMap<Integer, Integer>();
    Map<Integer, Map<Integer, Integer>> emisMatrix = new HashMap<Integer, Map<Integer, Integer>>();
    NodeBank<String, Node<String>> stateBank = new NodeBank<String, Node<String>>();
    NodeBank<Character, Node<Character>> observeBank = new NodeBank<Character, Node<Character>>();
    //
    TreeNode<String> ngram = new TreeNode<String>();
    TreeNodeSortor<String> sortor = null;
    Comparator<String> comparator = null;

    public WordToPinyin() {
        sortor = new TreeNodeBinarySort<String>();
        comparator = new Comparator<String>() {

            public int compare(String t, String t1) {
                return t.compareTo(t1);
            }
        };

        sortor.setComparator(new Comparator<TreeNode<String>>() {

            public int compare(TreeNode<String> t, TreeNode<String> t1) {
                return t.getKey().compareTo(t1.getKey());
            }
        });
    }

    public NodeBank<Character, Node<Character>> getObserveBank() {
        return observeBank;
    }

    public NodeBank<String, Node<String>> getStateBank() {
        return stateBank;
    }

    public void setN(int n) {
        viterbi.setN(n);
    }

    public void init() {

//        ngram.buildIndex(ngram.getCount(), sortor);

        viterbi.setObserveBank(observeBank);
        viterbi.setStateBank(stateBank);

        Transition<String> tran = new Transition<String>(ngram, stateBank);
        tran.setComparator(comparator);
        tran.setSortor(sortor);

        Emission emis = new Emission(emisMatrix);
        emis.setObserveBank(observeBank);

        Pi pi = new Pi(pii);

        viterbi.setPi(pi);
        viterbi.setTran(tran);
        viterbi.setE(emis);

        pii.clear();
//        tranMatrix.clear();
        emisMatrix.clear();
        pii = null;
//        tranMatrix = null;
        emisMatrix = null;
    }

    public List<Node<String>> classify(Character[] o) {
        List<Character> observeList = new ArrayList<Character>(o.length);
        for (Character ob : o) {
//            Node<String> observe = observeBank.get(ob);
            observeList.add(ob);
        }
        return classify(observeList);
    }

    public List<Node<String>> classify(List<Character> o) {
        return viterbi.caculateWithLog(o);
    }

    public HmmResult viterbi(char[] o) {
        List<Character> observeList = new ArrayList<Character>(o.length);
        for (Character ob : o) {
//            Node observe = observeBank.get(ob);
            observeList.add(ob);
        }
        return viterbi(observeList);
    }

    public HmmResult viterbi(List<Character> o) {
        return viterbi.caculateHmmResult(o);
    }

    public void save(String filename) {
        try {
            ngram.buildIndex(ngram.getCount(), sortor);
            ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(filename)));
            oos.writeObject(stateBank);
            oos.writeObject(observeBank);
            oos.writeObject(pii);
//            oos.writeObject(tranMatrix);
            oos.writeObject(emisMatrix);
            oos.writeObject(ngram);
            oos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void load(String filename) {
        File file = new File(filename);
        InputStream is = null;
        if (!file.exists()) {
            is = this.getClass().getClassLoader().getResourceAsStream("ptw.m");
            load(is);
        } else {
            try {
                is = new FileInputStream(file);
                load(is);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void load(InputStream is) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(is));
            stateBank = (NodeBank<String, Node<String>>) ois.readObject();
            observeBank = (NodeBank<Character, Node<Character>>) ois.readObject();
            pii = (Map<Integer, Integer>) ois.readObject();
//            tranMatrix = (Map<String, Map<Integer, Integer>>) ois.readObject();
            emisMatrix = (Map<Integer, Map<Integer, Integer>>) ois.readObject();
            ngram = (TreeNode<String>) ois.readObject();
            is.close();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void train(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            System.out.println("train file " + file.getAbsolutePath());
            try {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                        new FileInputStream(file), "UTF-8"));
                String line = br.readLine();
                Window<Pair<String, String>> window = new Window<Pair<String, String>>(Flag.n);
                Pair<String, String> cur = null;
                while (null != line) {
                    cur = parseLine(line.trim());

                    if (null == cur) {
                        window.clear();
                    } else {
                        window.add(cur);
                        String o = cur.getKey();
                        boolean isHeadOfWord = o.startsWith("Head");
                        o = isHeadOfWord ? o.substring(4) : o;
                        Node<Character> observe = new Observe<Character>(Character.valueOf(o.charAt(0)));
                        observe = observeBank.add(observe);
                        String s = cur.getValue();
                        Node<String> state = new State(s);
                        state = stateBank.add(state);

                        //Pii
//                        if (isHeadOfWord)
                        {
                            int index = state.getIndex();
                            int c = pii.containsKey(index) ? pii.get(index) + 1 : 1;
                            pii.put(index, c);
                        }

                        //Transition
                        Pair<String, String> array[] = window.toArray(new Pair[0]);
//                        for (int i = 0; i < array.length; i++) {
                        statisticNGram(array);
//                        }

                        //Emission
                        int si = state.getIndex();
                        int oi = observe.getIndex();
                        Map<Integer, Integer> row = null;
                        if (emisMatrix.containsKey(si)) {
                            row = emisMatrix.get(si);
                        } else {
                            row = new HashMap<Integer, Integer>();
                            emisMatrix.put(si, row);
                        }
                        int count = row.containsKey(oi) ? row.get(oi) + 1 : 1;
                        row.put(oi, count);
                    }
                    line = br.readLine();
                }
                br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void statisticNGram(Pair<String, String> array[]) {
        String[] ch = new String[array.length];
        for (int i = 0; i < ch.length; i++) {
            ch[i] = array[i].getValue();
        }

        ngram.insert(ch, sortor, comparator);
    }

    private Pair<String, String> parseLine(String line) {
        Pair<String, String> ret = null;
        if ("".equals(line) || line.startsWith("c")) {
            return ret;
        }

        String[] pair = line.split("\\s");

        if (null != pair && pair.length == 2) {
            ret = new Pair<String, String>(pair[0], pair[1]);
        }

        return ret;
    }

    public static void main(String[] args) {
        WordToPinyin c = new WordToPinyin();
        c.train("outputA.txt");
        c.train("outputB.txt");
        c.train("outputC.txt");
        c.train("outputD.txt");
        c.train("outputE.txt");
        c.train("outputF.txt");
        c.train("outputG.txt");
        c.train("outputH.txt");
        c.train("outputI.txt");
        c.train("outputJ.txt");
        c.train("outputK.txt");
        c.train("outputL.txt");
        c.train("outputM.txt");
        c.train("outputN.txt");
        c.train("outputP.txt");
        c.train("outputR.txt");
        c.save("wtp.m");

//        c.load("wtp.m");
//        c.init();
//        Character [] o = {'天','安','门'};
//        List<Node<String>> s = c.classify(o);
//        for (Node state : s) {
//            System.out.print(state.getName() + " ");
//        }
//        System.out.println();
    }
}
