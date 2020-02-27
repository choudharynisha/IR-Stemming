# getSentences.py 
# Required folder structure: folder of documents 
# Running: python getSentences.py  <folder of documents> "<outputFile>" 
# Gets all of the textfiles of the given folder and creates a master corpus file where each line is a sentence
import glob
import os
import string
from nltk.tokenize import sent_tokenize
import sys
#get filepath of folder with documents

docsFolder = os.getcwd() + "/" + sys.argv[1]
corpusFilePath = sys.argv[2]
tokenizedDocs = os.getcwd() + "/docInTokens"
# corpusFilePath = "corpus.txt"

def main(): 
    masterString = "" 
    for filename in os.listdir(docsFolder):
        if filename.endswith(".txt"):
            filePath = os.path.join(docsFolder, filename)
            with open (filePath, 'r', errors='ignore') as myfile: 
                #make files in one long string without new line characters 
                masterString += myfile.read().replace('\n', ' ')
        else:
            continue
    #split this string into sentences using NLTK tokenize library 
    sentences = sent_tokenize(masterString)
 
    masterCorpus =  open(corpusFilePath, "w", errors = "ignore")
    #write each sentence into a file
    for sentence in sentences: 
        masterCorpus.write(sentence + "\n")
     

if __name__ == "__main__":
    main()

 