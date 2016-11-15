import nltk
import unicodedata

class LemmatizerModule:
    
    def initialize_lemmatizer(self):
        self.wnl = nltk.WordNetLemmatizer()
        
    def lemmatize(self, verb):
        unicode_lemma_verb =self. wnl.lemmatize(verb, pos='v')
        return unicodedata.normalize('NFKD', unicode_lemma_verb).encode('ascii', 'ignore')


# if __name__ == '__main__':
#         lemma = LemmatizerModule()
#         lemma.initialize_lemmatizer()
#         print lemma.lemmatize("found")