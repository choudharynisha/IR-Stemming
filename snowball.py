# import these modules 
import nltk
from nltk.stem.snowball import SnowballStemmer
from nltk.tokenize import word_tokenize 
import sys 
import string

stemmer = SnowballStemmer("english")
corpusFilePath = sys.argv[1]
outputFilePath = sys.argv[2]
doc = open(corpusFilePath, "r") 
out = open(outputFilePath, "w", errors="ignore")
for line in doc:
    stemmedSentence = []
    sentence = ""
    for word in line.split():
        stemmedSentence.append(stemmer.stem(word))
    for word in stemmedSentence: 
        sentence = sentence + word + " " 
    out.write(sentence + "\n")
    
        
        
# choose some words to be stemmed 
words = ["program", "programs", "programer", "programing", "programers"] 

for w in words: 
    print(w, " : ", stemmer.stem(w)) 