/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apc.pinyin;

import com.apc.indextask.idx.Idx;
import com.apc.util.Pair;
import com.apc.util.Window;
import hmm.*;
import hmm.ngram.TreeNode;
import hmm.ngram.TreeNodeQuickSort;
import hmm.ngram.TreeNodeSortor;
import java.io.*;
import java.util.*;

/**
 *
 * @author ray
 */
public class PinyinToWord {

    Viterbi<Character, String> viterbi = new Viterbi<Character, String>();
    //
    Map<Integer, Integer> pii = new HashMap<Integer, Integer>();
    Map<Integer, Map<Integer, Integer>> emisMatrix = new HashMap<Integer, Map<Integer, Integer>>();
    NodeBank<Character, Node<Character>> stateBank = new NodeBank<Character, Node<Character>>();
    NodeBank<String, Node<String>> observeBank = new NodeBank<String, Node<String>>();
    //
    TreeNode<Character> ngram = new TreeNode<Character>();
    TreeNodeSortor<Character> sortor = null;
    Comparator<Character> comparator = null;

    public PinyinToWord() {
        sortor = new TreeNodeQuickSort<Character>();
        comparator = new Comparator<Character>() {

            public int compare(Character t, Character t1) {
                return t.charValue() - t1.charValue();
            }
        };

        sortor.setComparator(new Comparator<TreeNode<Character>>() {

            public int compare(TreeNode<Character> t, TreeNode<Character> t1) {
                return t.getKey() - t1.getKey();
            }
        });
    }

    public void setN(int n) {
        viterbi.setN(n);
    }

    public void init() {

        ngram.buildIndex(ngram.getCount(), sortor);

        viterbi.setObserveBank(observeBank);
        viterbi.setStateBank(stateBank);

        Transition<Character> tran = new Transition<Character>(ngram, stateBank);
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

    public List<Node<Character>> classify(String[] o) {
        List<String> observeList = new ArrayList<String>(o.length);
        for (String ob : o) {
//            Node<String> observe = observeBank.get(ob);
            observeList.add(ob);
        }
        return classify(observeList);
    }

    public List<Node<Character>> classify(List<String> o) {
        return viterbi.caculateWithLog(o);
    }

    public HmmResult viterbi(String[] o) {
        List<String> observeList = new ArrayList<String>(o.length);
        for (String ob : o) {
//            Node observe = observeBank.get(ob);
            observeList.add(ob);
        }
        return viterbi(observeList);
    }

    public HmmResult viterbi(List<String> o) {
        return viterbi.caculateHmmResult(o);
    }

    public void save(String filename) {
        try {
            ngram.buildIndex(ngram.getCount(), sortor);
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
            oos.writeObject(stateBank);
            oos.writeObject(observeBank);
            oos.writeObject(pii);
//            oos.writeObject(tranMatrix);
            oos.writeObject(emisMatrix);
            oos.writeObject(ngram);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void load(String filename) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
            stateBank = (NodeBank<Character, Node<Character>>) ois.readObject();
            observeBank = (NodeBank<String, Node<String>>) ois.readObject();
            pii = (Map<Integer, Integer>) ois.readObject();
//            tranMatrix = (Map<String, Map<Integer, Integer>>) ois.readObject();
            emisMatrix = (Map<Integer, Map<Integer, Integer>>) ois.readObject();
            ngram = (TreeNode<Character>) ois.readObject();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void train(String filename) {
        File file = new File(filename);
        if (file.exists()) {
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
                        String s = cur.getKey();
                        boolean isHeadOfWord = s.startsWith("Head");
                        s = isHeadOfWord ? s.substring(4) : s;
                        Node<Character> state = new State<Character>(Character.valueOf(s.charAt(0)));
                        state = stateBank.add(state);
                        String o = cur.getValue();
                        Node observe = new Observe(o);
                        observe = observeBank.add(observe);

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
        Character[] ch = new Character[array.length];
        for (int i = 0; i < ch.length; i++) {
            ch[i] = array[i].getKey().startsWith("Head") ? array[i].getKey().charAt(4) : array[i].getKey().charAt(0);
        }

        ngram.insert(ch, sortor, comparator);
    }

    private void statisticNGram(Node<Character> array[]) {
        Character[] ch = new Character[array.length];
        for (int i = 0; i < ch.length; i++) {
            ch[i] = array[i].getName();
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

    public void trainNgram(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                        new FileInputStream(file), "UTF-8"));

                String doc = Idx.parsePlainText(br);
                while (doc != null) {
                    Idx idx = new Idx(doc);

                    String content = idx.getTagValue("DRECONTENT");
                    Window<Node> window = new Window<Node>(Flag.n);
                    for (int i = 0; i < content.length(); i++) {
                        Character s = content.charAt(i);
                        Node<Character> state = stateBank.get(s);
                        if (state == null) {
                            window.clear();
                        } else {
                            window.add(state);
                            {
                                int index = state.getIndex();
                                int c = pii.containsKey(index) ? pii.get(index) + 1 : 1;
                                pii.put(index, c);
                            }

                            Node<Character> array[] = window.toArray(new State[0]);
                            for (int j = 0; j < array.length; j++) {
                                statisticNGram(array);
                            }
                        }
                    }

                    doc = Idx.parsePlainText(br);
                }
                br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void addUserDict(String words, String pinyin) {
        Character[] states = new Character[words.length()];
        for (int i = 0; i < states.length; i++) {
            states[i] = words.charAt(i);
        }
        String[] observes = pinyin.split("'");
        for (int n = 0; n < 50; n++) {
            ngram.insert(states, sortor, comparator);
        }
        for (int i = 0; i < states.length; i++) {
            Node<Character> state = new State<Character>(states[i]);
            Node<String> observe = new Observe<String>(observes[i]);
            //pi
            state = stateBank.add(state);
            int index = state.getIndex();
            int c = pii.containsKey(index) ? pii.get(index) + 1 : 1;
            pii.put(index, c);
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

    }

    public static void main(String[] args) {
        PinyinToWord c = new PinyinToWord();
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
        c.trainNgram("/app/bkidx/20100506/news-1.idx");
        c.save("ptw.m");

//        c.load("model");
//        c.init();
//        String [] o = {"xi","cai","ji"};
//        List<State> s = c.classify(o);
//        for (State state : s) {
//            System.out.print(state.getName() + " ");
//        }
//        System.out.println();
    }
}
