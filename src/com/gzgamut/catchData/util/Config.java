package com.gzgamut.catchData.util;

public class Config {
	// 数据源，其中http://www.cninfo.com.cn/search/stockfulltext.jsp为检索的页面；noticeType为公告类型，其中010301为年度报告；startTime为检索报告开始的时间，endTime为检索报告结束的时间。
	public static final String httpUrl = "http://www.cninfo.com.cn/search/stockfulltext.jsp?"
			+ "orderby=date11&noticeType=010301&startTime=2000-01-01&endTime=2014-04-01&"
			+ "Submit1.x=32&Submit1.y=9";
	// 请求文件路径
	public static final String index = "http://www.cninfo.com.cn";
	// 保存文件路径
	public static final String path = "E:/catchData/file";
}
