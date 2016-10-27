package com.neu.arithmeticproblemsolver.featureextractor;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(prefix = "m")
@Builder
public class FeatureDependency {

    private final String mRelation;
    private final String mDepWord;
    private final String mGovWord;
    private final String mDepTag;
    private final String mGovTag;
    private final int mDepIndex;
    private final int mGovIndex;

    public FeatureDependency (final String relation,
    						  final String depWord,
    						  final String govWord,
                              final String depTag,
                              final String govTag,
                              final int depIndex,
                              final int govIndex) {
        mRelation = relation;
        mDepWord = depWord;
        mGovWord = govWord;
        mDepTag = depTag;
        mGovTag = govTag;
        mDepIndex = depIndex;
        mGovIndex = govIndex;
    }

	@Override
	public String toString() {
		return "FeatureDependency [mRelation=" + mRelation + ", mDepWord=" + mDepWord + ", mGovWord=" + mGovWord
				+ ", mDepTag=" + mDepTag + ", mGovTag=" + mGovTag + ", mDepIndex=" + mDepIndex + ", mGovIndex="
				+ mGovIndex + "]";
	}

}
