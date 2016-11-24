package com.neu.arithmeticproblemsolver.datasetoperations;

import com.neu.arithmeticproblemsolver.featureextractor.SentenceInstance;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.*;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.neu.arithmeticproblemsolver.featureextractor.PublicKeys.*;
import static com.neu.arithmeticproblemsolver.featureextractor.PublicKeys.KEY_SENTENCE;

/**
 * Created by Niyati Jhaveri on 11/18/2016.
 */
public class QuestionDataExtractor {

    public static void main(String args[]) {

        extractQuestionTrainingData(TRAINING_DATA_FILE_PATH, "QuestionTrainingData.csv");
        extractQuestionTrainingData(TEST_DATA_FILE_PATH, "QuestionTestData.csv");
    }

    public static void extractQuestionTrainingData(String sourceFilePath, String destinationFilePath){
        try {
            final InputStream inputFileStream = new FileInputStream(sourceFilePath);
            final JsonReader jsonReader = Json.createReader(inputFileStream);
            final JsonArray fileArray = jsonReader.readArray();

            final File questionDataFilePath = new File(destinationFilePath);
            final FileWriter fileWriter = new FileWriter(questionDataFilePath);
            final int noOfQuestions = fileArray.size();
            for (int questionCounter = 0; questionCounter < noOfQuestions; questionCounter++) {
                final JsonObject questionObject = fileArray.getJsonObject(questionCounter);
                final JsonArray sentences = questionObject.getJsonArray(KEY_SENTENCES);
                final int noOfSentences = sentences.size();
                for (int sentenceCounter = 0; sentenceCounter < noOfSentences; sentenceCounter++) {
                    final JsonObject sentenceObject = sentences.getJsonObject(sentenceCounter);
                    final JsonArray simplifiedSentences = sentenceObject.getJsonArray(KEY_SIMPLIFIED_SENTENCES);
                    final int noOfSimplifiedSentences = simplifiedSentences.size();
                    for (int simpleSentenceCounter = 0; simpleSentenceCounter < noOfSimplifiedSentences; simpleSentenceCounter++) {
                        final JsonObject simpleSentenceObject = simplifiedSentences.getJsonObject(simpleSentenceCounter);
                        final String simpleSentence = simpleSentenceObject.getString((KEY_SENTENCE));
                        final String label = simpleSentenceObject.getString(KEY_LABEL);
                        if(label.equals("?")) {
                            fileWriter.write(simpleSentence);
                            fileWriter.write("\n");
                        }
                    }
                }
            }

            fileWriter.flush();
            fileWriter.close();

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

}
