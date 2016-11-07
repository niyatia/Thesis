package com.neu.arithmeticproblemsolver.featureextractor;

import static com.neu.arithmeticproblemsolver.featureextractor.PublicKeys.ADDITION_LABEL;
import static com.neu.arithmeticproblemsolver.featureextractor.PublicKeys.EQUALS_LABEL;
import static com.neu.arithmeticproblemsolver.featureextractor.PublicKeys.IRRELEVANT_LABEL;
import static com.neu.arithmeticproblemsolver.featureextractor.PublicKeys.LABEL_STRINGS;
import static com.neu.arithmeticproblemsolver.featureextractor.PublicKeys.QUESTION_LABEL;
import static com.neu.arithmeticproblemsolver.featureextractor.PublicKeys.SUBTRACTION_LABEL;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class PublicKeys {
	
	/** Dataset Path*/
	public static final String DATASET_DIRECTORY = "dataset/";
    public static final String DATASET_DIRECTORY_ESCAPED_PATH = URLDecoder
            .decode(Thread.currentThread().getContextClassLoader().getResource(DATASET_DIRECTORY).getPath());

    public static final String ADD_SUB_FILE_PATH = DATASET_DIRECTORY_ESCAPED_PATH + "AdditionSubtraction.json";	
    public static final String TRAINING_DATA_FILE_PATH = DATASET_DIRECTORY_ESCAPED_PATH + "TrainingData.json";
    public static final String TEST_DATA_FILE_PATH = DATASET_DIRECTORY_ESCAPED_PATH + "TestData.json";
    public static final String TRAINING_FEATURES_FILE_PATH = DATASET_DIRECTORY_ESCAPED_PATH + "TrainingFeatures.csv";
    public static final String TEST_FEATURES_FILE_PATH = DATASET_DIRECTORY_ESCAPED_PATH + "TestFeatures.csv";
    public static final String VERB_FREQUENCY_FILE_PATH = DATASET_DIRECTORY_ESCAPED_PATH + "VerbFrequency.csv";
    
    /** JSON Keys */
	public static final String KEY_PARENT_INDEX = "ParentIndex";
    public static final String KEY_S_QUESTION = "sQuestion";
    public static final String KEY_SENTENCE = "Sentence";
    public static final String KEY_LABEL = "label";
    public static final String KEY_SENTENCES = "Sentences";
    public static final String KEY_SYNTACTIC_PATTERN = "SyntacticPattern";
    public static final String KEY_SIMPLIFIED_SENTENCES = "SimplifiedSentences";

    /** Classification Labels. */
    public final static String ADDITION_LABEL = "+";
    public final static String SUBTRACTION_LABEL = "-";
    public final static String QUESTION_LABEL = "?";
    public final static String EQUALS_LABEL = "=";
    public final static String IRRELEVANT_LABEL = "i";

    /** String corresponding to labels. */
    public final static Map<String, String> LABEL_STRINGS = new HashMap<>();
    
    static {
	    LABEL_STRINGS.put(ADDITION_LABEL, "Addition");
		LABEL_STRINGS.put(SUBTRACTION_LABEL, "Subtraction");
		LABEL_STRINGS.put(QUESTION_LABEL, "Question");
		LABEL_STRINGS.put(IRRELEVANT_LABEL, "Irrelevant");
		LABEL_STRINGS.put(EQUALS_LABEL, "Equals");
    }
}
