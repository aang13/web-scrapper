import com.opencsv.CSVWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileWriter;
import java.io.IOException;

import java.util.*;


public class Solution {

    private static final String TABLE_URL = "https://finance.yahoo.com/quote/%5EVIX/history?p=%5EVIX&guccounter=1&guce_referrer=aHR0cHM6Ly93d3cuZ29vZ2xlLmNvbS8&guce_referrer_sig=AQAAAIzRvFlrwMXO1vyJpHD8UM9tsxG_WblZ8NKzNLWU9F_J8tTcOtFpvVK_9beOydzvAHjylka9pUKpa3vituJp43Fo3aPtmrnY6sJSymiSzozrVxEN1ydTJOXV4jJqN6OuahVYr35vZE72vzUgX4HpAjvn3N3cam0TwlA-689nalDX";

    public static Elements scrapData(Elements rows, List<String> headers, List<Map<String, String>> listMap) {

        WebDriver driver = new ChromeDriver();
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        if(TABLE_URL.isEmpty() || TABLE_URL==null){
            System.out.println("The url is invalid");
            return rows;
        }
        driver.get(TABLE_URL);

        for (int i = 0; i < 200; i++)
            javascriptExecutor.executeScript("window.scrollTo(0, 200000)");
        
        Document doc;

        doc = Jsoup.parse(driver.getPageSource());

        Element table = doc.getElementById("mrt-node-Col1-1-HistoricalDataTable");
        if(table==null){
            System.out.println("No table found in the url");
            return rows;
        }
        rows = table.select("tr");

        Elements first = rows.get(0).select("th,td");


        for (Element header : first)
            headers.add(header.text());


        for (int row = 1; row < rows.size() - 1; row++) {
            Elements colVals = rows.get(row).select("th,td");
            int colCount = 0;
            Map<String, String> tuple = new LinkedHashMap<String, String>();
            for (Element colVal : colVals)
                tuple.put(headers.get(colCount++), colVal.text());
            listMap.add(tuple);
        }

        return rows;
    }

    public static void writeToCsv(Elements rows, List<String> headers, List<Map<String, String>> listMap) throws IOException {
        CSVWriter writer;

        if(rows==null) return;
        if(listMap.size()==0) throw new RuntimeException("No Data Found");

        writer = new CSVWriter((new FileWriter("CBOE.csv")));

        writer.writeNext(headers.toArray(new String[headers.size()]));

        for (int row = 1; row < rows.size() - 1; row++) {
            int colCnt = headers.size();
            List<String> rowString = new ArrayList<>();
            for (int i = 0; i < colCnt; i++) rowString.add(listMap.get(row - 1).get(headers.get(i)));
            writer.writeNext(rowString.toArray(new String[headers.size()]));
        }

        writer.close();
    }


    public static void main(String[] args) throws IOException {
        Elements rows = null;
        List<String> headers = new ArrayList<>();
        List<Map<String, String>> listMap = new ArrayList<Map<String, String>>();

        rows = scrapData(rows, headers, listMap);

        writeToCsv(rows, headers, listMap);
    }

}
