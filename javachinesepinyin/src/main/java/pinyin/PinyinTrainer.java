/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pinyin;

import java.io.File;
import pinyin.classifier.*;

/**
 *
 * @author ray
 */
public class PinyinTrainer {

    public static void main(String[] args) {
        if (args.length > 0) {
            SeqLabelClassifier<Character, String> ptw = new PinyinToWord();
            SeqLabelClassifier<String, Character> wtp = new WordToPinyin();
            File dir = new File(args[0]);
            if (dir.isDirectory() && dir.exists()) {
                for (File f : dir.listFiles()) {
                    ptw.train(f.getAbsolutePath());
                    wtp.train(f.getAbsolutePath());
                }
                ptw.save("ptw.m");
                wtp.save("wtp.m");
            }
        } else {
            System.out.println("Please input directory of corpus:\n\t/>./train.sh ../corpus/LCMC");
        }
    }
}
