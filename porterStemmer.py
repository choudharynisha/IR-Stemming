# import these modules 
import nltk
from nltk.stem import PorterStemmer 
from nltk.tokenize import word_tokenize 
import sys 
import string

allPunctTranslator = str.maketrans('', '', string.punctuation)

ps = PorterStemmer() 
corpusFilePath = sys.argv[1]
outputFilePath = sys.argv[2]
doc = open(corpusFilePath, "r") 
out = open(outputFilePath, "w", errors="ignore")
for line in doc:
    stemmedSentence = []
    sentence = ""
    for word in line.split():
        stemmedSentence.append(ps.stem(word))
    for word in stemmedSentence: 
        sentence = sentence + word + " " 
    out.write(sentence + "\n")
    
        
        
# choose some words to be stemmed 
words = ["program", "programs", "programer", "programing", "programers"] 

for w in words: 
    print(w, " : ", ps.stem(w)) 