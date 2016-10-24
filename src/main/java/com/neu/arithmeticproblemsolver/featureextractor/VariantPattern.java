package com.neu.arithmeticproblemsolver.featureextractor;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m") 

public class VariantPattern {

	@Getter final private Map<String, Integer> mLabelCount;
	@Getter final private String mPattern;
	@Getter private int mSumOfCount = 0;
	
	private int minCount = Integer.MAX_VALUE;
	private int maxCount = Integer.MIN_VALUE;
	
	
	public VariantPattern(final String pattern) {
		mPattern = pattern;
		mLabelCount = new HashMap<>();
	}
	
	/**
	 * Add the pattern count for a label. Should call only once for a label and pattern combination.
	 * @param label: the label
	 * @param count: count of the pattern for that label.
	 */
	public void addCount(final String label, final int count){
		minCount = Math.min(minCount, count);
		maxCount = Math.max(maxCount, count);
		mSumOfCount += count;
		mLabelCount.put(label, count);
	}
	
	/*
	 * @param t: the required difference between min and max.
	 * @return: if the difference between max and min is greater than t.
	 */
	public boolean isDifferenceGreaterThanT(final int t) {
		return maxCount - minCount > t;
	}

	@Override
	public String toString() {
		return "VariantPattern [mLabelCount=" + mLabelCount + ", mPattern=" + mPattern + "]\n";
	}
	
	
}
