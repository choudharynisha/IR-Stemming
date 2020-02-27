import glob
import os
import string

#to get the current working directory name
docsFolder = os.getcwd() + "/docs"
tokenizedDocs = os.getcwd() + "/docInTokens"
corpusFilePath = "corpus.txt"

#set up for acceptable/unacceptable punctuation
acceptablePunctuations = {"'", "-"} 
punctuation = ''.join((set(string.punctuation)).difference(acceptablePunctuations))
translator = str.maketrans('', '', punctuation)
allPunctTranslator = str.maketrans('', '', string.punctuation)

#statistics
totalWords = 0

def main():
    i=0
    out = open(corpusFilePath, "w", errors = "ignore")
    for filename in os.listdir(tokenizedDocs):
        if filename.endswith(".txt"):
            filePath = os.path.join(tokenizedDocs, filename)
            createMaster(filePath,out)
        else:
            continue
        i = i + 1
def createMaster(file, outputFile):
    #clean headers + ending
    doc = open(file, errors='ignore')
    for line in doc:
        for word in line.split():
            outputFile.write(word + "\n")
if __name__== "__main__":
  main()
