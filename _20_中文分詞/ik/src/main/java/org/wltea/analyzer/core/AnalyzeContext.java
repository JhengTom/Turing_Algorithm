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
 */
package org.wltea.analyzer.core;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.dic.Dictionary;
import org.wltea.analyzer.model.analyzer.Lexeme;

/**
 *
 * 分詞器上下文狀態
 *
 */
public class AnalyzeContext {

    // 默認緩衝區大小
    private static final int BUFF_SIZE = 3072;
    // 緩衝區耗盡的臨界值
    private static final int BUFF_EXHAUST_CRITICAL = 48;

    // 字元竄讀取緩衝
    private char[] segmentBuff;
    // 字元類型陣列
    private int[] charTypes;

    // 記錄Reader內已分析的字串總長度
    // 在分多段分析詞元時，該變數累計當前的segmentBuff相對於reader起始位置的位移
    private int buffOffset;
    // 當前緩衝區位置指針
    private int cursor;
    // 最近一次讀入的,可處理的字串長度
    private int available;

    // 子分詞器鎖
    // 該集合非空，說明有子分詞器在佔用segmentBuff
    private Set<String> buffLocker;

    // 原始分詞結果集合，未經歧義處理
    private QuickSortSet orgLexemes;
    // LexemePath位置索引表
    private Map<Integer, LexemePath> pathMap;
    // 最終分詞結果集
    private LinkedList<Lexeme> results;

    // 分詞器配置項
    private Configuration cfg;

    public AnalyzeContext(Configuration cfg) {
        this.cfg = cfg;
        this.segmentBuff = new char[BUFF_SIZE];
        this.charTypes = new int[BUFF_SIZE];
        this.buffLocker = new HashSet<String>();
        this.orgLexemes = new QuickSortSet();
        this.pathMap = new HashMap<Integer, LexemePath>();
        this.results = new LinkedList<Lexeme>();
    }

    public int getCursor() {
        return this.cursor;
    }

    //
    // void setCursor(int cursor){
    // this.cursor = cursor;
    // }

    public char[] getSegmentBuff() {
        return this.segmentBuff;
    }

    public char getCurrentChar() {
        return this.segmentBuff[this.cursor];
    }

    public int getCurrentCharType() {
        return this.charTypes[this.cursor];
    }

    public int getBufferOffset() {
        return this.buffOffset;
    }

    /**
     * 根據context的上下文情況，填充segmentBuff
     *
     * @param reader
     * @return 返回待分析的（有效的）字串長度
     * @throws IOException
     */
    int fillBuffer(Reader reader) throws IOException {
        int readCount = 0;
        if (this.buffOffset == 0) {
            // 首次讀取reader
            readCount = reader.read(segmentBuff);
        } else {
            int offset = this.available - this.cursor;
            if (offset > 0) {
                // 最近一次讀取的>最近一次處理的，將未處理的字串拷貝到segmentBuff頭部
                System.arraycopy(this.segmentBuff, this.cursor, this.segmentBuff, 0, offset);
                readCount = offset;
            }
            // 繼續讀取reader ，以onceReadIn - onceAnalyzed為起始位置，繼續填充segmentBuff剩餘的部分
            readCount += reader.read(this.segmentBuff, offset, BUFF_SIZE - offset);
        }
        // 記錄最後一次從Reader中讀入的可用字元長度
        this.available = readCount;
        // 重置當前指針
        this.cursor = 0;
        return readCount;
    }

    /**
     * 初始化buff指標，處理第一個字元
     */
    void initCursor() {
        this.cursor = 0;
        this.segmentBuff[this.cursor] = CharacterUtil.regularize(this.segmentBuff[this.cursor]);
        this.charTypes[this.cursor] = CharacterUtil.identifyCharType(this.segmentBuff[this.cursor]);
    }

