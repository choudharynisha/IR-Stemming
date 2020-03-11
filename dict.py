#!/usr/bin/env python3

# Names: Sophia Trump, Eunsoo Jang, Maria Vivanco, Emily Lobel
# File: makeCleanDictionary.py
# Description: Takes 3 dictionary files and cleans them, saving the mega combined cleaned dictionary
# into a file called "cleanedDictionary.txt". 
# Run in the cmd with python3 makeCleanDictionary.py <path to dictionary file>

import sys
import re

def makeCleanDictionary():
    # create a list of words from the dictionary, which is delimited by newline
    listUnixDictionaryWords = open("unix-words.txt").read().split('\n')
    listScrabbleWords = open("masterdict.txt").read().split('\n')
    print("Checking", len(listUnixDictionaryWords), "words from dictionary 1...")
    print("Checking", len(listScrabbleWords), "words from dictionary 2...")
    # create common dictionary without repeats
    dictionaryWordsNoRepeats = list(set(listUnixDictionaryWords) | set(listScrabbleWords))
    f = open("cleanedDictionary.txt", "w+")

    for word in dictionaryWordsNoRepeats:
        f.write(word + '\n')
    # close the files
    f.close()

    
def main():
    makeCleanDictionary()

if __name__ == "__main__":
    main()
