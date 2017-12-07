import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {
	Queue<String> frontier;
	ArrayList<String> visitedLinks;
	ArrayList<String> domains;
	ArrayList<String> disallowedUrls;
	HashMap<String, Long> timeVisitedDomain;
	ArrayList<String> visitedRobots;
	public HashMap<Integer,Integer> xyValues;
	int count =1;

	public Crawler(String firstUrl){
		frontier = new LinkedList<String>();
		frontier.add(firstUrl);
		visitedLinks = new ArrayList<String>();
		domains = new ArrayList<String>();
		disallowedUrls = new ArrayList<String>();
		visitedRobots = new ArrayList<String>();
		timeVisitedDomain = new HashMap<String, Long>();
		xyValues = new HashMap<Integer, Integer>();
	}
//main crawler, each link first checks the robots.txt file, then the politeness and finally adds to the frontier.
	public void crawl() throws URISyntaxException, FileNotFoundException{
		
		while(!frontier.isEmpty() && visitedLinks.size()<=1000){
			String url = frontier.poll();
			//System.out.println("added link is "+url);
			String domain = getDomainName(url);
			parseRobotsTxt(domain);
			politeness(domain);
			try {
				if(!disallowedUrls.contains(url)){
					Document doc = Jsoup.connect(url).get();
					Elements urlLinks = doc.select("a[href]");
					for(Element link : urlLinks){
						String ll = link.attr("abs:href");
						if(ll.startsWith("http")){
							if(!visitedLinks.contains(ll) && inDomain(ll)){ //removing the inDomain condition will allow us to explore pages beyond cs.umass.edu
								//System.out.println("indomain " + ll);
								frontier.add(ll);
								visitedLinks.add(ll);
							}	
						}
					}
					xyValues.put(count++, frontier.size());
					
				} 
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		Writer writer = null;
		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream("/Users/rohantandon/Desktop/urls2.txt"), "utf-8"));
		    for(int i=0;i<visitedLinks.size();i++){
				System.out.println(i +" "+ visitedLinks.get(i));
				writer.write(visitedLinks.get(i)+"\n");
			}
		    writer.write("Number of robots.txt files = " + visitedRobots.size());
		    
		} catch (IOException ex) {
		  // report
		} finally {
		   try {writer.close();} catch (Exception ex) {/*ignore*/}
		}
		for(Entry<Integer, Integer> entry : xyValues.entrySet()){
			System.out.println(entry.getValue() +" "+ entry.getKey()+"\n");
		}
		
	}
//parsing the robots.txt file line by line and splitting each line upon seeing disallow
	public void parseRobotsTxt(String domain){ 
		String robots = "http://" + domain + "/robots.txt";
		String url = "http://" + domain;
		if(!visitedRobots.contains(robots)){
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(new URL(robots).openStream()));
				String line = in.readLine();
				while(line!=null){
					String[] array = line.split("Disallow:");
					if(array[0]=="Disallow:"){
						disallowedUrls.add(url+array[1]);
					}
					line = in.readLine();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			domains.add(domain);
			visitedRobots.add(robots);
			timeVisitedDomain.put(domain, System.currentTimeMillis());
		}
	}

	//checking the politeness by saving the last time visited at the webserver in a hashmap. 
	public void politeness(String domain) throws URISyntaxException{
		//if domain has been visited in last 5 seconds thread goes to sleep
		if(domains.contains(domain)){
			if(timeVisitedDomain.containsKey(domain)){
				if((System.currentTimeMillis()-timeVisitedDomain.get(domain)<5000)){
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

//obtaining the domain name. Referenced from: http://stackoverflow.com/questions/9607903/get-domain-name-from-given-url
	public String getDomainName(String url) throws URISyntaxException {
		URI uri = new URI(url);
		//System.out.println("host is "+uri.getHost());
		return uri.getHost();
	}
	//checking if the domain is under cs.umass.edu or cics.umass.edu
	public boolean inDomain(String url) throws URISyntaxException {
		if(getDomainName(url).equals("www.cs.umass.edu") || getDomainName(url).equals("www.cics.umass.edu")){
			return true;
		}
		else{
			return false;
		}
	}
	
	public void createGraph(HashMap<Integer, Integer> graph) throws IOException{
		Writer writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream("/Users/rohantandon/Desktop/graphs.txt"), "utf-8"));
		for(Entry<Integer, Integer> entry : graph.entrySet()){
			writer.write(entry.getValue() +" "+ entry.getKey()+"\n");
		}
		
	}
}
