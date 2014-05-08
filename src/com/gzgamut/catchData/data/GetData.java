package com.gzgamut.catchData.data;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import com.gzgamut.catchData.model.Data;
import com.gzgamut.catchData.util.Config;
import com.gzgamut.catchData.util.FileHelper;
import com.gzgamut.catchData.util.StringHelper;

/**
 * 
 * @author Vincent_Melancholy
 * 
 */
public class GetData {

	private static Logger logger = Logger.getLogger(GetData.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		collectData();
	}

	/**
	 * 获取数据
	 */
	public static void collectData() {
		Map<String, String> map = FileHelper.readfile(Config.path);
		// 总的年报数
		int countTotal = 0;
		// 符合条件数据的条数(符合条件是指成功获取股票代码和年报年份的数据条数)
		int countQualified = 0;
		// 有效数据的条数(是指研究费用或广告费用不为0的数据条数)
		int countEffective = 0;
		// 研究费用有效的条数(是指研究费用不为0的数据条数)
		int countResearchExpenses = 0;
		// 广告费用有效的条数(是指广告费用不为0的数据条数)
		int countAdvertisingExpenses = 0;

		if (map != null) {
			TreeMap<String, Data> treemap = new TreeMap<String, Data>();
			String flag = "";
			Set<String> keySet = map.keySet();
			Iterator<String> it = keySet.iterator();
			while (it.hasNext()) {
				String key = it.next();
				String value = map.get(key);
				logger.info(value);
				if (value.indexOf(".txt") > 0 && value.indexOf("Url") == -1) {
					countTotal++;
					String content = CreateTXT.getTxtContent(value);
					String stockCodeStr = getStockCode(content);
					String yearStr = getYear(content);
					if (stockCodeStr != null && yearStr != null) {
						countQualified++;
						if (flag.equals("")) {
							flag = stockCodeStr;
						}
						long researchExpenses = getResearchExpenses(content);
						long advertisingExpenses = getAdvertisingExpenses(content);
						if (researchExpenses != 0) {
							countResearchExpenses++;
						}
						if (advertisingExpenses != 0) {
							countAdvertisingExpenses++;
						}
						if (researchExpenses != 0 || advertisingExpenses != 0) {
							countEffective++;
						}

						// 如果flag不一致，则代表将要插入的数据和treemap中的数据属于两个不同的股票，马上把treemap中的数据写入excel中并清空
						if (!flag.equals(stockCodeStr)) {
							writeToExcel(treemap);
							treemap.clear();
							flag = stockCodeStr;
						}
						treemap.put(yearStr, new Data(stockCodeStr, yearStr,
								researchExpenses, advertisingExpenses));
					} else {
						logger.info("股票代码或年份不符合条件");
					}
				}
			}
			writeToExcel(treemap);
			StringHelper.countIndex(countTotal, countQualified, countEffective,
					countResearchExpenses, countAdvertisingExpenses);
		}
	}

	/**
	 * 根据内容获取年度报告的股票代码
	 * 
	 * @param content
	 * @return
	 */
	private static String getStockCode(String content) {
		String[] locationStr = { "股票代码", "股票简称及代码", "证券代码", "股票编号", "股 票 代 码",
				"股  票  代  码", "A股代码", "B股代码", "A 股代码", "B 股代码" };
		int location = -1;
		for (String temp : locationStr) {
			location = content.indexOf(temp);
			while (location != -1) {
				String stockCode = StringHelper
						.replaceSpecialCharacters(content.substring(
								location + 5, location + 40));
				logger.info("股票代码原始数据为：" + stockCode);
				// 检查是否为六位数的年份
				Pattern pattern = Pattern.compile("\\d{6}");
				Matcher matcher = pattern.matcher(stockCode);
				if (matcher.find()) {
					String result = matcher.group();
					logger.info("股票代码数据处理成功！");
					logger.info("股票代码处理过后为：" + result);
					return result;
				}
				location = content.indexOf(temp, location + 3);
			}
		}
		return null;
	}

