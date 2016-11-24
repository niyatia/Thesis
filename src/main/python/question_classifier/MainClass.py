from Feature_Extractor import Feature_Extractor
from RandomForest_Questions import RandomForest_Questions
from EnsembleMethods_Questions import EnsembleMethods_Questions
from SVM_Questions import SVM_Questions
class MainClass:

    if __name__ == '__main__':
        # feature_extractor = Feature_Extractor()
        # feature_extractor.read_file()
        training_features_file_path = r"C:\Users\Niyati Jhaveri\Desktop\Vishal\ArithmeticProblemSolver\src\main\resources\dataset\QuestionTrainingFeatures.csv"
        test_features_file_path = r"C:\Users\Niyati Jhaveri\Desktop\Vishal\ArithmeticProblemSolver\src\main\resources\dataset\QuestionTestFeatures.csv"

        # random_forest = RandomForest_Questions(training_features_file_path, test_features_file_path)
        # random_forest.train()
        # random_forest.test()
        # random_forest.predict()

        # ensemble = EnsembleMethods_Questions(training_features_file_path, test_features_file_path)
        # ensemble.train()
        # ensemble.prepare_test_data()
        # ensemble.predict()

        svm = SVM_Questions(training_features_file_path, test_features_file_path)
        svm.train()
        svm.test()
        svm.predict()