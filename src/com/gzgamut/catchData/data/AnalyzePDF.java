package com.gzgamut.catchData.data;

import java.io.File;
import java.io.FileInputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

/**
 * 解析pdf
 * 
 * @author Administrator
 * 
 */
public class AnalyzePDF {

	/**
	 * 获取PDF文档的纯文本数据
	 * 
	 * @param resource
	 * @return
	 */
	public String getTextOfPDF(String resource) {
		String contenttxt = null;
		PDDocument pdfdocument = null;
		FileInputStream input = null;
		try {
			File file = new File(resource);
			input = new FileInputStream(file);

			pdfdocument = PDDocument.load(input); // 获取解析器的PDF文档对象
			PDFTextStripper pdfstripper = new PDFTextStripper(); // 生成PDF文档内容剥离器
			contenttxt = pdfstripper.getText(pdfdocument);
			// Pattern p1 = Pattern.compile("\n");
			// Pattern p2 = Pattern.compile(".*\\d+.*");
			// String[] str = p1.split(contenttxt);
			//
			// for(String s:str){
			//
			// if(((s.indexOf("宣传") >0)|| (s.indexOf("推广") >0) ||
			// (s.indexOf("广告") >0))&&
			// (s.indexOf("费") >0 || s.indexOf("支出") >0)){
			// Matcher m = p2.matcher(s);
			// while(m.find())
			// System.out.println("广告费用："+m.group());
			// }
			// if((s.indexOf("研发") >0) ){
			// Matcher m = p2.matcher(s);
			// while(m.find())
			// System.out.println("研发费用"+m.group());
			// }
			// }
		} catch (Exception e) {
			System.out.println("解析文件" + resource + "时发生错误");
			System.out.println(e.getCause());
		} finally {
			try {
				if (pdfdocument != null) {
					pdfdocument.close();
				}
				if (input != null) {
					input.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 利用剥离器获取文档
		return contenttxt;
	}
}
