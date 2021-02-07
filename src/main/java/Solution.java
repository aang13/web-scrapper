import com.opencsv.CSVWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;

import java.util.*;

public class Solution {
    public static void main(String[] args) throws IOException {
        String url = "https://finance.yahoo.com/quote/%5EVIX/history?p=%5EVIX&guccounter=1&guce_referrer=aHR0cHM6Ly9tYWlsLmdvb2dsZS5jb20v&guce_referrer_sig=AQAAAKU5UXnZEhNK_s1k-l6fQ7l-jFaR2xghH5NOhaohsec-HThT1BaEsni-hUlysVCFWpzd4qa2OZ2YZtBDJNQqKw1Uh64_nppDI4RnzPnTgxDGta123-A_SbIBm4SA5B0xopHvDcl5A21esFvWceZnRJPk6ohtud7OGJpWcNLdADYT";
        Document doc = Jsoup.connect(url).get();

        Element table = doc.getElementById("mrt-node-Col1-1-HistoricalDataTable");
        Elements rows=table.select("tr");

        Elements first=rows.get(0).select("th,td");
        List<String>headers=new ArrayList<>();

        for(Element header:first)
            headers.add(header.text());


        List<Map<String,String>> listMap = new ArrayList<Map<String,String>>();
        for(int row=1;row<rows.size()-1;row++) {
            Elements colVals = rows.get(row).select("th,td");
            int colCount = 0;
            Map<String,String> tuple = new LinkedHashMap<String,String>();
            for(Element colVal : colVals)
                tuple.put(headers.get(colCount++), colVal.text());
            listMap.add(tuple);
        }


        CSVWriter writer=new CSVWriter((new FileWriter("CBOE.csv")));
        writer.writeNext(headers.toArray(new String[headers.size()]));

        for(int row=1;row<rows.size()-1;row++) {
            int colCnt = headers.size();
            List<String>rowString=new ArrayList<>();
            for(int i=0;i<colCnt;i++)rowString.add(listMap.get(row).get(headers.get(i)));
            writer.writeNext(rowString.toArray(new String[headers.size()]));
        }

        writer.close();
    }

}
