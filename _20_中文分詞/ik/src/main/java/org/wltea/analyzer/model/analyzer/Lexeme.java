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
package org.wltea.analyzer.model.analyzer;

import java.io.Serializable;

/**
 * IK詞元對象
 */
public class Lexeme implements Serializable {
    private static final long serialVersionUID = -654351052872259557L;
    // lexemeType常量
    // 未知
    public static final int TYPE_UNKNOWN = 0;
    // 英文
    public static final int TYPE_ENGLISH = 1;
    // 數字
    public static final int TYPE_ARABIC = 2;
    // 英文數位混合
    public static final int TYPE_LETTER = 3;
    // 中文詞元
    public static final int TYPE_CNWORD = 4;
    // 日韓文字
    public static final int TYPE_OTHER_CJK = 8;
    // 中文數詞
    public static final int TYPE_CNUM = 16;
    // 中文量詞
    public static final int TYPE_COUNT = 32;
    // 中文數量詞
    public static final int TYPE_CQUAN = 48;

    // 詞元的起始位移
    private int offset;
    // 詞元的相對起始位置
    private int begin;
    // 詞元的長度
    private int length;
    // 詞元文本
    private String lexemeText;
    // 詞元類型
    private int lexemeType;
    //
    private String props = "null";

    public Lexeme(int offset, int begin, int length, int lexemeType) {
        this.offset = offset;
        this.begin = begin;
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        this.length = length;
        this.lexemeType = lexemeType;
    }

    /*
     * 判斷詞元相等演算法 起始位置偏移、起始位置、終止位置相同
     *
     * @see java.lang.Object#equals(Object o)
     */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (this == o) {
            return true;
        }

        if (o instanceof Lexeme) {
            Lexeme other = (Lexeme) o;
            if (this.offset == other.getOffset() && this.begin == other.getBegin() && this.length == other.getLength()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /*
     * 詞元雜湊編碼演算法
     *
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int absBegin = getBeginPosition();
        int absEnd = getEndPosition();
        return (absBegin * 37) + (absEnd * 31) + ((absBegin * absEnd) % getLength()) * 11;
    }

    /*
     * 詞元在排序集合中的比較演算法
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Lexeme other) {
        // 起始位置優先
        if (this.begin < other.getBegin()) {
            return -1;
        } else if (this.begin == other.getBegin()) {
            // 詞元長度優先
            if (this.length > other.getLength()) {
                return -1;
            } else if (this.length == other.getLength()) {
                return 0;
            } else {// this.length < other.getLength()
                return 1;
            }

        } else {// this.begin > other.getBegin()
            return 1;
        }
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getBegin() {
        return begin;
    }

    /**
     * 獲取詞元在文本中的起始位置
     *
     * @return int
     */
    public int getBeginPosition() {
        return offset + begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    /**
     * 獲取詞元在文本中的結束位置
     *
     * @return int
     */
    public int getEndPosition() {
        return offset + begin + length;
    }

    /**
     * 獲取詞元的字元長度
     *
     * @return int
     */
    public int getLength() {
        return this.length;
    }

    public void setLength(int length) {
        if (this.length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        this.length = length;
    }

    /**
     * 獲取詞元的文本內容
     *
     * @return String
     */
    public String getLexemeText() {
        if (lexemeText == null) {
            return "";
        }
        return lexemeText;
    }

    public void setLexemeText(String lexemeText) {
        if (lexemeText == null) {
            this.lexemeText = "";
            this.length = 0;
        } else {
            this.lexemeText = lexemeText;
            this.length = lexemeText.length();
        }
    }

    /**
     * 獲取詞元類型
     *
     * @return int
     */
    public int getLexemeType() {
        return lexemeType;
    }

    public void setLexemeType(int lexemeType) {
        this.lexemeType = lexemeType;
    }

    /**
     * 合併兩個相鄰的詞元
     *
     * @param l
     * @param lexemeType
     * @return boolean 詞元是否成功合併
     */
    public boolean append(Lexeme l, int lexemeType) {
        if (l != null && this.getEndPosition() == l.getBeginPosition()) {
            this.length += l.getLength();
            this.lexemeType = lexemeType;
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     */
    public String toString() {
        StringBuffer strbuf = new StringBuffer();
        strbuf.append(this.getBeginPosition()).append("-").append(this.getEndPosition());
        strbuf.append("\t").append(this.lexemeText).append("\t");
        switch (lexemeType) {
            case TYPE_UNKNOWN:
                strbuf.append("UNKONW");
                break;
            case TYPE_ENGLISH:
                strbuf.append("ENGLISH");
                break;
            case TYPE_ARABIC:
                strbuf.append("ARABIC");
                break;
            case TYPE_LETTER:
                strbuf.append("LETTER");
                break;
            case TYPE_CNWORD:
                strbuf.append("CN_WORD");
                break;
            case TYPE_OTHER_CJK:
                strbuf.append("OTHER_CJK");
                break;
            case TYPE_COUNT:
                strbuf.append("COUNT");
                break;
            case TYPE_CNUM:
                strbuf.append("CN_NUM");
                break;
            case TYPE_CQUAN:
                strbuf.append("CN_QUAN");
                break;

        }
        //
        strbuf.append("\t" + props);
        return strbuf.toString();
    }

    public String getProps() {
        return props;
    }

    public void setProps(String props) {
        this.props = props;
    }

}

