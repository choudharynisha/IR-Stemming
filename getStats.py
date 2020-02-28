import glob
import os
import string
import sys

corpusFilePath =  sys.argv[1]

#statistics


def main():
    doc = open(corpusFilePath, errors = "ignore")
    wordMap = dict()
    totalWords = 0
    totalUniqueWords = 0 
    for line in doc: 
        for word in line.split(): 
            totalWords += 1 
            if word not in wordMap.keys(): 
                wordMap[word] = 1 
            else: 
                wordMap[word] += 1 
    totalUniqueWords = len(wordMap.keys())
    print("Total unique words: " + str(totalUniqueWords))
    print("Total words: " + str(totalWords))
    
    
if __name__== "__main__":
  main()
