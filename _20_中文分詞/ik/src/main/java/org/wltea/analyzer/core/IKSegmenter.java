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
 */
package org.wltea.analyzer.core;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.dic.Dictionary;
import org.wltea.analyzer.model.analyzer.Lexeme;

/**
 * IK分詞器主類
 *
 */
public final class IKSegmenter {

	// 字元竄reader
	private Reader input;
	// 分詞器配置項
	private Configuration cfg;
	// 分詞器上下文
	private AnalyzeContext context;
	// 分詞處理器列表
	private List<ISegmenter> segmenters;
	// 分詞歧義裁決器
	private IKArbitrator arbitrator;

	/**
	 * IK分詞器構造函數
	 *
	 * @param input
	 * @param useSmart
	 *            為true，使用智慧分詞策略
	 *
	 *            非智能分詞：細細微性輸出所有可能的切分結果 智慧分詞： 合併數詞和量詞，對分詞結果進行歧義判斷
	 */
	public IKSegmenter(Reader input, boolean useSmart) {
		this.input = input;
		this.cfg = DefaultConfig.getInstance();
		this.cfg.setUseSmart(useSmart);
		this.init();
	}


	/**
	 * IK分詞器構造函數
	 *
	 * @param input
	 * @param cfg
	 *            使用自訂的Configuration構造分詞器
	 *
	 */
	public IKSegmenter(Reader input, Configuration cfg) {
		this.input = input;
		this.cfg = cfg;
		this.init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		// 初始化詞典單例
		Dictionary.initial(this.cfg);
		// 初始化分詞上下文
		this.context = new AnalyzeContext(this.cfg);
		// 載入子分詞器
		this.segmenters = this.loadSegmenters();
		// 載入歧義裁決器
		this.arbitrator = new IKArbitrator();
	}

	/**
	 * 初始化詞典，載入子分詞器實現
	 *
	 * @return List<ISegmenter>
	 */
	private List<ISegmenter> loadSegmenters() {
		List<ISegmenter> segmenters = new ArrayList<ISegmenter>(4);
		// 處理字母的子分詞器
		segmenters.add(new LetterSegmenter());
		// 處理中文數量詞的子分詞器
		segmenters.add(new CN_QuantifierSegmenter());
		// 處理中文詞的子分詞器
		segmenters.add(new CJKSegmenter());
		return segmenters;
	}

	/**
	 * 分詞，獲取下一個詞元
	 *
	 * @return Lexeme 詞元對象
	 * @throws IOException
	 */
	public synchronized Lexeme next() throws IOException {
		if (this.context.hasNextResult()) {
			// 存在尚未輸出的分詞結果
			return this.context.getNextLexeme();
		} else {
			/*
			 * 從reader中讀取資料，填充buffer 如果reader是分次讀入buffer的，那麼buffer要進行移位處理
			 * 移位處理上次讀入的但未處理的資料
			 */
			int available = context.fillBuffer(this.input);
			if (available <= 0) {
				// reader已經讀完
				context.reset();
				return null;

			} else {
				// 初始化指標
				context.initCursor();
				do {
					// 遍歷子分詞器
					for (ISegmenter segmenter : segmenters) {
						segmenter.analyze(context);
					}
					// 字元緩衝區接近讀完，需要讀入新的字元
					if (context.needRefillBuffer()) {
						break;
					}
					// 向前移動指標
				} while (context.moveCursor());
				// 重置子分詞器，為下輪迴圈進行初始化
				for (ISegmenter segmenter : segmenters) {
					segmenter.reset();
				}
			}
			// 對分詞進行歧義處理
			this.arbitrator.process(context, this.cfg.useSmart());
			// 處理未切分CJK字元
			context.processUnkownCJKChar();
			// 記錄本次分詞的緩衝區位移
			context.markBufferOffset();
			// 輸出詞元
			if (this.context.hasNextResult()) {
				return this.context.getNextLexeme();
			}
			return null;
		}
	}

	/**
	 * 重置分詞器到初始狀態
	 *
	 * @param input
	 */
	public synchronized void reset(Reader input) {
		this.input = input;
		context.reset();
		for (ISegmenter segmenter : segmenters) {
			segmenter.reset();
		}
	}
}

