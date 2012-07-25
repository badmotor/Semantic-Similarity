package org.knoesis.tags.analysis;

import java.util.Map;

/**
 * The classes implementing this interface analyzes the hashtags 
 * either for consideration or for deletion from crawling. 
 * 
 *  Example implementations
 *  1. Frequency Analyzer
 *  2. Consistency Analyzer etc. 
 *  
 * @author pavan
 *
 */
public interface Analyzer {
	public void analyze();
	
	public Map<String,Double> getResults();
}
