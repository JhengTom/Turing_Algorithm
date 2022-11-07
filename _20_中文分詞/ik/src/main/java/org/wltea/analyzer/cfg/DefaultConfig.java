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
package org.wltea.analyzer.cfg;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;

/**
 * Configuration 默認實現 2012-5-8
 *
 */
public class DefaultConfig implements Configuration {

    /*
     * 分詞器預設字典路徑
     */
    private static final String PATH_DIC_MAIN = "org/wltea/analyzer/dic/main2012.dic";
    private static final String PATH_DIC_QUANTIFIER = "org/wltea/analyzer/dic/quantifier.dic";

    /*
     * 分詞器設定檔路徑
     */
    private static final String FILE_NAME = "IKAnalyzer.cfg.xml";
    // 配置屬性——擴展字典
    private static final String EXT_DICT = "ext_dict";
    // 配置屬性——擴展停止詞典
    private static final String EXT_STOP = "ext_stopwords";

    private Properties props;
    /*
     * 是否使用smart方式分詞
     */
    private boolean useSmart;

    /**
     * 返回單例
     *
     * @return Configuration單例
     */
    public static Configuration getInstance() {
        return new DefaultConfig();
    }

    /*
     * 初始化設定檔
     */
    private DefaultConfig() {
        props = new Properties();

        InputStream input = this.getClass().getClassLoader().getResourceAsStream(FILE_NAME);
        if (input != null) {
            try {
                props.loadFromXML(input);
            } catch (InvalidPropertiesFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 返回useSmart標誌位元 useSmart =true ，分詞器使用智慧切分策略， =false則使用細細微性切分
     *
     * @return useSmart
     */
    public boolean useSmart() {
        return useSmart;
    }

    /**
     * 設置useSmart標誌位元 useSmart =true ，分詞器使用智慧切分策略， =false則使用細細微性切分
     *
     * @param useSmart
     */
    public void setUseSmart(boolean useSmart) {
        this.useSmart = useSmart;
    }

    /**
     * 獲取主詞典路徑
     *
     * @return String 主詞典路徑
     */
    public String getMainDictionary() {
        return PATH_DIC_MAIN;
    }

    /**
     * 獲取量詞詞典路徑
     *
     * @return String 量詞詞典路徑
     */
    public String getQuantifierDicionary() {
        return PATH_DIC_QUANTIFIER;
    }

    /**
     * 獲取擴展字典配置路徑
     *
     * @return List<String> 相對類載入器的路徑
     */
    public List<String> getExtDictionarys() {
        List<String> extDictFiles = new ArrayList<String>(2);
        String extDictCfg = props.getProperty(EXT_DICT);
        if (extDictCfg != null) {
            // 使用;分割多個擴展字典配置
            String[] filePaths = extDictCfg.split(";");
            if (filePaths != null) {
                for (String filePath : filePaths) {
                    if (filePath != null && !"".equals(filePath.trim())) {
                        extDictFiles.add(filePath.trim());
                    }
                }
            }
        }
        return extDictFiles;
    }

    /**
     * 獲取擴展停止詞典配置路徑
     *
     * @return List<String> 相對類載入器的路徑
     */
    public List<String> getExtStopWordDictionarys() {
        List<String> extStopWordDictFiles = new ArrayList<String>(2);
        String extStopWordDictCfg = props.getProperty(EXT_STOP);
        if (extStopWordDictCfg != null) {
            // 使用;分割多個擴展字典配置
            String[] filePaths = extStopWordDictCfg.split(";");
            if (filePaths != null) {
                for (String filePath : filePaths) {
                    if (filePath != null && !"".equals(filePath.trim())) {
                        extStopWordDictFiles.add(filePath.trim());
                    }
                }
            }
        }
        return extStopWordDictFiles;
    }

}

