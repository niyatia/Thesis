import csv
import numpy as np
from sklearn.ensemble import RandomForestClassifier
from sklearn.ensemble import AdaBoostClassifier
from sklearn.metrics import confusion_matrix
from numpy import dtype
from imblearn.over_sampling import RandomOverSampler
from sklearn import linear_model

class Adaboost_Operators:
    
    def __init__(self, training_features, testing_features):
        self.m_training_features = training_features
        self.m_testing_features = testing_features
        
        color_map = {}
        color_map['+'] = 1
        color_map['-'] = 2
        color_map['?'] = 3
        color_map['='] = 4
        color_map['i'] = 5
        
        self.m_color_map = color_map
        self.m_label_key = "Label"
        self.m_clf = RandomForestClassifier(n_estimators = 10,
                                            criterion = "entropy",
                                            max_features = 5,
                                            class_weight = {
                                                1:1,
                                                2:1,
                                                3:1,
                                                4:1,
                                                5:1
                                            },
                                            random_state=1000)
        self.m_adaboost = AdaBoostClassifier(
                            linear_model.LogisticRegression(C=1e5),
                            n_estimators=500,
                            learning_rate=1.5,
                            algorithm="SAMME")
    
    def train(self):
        n_training_samples = 3787
        n_training_features = 38
        training_data = np.empty((n_training_samples, n_training_features))
        training_labels = np.empty((n_training_samples, 1), dtype=np.int)
        
        with open(self.m_training_features) as training_csv_file:
            training_features_file = csv.DictReader(training_csv_file)
            for index, ir in enumerate(training_features_file):
                training_labels[index] = np.asarray(self.m_color_map.get(ir.pop(self.m_label_key)), dtype=np.int)
                training_data[index] = np.asarray(ir.values(), dtype=np.float64)
                
        cc = RandomOverSampler()
        training_data_resampled, training_labels_resampled = cc.fit_sample(training_data, training_labels)
        self.m_adaboost.fit(training_data_resampled, training_labels_resampled)
        
    def prepare_test_data(self):
        n_test_samples = 749
        self.n_test_samples = n_test_samples
        n_test_features = 38
        test_data = np.empty((n_test_samples, n_test_features))
        test_labels = np.empty((n_test_samples, 1), dtype=np.int)
        
        with open(self.m_testing_features) as test_csv_file:
            test_features_file = csv.DictReader(test_csv_file)
            for index, ir in enumerate(test_features_file):
                test_labels[index] = np.asarray(self.m_color_map.get(ir.pop(self.m_label_key)), dtype=np.int)
                test_data[index] = np.asarray(ir.values(), dtype=np.float64)
    
        self.m_test_labels = test_labels
        self.m_test_data = test_data
        
    def predict(self):
        predict_result = self.m_adaboost.predict(self.m_test_data)
        testcounter = 0
        correct = 0
        for test_label in self.m_test_labels:
            if test_label == predict_result[testcounter]:
                correct = correct + 1
            testcounter = testcounter + 1
        
        print "Predict result"
        print correct
        print float(correct)/self.n_test_samples
        
        print "Confusion Matrix"
        print confusion_matrix(self.m_test_labels, predict_result)
