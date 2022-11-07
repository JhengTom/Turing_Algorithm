/**
 *
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
 */
package org.wltea.analyzer.dic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 詞典樹分段，表示詞典樹的一個分枝
 */
class DictSegment implements Comparable<DictSegment> {

    // 公用字典表，存儲漢字
    private static final Map<Character, Character> charMap = new HashMap<Character, Character>(16, 0.95f);
    // 陣列大小上限
    private static final int ARRAY_LENGTH_LIMIT = 3;

    // Map存儲結構
    private Map<Character, DictSegment> childrenMap;
    // 陣列方式存儲結構
    private DictSegment[] childrenArray;

    // 當前節點上存儲的字元
    private Character nodeChar;
    // 當前節點存儲的Segment數目
    // storeSize <=ARRAY_LENGTH_LIMIT ，使用陣列存儲， storeSize >ARRAY_LENGTH_LIMIT
    // ,則使用Map存儲
    private int storeSize = 0;
    // 當前DictSegment狀態 ,預設 0 , 1表示從根節點到當前節點的路徑表示一個詞
    private int nodeState = 0;
    //
    private String props = "";

    DictSegment(Character nodeChar) {
        if (nodeChar == null) {
            throw new IllegalArgumentException("參數為空異常，字元不能為空");
        }
        this.nodeChar = nodeChar;
    }

    Character getNodeChar() {
        return nodeChar;
    }

    /*
     * 判斷是否有下一個節點
     */
    boolean hasNextNode() {
        return this.storeSize > 0;
    }

    /**
     * 匹配詞段
     *
     * @param charArray
     * @return Hit
     */
    Hit match(char[] charArray) {
        return this.match(charArray, 0, charArray.length, null);
    }

    /**
     * 匹配詞段
     *
     * @param charArray
     * @param begin
     * @param length
     * @return Hit
     */
    Hit match(char[] charArray, int begin, int length) {
        return this.match(charArray, begin, length, null);
    }

    /**
     * 匹配詞段
     *
     * @param charArray
     * @param begin
     * @param length
     * @param searchHit
     * @return Hit
     */
    Hit match(char[] charArray, int begin, int length, Hit searchHit) {

        if (searchHit == null) {
            // 如果hit為空，新建
            searchHit = new Hit();
            // 設置hit的其實文本位置
            searchHit.setBegin(begin);
        } else {
            // 否則要將HIT狀態重置
            searchHit.setUnmatch();
        }
        // 設置hit的當前處理位置
        searchHit.setEnd(begin);

        Character keyChar = new Character(charArray[begin]);
        DictSegment ds = null;

        // 引用執行個體變數為本地變數，避免查詢時遇到更新的同步問題
        DictSegment[] segmentArray = this.childrenArray;
        Map<Character, DictSegment> segmentMap = this.childrenMap;

        // STEP1 在節點中查找keyChar對應的DictSegment
        if (segmentArray != null) {
            // 在陣列中查找
            DictSegment keySegment = new DictSegment(keyChar);
            int position = Arrays.binarySearch(segmentArray, 0, this.storeSize, keySegment);
            if (position >= 0) {
                ds = segmentArray[position];
            }

        } else if (segmentMap != null) {
            // 在map中查找
            ds = segmentMap.get(keyChar);
        }

        // STEP2 找到DictSegment，判斷詞的匹配狀態，是否繼續遞迴，還是返回結果
        if (ds != null) {
            if (length > 1) {
                // 詞未匹配完，繼續往下搜索
                return ds.match(charArray, begin + 1, length - 1, searchHit);
            } else if (length == 1) {

                // 搜索最後一個char
                if (ds.nodeState == 1) {
                    // 添加HIT狀態為完全匹配
                    searchHit.setMatch();
                    searchHit.setProps(ds.props);
                }
                if (ds.hasNextNode()) {
                    // 添加HIT狀態為首碼匹配
                    searchHit.setPrefix();
                    // 記錄當前位置的DictSegment
                    searchHit.setMatchedDictSegment(ds);
                }
                return searchHit;
            }

        }
        // STEP3 沒有找到DictSegment， 將HIT設置為不匹配
        return searchHit;
    }

    /**
     * 載入填充詞典片段
     *
     * @param charArray
     */
    void fillSegment(char[] charArray, String props) {
        this.fillSegment(charArray, 0, charArray.length, 1, props);
    }

    /**
     * 遮罩詞典中的一個詞
     *
     * @param charArray
     */
    void disableSegment(char[] charArray, String props) {
        this.fillSegment(charArray, 0, charArray.length, 0, props);
    }