	/**
	 * 根据内容获取年度报告的年份
	 * 
	 * @param content
	 * @return
	 */
	private static String getYear(String content) {
		String[] locationStr = { "年度报告", "年 度 报 告", "年年度报告", "年 年 度 报 告",
				"年 度报告" };
		int location = -1;
		for (String temp : locationStr) {
			location = content.indexOf(temp);
			if (location != -1) {
				if (location - 8 > -1) {
					String year = StringHelper.replaceSpecialCharacters(content
							.substring(location - 8, location));
					logger.info("年份原始数据为：" + year);
					// 检查是否为四位数的年份
					Pattern pattern = Pattern.compile("\\d");
					Matcher matcher = pattern.matcher(year);
					String result = "";
					while (matcher.find()) {
						result += matcher.group();
					}
					if (!result.equals("") && result.length() - 4 > -1) {
						result = result.substring(result.length() - 4,
								result.length());
						logger.info("年份数据处理成功！");
						logger.info("年份处理过后为：" + result);
						return result;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 根据内容获取年度报告的研发费用
	 * 
	 * @param content
	 * @return
	 */
	private static long getResearchExpenses(String content) {
		String[] taxStr = { "支付其他与经营活动有关的现金", "支付的其他与经营活动有关的现金",
				"其他与经营活动有关的现金", "与经营活动有关", "" };
		int tax = -1;
		for (String temp : taxStr) {
			tax = content.indexOf(temp);
			while (tax != -1) {
				String[] locationStr = { "研发费用", "研发支出", "研究开发费", "技术研究费",
						"技术开发费", "研发费", "科研费", "咨询及技术开发费" };
				int location = -1;
				for (String templocation : locationStr) {
					location = content.indexOf(templocation, tax);
					while (location != -1) {
						String researchExpensesStr = StringHelper
								.replaceSpecialCharacters(content.substring(
										location + 3, location + 34));
						logger.info("原本的字符串为:" + researchExpensesStr);
						String researchExpenses = "";
						int count = 0;
						for (int i = 0; i < researchExpensesStr.length(); i++) {
							String character = researchExpensesStr.substring(i,
									i + 1);
							if (!character.equals(",")) {
								if (character.equals(".")) {
									count = 0;
									researchExpenses += character;
									count++;
								} else if (count == 3) {
									break;
								} else {
									researchExpenses += character;
									count++;
								}
							} else {
								count = 0;
							}
						}
						logger.info("处理过后的字符串为:" + researchExpenses);
						if (!researchExpenses.equals("")) {
							// 根据计量单位整理数据
							String[] unitString = { "单位", "人民币" };
							int unit = -1;
							for (String tempunit : unitString) {
								unit = content.lastIndexOf(tempunit, location);
								if (unit != -1) {
									break;
								}
							}
							if (unit == -1) {
								unit = 0;
							}
							String unitStr = content.substring(unit, location);
							// 匹配单位
							long unitResult = StringHelper.unitMatcher(
									researchExpenses, unitStr);
							return unitResult;
						}
						location = content.indexOf(templocation, location + 3);
					}
				}
				tax = content.indexOf(temp, tax + 3);
			}
		}
		return 0;
	}

	/**
	 * 根据内容获取年度报告的广告费用
	 * 
	 * @param content
	 * @return
	 */
	private static long getAdvertisingExpenses(String content) {
		String[] taxStr = { "销售费用", "" };
		int tax = -1;
		for (String temp : taxStr) {
			tax = content.indexOf(temp);
			while (tax != -1) {
				String[] locationStr = { "广告宣传费", "广告费用", "广告费" };
				int location = -1;
				for (String templocation : locationStr) {
					location = content.indexOf(templocation, tax);
					while (location != -1) {
						String advertisingExpensesStr = StringHelper
								.replaceSpecialCharacters(content.substring(
										location + 4, location + 34));
						logger.info("原本的字符串为:" + advertisingExpensesStr);
						String advertisingExpenses = "";
						int count = 0;
						for (int i = 0; i < advertisingExpensesStr.length(); i++) {
							String character = advertisingExpensesStr
									.substring(i, i + 1);
							if (!character.equals(",")) {
								if (character.equals(".")) {
									count = 0;
									advertisingExpenses += character;
									count++;
								} else if (count == 3) {
									break;
								} else {
									advertisingExpenses += character;
									count++;
								}
							} else {
								count = 0;
							}
						}
						logger.info("处理过后的字符串为:" + advertisingExpenses);
						if (!advertisingExpenses.equals("")) {
							// 根据计量单位整理数据
							String[] unitString = { "单位", "人民币" };
							int unit = -1;
							for (String tempunit : unitString) {
								unit = content.lastIndexOf(tempunit, location);
								if (unit != -1) {
									break;
								}
							}
							if (unit == -1) {
								unit = 0;
							}
							String unitStr = content.substring(unit, location);
							// 匹配单位
							long unitResult = StringHelper.unitMatcher(
									advertisingExpenses, unitStr);
							return unitResult;
						}
						location = content.indexOf(templocation, location + 3);
					}
				}
				tax = content.indexOf(temp, tax + 3);
			}
		}
		return 0;
	}

	/**
	 * 把相关数据写到excel当中
	 * 
	 * @param treemap
	 */
	private static void writeToExcel(TreeMap<String, Data> treemap) {
		Set<String> keySet = treemap.keySet();
		Iterator<String> it = keySet.iterator();
		while (it.hasNext()) {
			String key = it.next();
			Data value = treemap.get(key);
			String stockCode = value.getStockCode();
			String year = value.getYear();
			long researchExpenses = value.getResearchExpenses();
			long advertisingExpenses = value.getAdvertisingExpenses();
			writeToExcel(stockCode, year, researchExpenses, advertisingExpenses);
		}
	}

	/**
	 * 把相关数据写到excel当中
	 * 
	 * @param company
	 *            公司名称
	 * @param year
	 *            年份
	 * @param rearchExpenses
	 *            研发费用
	 * @param advertisingExpenses
	 *            广告费用
	 */
	private static void writeToExcel(String company, String year,
			long rearchExpenses, long advertisingExpenses) {
		// 打开文件
		WritableWorkbook book = null;
		try {
			File file = new File("数据.xls");
			WritableSheet sheet = null;
			if (file.exists()) {
				// 获得Excel文件
				Workbook wb = Workbook.getWorkbook(file);
				// 打开一个文件副本，并且指定数据写回到原文件
				book = Workbook.createWorkbook(file, wb);
				sheet = book.getSheet("研发费用");
			} else {
				book = Workbook.createWorkbook(file);
				// 生成名为“研发费用”的工作表，参数0表示这是第一页
				sheet = book.createSheet("研发费用", 0);
				// 构造属性名称
				Label label1 = new Label(0, 0, "公司名称");
				sheet.addCell(label1);
				Label label2 = new Label(1, 0, "年份");
				sheet.addCell(label2);
				Label label3 = new Label(2, 0, "研发费用");
				sheet.addCell(label3);
				Label label4 = new Label(3, 0, "广告费用");
				sheet.addCell(label4);
			}

			int rowNumber = sheet.getRows();
			Label label_company = new Label(0, rowNumber, company);
			sheet.addCell(label_company);
			Label label_year = new Label(1, rowNumber, year);
			sheet.addCell(label_year);
			jxl.write.Number number_rearchExpense = new jxl.write.Number(2,
					rowNumber, rearchExpenses);
			sheet.addCell(number_rearchExpense);
			jxl.write.Number number_advertisingExpenses = new jxl.write.Number(
					3, rowNumber, advertisingExpenses);
			sheet.addCell(number_advertisingExpenses);

			// 写入数据
			book.write();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (book != null) {
					book.close();
				}
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
