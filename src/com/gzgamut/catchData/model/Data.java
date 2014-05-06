package com.gzgamut.catchData.model;

public class Data {

	private String stockCode;
	private String year;
	private long researchExpenses;
	private long advertisingExpenses;

	public Data(String stockCode, String year, long researchExpenses,
			long advertisingExpenses) {
		this.stockCode = stockCode;
		this.year = year;
		this.researchExpenses = researchExpenses;
		this.advertisingExpenses = advertisingExpenses;
	}

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public long getResearchExpenses() {
		return researchExpenses;
	}

	public void setResearchExpenses(long researchExpenses) {
		this.researchExpenses = researchExpenses;
	}

	public long getAdvertisingExpenses() {
		return advertisingExpenses;
	}

	public void setAdvertisingExpenses(long advertisingExpenses) {
		this.advertisingExpenses = advertisingExpenses;
	}
}
