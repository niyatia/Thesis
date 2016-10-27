package com.neu.arithmeticproblemsolver.featureextractor;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(prefix = "m")
public class FeatureDependency {

    private final String mRelation;
    private final String mDepTag;
    private final String mGovTag;

    public FeatureDependency (final String relation,
                              final String depTag,
                              final String govTag){

        mRelation = relation;
        mDepTag = depTag;
        mGovTag = govTag;
    }

    @Override
    public String toString() {
        return "Relation = " + mRelation + ", DepTag = " + mDepTag + ", GovTag = " + mGovTag +  " \n";
    }

}
