/**
 * IK 中文分詞  版本 5.0
 * IK Analyzer release 5.0
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 原始程式碼由林良益(linliangyi2005@gmail.com)提供
 * 版權聲明 2012，烏龍茶工作室
 * provided by Linliangyi and copyright 2012 by Oolong studio
 *
 *
 */
package org.wltea.analyzer.dic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;

import org.wltea.analyzer.cfg.Configuration;

/**
 * 詞典管理類,單子模式
 */
public class Dictionary {
    /*
     * 詞典單子實例
     */
    private static Dictionary singleton;

    /*
     * 主詞典對象
     */
    private DictSegment _MainDict;

    /*
     * 停止詞詞典
     */
    private DictSegment _StopWordDict;
    /*
     * 量詞詞典
     */
    private DictSegment _QuantifierDict;

    /**
     * 配置物件
     */
    private Configuration cfg;

    private Dictionary(Configuration cfg) {
        this.cfg = cfg;
        this.loadMainDict();
        this.loadStopWordDict();
        this.loadQuantifierDict();
        this.loadExtDict();       //載入擴展詞典
    }

    /**
     * 詞典初始化 由於IK Analyzer的詞典採用Dictionary類的靜態方法進行詞典初始化
     * 只有當Dictionary類被實際調用時，才會開始載入詞典， 這將延長首次分詞操作的時間 該方法提供了一個在應用載入階段就初始化字典的手段
     *
     * @return Dictionary
     */
    public static Dictionary initial(Configuration cfg) {
        if (singleton == null) {
            synchronized (Dictionary.class) {
                if (singleton == null) {
                    singleton = new Dictionary(cfg);
                    return singleton;
                }
            }
        }
        return singleton;
    }

    /**
     * 獲取詞典單子實例
     *
     * @return Dictionary 單例對象
     */
    public static Dictionary getSingleton() {
        if (singleton == null) {
            throw new IllegalStateException("詞典尚未初始化，請先調用initial方法");
        }
        return singleton;
    }

    /**
     * 批量載入新詞條
     *
     * @param words
     *            Collection<String>詞條列表
     */
    public void addWords(Collection<Term> words) {
        if (words != null) {
            for (Term word : words) {
                if (word != null) {
                    // 批量載入詞條到主記憶體詞典中
                    singleton._MainDict.fillSegment(word.getTermText().toLowerCase().toCharArray(), word.getProps());
                }
            }
        }
    }

    /**
     * 批量移除（遮罩）詞條
     *
     * @param words
     */
    public void disableWords(Collection<Term> words) {
        if (words != null) {
            for (Term word : words) {
                if (word != null) {
                    // 批量遮罩詞條
                    singleton._MainDict.disableSegment(word.getTermText().trim().toLowerCase().toCharArray(), word.getProps());
                }
            }
        }
    }

    /**
     * 檢索匹配主詞典
     *
     * @param charArray
     * @return Hit 匹配結果描述
     */
    public Hit matchInMainDict(char[] charArray) {
        return singleton._MainDict.match(charArray);
    }

    /**
     * 檢索匹配主詞典
     *
     * @param charArray
     * @param begin
     * @param length
     * @return Hit 匹配結果描述
     */
    public Hit matchInMainDict(char[] charArray, int begin, int length) {
        return singleton._MainDict.match(charArray, begin, length);
    }

    /**
     * 檢索匹配量詞詞典
     *
     * @param charArray
     * @param begin
     * @param length
     * @return Hit 匹配結果描述
     */
    public Hit matchInQuantifierDict(char[] charArray, int begin, int length) {
        return singleton._QuantifierDict.match(charArray, begin, length);
    }

    /**
     * 從已匹配的Hit中直接取出DictSegment，繼續向下匹配
     *
     * @param charArray
     * @param currentIndex
     * @param matchedHit
     * @return Hit
     */
    public Hit matchWithHit(char[] charArray, int currentIndex, Hit matchedHit) {
        DictSegment ds = matchedHit.getMatchedDictSegment();
        return ds.match(charArray, currentIndex, 1, matchedHit);
    }

    /**
     * 判斷是否是停止詞
     *
     * @param charArray
     * @param begin
     * @param length
     * @return boolean
     */
    public boolean isStopWord(char[] charArray, int begin, int length) {
        return singleton._StopWordDict.match(charArray, begin, length).isMatch();
    }

    // ------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------

