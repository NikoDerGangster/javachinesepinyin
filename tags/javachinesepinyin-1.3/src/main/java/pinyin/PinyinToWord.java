/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pinyin;

import pinyin.util.Pair;
import pinyin.util.Window;
import hmm.*;
import hmm.ngram.TreeNode;
import hmm.ngram.TreeNodeQuickSort;
import hmm.ngram.TreeNodeSortor;
import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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

            @Override
            public int compare(Character t, Character t1) {
                return t.charValue() - t1.charValue();
            }
        };

        sortor.setComparator(new Comparator<TreeNode<Character>>() {

            @Override
            public int compare(TreeNode<Character> t, TreeNode<Character> t1) {
                return t.getKey() - t1.getKey();
            }
        });
    }

    public NodeBank<String, Node<String>> getObserveBank() {
        return observeBank;
    }

    public NodeBank<Character, Node<Character>> getStateBank() {
        return stateBank;
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

    public List<Node<Character>> classify(String[] o) throws ObserveListException {
        List<String> observeList = new ArrayList<String>(o.length);
        observeList.addAll(Arrays.asList(o));
        return classify(observeList);
    }

    public List<Node<Character>> classify(List<String> o) throws ObserveListException {
        return viterbi.caculateWithLog(o);

    }

    public HmmResult viterbi(String[] o) throws ObserveListException {
        List<String> observeList = new ArrayList<String>(o.length);
        observeList.addAll(Arrays.asList(o));
        return viterbi(observeList);
    }

    public HmmResult viterbi(List<String> o) throws ObserveListException {
        return viterbi.caculateHmmResult(o);
    }

    public void save(String filename) {
        try {
            ngram.buildIndex(ngram.getCount(), sortor);
            ngram.cutCountLowerThan(Flag.getInstance().getNgramMinimumShowTimes());
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
            stateBank = (NodeBank<Character, Node<Character>>) ois.readObject();
            observeBank = (NodeBank<String, Node<String>>) ois.readObject();
            pii = (Map<Integer, Integer>) ois.readObject();
//            tranMatrix = (Map<String, Map<Integer, Integer>>) ois.readObject();
            emisMatrix = (Map<Integer, Map<Integer, Integer>>) ois.readObject();
            ngram = (TreeNode<Character>) ois.readObject();
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
            System.out.println("[PinyinToWord] train file " + file.getAbsolutePath());
            try {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                        new FileInputStream(file), "UTF-8"));
                String line = br.readLine();
                Window<Pair<String, String>> window = new Window<Pair<String, String>>(Flag.getInstance().n);
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
            System.out.println("[PinyinToWord] train file " + file.getAbsolutePath());
            try {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                        new FileInputStream(file), "UTF-8"));

                String doc = Idx.parsePlainText(br);
                while (doc != null) {
                    Idx idx = new Idx(doc);

                    String content = idx.getTagValue("DRECONTENT");
                    Window<Node> window = new Window<Node>(Flag.getInstance().n);
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

    public void addCustomWord(String words, String pinyin, int showtimes) {
//        int showtimes = 50;
        int d = words.length() - Flag.getInstance().n;
        d = d > 0 ? d + 1 : 1;
        int n = words.length() >= Flag.getInstance().n ? Flag.getInstance().n : words.length();
        String[] arrayPinyin = pinyin.split("'");
        for (int k = 0; k < d; k++) {
            Character[] states = new Character[n];
            String[] observes = new String[n];
            for (int i = 0; i < n; i++) {
                states[i] = words.charAt(k + i);
                observes[i] = arrayPinyin[k + i];
            }
            for (int i = 0; i < showtimes; i++) {
                ngram.insert(states, sortor, comparator);
                for (int j = 0; j < states.length; j++) {
                    Node<Character> state = new State<Character>(states[j]);
                    Node<String> observe = new Observe<String>(observes[j]);
                    //pi
                    state = stateBank.add(state);
                    int index = state.getIndex();
                    int c = pii.containsKey(index) ? pii.get(index) + 1 : 1;
                    pii.put(index, c);
                    //Emission
                    int sj = state.getIndex();
                    int oj = observe.getIndex();
                    Map<Integer, Integer> row = null;
                    if (emisMatrix.containsKey(sj)) {
                        row = emisMatrix.get(sj);
                    } else {
                        row = new HashMap<Integer, Integer>();
                        emisMatrix.put(sj, row);
                    }
                    int count = row.containsKey(oj) ? row.get(oj) + 1 : 1;
                    row.put(oj, count);
                }
            }
        }
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            PinyinToWord c = new PinyinToWord();
            File dir = new File(args[0]);
            if (dir.isDirectory() && dir.exists()) {
                for (File f : dir.listFiles()) {
                    String fname = f.getName();
                    if (fname.endsWith(".txt")) {
                        c.train(f.getAbsolutePath());
                    } else if (fname.endsWith(".idx")) {
                        c.trainNgram(f.getAbsolutePath());
                    }
                }
                c.save("ptw.m");
            }
        } else {
            System.out.println("Please input directory of corpus:\n\t/>./train.sh ../corpus/LCMC");
        }
    }
}
