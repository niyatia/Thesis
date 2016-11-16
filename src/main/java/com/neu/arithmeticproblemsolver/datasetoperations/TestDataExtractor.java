package com.neu.arithmeticproblemsolver.datasetoperations;

import static com.neu.arithmeticproblemsolver.featureextractor.PublicKeys.ADD_SUB_FILE_PATH;
import static com.neu.arithmeticproblemsolver.featureextractor.PublicKeys.TRAINING_DATA_FILE_PATH;
import static com.neu.arithmeticproblemsolver.featureextractor.PublicKeys.TEST_DATA_FILE_PATH;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;

public class TestDataExtractor {
 
	public static void extractTestData() {
		try {
			final InputStream inputFileStream = new FileInputStream(ADD_SUB_FILE_PATH);
	        final JsonReader jsonReader = Json.createReader(inputFileStream);
	        final JsonArray fileArray = jsonReader.readArray();
	        
	        final int noOfQuestions = fileArray.size();
	        final int noOfTestInstancesToExtract = noOfQuestions/6;
	        System.out.println(noOfTestInstancesToExtract + ":No of test instances.");

			final SortedSet<Integer> testIndices = new TreeSet<>();
			for (int randomCounter = 1; randomCounter <= noOfTestInstancesToExtract; randomCounter++) {
				final int randomNumber = (int) Math.ceil(Math.random() * noOfQuestions);
				final int randomIndex = randomNumber - 1;
				if (testIndices.contains(randomIndex)) {
	        		randomCounter--;
	        		continue;
	        	}
				testIndices.add(randomIndex);
			}

			final JsonArrayBuilder testDataArrayBuilder = Json.createArrayBuilder();
			for (final int index : testIndices) {
				final JsonObject question = fileArray.getJsonObject(index);
				testDataArrayBuilder.add(question);
			}

	        final File testDataFile = new File(TEST_DATA_FILE_PATH);
	        final FileOutputStream testDataFileWriter = new FileOutputStream(testDataFile);
	        final JsonWriter testDataJsonWriter = Json.createWriter(testDataFileWriter);
	        testDataJsonWriter.write(testDataArrayBuilder.build());
	        testDataJsonWriter.close();
	        System.out.println(testIndices.size());
	        
	        
	        final JsonArrayBuilder trainingDataArrayBuilder = Json.createArrayBuilder();
	        for (int trainingCounter = 0; trainingCounter < fileArray.size(); trainingCounter++) {
	        	if (!testIndices.contains(trainingCounter)) {
	        		final JsonObject currentQuestion = fileArray.getJsonObject(trainingCounter);
	        		trainingDataArrayBuilder.add(currentQuestion);
	        	}	        	
	        }
	        final File trainingDataFile = new File(TRAINING_DATA_FILE_PATH);
	        final FileOutputStream trainingDataFileWriter = new FileOutputStream(trainingDataFile);
	        final JsonWriter trainingDataJsonWriter = Json.createWriter(trainingDataFileWriter);
	        trainingDataJsonWriter.write(trainingDataArrayBuilder.build());
	        trainingDataJsonWriter.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(final String[] args){
		extractTestData();
	}
}
