package com.neu.arithmeticproblemsolver.ngramranker;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors (prefix = "m")
public class RankedNGram {

	private final String mPattern;
	private final float mScore;
	
	public RankedNGram(final String pattern, final float score) {
		mPattern = pattern;
		mScore = score;
	}
}
