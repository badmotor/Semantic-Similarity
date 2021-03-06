package org.knoesis.similarity.wikipedia;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.knoesis.api.DBpediaTypeGetter;
import org.knoesis.api.WikipediaParser;
import org.knoesis.similarity.JaccardCoefficientSimilarityCalculator;
import org.openjena.atlas.logging.Log;

/**
 * This class generates a list of entities with weights 
 * that are related to a particular topic. The entities or the 
 * concepts are taken from wikipedia that can later be 
 * transformed to DBPedia URIs.
 * 
 * TODO: For now the topic calculator takes only 2 hops into consideration
 * 		 Later this has to be changed to provide for more hops. 
 * 		
 * TODO: Also the scalability should be considered. Transform the problem to Map Reduce.
 * 
 * FIXME: Use logging everytime you code.
 * @author pavan
 *
 */
public class TopicSpecificSimilarityCalculator {
	private static String wikipediaTopic;
	private static int hops;
	private WikipediaParser parser;
	private static Logger logger = Logger.getLogger(TopicSpecificSimilarityCalculator.class);

	/**
	 *  
	 * @param wikipediaTopic
	 * @param hops
	 */
	public TopicSpecificSimilarityCalculator(String wikipediaTopic, int hops) {
		this.wikipediaTopic = wikipediaTopic;
		this.hops = hops;
		parser = new WikipediaParser(wikipediaTopic);
	}

	/**
	 * 
	 * @param wikipediaTopic
	 */
	public TopicSpecificSimilarityCalculator(String wikipediaTopic) {
		this.wikipediaTopic = wikipediaTopic;
		this.hops = 2;
		parser = new WikipediaParser(wikipediaTopic);
	}
	/**
	 * FIXME: In this method, the articles retreived can be 
	 * 		 1. Categories -- Ignore
	 * 		 2. Wikipedia  -- Not sure what this is
	 * 		 3. Templates -- Remove Template to compare.
	 * 		 4. Find more
	 * @return
	 */
	public Map<String, Double> calculate(){
		Set<String> firstLinks = parser.getLinks();
		Map<String, Set<String>> secondLinks = new HashMap<String, Set<String>>();

		for(String link: firstLinks){
			// FIXME: Find a better way to do this, as of now just replacing Templates 
			if(!link.contains("Category") || !link.contains("Wikipedia")){
				logger.info("processing "+link+" article");
				String linkWithoutTemplates = link.replace("Template:", "");
				parser.setWikipediaPage(linkWithoutTemplates);
				secondLinks.put(linkWithoutTemplates, parser.getLinks());
			}
		}
		//System.out.println(secondLinks);

		return calculateWeights(firstLinks, secondLinks);
	}

	/**
	 * This method calculates the weight based on following parameters
	 * 
	 * 1. Unidirectional or bidirectional relationship to the topic
	 * 2. Jakkard co-efficient of the Topic to the first links 
	 * 
	 * @param firstLinks
	 * @param secondLinks
	 * @return
	 */
	private Map<String, Double> calculateWeights(Set<String> firstLinks, Map<String, Set<String>> secondLinks){
		Map<String, Double> results = new HashMap<String, Double>();
		for(String secondLink: secondLinks.keySet()){
			Set<String> links = secondLinks.get(secondLink);
			// If there are no further links. Then return two since it is connected.
			if(links==null){
				// Comment this line if you need just the jaccard coefficient
				results.put(secondLink, 2.0d);
				continue;
			}
			double jakkardIndex = JaccardCoefficientSimilarityCalculator.calculate(firstLinks, links);
			// Comment the following if else loop for just the Jaccard coefficient
			if(links.contains(wikipediaTopic))
				results.put(secondLink, 3.0d+jakkardIndex);
			else
				results.put(secondLink, 2.0d+jakkardIndex);
			
			// Uncomment to print just the jaccardcoefficient
			//results.put(secondLink, jakkardIndex);
			// TODO: For now not considering the second hop
			/*
			 * for(String thirdLink: links){
			 
				if(!results.keySet().contains(thirdLink))
					results.put(thirdLink, 1.0d);
			}
			*/
		}
			return results;
		}

		public static void main(String[] args) {
			logger.info("Starting the process for US Elections 2012 " + System.currentTimeMillis());

			try {
				Writer write = new FileWriter(new File("analysis/uselections_jakkard.model"));
				Writer writeTypes = new FileWriter(new File("analysis/uselections.types"));
				//United_States_presidential_election,_2012
				TopicSpecificSimilarityCalculator wikiCalc = new TopicSpecificSimilarityCalculator("United States presidential election, 2012");
				Map<String, Double> relatedLinks = wikiCalc.calculate();
				for(String link: relatedLinks.keySet()){
					write.append(link+"\t"+relatedLinks.get(link)+"\n");
					List<String> types = DBpediaTypeGetter.getType(link);
					for(String type: types){
						writeTypes.append(link+"\t"+type+"\n");
					}
				}
			
				write.close();
				writeTypes.close();
				logger.info("Ending the process for US Elections 2012 " + System.currentTimeMillis());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
