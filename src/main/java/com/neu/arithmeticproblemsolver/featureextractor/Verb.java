package com.neu.arithmeticproblemsolver.featureextractor;

public enum Verb
    {
    	VB,     /** Verb, base form */
        VBD,    /** Verb, past tense */
        VBG,    /** Verb, gerund or present participle */
        VBN,    /** Verb, past participle */
        VBP,    /** Verb, non-3rd person singular present */
        VBZ,    /** Verb, 3rd person singular present */
        NONE;
        
        public static Verb valueOfNullable(final String str) {
        	Verb toReturn;
    		try {
    			toReturn = Verb.valueOf(str);
    		} catch (final Exception e) {
    			toReturn = Verb.NONE;
    		}
    		return toReturn;
    	}
    }