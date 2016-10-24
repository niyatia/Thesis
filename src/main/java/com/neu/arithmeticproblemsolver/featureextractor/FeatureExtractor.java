package com.neu.arithmeticproblemsolver.featureextractor;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;


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

    final static Map<String, String> LABEL_STRINGS = new HashMap<>();
    final static String ADDITION_LABEL = "+";
    final static String SUBTRACTION_LABEL = "-";
    final static String QUESTION_LABEL = "?";
    final static String EQUALS_LABEL = "=";
    final static String IRRELEVANT_LABEL = "i";

    private static final String QUESTION_SENTENCE = "sQuestion";
    private static final String PARENT_INDEX = "ParentIndex";
    private static final TreebankLanguagePack TREE_LANGUAGE_PACK;
    private static final MaxentTagger MAXENT_TAGGER;
    private static final GrammaticalStructureFactory GRAMMATICAL_STRUCTURE_FACTORY;
    private static final DependencyParser DEPENDENCY_PARSER;
    
    static {
        TREE_LANGUAGE_PACK = new PennTreebankLanguagePack();
        GRAMMATICAL_STRUCTURE_FACTORY = TREE_LANGUAGE_PACK.grammaticalStructureFactory();
        DEPENDENCY_PARSER = DependencyParser.loadFromModelFile("models/english_UD.gz");
        MAXENT_TAGGER = new MaxentTagger("models/english-left3words/english-left3words-distsim.tagger");
    }

    public static void main(final String[] args) {
    	LABEL_STRINGS.put(ADDITION_LABEL, "Addition");
    	LABEL_STRINGS.put(SUBTRACTION_LABEL, "Subtraction");
    	LABEL_STRINGS.put(QUESTION_LABEL, "Question");
    	LABEL_STRINGS.put(IRRELEVANT_LABEL, "Irrelevant");
    	LABEL_STRINGS.put(EQUALS_LABEL, "Equals");
        extractFeatures();
    }

    /**
     * Extracts features from the dataset.
     */
    public static void extractFeatures(){
        try {
        	final String datasetFilePath = URLDecoder.decode(Thread.currentThread()
				                    .getContextClassLoader()
				                    .getResource(ADD_SUB_FILE_PATH)
				                    .getPath());
            final InputStream inputFileStream = new FileInputStream(datasetFilePath);
            final JsonReader jsonReader = Json.createReader(inputFileStream);
            final JsonArray fileArray = jsonReader.readArray();
            Map<String, Map<String, Integer>> labelToPatternFrequencies = new HashMap<String, Map<String, Integer>>();

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

                        generateNGramForSyntacticPattern(label, syntacticPattern, labelToPatternFrequencies);
                    }
                }
            }
            
            final SortedSet<VariantPattern> topKPatterns = getTopKVariantPatterns(labelToPatternFrequencies, 20, 10);
            System.out.println(topKPatterns);
            
            generateComparisonFiles(labelToPatternFrequencies);
            System.out.println(labelToPatternFrequencies);
            System.out.println(labelToPatternFrequencies);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates the n-grams of the given syntactic pattern and adds it to the nGrams.
     * @param label: The label of the syntactic pattern.
     * @param syntacticPattern: the syntactic pattern.
     * @param nGrams: The n-grams corresponding to each label. 
     */
    private static void generateNGramForSyntacticPattern(final String label,
                                       final String syntacticPattern,
                                       Map<String, Map<String, Integer>> nGrams){
        Map<String, Integer> frequency;
        frequency = nGrams.get(label);
        if (frequency == null){
            frequency = new HashMap<String, Integer>();
            nGrams.put(label, frequency);
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
            nGrams.put(label, frequency);
        }
    }
    
    private static SortedSet<VariantPattern> getTopKVariantPatterns(final Map<String, Map<String, Integer>> featureMap, final int k, final int difference){
    	
    	final SortedSet<VariantPattern> topKVariantPatterns = new TreeSet<>(new Comparator<VariantPattern>() {
			@Override
			public int compare(final VariantPattern o1, final VariantPattern o2) {
				return o1.getSumOfCount() - o2.getSumOfCount();
			}
    	});

    	final Map<String, Integer> additionMap = featureMap.get(ADDITION_LABEL);
    	int noOfVariantPatternsFound = 0;
    	for (final String pattern: additionMap.keySet()) {
    		
    		boolean patternExistsInEveryOtherMap = true;
    		final VariantPattern variantPattern = new VariantPattern(pattern);
    		for (final Entry<String, Map<String, Integer>> patternMaps: featureMap.entrySet()) {
    			final String label = patternMaps.getKey();
    			final Map<String, Integer> patternFrequencies = patternMaps.getValue();
    			    			
    			if (!patternFrequencies.containsKey(pattern)) {
    				patternExistsInEveryOtherMap = false;
    			} else {
    				variantPattern.addCount(label, patternFrequencies.get(pattern));
    			}
    		}
    		
    		if (patternExistsInEveryOtherMap) {
    			if (variantPattern.isDifferenceGreaterThanT(difference)) {
    				topKVariantPatterns.add(variantPattern);
    				noOfVariantPatternsFound++;
    				
    				if (noOfVariantPatternsFound == k) {
    					break;
    				}
    			}
    		}
    	}    	
    	return topKVariantPatterns;
    }
    
    
    /**
     * Generates comparison CSVs for each combination of two labels found in featureMap.
     * @param featureMap The map of labels to pattern frequencies.
     */
    private static void generateComparisonFiles(final Map<String, Map<String, Integer>> featureMap) {
    	Object[] labels = featureMap.keySet().toArray();
    	final int noOfLabels = labels.length;
    	
    	for (int labelIndex = 0; labelIndex < noOfLabels; labelIndex++) {
    		final String currentLabel = (String)labels[labelIndex];
    		
    		for (int comparisonLabelIndex = labelIndex + 1; comparisonLabelIndex < noOfLabels; comparisonLabelIndex++) {
    			final String currentComparisonLabel = (String)labels[comparisonLabelIndex];
    			String fileName = "Comparison-";
        		fileName += LABEL_STRINGS.containsKey(currentLabel) ? LABEL_STRINGS.get(currentLabel) : labelIndex + "-";
        		fileName += LABEL_STRINGS.containsKey(currentComparisonLabel) ? LABEL_STRINGS.get(currentComparisonLabel) : comparisonLabelIndex ;
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
