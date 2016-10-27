package com.neu.arithmeticproblemsolver.ngramranker;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors (prefix = "m")
public class RankedNGram {

	private final String mPattern;
	private final int mScore;
	
	public RankedNGram(final String pattern, final int score) {
		mPattern = pattern;
		mScore = score;
	}
}