    /**
     * 載入填充詞典片段
     *
     * @param charArray
     * @param begin
     * @param length
     * @param enabled
     */
    private synchronized void fillSegment(char[] charArray, int begin, int length, int enabled, String props) {
        // 獲取字典表中的漢字物件
        Character beginChar = new Character(charArray[begin]);
        Character keyChar = charMap.get(beginChar);
        // 字典中沒有該字，則將其添加入字典
        if (keyChar == null) {
            charMap.put(beginChar, beginChar);
            keyChar = beginChar;
        }

        // 搜索當前節點的存儲，查詢對應keyChar的keyChar，如果沒有則創建
        DictSegment ds = lookforSegment(keyChar, enabled);
        if (ds != null) {
            // 處理keyChar對應的segment
            if (length > 1) {
                // 詞元還沒有完全加入詞典樹
                ds.fillSegment(charArray, begin + 1, length - 1, enabled, props);
            } else if (length == 1) {
                // 已經是詞元的最後一個char,設置當前節點狀態為enabled，
                // enabled=1表明一個完整的詞，enabled=0表示從詞典中遮罩當前詞
                ds.nodeState = enabled;
                //
                ds.props = props;
            }
        }

    }

    /**
     * 查找本節點下對應的keyChar的segment *
     *
     * @param keyChar
     * @param create
     *            =1如果沒有找到，則創建新的segment ; =0如果沒有找到，不創建，返回null
     * @return
     */
    private DictSegment lookforSegment(Character keyChar, int create) {

        DictSegment ds = null;

        if (this.storeSize <= ARRAY_LENGTH_LIMIT) {
            // 獲取陣列容器，如果陣列未創建則創建陣列
            DictSegment[] segmentArray = getChildrenArray();
            // 搜尋陣列
            DictSegment keySegment = new DictSegment(keyChar);
            int position = Arrays.binarySearch(segmentArray, 0, this.storeSize, keySegment);
            if (position >= 0) {
                ds = segmentArray[position];
            }

            // 遍歷陣列後沒有找到對應的segment
            if (ds == null && create == 1) {
                ds = keySegment;
                if (this.storeSize < ARRAY_LENGTH_LIMIT) {
                    // 陣列容量未滿，使用陣列存儲
                    segmentArray[this.storeSize] = ds;
                    // segment數目+1
                    this.storeSize++;
                    Arrays.sort(segmentArray, 0, this.storeSize);

                } else {
                    // 陣列容量已滿，切換Map存儲
                    // 獲取Map容器，如果Map未創建,則創建Map
                    Map<Character, DictSegment> segmentMap = getChildrenMap();
                    // 將陣列中的segment遷移到Map中
                    migrate(segmentArray, segmentMap);
                    // 存儲新的segment
                    segmentMap.put(keyChar, ds);
                    // segment數目+1 ， 必須在釋放陣列前執行storeSize++ ， 確保極端情況下，不會取到空的陣列
                    this.storeSize++;
                    // 釋放當前的陣列引用
                    this.childrenArray = null;
                }

            }

        } else {
            // 獲取Map容器，如果Map未創建,則創建Map
            Map<Character, DictSegment> segmentMap = getChildrenMap();
            // 搜索Map
            ds = segmentMap.get(keyChar);
            if (ds == null && create == 1) {
                // 構造新的segment
                ds = new DictSegment(keyChar);
                segmentMap.put(keyChar, ds);
                // 當前節點存儲segment數目+1
                this.storeSize++;
            }
        }

        return ds;
    }

    /**
     * 獲取陣列容器 執行緒同步方法
     */
    private DictSegment[] getChildrenArray() {
        if (this.childrenArray == null) {
            synchronized (this) {

                if (this.childrenArray == null) {
                    this.childrenArray = new DictSegment[ARRAY_LENGTH_LIMIT];
                }
            }
        }
        return this.childrenArray;
    }

    /**
     * 獲取Map容器 執行緒同步方法
     */
    private Map<Character, DictSegment> getChildrenMap() {
        if (this.childrenMap == null) {
            synchronized (this) {
                if (this.childrenMap == null) {
                    this.childrenMap = new HashMap<Character, DictSegment>(ARRAY_LENGTH_LIMIT * 2, 0.8f);
                }
            }
        }
        return this.childrenMap;
    }

    /**
     * 將陣列中的segment遷移到Map中
     *
     * @param segmentArray
     */
    private void migrate(DictSegment[] segmentArray, Map<Character, DictSegment> segmentMap) {
        for (DictSegment segment : segmentArray) {
            if (segment != null) {
                segmentMap.put(segment.nodeChar, segment);
            }
        }
    }

    /**
     * 實現Comparable介面
     *
     * @param o
     * @return int
     */
    public int compareTo(DictSegment o) {
        // 對當前節點存儲的char進行比較
        return this.nodeChar.compareTo(o.nodeChar);
    }

    public String getProps() {
        return props;
    }

}

