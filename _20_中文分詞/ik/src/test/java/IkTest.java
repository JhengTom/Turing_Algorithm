
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.model.analyzer.Lexeme;

public class IkTest {

    private static IKSegmenter ikSmart = new IKSegmenter(new StringReader(""), true);     //會選擇一個最大的
    private static IKSegmenter ik = new IKSegmenter(new StringReader(""), false);     //最大顆粒，會輸出所有的詞

    public static void main(String[] args) {
        //敏感詞過濾,電商有個廣告法，不能出現最好 最便宜 最大，
        //我抓了百度的中藥處方，要把裡面的中藥拆分出阿裡
//        String str = "my name is"; //String.valueof();
        String str = "我是台灣人，習大大想稱帝"; //String.valueof();
        List<Lexeme> lexemes = analyzerNoSmart(str);
        System.out.println("noSmart分詞後的結果："); // 文章分類
        /***
         * 關鍵詞 是 也 在 呢 嗎 這些關鍵詞可視為停用詞，可以過濾掉。
         * 可以用配置的方式過濾掉
         */
        //有一篇文章是寫三峽大壩 -》水利工程裡面去。
        //可以優化你們的模糊匹配
        for (Lexeme lexeme : lexemes) {
            System.out.println(lexeme.getLexemeText() + ":" + lexeme.getProps());
        }


//        lexemes = analyzerSmart(str);
//        System.out.println("smart分詞後的結果：");
//        for (Lexeme
//                lexeme : lexemes) {
//            System.out.println(lexeme.getLexemeText() + ":" +
//                    lexeme.getProps());
//        }


    }

    public static List<Lexeme> analyzerSmart(String str) {
        List<Lexeme> rs = new ArrayList<>();
        ikSmart.reset(new StringReader(str));
        Lexeme l = null;
        Map<String, Boolean> map = new HashMap<>();
        try {
            while ((l = ikSmart.next()) != null) {
                if (l == null || l.getLexemeText() == null)
                    continue;
                if (map.containsKey(l.getLexemeText()))
                    continue;
                rs.add(l);
                map.put(l.getLexemeText(), true);
            }
        } catch (IOException e) {
        }
        return rs;
    }

    public static List<Lexeme> analyzerNoSmart(String str) {
        List<Lexeme> rs = new ArrayList<>();
        ik.reset(new StringReader(str));
        Lexeme l = null;
        Map<String, Boolean> map = new HashMap<>();
        try {
            while ((l = ik.next()) != null) {
                if (l == null || l.getLexemeText() == null)
                    continue;
                if (map.containsKey(l.getLexemeText()))
                    continue;
                rs.add(l);
                map.put(l.getLexemeText(), true);
            }
        } catch (IOException e) {
        }
        return rs;
    }
}

