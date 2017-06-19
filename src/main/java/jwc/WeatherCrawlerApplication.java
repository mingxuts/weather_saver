package jwc;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import de.siegmar.fastcsv.writer.CsvAppender;
import de.siegmar.fastcsv.writer.CsvWriter;

@SpringBootApplication
public class WeatherCrawlerApplication implements CommandLineRunner {
	
	@Autowired
	private Environment env;
	
	private static final Logger logger = LoggerFactory.getLogger(WeatherCrawlerApplication.class);
	
	private final String page_url = "https://www.wunderground.com/history/airport/ZSSS/"
			+ "%s/%s/%d/DailyHistory.html?req_city=%s&req_state=&req_statename=China"
			+ "&reqdb.zip=00000&reqdb.magic=738&reqdb.wmo=58367&MR=1";
	
	private int[] days = getDaysRange();
	
	private CsvWriter writer = new CsvWriter();
	
	private CsvAppender appender = null;

	public static void main(String[] args) {
		SpringApplication.run(WeatherCrawlerApplication.class, args);
	}

	private int[] getDaysRange() {
		int[] tempDays = new int[]{20, 21, 22, 23, 24, 25, 26, 27};
		return tempDays;
	}

	@Override
	public void run(String... arg0) throws Exception {
		String city = env.getProperty("page.city");
		String year = env.getProperty("page.year");
		String month = env.getProperty("page.month");
		
		File file = new File("shanghai.csv");
		
		appender = writer.append(file, StandardCharsets.UTF_8);
		appender.appendLine("city", "date", "time", "temp", "humi", "ws");	
		
		for(int i=0; i < days.length; i++){
			int day = days[i];
			
			String url = String.format(page_url, year, month, day, city);
			logger.info(url);
			Connection conn =  Jsoup.connect(url); 
			
			Document doc = conn.get();
			//logger.info(doc.toString());
			List<PerWeather> listofWeather = getWeather(doc);
			PerDay p = new PerDay(year + "-" + month + "-" + day, listofWeather);
			saveToCsv(p);			
		}

	}
	
	private List<PerWeather> getWeather(Document doc) {
		List<PerWeather> values = new ArrayList<>();
		Elements rows = doc.select("table[id=obstable]>tbody>tr");
		for (Element e : rows) {
			values.add(loadInfo(e));
			logger.info(loadInfo(e).toString());
		}
		return values;
	}
	
	private PerWeather loadInfo(Element row) {
		PerWeather value= new PerWeather();
		String time = row.select("td").first().text();
		value.setTime(time);
		
		float temp = Float.parseFloat(row.select(".wx-data .wx-value").first().text());
		value.setTemperature(temp);

		int humiCell = isCellHasPercentage(row.select("td").get(3).text()) ? 3 : 4;
		int humi = Integer.parseInt(row.select("td").get(humiCell).text().substring(0, 2));
		value.setHumidity(humi);
		
		int wsCell = isCellHasSpan(row.select("td").get(7)) ? 7 : 8;
		String ws = row.select("td").get(wsCell).text();
		value.setWindSpeed(ws);
		return value;
	}
	
	public void saveToCsv(PerDay perDay) throws Exception {
		for (PerWeather p : perDay.getWeatherHistory()){
			appender.appendLine("上海", perDay.getDate(), 
					ampmTo24Hour(p.getTime()),  
					String.valueOf(tempToInt(p.getTemperature())), 
					String.valueOf(p.getHumidity()), 
					p.getWindSpeed());
			appender.flush();
		}
	}
	
	private boolean isCellHasPercentage(String text) {
		String last = text.substring(text.length() -1);
		if (last.equalsIgnoreCase("%"))
			return true;
		else 
			return false;
	}
	
	private boolean isCellHasSpan(Element element) {
		if (element.select("span").size() > 1) {
			return true;
		} else 
			return false;
	}
	
	private String ampmTo24Hour(String time) {
	    SimpleDateFormat date12Format = new SimpleDateFormat("hh:mm a");

	    SimpleDateFormat date24Format = new SimpleDateFormat("HH:mm");

	    try {
			return date24Format.format(date12Format.parse(time)) + ":00";
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}	
	    return "";
	}
	
	private int tempToInt(float temperature) {
		return (int) temperature; 
	}
}
