package org.djkazic.RoomOS.modules;

import java.net.URL;
import java.util.List;

import org.djkazic.RoomOS.RTCore;
import org.djkazic.RoomOS.Settings;
import org.djkazic.RoomOS.Utils;
import org.djkazic.RoomOS.modules.bases.Module;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class NewsModule extends Module {

	private Utils uc;
	private String type;
	private String url;
	
	public NewsModule() {
		super("cmd_check_news");
		uc = new Utils();
	}

	public void process() {
		type = "news";
		url = Settings.newsGeneric;
		determineType();
		uc.speak("Connecting to online data feeds.");
		uc.speak("Your top stories today for " + type + ": ");
		try {
			URL feedUrl = new URL(url);

			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new XmlReader(feedUrl));

			List<SyndEntry> allNews = feed.getEntries();
			int counter = 0;
			
			for(int i=0; i < allNews.size(); i++) {
			SyndEntry curEntry = allNews.get(i);
				if(!RTCore.alreadyRead.contains(curEntry) && counter <= 3) {
					String entryStr = curEntry.getTitle();
					
					if(type.equals("news")) {
						String[] entrySplit = entryStr.split(" - ");
						uc.speak("From " + entrySplit[1] + ": " + entrySplit[0]);
					} else {
						uc.speak(entryStr);
					}
					RTCore.alreadyRead.add(allNews.get(i));
					counter++;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		uc.speak("For additional stories, ask again.");
		latch.countDown();
	}
	
	private void determineType() {
		if(resultText.endsWith("finance")) {
			type = "finance";
			url = Settings.newsFinance;
		} else if(resultText.endsWith("sports")) {
			type = "sports";
			url = Settings.newsSports;
		} else if(resultText.endsWith("tech") || (resultText.endsWith("technology"))) {
			type = "tech";
			url = Settings.newsTechnology;
		}
	}
}
