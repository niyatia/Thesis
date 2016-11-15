from LogisticRegression_Operators import LogisticRegression_Operators
from LogisticRegression_Verbs import LogisticRegression_Verbs
from RandomForest_Operators import RandomForest_Operators
from EnsembleMethods_Operators import EnsembleMethods_Operators
from Adaboost_Operators import Adaboost_Operators

class MainClass:
    
    if __name__ == '__main__':
        training_features_file = "/Users/rajpav/git/ArithmeticProblemSolver/target/classes/dataset/TrainingFeatures.csv"
        test_features_file = "/Users/rajpav/git/ArithmeticProblemSolver/target/classes/dataset/TestFeatures.csv"
        print training_features_file
        
        adaboost_operators = Adaboost_Operators(training_features_file, test_features_file)
        adaboost_operators.train()
        adaboost_operators.prepare_test_data()
        adaboost_operators.predict()

#         ensemble_methods = EnsembleMethods_Operators(training_features_file, test_features_file)
#         ensemble_methods.prepare_test_data();
#         ensemble_methods.train();

#         random_forests = RandomForest_Operators(training_features_file, test_features_file)
#         random_forests.train()
#         random_forests.test()
#         random_forests.predict()
        
#          training_features_file = "/Users/rajpav/git/ArithmeticProblemSolver/target/classes/dataset/TrainingFeatures.csv"
#          test_features_file = "/Users/rajpav/git/ArithmeticProblemSolver/target/classes/dataset/TestFeatures.csv"
#          print training_features_file
#          logistic_regression = LogisticRegression_Operators(training_features_file, test_features_file)
#          logistic_regression.train()
#          logistic_regression.test()
#          logistic_regression.accuracy()
#          logistic_regression.predict()

#        training_features_file = "/Users/rajpav/git/ArithmeticProblemSolver/target/classes/dataset/VerbFeaturesTraining.csv"
#        test_features_file = "/Users/rajpav/git/ArithmeticProblemSolver/target/classes/dataset/VerbFeaturesTest.csv"
#        print training_features_file
#        logistic_regression = LogisticRegression_Verbs(training_features_file, test_features_file)
#        logistic_regression.train()
#        logistic_regression.test()
#        logistic_regression.accuracy()
#        logistic_regression.predict()
