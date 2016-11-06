package com.neu.arithmeticproblemsolver.featureextractor;

import java.util.List;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(prefix = "m")
public class QuestionInstance {

	private final int mQuestionIndex;
	private final List<SentenceInstance> mSentenceInstances;
	
	public QuestionInstance(final int questionIndex, final List<SentenceInstance> sentenceInstances) {
		mQuestionIndex = questionIndex;
		mSentenceInstances = sentenceInstances;
	}

	@Override
	public String toString() {
		return "QuestionInstance [mQuestionIndex=" + mQuestionIndex + ", mSentenceInstances=" + mSentenceInstances
				+ "]";
	}
	
	
}
