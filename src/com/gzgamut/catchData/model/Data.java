package com.gzgamut.catchData.model;

public class Data {

	private String stockCode;
	private String year;
	private double researchExpenses;
	private double advertisingExpenses;

	public Data(String stockCode, String year, double researchExpenses,
			double advertisingExpenses) {
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

	public double getResearchExpenses() {
		return researchExpenses;
	}

	public void setResearchExpenses(double researchExpenses) {
		this.researchExpenses = researchExpenses;
	}

	public double getAdvertisingExpenses() {
		return advertisingExpenses;
	}

	public void setAdvertisingExpenses(double advertisingExpenses) {
		this.advertisingExpenses = advertisingExpenses;
	}
}
