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

import org.wltea.analyzer.model.analyzer.Lexeme;

/**
 * IK分詞器專用的Lexem快速排序集合
 */
class QuickSortSet {
    // 鏈表頭
    private Cell head;
    // 鏈表尾
    private Cell tail;
    // 鏈表的實際大小
    private int size;

    QuickSortSet() {
        this.size = 0;
    }

    /**
     * 向鏈表集合添加詞元
     *
     * @param lexeme
     */
    boolean addLexeme(Lexeme lexeme) {
        Cell newCell = new Cell(lexeme);
        if (this.size == 0) {
            this.head = newCell;
            this.tail = newCell;
            this.size++;
            return true;

        } else {
            if (this.tail.compareTo(newCell) == 0) {// 詞元與尾部詞元相同，不放入集合
                return false;

            } else if (this.tail.compareTo(newCell) < 0) {// 詞元接入鏈表尾部
                this.tail.next = newCell;
                newCell.prev = this.tail;
                this.tail = newCell;
                this.size++;
                return true;

            } else if (this.head.compareTo(newCell) > 0) {// 詞元接入鏈表頭部
                this.head.prev = newCell;
                newCell.next = this.head;
                this.head = newCell;
                this.size++;
                return true;

            } else {
                // 從尾部上逆
                Cell index = this.tail;
                while (index != null && index.compareTo(newCell) > 0) {
                    index = index.prev;
                }
                if (index.compareTo(newCell) == 0) {// 詞元與集合中的詞元重複，不放入集合
                    return false;

                } else if (index.compareTo(newCell) < 0) {// 詞元插入鏈表中的某個位置
                    newCell.prev = index;
                    newCell.next = index.next;
                    index.next.prev = newCell;
                    index.next = newCell;
                    this.size++;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 返回鏈表頭部元素
     *
     * @return
     */
    Lexeme peekFirst() {
        if (this.head != null) {
            return this.head.lexeme;
        }
        return null;
    }

    /**
     * 取出鏈表集合的第一個元素
     *
     * @return Lexeme
     */
    Lexeme pollFirst() {
        if (this.size == 1) {
            Lexeme first = this.head.lexeme;
            this.head = null;
            this.tail = null;
            this.size--;
            return first;
        } else if (this.size > 1) {
            Lexeme first = this.head.lexeme;
            this.head = this.head.next;
            this.size--;
            return first;
        } else {
            return null;
        }
    }

    /**
     * 返回鏈表尾部元素
     *
     * @return
     */
    Lexeme peekLast() {
        if (this.tail != null) {
            return this.tail.lexeme;
        }
        return null;
    }

    /**
     * 取出鏈表集合的最後一個元素
     *
     * @return Lexeme
     */
    Lexeme pollLast() {
        if (this.size == 1) {
            Lexeme last = this.head.lexeme;
            this.head = null;
            this.tail = null;
            this.size--;
            return last;

        } else if (this.size > 1) {
            Lexeme last = this.tail.lexeme;
            this.tail = this.tail.prev;
            this.size--;
            return last;

        } else {
            return null;
        }
    }

    /**
     * 返回集合大小
     *
     * @return
     */
    int size() {
        return this.size;
    }

    /**
     * 判斷集合是否為空
     *
     * @return
     */
    boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * 返回lexeme鏈的頭部
     *
     * @return
     */
    Cell getHead() {
        return this.head;
    }

    /**
     *
     * IK 中文分詞 版本 5.0 IK Analyzer release 5.0
     *
     * Licensed to the Apache Software Foundation (ASF) under one or more
     * contributor license agreements. See the NOTICE file distributed with this
     * work for additional information regarding copyright ownership. The ASF
     * licenses this file to You under the Apache License, Version 2.0 (the
     * "License"); you may not use this file except in compliance with the
     * License. You may obtain a copy of the License at
     *
     * http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
     * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
     * License for the specific language governing permissions and limitations
     * under the License.
     *
     * 原始程式碼由林良益(linliangyi2005@gmail.com)提供 版權聲明 2012，烏龍茶工作室 provided by
     * Linliangyi and copyright 2012 by Oolong studio
     *
     * QuickSortSet集合單元
     *
     */
    class Cell implements Comparable<Cell> {
        private Cell prev;
        private Cell next;
        private Lexeme lexeme;

        Cell(Lexeme lexeme) {
            if (lexeme == null) {
                throw new IllegalArgumentException("lexeme must not be null");
            }
            this.lexeme = lexeme;
        }

        public int compareTo(Cell o) {
            return this.lexeme.compareTo(o.lexeme);
        }

        public Cell getPrev() {
            return this.prev;
        }

        public Cell getNext() {
            return this.next;
        }

        public Lexeme getLexeme() {
            return this.lexeme;
        }
    }
}

