import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebcrawlerDriver {
	

	public static void main(String[] args) throws URISyntaxException, IOException {
		// TODO Auto-generated method stub
		Crawler webcrawler= new Crawler("http://ciir.cs.umass.edu");
		webcrawler.crawl();
		webcrawler.createGraph(webcrawler.xyValues);
	}

}
