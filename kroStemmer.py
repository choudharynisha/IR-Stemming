# import these modules 
import sys 
import string
import krovetz
ks = krovetz.PyKrovetzStemmer()

corpusFilePath = sys.argv[1]
outputFilePath = sys.argv[2]
doc = open(corpusFilePath, "r", encoding="UTF-8") 
out = open(outputFilePath, "w", encoding="UTF-8")
for line in doc:
    stemmedSentence = []
    sentence = ""
    for word in line.split():
        word.replace("-", "")
        stemmedSentence.append(ks.stem(word))
    for word in stemmedSentence: 
        sentence = sentence + word + " "
    out.write(sentence + "\n")