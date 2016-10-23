package com.neu.arithmeticproblemsolver.featureextractor;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Extracts the frequencies of n-gram sub pattern features for each label in the dataset.
 */
public class FeatureExtractor {

    private static final String DATASET_DIRECTORY = "dataset/";
    private static final String ADD_SUB_FILE_PATH = DATASET_DIRECTORY + "AdditionSubtraction.json";
    private static final String SENTENCES = "Sentences";
    private static final String SIMPLIFIED_SENTENCES = "SimplifiedSentences";
    private static final String LABEL = "label";
    private static final String SYNTACTIC_PATTERN = "SyntacticPattern";

    public static void main(final String[] args) {
            getData();
    }

    public static void getData(){
        final String datasetFilePath = URLDecoder.decode(Thread.currentThread()
                                                 .getContextClassLoader()
                                                 .getResource(ADD_SUB_FILE_PATH)
                                                 .getPath());

        try {
            final InputStream inputFileStream = new FileInputStream(datasetFilePath);
            final JsonReader jsonReader = Json.createReader(inputFileStream);
            final JsonArray fileArray = jsonReader.readArray();
            Map<String, Map<String, Integer>> features = new HashMap<String, Map<String, Integer>>();

            for (int questionCounter = 0; questionCounter < fileArray.size(); questionCounter++) {
                final JsonObject questionObject = fileArray.getJsonObject(questionCounter);
                final JsonArray sentences = questionObject.getJsonArray(SENTENCES);

                for (int sentenceCounter = 0; sentenceCounter < sentences.size(); sentenceCounter++) {
                    final JsonObject sentenceObject = sentences.getJsonObject(sentenceCounter);
                    final JsonArray simplifiedSentences = sentenceObject.getJsonArray(SIMPLIFIED_SENTENCES);

                    for (int simpleSentenceCounter = 0; simpleSentenceCounter < simplifiedSentences.size(); simpleSentenceCounter++) {
                        final JsonObject simpleSentenceObject = simplifiedSentences.getJsonObject(simpleSentenceCounter);
                        final String label = simpleSentenceObject.getString(LABEL);
                        final String syntacticPattern = simpleSentenceObject.getString(SYNTACTIC_PATTERN);

                        extractFeature(label, syntacticPattern, features);
                    }
                }
            }
            
<<<<<<< HEAD
            int count = 1;
            for (Map.Entry<String,Map<String, Integer>> entry: features.entrySet()) {
            	String label = entry.getKey();
            	try {
            		FileWriter writer = new FileWriter("Label" + count +".csv");
                    writer.write(label);
            		Map<String, Integer> valueMap = entry.getValue();
            		for(Map.Entry<String, Integer> valueEntry: valueMap.entrySet()) {
            			writer.write(valueEntry.getKey() + " , " + valueEntry.getValue() + "\n");
            		}
            		writer.flush();
            		writer.close();
            	} catch (final Exception e) {
            		e.printStackTrace();
            	}

                count++;
            }
            writeData(features);
            System.out.println(features);
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void extractFeature(final String label,
                                       final String syntacticPattern,
                                       Map<String, Map<String, Integer>> features){
        Map<String, Integer> frequency;
        frequency = features.get(label);
        if (frequency == null){
            frequency = new HashMap<String, Integer>();
            features.put(label, frequency);
        }

        for (int charCounter = 0; charCounter < syntacticPattern.length(); charCounter++){
            for (int patternCounter = charCounter + 1; patternCounter < syntacticPattern.length(); patternCounter++){
                String pattern = syntacticPattern.substring(charCounter, patternCounter);
                if (frequency.containsKey(pattern)){
                    frequency.put(pattern, frequency.get(pattern) + 1);
                } else {
                    frequency.put(pattern, 1);
                }
            }
            features.put(label, frequency);
        }
    }
    
    private static void writeData(final Map<String, Map<String, Integer>> feateureMap) {
    	final Set<String> uniquePatterns = new HashSet<>();
    	final Map<String, String> labelStringMap = new HashMap<>();
    	labelStringMap.put("+", "Addition");
    	labelStringMap.put("-", "Subtraction");
    	labelStringMap.put("?", "Question");
    	labelStringMap.put("i", "Irrelevant");
    	labelStringMap.put("=", "Equals");
    	for (Map.Entry<String,Map<String, Integer>> entry: feateureMap.entrySet()) {
        	String label = entry.getKey();
        	Map<String, Integer> valueMap = entry.getValue();
        	for(Map.Entry<String, Integer> valueEntry: valueMap.entrySet()) {
        		uniquePatterns.add(valueEntry.getKey());
        	}
    	}
    	
    	for (String uniquePattern: uniquePatterns) {
    		for (Map<String, Integer> entry: feateureMap.values()) {
    			if (!entry.containsKey(uniquePattern)) {
    				entry.put(uniquePattern, 0);
    			}
    		}
    	}
    	
    	for (Map.Entry<String,Map<String, Integer>> entry: feateureMap.entrySet()) {
        	String label = entry.getKey();
        	try {
        		String labelString = labelStringMap.containsKey(label) ? labelStringMap.get(label) : label;
        		FileWriter writer = new FileWriter("Label-"+labelString+".csv");
        		Map<String, Integer> valueMap = entry.getValue();
        		for(Map.Entry<String, Integer> valueEntry: valueMap.entrySet()) {
        			writer.write(valueEntry.getKey() + " , " + valueEntry.getValue() + "\n");
        		}
        		writer.flush();
        		writer.close();
        	} catch (final Exception e) {
        		e.printStackTrace();
        	}
        }
    	
    }
}
