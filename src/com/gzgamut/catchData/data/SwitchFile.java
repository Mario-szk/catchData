package com.gzgamut.catchData.data;

import java.io.File;
import java.util.List;
import org.apache.log4j.Logger;
import com.gzgamut.catchData.util.GetListByFile;

/**
 * 将pdf文件转换成txt格式
 * 
 * @author Administrator
 * 
 */
public class SwitchFile {
	private Logger logger = Logger.getLogger(SwitchFile.class);

	/**
	 * 分析所有的PDF文档后，把PDF文档的内容转换为txt格式存储
	 * 
	 * @param symbols
	 *            为股票代码集
	 */
	public void analyzeAllFile(List<String> symbols) {
		logger.info("开始把PDF文档转换为txt格式");
		AnalyzePDF pdf = new AnalyzePDF();
		CreateTXT txt = new CreateTXT();
		for (String symbol : symbols) {
			symbol = symbol.substring(0, symbol.lastIndexOf("."));
			List<String> file = GetListByFile.getFile(symbol);
			for (String filePath : file) {
				String txtPath = filePath.substring(0,
						filePath.lastIndexOf("."))
						+ ".txt";
				File tempFile = new File(txtPath);
				if (tempFile.exists()) {
					logger.info(txtPath + "已经存在，跳过。");
				} else {
					logger.info(txtPath + "不存在，开始转换为txt格式。");
					String text = pdf.getTextOfPDF(filePath);
					if (text != null) {
						txt.createFile(text, txtPath);
					}
					logger.info(txtPath + "开始转换为txt格式完成！");
				}
			}
		}
		logger.info("完成所有PDF文件向TXT文本的转换");
	}
}