    /**
     * 指針+1 成功返回 true； 指標已經到了buff尾部，不能前進，返回false 並處理當前字元
     */
    boolean moveCursor() {
        if (this.cursor < this.available - 1) {
            this.cursor++;
            this.segmentBuff[this.cursor] = CharacterUtil.regularize(this.segmentBuff[this.cursor]);
            this.charTypes[this.cursor] = CharacterUtil.identifyCharType(this.segmentBuff[this.cursor]);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 設置當前segmentBuff為鎖定狀態 加入佔用segmentBuff的子分詞器名稱，表示佔用segmentBuff
     *
     * @param segmenterName
     */
    public void lockBuffer(String segmenterName) {
        this.buffLocker.add(segmenterName);
    }

    /**
     * 移除指定的子分詞器名，釋放對segmentBuff的佔用
     *
     * @param segmenterName
     */
    public void unlockBuffer(String segmenterName) {
        this.buffLocker.remove(segmenterName);
    }

    /**
     * 只要buffLocker中存在segmenterName 則buffer被鎖定
     *
     * @return boolean 緩衝去是否被鎖定
     */
    public boolean isBufferLocked() {
        return this.buffLocker.size() > 0;
    }

    /**
     * 判斷當前segmentBuff是否已經用完 當前執針cursor移至segmentBuff末端this.available - 1
     *
     * @return
     */
    public boolean isBufferConsumed() {
        return this.cursor == this.available - 1;
    }

    /**
     * 判斷segmentBuff是否需要讀取新資料
     *
     * 滿足一下條件時， 1.available == BUFF_SIZE 表示buffer滿載 2.buffIndex < available - 1
     * && buffIndex > available - BUFF_EXHAUST_CRITICAL表示當前指針處於臨界區內
     * 3.!context.isBufferLocked()表示沒有segmenter在佔用buffer
     * 要中斷當前迴圈（buffer要進行移位，並再讀取資料的操作）
     *
     * @return
     */
    public boolean needRefillBuffer() {
        return this.available == BUFF_SIZE && this.cursor < this.available - 1
                && this.cursor > this.available - BUFF_EXHAUST_CRITICAL && !this.isBufferLocked();
    }

    /**
     * 累計當前的segmentBuff相對於reader起始位置的位移
     */
    public void markBufferOffset() {
        this.buffOffset += this.cursor;
    }

    /**
     * 向分詞結果集添加詞元
     *
     * @param lexeme
     */
    public void addLexeme(Lexeme lexeme) {
        this.orgLexemes.addLexeme(lexeme);
    }

    /**
     * 添加分詞結果路徑 路徑起始位置 ---> 路徑 映射表
     *
     * @param path
     */
    public void addLexemePath(LexemePath path) {
        if (path != null) {
            this.pathMap.put(path.getPathBegin(), path);
        }
    }

    /**
     * 返回原始分詞結果
     *
     * @return
     */
    public QuickSortSet getOrgLexemes() {
        return this.orgLexemes;
    }

    /**
     * 處理未知類型的CJK字元
     */
    public void processUnkownCJKChar() {
        int index = 0;
        for (; index < this.available;) {
            // 跳過標點符號等字元
            if (CharacterUtil.CHAR_USELESS == this.charTypes[index]) {
                index++;
                continue;
            }
            // 從pathMap找出對應index位置的LexemePath
            LexemePath path = this.pathMap.get(index);
            if (path != null) {
                // 輸出LexemePath中的lexeme到results集合
                Lexeme l = path.pollFirst();
                while (l != null) {
                    this.results.add(l);
                    // 將index移至lexeme後
                    index = l.getBegin() + l.getLength();
                    l = path.pollFirst();
                    if (l != null) {
                        // 輸出path內部，詞元間遺漏的單字
                        for (; index < l.getBegin(); index++) {
                            this.outputSingleCJK(index);
                        }
                    }
                }
            } else {// pathMap中找不到index對應的LexemePath
                // 單字輸出
                this.outputSingleCJK(index);
                index++;
            }
        }
        // 清空當前的Map
        this.pathMap.clear();
    }

    /**
     * 對CJK字元進行單字輸出
     *
     * @param index
     */
    private void outputSingleCJK(int index) {
        if (CharacterUtil.CHAR_CHINESE == this.charTypes[index]) {
            Lexeme singleCharLexeme = new Lexeme(this.buffOffset, index, 1, Lexeme.TYPE_CNWORD);
            this.results.add(singleCharLexeme);
        } else if (CharacterUtil.CHAR_OTHER_CJK == this.charTypes[index]) {
            Lexeme singleCharLexeme = new Lexeme(this.buffOffset, index, 1, Lexeme.TYPE_OTHER_CJK);
            this.results.add(singleCharLexeme);
        }
    }

    /**
     * 判斷結果集中是否還有為輸出的結果
     *
     * @return
     */
    public boolean hasNextResult() {
        return !this.results.isEmpty();
    }

    /**
     * 返回lexeme
     *
     * 同時處理合併
     *
     * @return
     */
    public Lexeme getNextLexeme() {
        // 從結果集取出，並移除第一個Lexme
        Lexeme result = this.results.pollFirst();
        while (result != null) {
            // 數量詞合併
            this.compound(result);
            if (Dictionary.getSingleton().isStopWord(this.segmentBuff, result.getBegin(), result.getLength())) {
                // 是停止詞繼續取列表的下一個
                result = this.results.pollFirst();
            } else {
                // 不是停止詞, 生成lexeme的詞元文本,輸出
                result.setLexemeText(String.valueOf(segmentBuff, result.getBegin(), result.getLength()));
                break;
            }
        }
        return result;
    }

    /**
     * 重置分詞上下文狀態
     */
    public void reset() {
        this.buffLocker.clear();
        this.orgLexemes = new QuickSortSet();
        this.available = 0;
        this.buffOffset = 0;
        this.charTypes = new int[BUFF_SIZE];
        this.cursor = 0;
        this.results.clear();
        this.segmentBuff = new char[BUFF_SIZE];
    }

    /**
     * 組合詞元
     */
    private void compound(Lexeme result) {
        if (!this.cfg.useSmart()) {
            return;
        }
        // 數量詞合併處理
        if (!this.results.isEmpty()) {

            if (Lexeme.TYPE_ARABIC == result.getLexemeType()) {
                Lexeme nextLexeme = this.results.peekFirst();
                boolean appendOk = false;
                if (Lexeme.TYPE_CNUM == nextLexeme.getLexemeType()) {
                    // 合併英文數詞+中文數詞
                    appendOk = result.append(nextLexeme, Lexeme.TYPE_CNUM);
                } else if (Lexeme.TYPE_COUNT == nextLexeme.getLexemeType()) {
                    // 合併英文數詞+中文量詞
                    appendOk = result.append(nextLexeme, Lexeme.TYPE_CQUAN);
                }
                if (appendOk) {
                    // 彈出
                    this.results.pollFirst();
                }
            }

            // 可能存在第二輪合併
            if (Lexeme.TYPE_CNUM == result.getLexemeType() && !this.results.isEmpty()) {
                Lexeme nextLexeme = this.results.peekFirst();
                boolean appendOk = false;
                if (Lexeme.TYPE_COUNT == nextLexeme.getLexemeType()) {
                    // 合併中文數詞+中文量詞
                    appendOk = result.append(nextLexeme, Lexeme.TYPE_CQUAN);
                }
                if (appendOk) {
                    // 彈出
                    this.results.pollFirst();
                }
            }

        }
    }

}

