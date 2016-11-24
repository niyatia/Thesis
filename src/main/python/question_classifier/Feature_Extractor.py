import csv
from collections import OrderedDict
from spacy.en import English
parser = English()

SINGULAR_PRONOUNS = ["he", "she", "it", "him", "her", "you", "i"]
PLURAL_PRONOUNS = ["they", "them"]
class Feature_Extractor:
    def __init__(self):
        self.m_features = []

        self.m_training_data_file_path = r"C:\Users\Niyati Jhaveri\Desktop\Vishal\ArithmeticProblemSolver\src\main\resources\dataset\QuestionTrainingData.csv"
        self.m_test_data_file_path = r"C:\Users\Niyati Jhaveri\Desktop\Vishal\ArithmeticProblemSolver\src\main\resources\dataset\QuestionTestData.csv"

        self.m_training_features_file_path = r"C:\Users\Niyati Jhaveri\Desktop\Vishal\ArithmeticProblemSolver\src\main\resources\dataset\QuestionTrainingFeatures.csv"
        self.m_test_features_file_path = r"C:\Users\Niyati Jhaveri\Desktop\Vishal\ArithmeticProblemSolver\src\main\resources\dataset\QuestionTestFeatures.csv"

    def read_file(self):
        # example = u"He placed 5 apples on the desk."
        # parsedEx = parser(example)
        # # shown as: original token, dependency tag, head word, left dependents, right dependents
        # for token in parsedEx:
        #     print(token.orth_, token.dep_, token.head.orth_, [t.orth_ for t in token.lefts], [t.orth_ for t in token.rights])


        with open(self.m_training_data_file_path) as training_csv_file:
            training_data_file = csv.DictReader(training_csv_file)
            for index, ir in enumerate(training_data_file):
                question = unicode(ir["Question"])
                self.get_features_for_sentence(question)

        self.print_feature_vector()

    def get_features_for_sentence(self, question):
        parsed_ex = parser(question)
        feature_dict = OrderedDict()
        feature_dict["label"] = 0
        for pronoun in SINGULAR_PRONOUNS:
            feature_dict[pronoun] = 0
        for pronoun in PLURAL_PRONOUNS:
            feature_dict[pronoun] = 0

        for token in parsed_ex:
            word = token.orth_
            if word in SINGULAR_PRONOUNS:
                feature_dict[word] = 1
            elif word in PLURAL_PRONOUNS:
                feature_dict[word] = 1
                feature_dict["label"] = 1

        self.m_features.append(feature_dict.values())

    def print_feature_vector(self):
        with open(self.m_training_features_file_path, 'wb') as feature_csv_file:
            writer = csv.writer(feature_csv_file)

            for features in self.m_features:
                writer.writerow(features)


