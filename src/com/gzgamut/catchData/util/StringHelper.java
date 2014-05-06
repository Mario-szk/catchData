package com.gzgamut.catchData.util;

import java.text.NumberFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

public class StringHelper {

	private static Logger logger = Logger.getLogger(StringHelper.class);

	/**
	 * 去除String字符串中的空格、回车、换行符、制表符
	 */
	public static String replaceSpecialCharacters(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");// 空格、回车、换行符、制表符
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");

			dest = dest.replaceAll("一", "1");
			dest = dest.replaceAll("二", "2");
			dest = dest.replaceAll("三", "3");
			dest = dest.replaceAll("四", "4");
			dest = dest.replaceAll("五", "5");
			dest = dest.replaceAll("六", "6");
			dest = dest.replaceAll("七", "7");
			dest = dest.replaceAll("八", "8");
			dest = dest.replaceAll("九", "9");
			dest = dest.replaceAll("零", "0");
			dest = dest.replaceAll("○", "0");
			dest = dest.replaceAll("〇", "0");

			p = Pattern.compile("[\u4e00-\u9fa5]");// 汉字
			m = p.matcher(dest);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 * 计时器
	 * 
	 * @param startTime
	 *            开始的时间（毫秒）
	 * @return
	 */
	public static String timer(long startTime) {
		// 结束的时间
		long endTime = new Date().getTime();
		int time = (int) ((endTime - startTime) / 1000);
		int second = time % 60;
		time /= 60;
		int minute = time % 60;
		time /= 60;
		int hour = time % 24;
		int day = time / 24;
		String result = day + "天" + hour + "时" + minute + "分" + second + "秒";
		return result;
	}

	/**
	 * 返回单位尾数
	 * 
	 * @param numString
	 * @param unitStr
	 * @return
	 */
	public static long unitMatcher(String numString, String unitStr) {
		long result = 0;
		try {
			result = Long.parseLong(numString);
		} catch (NumberFormatException e) {
			// 如果格式错误，则返回0
			return 0;
		}
		String unitResult = null;
		// 检查是否以元为单位
		Pattern pattern = Pattern.compile("元");
		Matcher matcher = pattern.matcher(unitStr);
		if (matcher.find()) {
			unitResult = "元";
		}
		// 检查是否以千元为单位
		pattern = Pattern.compile("千元");
		matcher = pattern.matcher(unitStr);
		if (matcher.find()) {
			unitResult = "千元";
		}
		// 检查是否以万元为单位
		pattern = Pattern.compile("万元");
		matcher = pattern.matcher(unitStr);
		if (matcher.find()) {
			unitResult = "万元";
		}
		// 检查是否以百万元为单位
		pattern = Pattern.compile("百万元");
		matcher = pattern.matcher(unitStr);
		if (matcher.find()) {
			unitResult = "百万元";
		}

		if (unitResult == null) {
			return result;// 原本为return null，但考虑到某些报表不写单位，所以默认为以元作为单位
		} else if (unitResult.equals("元")) {
			return result;
		} else if (unitResult.equals("千元")) {
			return result * 1000;
		} else if (unitResult.equals("万元")) {
			return result * 10000;
		} else if (unitResult.equals("百万元")) {
			return result * 1000000;
		}
		return result;
	}

	/**
	 * 计算各类指标
	 * 
	 * @param countTotal
	 *            总的年报数
	 * @param countQualified
	 *            符合条件数据的条数(符合条件是指成功获取股票代码和年报年份的数据条数)
	 * @param countEffective
	 *            有效数据的条数(是指研究费用或广告费用不为0的数据条数)
	 * @param countResearchExpenses
	 *            研究费用有效的条数(是指研究费用不为0的数据条数)
	 * @param countAdvertisingExpenses
	 *            广告费用有效的条数(是指广告费用不为0的数据条数)
	 */
	public static void countIndex(double countTotal, double countQualified,
			double countEffective, double countResearchExpenses,
			double countAdvertisingExpenses) {
		NumberFormat nFromat = NumberFormat.getPercentInstance();
		double rateQualified = countQualified / countTotal;
		double rateEffective = countEffective / countQualified;
		double rateResearchExpenses = countResearchExpenses / countEffective;
		double rateAdvertisingExpenses = countAdvertisingExpenses
				/ countEffective;
		logger.info("合格率为：" + nFromat.format(rateQualified));
		logger.info("有效率为：" + nFromat.format(rateEffective));
		logger.info("研究率为：" + nFromat.format(rateResearchExpenses));
		logger.info("广告率为：" + nFromat.format(rateAdvertisingExpenses));
	}
}
