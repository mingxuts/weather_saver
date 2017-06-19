package jwc;

public class PerWeather {

	private String time;
	private float temperature;
	private int humidity;
	private String windSpeed;
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public float getTemperature() {
		return temperature;
	}
	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}
	public int getHumidity() {
		return humidity;
	}
	public void setHumidity(int humidity) {
		this.humidity = humidity;
	}
	public String getWindSpeed() {
		
		return windSpeed;
	}
	public void setWindSpeed(String windSpeed) {
		this.windSpeed = windSpeed;
	}
	
	@Override
	public String toString() {
		return "PerWeather [time=" + time + ", temperature=" + temperature + ", humidity=" + humidity + ", windSpeed="
				+ windSpeed + "]";
	}
	
}
