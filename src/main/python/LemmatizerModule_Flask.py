from flask import Flask
import nltk
import unicodedata

app = Flask(__name__)

wnl = None

@app.route("/initialize")
def initialize_lemmatizer():    
    app.wnl = nltk.WordNetLemmatizer()
    return "initialized"
        
@app.route('/lemmatize/<verb>')
def lemmatize(verb=None):
    unicode_lemma_verb = app.wnl.lemmatize(verb, pos='v')
    return unicodedata.normalize('NFKD', unicode_lemma_verb).encode('ascii', 'ignore')

if __name__ == "__main__":    
    app.run()

# class LemmatizerModule_Flask:
    
    
    
    
#     wnl = None
#     @app.route("/initialize")
#     @staticmethod
#     def initialize_lemmatizer():
#         LemmatizerModule_Flask.wnl = nltk.WordNetLemmatizer()
#         
#     @app.route('/lemmatize/<verb>')
#     @staticmethod
#     def lemmatize(verb=None):
#         unicode_lemma_verb =LemmatizerModule_Flask.wnl.lemmatize(verb, pos='v')
#         return unicodedata.normalize('NFKD', unicode_lemma_verb).encode('ascii', 'ignore')



# if __name__ == '__main__':
#         lemma = LemmatizerModule()
#         lemma.initialize_lemmatizer()
#         print lemma.lemmatize("found")