    private Term format(String oriTerm) {
        if (oriTerm != null && !"".equals(oriTerm.trim())) {
            Term term = new Term();
            String[] ss = oriTerm.split("\t");
            if (ss.length == 1) {
                term.setTermText(ss[0].trim().toLowerCase());
                term.setProps("null");
            } else if (ss.length == 2) {
                term.setTermText(ss[0].trim().toLowerCase());
                term.setProps(ss[1].trim().toLowerCase());
            } else
                return null;
            return term;
        } else
            return null;
    }

    /**
     * 載入主詞典及擴展詞典
     */
    public void loadMainDict() {
        // 建立一個主詞典實例
        _MainDict = new DictSegment((char) 0);
        // 讀取主詞典文件
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(cfg.getMainDictionary());
        if (is == null) {
            // throw new RuntimeException("Main Dictionary not found!!!");
            return;
        }

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"), 512);
            String theWord = null;
            do {
                theWord = br.readLine();
                Term term = format(theWord);
                if (term != null) {
                    _MainDict.fillSegment(term.getTermText().toCharArray(), term.props);
                }
            } while (theWord != null);

        } catch (IOException ioe) {
            System.err.println("Main Dictionary loading exception.");
            ioe.printStackTrace();

        } finally {
            try {
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 載入擴展詞典
        //   this.loadExtDict();
    }

    /**
     * 載入使用者配置的擴展詞典到主詞庫表
     */
    public void loadExtDict() {
        // 載入擴展詞典配置
        List<String> extDictFiles = cfg.getExtDictionarys();
        if (extDictFiles != null) {
            InputStream is = null;
            for (String extDictName : extDictFiles) {
                // 讀取擴展詞典檔
                System.out.println("載入擴展詞典：" + extDictName);
                is = this.getClass().getClassLoader().getResourceAsStream(extDictName);
                // 如果找不到擴展的字典，則忽略
                if (is == null) {
                    continue;
                }
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"), 512);
                    String theWord = null;
                    do {
                        theWord = br.readLine();
                        Term term = format(theWord);
                        if (term != null) {
                            _MainDict.fillSegment(term.getTermText().toCharArray(), term.props);
                        }

                    } while (theWord != null);

                } catch (IOException ioe) {
                    System.err.println("Extension Dictionary loading exception.");
                    ioe.printStackTrace();

                } finally {
                    try {
                        if (is != null) {
                            is.close();
                            is = null;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 載入用戶擴展的停止詞詞典
     */
    private void loadStopWordDict() {
        // 建立一個主詞典實例
        _StopWordDict = new DictSegment((char) 0);
        // 載入擴展停止詞典
        List<String> extStopWordDictFiles = cfg.getExtStopWordDictionarys();
        if (extStopWordDictFiles != null) {
            InputStream is = null;
            for (String extStopWordDictName : extStopWordDictFiles) {
                System.out.println("載入擴展停止詞典：" + extStopWordDictName);
                // 讀取擴展詞典檔
                is = this.getClass().getClassLoader().getResourceAsStream(extStopWordDictName);
                // 如果找不到擴展的字典，則忽略
                if (is == null) {
                    continue;
                }
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"), 512);
                    String theWord = null;
                    do {
                        theWord = br.readLine();

                        Term term = format(theWord);
                        if (term != null) {
                            _StopWordDict.fillSegment(term.getTermText().toCharArray(), term.props);
                        }

                    } while (theWord != null);

                } catch (IOException ioe) {
                    System.err.println("Extension Stop word Dictionary loading exception.");
                    ioe.printStackTrace();

                } finally {
                    try {
                        if (is != null) {
                            is.close();
                            is = null;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 載入量詞詞典
     */
    private void loadQuantifierDict() {
        // 建立一個量詞典實例
        _QuantifierDict = new DictSegment((char) 0);
        // 讀取量詞詞典文件
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(cfg.getQuantifierDicionary());
        if (is == null) {
            // throw new RuntimeException("Quantifier Dictionary not found!!!");
            return;
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"), 512);
            String theWord = null;
            do {
                theWord = br.readLine();

                Term term = format(theWord);
                if (term != null) {
                    _QuantifierDict.fillSegment(term.getTermText().toCharArray(), term.props);
                }

            } while (theWord != null);

        } catch (IOException ioe) {
            System.err.println("Quantifier Dictionary loading exception.");
            ioe.printStackTrace();

        } finally {
            try {
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

