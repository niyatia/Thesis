from LogisticRegression import LogisticRegression

class MainClass:
    
    if __name__ == '__main__':
        training_features_file = "/Users/rajpav/git/ArithmeticProblemSolver/target/classes/dataset/TrainingFeatures.csv"
        test_features_file = "/Users/rajpav/git/ArithmeticProblemSolver/target/classes/dataset/TestFeatures.csv"
        print training_features_file
        logistic_regression = LogisticRegression(training_features_file, test_features_file)
        logistic_regression.train()
        logistic_regression.test()
        logistic_regression.accuracy()
        logistic_regression.predict()