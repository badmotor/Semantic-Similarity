package org.knoesis.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.knoesis.twarql.extractions.Extractor;
import org.knoesis.twarql.extractions.TagExtractor;
import org.knoesis.twarql.extractions.TermFrequencyGenerator;
import org.knoesis.twitter.crawler.SearchTwitter;

/**
 * This data has either have to go to the 
 * database or has to be cached when running 
 * 
 * Models all the analysis of the hashtag either to 
 * be dumped into the db or to be stored in a file.
 * 
 * TODO: Look into how to modify this when the hashtag analysis get 
 * 		time sensitive.
 * @author pavan
 *
 */
public class HashTagAnalytics {
	private String hashTag;
	private String termWithoutHash;

	// To be determined by analyzers 
	private double consistencyMeaure;
	private double frequencyMeasure;
	private double specificityMeasure;
	private int noOfTweets;
	private int noOfReTweets;

	private Map<String, Double> termFrequencyOfHashTag;
	private Map<String, Double> termFrequencyOfKeyword;
	private List<AnnotatedTweet> aTweetsOfHashTag;
	private List<AnnotatedTweet> aTweetsOfKeyword;
	private long timeOfAnalysis;
	private int distinctUsersMentionHashTag;
	private TermFrequencyGenerator termFreqGenerator;
	private SearchTwitter searchTwitter;
	// This will get the last 1500 annotated Tweets.

	public HashTagAnalytics(String hashTag) {
		this.setHashTag(hashTag);
		this.setTermWithoutHash(hashTag.replace("#", ""));
		this.setTimeOfAnalysis(System.currentTimeMillis());
		this.generateTweets();
	}
	
	/**
	 * Uses SearchTwitter API to generate 
	 * 1. Tweets for hashtags and keywords
	 * 2. TermFrequency for hashtags and keywords
	 */
	private void generateTweets() {
		// TODO: This has to be set in the config file based on analysis;
		List<Extractor> extractors = new ArrayList<Extractor>();
		extractors.add(new TagExtractor());
		searchTwitter = new SearchTwitter(extractors);
		termFreqGenerator = new TermFrequencyGenerator();
		List<AnnotatedTweet> aTweetsOfHashTagFromAPI = searchTwitter.getTweets(hashTag, true, true);
		this.setNoOfTweets(aTweetsOfHashTagFromAPI.size());
		this.setaTweetsOfHashTag(aTweetsOfHashTagFromAPI);
		// The third parameter here is flag to set to whether to store the tweets into DB or not.
		this.setaTweetsOfKeyword(searchTwitter.getTweets(termWithoutHash, false, false));
		this.setTermFrequencyOfHashTag(termFreqGenerator.extractListTweets(this.aTweetsOfHashTag));
		this.setTermFrequencyOfKeyword(termFreqGenerator.extractListTweets(this.aTweetsOfKeyword));
	}

	
	public String getHashTag() {
		return hashTag;
	}

	public void setHashTag(String hashTag) {
		this.hashTag = hashTag;
		setTermWithoutHash(hashTag.replace("#", ""));
	}

	public String getTermWithoutHash() {
		return termWithoutHash;
	}

	public void setTermWithoutHash(String termWithoutHash) {
		this.termWithoutHash = termWithoutHash;
	}

	public double getConsistencyMeaure() {
		return consistencyMeaure;
	}

	public void setConsistencyMeaure(double consistencyMeaure) {
		this.consistencyMeaure = consistencyMeaure;
	}

	public double getFrequencyMeasure() {
		return frequencyMeasure;
	}

	public void setFrequencyMeasure(double frequencyMeasure) {
		this.frequencyMeasure = frequencyMeasure;
	}

	public double getSpecificityMeasure() {
		return specificityMeasure;
	}

	public void setSpecificityMeasure(double specificityMeasure) {
		this.specificityMeasure = specificityMeasure;
	}

	public Map<String, Double> getTermFrequencyOfHashTag() {
		return termFrequencyOfHashTag;
	}

	public void setTermFrequencyOfHashTag(Map<String, Double> termFrequencyOfHashTag) {
		this.termFrequencyOfHashTag = termFrequencyOfHashTag;
	}

	public Map<String, Double> getTermFrequencyOfKeyword() {
		return termFrequencyOfKeyword;
	}

	public void setTermFrequencyOfKeyword(Map<String, Double> termFrequencyOfKeyword) {
		this.termFrequencyOfKeyword = termFrequencyOfKeyword;
	}

	public long getTimeOfAnalysis() {
		return timeOfAnalysis;
	}

	public void setTimeOfAnalysis(long timeOfAnalysis) {
		this.timeOfAnalysis = timeOfAnalysis;
	}

	public int getDistinctUsersMentionHashTag() {
		return distinctUsersMentionHashTag;
	}

	public void setDistinctUsersMentionHashTag(int distinctUsersMentionHashTag) {
		this.distinctUsersMentionHashTag = distinctUsersMentionHashTag;
	}

	public List<AnnotatedTweet> getaTweetsOfHashTag() {
		return aTweetsOfHashTag;
	}

	public void setaTweetsOfHashTag(List<AnnotatedTweet> aTweetsOfHashTag) {
		this.aTweetsOfHashTag = aTweetsOfHashTag;
	}

	public List<AnnotatedTweet> getaTweetsOfKeyword() {
		return aTweetsOfKeyword;
	}

	public void setaTweetsOfKeyword(List<AnnotatedTweet> aTweetsOfKeyword) {
		this.aTweetsOfKeyword = aTweetsOfKeyword;
	}
	
	public int getNoOfTweets() {
		return noOfTweets;
	}

	public void setNoOfTweets(int noOfTweets) {
		this.noOfTweets = noOfTweets;
	}
	
	public int getNoOfReTweets() {
		return noOfReTweets;
	}

	public void setNoOfReTweets(int noOfReTweets) {
		this.noOfReTweets = noOfReTweets;
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return  getNoOfTweets() + "," 
				+getNoOfReTweets() + ","
				+getDistinctUsersMentionHashTag()+ ","
				+getFrequencyMeasure() + ","
				+getSpecificityMeasure();
	}

	
	
}
