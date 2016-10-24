package com.neu.arithmeticproblemsolver.featureextractor;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.*;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.*;
import java.net.URLDecoder;
import java.util.*;

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
    private static final String QUESTION_SENTENCE = "sQuestion";
    private static final String PARENT_INDEX = "ParentIndex";
    private static final TreebankLanguagePack TREE_LANGUAGE_PACK;
    private static final MaxentTagger MAXENT_TAGGER;
    private static final GrammaticalStructureFactory GRAMMATICAL_STRUCTURE_FACTORY;
    private static final DependencyParser DEPENDENCY_PARSER;
    static{
        TREE_LANGUAGE_PACK = new PennTreebankLanguagePack();
        GRAMMATICAL_STRUCTURE_FACTORY = TREE_LANGUAGE_PACK.grammaticalStructureFactory();
        DEPENDENCY_PARSER = DependencyParser.loadFromModelFile("models/english_UD.gz");
        MAXENT_TAGGER = new MaxentTagger("models/english-left3words/english-left3words-distsim.tagger");
    }

    public static void main(final String[] args) {
        getDependencies();
        //getData();
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
    

    private static void writeData(final Map<String, Map<String, Integer>> featureMap) {
    	final Set<String> uniquePatterns = new HashSet<>();
    	final Map<String, String> labelStringMap = new HashMap<>();
    	labelStringMap.put("+", "Addition");
    	labelStringMap.put("-", "Subtraction");
    	labelStringMap.put("?", "Question");
    	labelStringMap.put("i", "Irrelevant");
    	labelStringMap.put("=", "Equals");
    	for (Map.Entry<String,Map<String, Integer>> entry: featureMap.entrySet()) {
        	String label = entry.getKey();
        	Map<String, Integer> valueMap = entry.getValue();
        	for(Map.Entry<String, Integer> valueEntry: valueMap.entrySet()) {
        		uniquePatterns.add(valueEntry.getKey());
        	}
    	}
    	
    	Object[] labels = featureMap.keySet().toArray();
    	final int noOfLabels = labels.length;
    	
    	for (int labelIndex = 0; labelIndex < noOfLabels; labelIndex++) {
    		final String currentLabel = (String)labels[labelIndex];
    		
    		for (int comparisonLabelIndex = labelIndex + 1; comparisonLabelIndex < noOfLabels; comparisonLabelIndex++) {
    			final String currentComparisonLabel = (String)labels[comparisonLabelIndex];
    			String fileName = "Comparison-";
        		fileName += labelStringMap.containsKey(currentLabel) ? labelStringMap.get(currentLabel) : labelIndex + "-";
        		fileName += labelStringMap.containsKey(currentComparisonLabel) ? labelStringMap.get(currentComparisonLabel) : comparisonLabelIndex ;
        		fileName += ".csv";
        		try {
        			FileWriter writer = new FileWriter(fileName);
        			writer.write("Pattern," + currentLabel + "," + currentComparisonLabel + "\n");
        			final Map<String, Integer> labelMap = featureMap.get(currentLabel);
        			final Map<String, Integer> comparisonLabelMap = featureMap.get(currentComparisonLabel);
        			
        			for (Map.Entry<String, Integer> patternKey: labelMap.entrySet()) {
        				final String pattern = patternKey.getKey();
        				if (comparisonLabelMap.containsKey(pattern)){
        					writer.write(pattern + "," + patternKey.getValue() + "," + comparisonLabelMap.get(pattern) + "\n");
        				}
        			}
        			
        			writer.flush();
        			writer.close();
        		} catch (final Exception e){
        			e.printStackTrace();
        		}
    		}
    		
    	}
    	
    	
    	for (String uniquePattern: uniquePatterns) {
    		for (Map<String, Integer> entry: featureMap.values()) {
    			if (!entry.containsKey(uniquePattern)) {
    				entry.put(uniquePattern, 0);
    			}
    		}
    	}
    	
    	for (Map.Entry<String,Map<String, Integer>> entry: featureMap.entrySet()) {
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

    private static void getDependencies(){
        Map<Integer, Set<String>> dependencyMap = new HashMap<>();
        final String datasetFilePath = URLDecoder.decode(Thread.currentThread()
                .getContextClassLoader()
                .getResource(ADD_SUB_FILE_PATH)
                .getPath());

        try {
            final InputStream inputFileStream = new FileInputStream(datasetFilePath);
            final JsonReader jsonReader = Json.createReader(inputFileStream);
            final JsonArray fileArray = jsonReader.readArray();
            Integer questionIndex;
            for (int questionCounter = 0; questionCounter < fileArray.size(); questionCounter++) {
                final JsonObject questionObject = fileArray.getJsonObject(questionCounter);
                questionIndex = questionObject.getInt(PARENT_INDEX);
                Set<String> dependencyList = new TreeSet<>();
                final String question = questionObject.getString(QUESTION_SENTENCE);
                final StringReader textReader = new StringReader(question);
                final DocumentPreprocessor textProcessor = new DocumentPreprocessor(textReader);
                textProcessor.setTokenizerFactory(TREE_LANGUAGE_PACK.getTokenizerFactory());

                for (List<HasWord> sentenceWordList : textProcessor) {
                    List<TaggedWord> taggedWords = MAXENT_TAGGER.tagSentence(sentenceWordList);
                    GrammaticalStructure grammaticalStructure = DEPENDENCY_PARSER.predict(taggedWords);

                    Collection<TypedDependency> sentenceDependencies = grammaticalStructure.typedDependenciesCCprocessed();
                    for(TypedDependency sentenceDependency : sentenceDependencies){
                        dependencyList.add(sentenceDependency.reln().getShortName());
                    }

                    dependencyMap.put(questionIndex, dependencyList);
                }
            }

            System.out.print(dependencyMap);
        }
        catch (final Exception e){
            e.printStackTrace();
        }

    }
}
