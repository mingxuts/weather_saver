package jwc;

import java.util.List;

public class PerDay {
	
	private String dateOfList;
	
	private List<PerWeather> weatherHistory = null;
	
	public PerDay(String date, List<PerWeather> input) {
		this.dateOfList = date;
		this.weatherHistory = input;		
	}
	
	public List<PerWeather> getWeatherHistory() {
		return weatherHistory;
	}
	
	public String getDate() {
		return this.dateOfList;
	}
}
