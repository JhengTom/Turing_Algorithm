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
package org.wltea.analyzer.cfg;

import java.util.List;

/**
 *
 * 配置管理類介面
 *
 */
public interface Configuration {

    /**
     * 返回useSmart標誌位元 useSmart =true ，分詞器使用智慧切分策略， =false則使用細細微性切分
     *
     * @return useSmart
     */
    public boolean useSmart();

    /**
     * 設置useSmart標誌位元 useSmart =true ，分詞器使用智慧切分策略， =false則使用細細微性切分
     *
     * @param useSmart
     */
    public void setUseSmart(boolean useSmart);

    /**
     * 獲取主詞典路徑
     *
     * @return String 主詞典路徑
     */
    public String getMainDictionary();

    /**
     * 獲取量詞詞典路徑
     *
     * @return String 量詞詞典路徑
     */
    public String getQuantifierDicionary();

    /**
     * 獲取擴展字典配置路徑
     *
     * @return List<String> 相對類載入器的路徑
     */
    public List<String> getExtDictionarys();

    /**
     * 獲取擴展停止詞典配置路徑
     *
     * @return List<String> 相對類載入器的路徑
     */
    public List<String> getExtStopWordDictionarys();

}

