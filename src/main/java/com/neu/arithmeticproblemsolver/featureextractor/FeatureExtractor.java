package com.neu.arithmeticproblemsolver.featureextractor;

import static com.neu.arithmeticproblemsolver.featureextractor.PublicKeys.*;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URLDecoder;
import java.util.*;
import java.util.Map.Entry;


/**
 * Extracts the frequencies of n-gram sub pattern features for each label in the dataset.
 */
public class FeatureExtractor {
    
    private static final TreebankLanguagePack TREE_LANGUAGE_PACK;
    private static final MaxentTagger MAXENT_TAGGER;
    private static final DependencyParser DEPENDENCY_PARSER;
    
    static {
        TREE_LANGUAGE_PACK = new PennTreebankLanguagePack();
        DEPENDENCY_PARSER = DependencyParser.loadFromModelFile("models/english_UD.gz");
        MAXENT_TAGGER = new MaxentTagger("models/english-left3words/english-left3words-distsim.tagger");        
    }

    private final SortedSet<QuestionInstance> mQuestionInstances;
    
    public static void main(final String[] args) { 
    	FeatureExtractor extractor = new FeatureExtractor();
    	extractor.extractFeatures();
    	System.out.println(extractor.mQuestionInstances);
    }

    public FeatureExtractor() {
    	mQuestionInstances = new TreeSet<>(getQuestionInstancesComparator());
    }
    /**
     * Extracts features from the dataset.
     */
    public void extractFeatures(){
        try {
            final InputStream inputFileStream = new FileInputStream(ADD_SUB_FILE_PATH);
            final JsonReader jsonReader = Json.createReader(inputFileStream);
            final JsonArray fileArray = jsonReader.readArray();
            Map<String, Map<String, Integer>> labelToPatternFrequencies = new HashMap<String, Map<String, Integer>>();

            for (int questionCounter = 0; questionCounter < fileArray.size(); questionCounter++) {
                final JsonObject questionObject = fileArray.getJsonObject(questionCounter);
                final JsonArray sentences = questionObject.getJsonArray(KEY_SENTENCES);
                
                final QuestionInstance questionInstance = extractDependencyFeatures(questionObject);
                mQuestionInstances.add(questionInstance);
                
                for (int sentenceCounter = 0; sentenceCounter < sentences.size(); sentenceCounter++) {
                    final JsonObject sentenceObject = sentences.getJsonObject(sentenceCounter);
                    final JsonArray simplifiedSentences = sentenceObject.getJsonArray(KEY_SIMPLIFIED_SENTENCES);

                    for (int simpleSentenceCounter = 0; simpleSentenceCounter < simplifiedSentences.size(); simpleSentenceCounter++) {
                        final JsonObject simpleSentenceObject = simplifiedSentences.getJsonObject(simpleSentenceCounter);
                        final String label = simpleSentenceObject.getString(KEY_LABEL);
                        final String syntacticPattern = simpleSentenceObject.getString(KEY_SYNTACTIC_PATTERN);

                        generateNGramForSyntacticPattern(label, syntacticPattern, labelToPatternFrequencies);
                    }
                }
            }
            
            final SortedSet<VariantPattern> topKPatterns = getTopKVariantPatterns(labelToPatternFrequencies, 20, 10);
            System.out.println(topKPatterns);
            
            generateComparisonFiles(labelToPatternFrequencies);
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
    private void generateNGramForSyntacticPattern(final String label,
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
    
    private SortedSet<VariantPattern> getTopKVariantPatterns(final Map<String, Map<String, Integer>> featureMap, final int k, final int difference){
    	
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
    private void generateComparisonFiles(final Map<String, Map<String, Integer>> featureMap) {
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

    /**
     *  Get the sentence dependencies and relation tags for each sentence in the question.
     *  @param questionObject: the json object of the question.
     **/
    private QuestionInstance extractDependencyFeatures(final JsonObject questionObject){
    	final SortedSet<SentenceInstance> sentenceInstances = new TreeSet<>(getSentenceInstancesComparator());
        
        final int questionIndex = questionObject.getInt(KEY_PARENT_INDEX);
        final JsonArray sentences = questionObject.getJsonArray(KEY_SENTENCES);

        for (int sentenceCounter = 0; sentenceCounter < sentences.size(); sentenceCounter++) {
        	final int sentenceIndex = sentenceCounter + 1;
        	final Set<FeatureDependency> dependencies = new HashSet<>();
            final JsonObject sentenceObject = sentences.getJsonObject(sentenceCounter);
            final String sentence = sentenceObject.getString(KEY_SENTENCE);
            final StringReader textReader = new StringReader(sentence);
            final DocumentPreprocessor textProcessor = new DocumentPreprocessor(textReader);
            textProcessor.setTokenizerFactory(TREE_LANGUAGE_PACK.getTokenizerFactory());

            for (final List<HasWord> sentenceWordList : textProcessor) {
                final List<TaggedWord> taggedWords = MAXENT_TAGGER.tagSentence(sentenceWordList);
                final GrammaticalStructure grammaticalStructure = DEPENDENCY_PARSER.predict(taggedWords);
                final Collection<TypedDependency> sentenceDependencies = grammaticalStructure.typedDependenciesCCprocessed();

                for(final TypedDependency sentenceDependency : sentenceDependencies){
                    final String relationTag = sentenceDependency.reln().getShortName();
                    final String depTag = sentenceDependency.dep().tag() != null ?  sentenceDependency.dep().tag().toString() : "";
                    final String govTag = sentenceDependency.gov().tag() != null ?  sentenceDependency.gov().tag().toString() : "";
                    final FeatureDependency currentFeatureDependency = new FeatureDependency(relationTag, depTag, govTag); 
                    dependencies.add(currentFeatureDependency);
                }
            }            
            final SentenceInstance sentenceInstance = new SentenceInstance(questionIndex, sentenceIndex, sentence, dependencies);
            sentenceInstances.add(sentenceInstance);
        }
        final QuestionInstance questionInstance = new QuestionInstance(questionIndex, sentenceInstances);
        return questionInstance;
    }
    
    /**
     * 
     * @return
     */
    private Comparator<SentenceInstance> getSentenceInstancesComparator() {
    	return new Comparator<SentenceInstance> () {

			@Override
			public int compare(final SentenceInstance o1, final SentenceInstance o2) {
				return o1.getSentenceIndex() - o2.getSentenceIndex();
			}
    		
    	};
    }
    
    /**
     * 
     * @return
     */
    private Comparator<QuestionInstance> getQuestionInstancesComparator() {
    	return new Comparator<QuestionInstance> () {
			@Override
			public int compare(final QuestionInstance o1, final QuestionInstance o2) {
				return o1.getQuestionIndex() - o2.getQuestionIndex();
			}
    		
    	};
    }
}
