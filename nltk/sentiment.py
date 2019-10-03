# -*- coding: utf-8 -*-
"""
Created on Sat Feb  4 10:19:13 2017

@author: ZNevzz
"""

from nltk.sentiment.vader import SentimentIntensityAnalyzer
from nltk import tokenize

sentences = ["VADER is smart, handsome, and funny."]
sid = SentimentIntensityAnalyzer()
for sentence in sentences:
    print(sentence)
    ss = sid.polarity_scores(sentence)
    print(type(ss))
    for k in sorted(ss):
        print('{0}: {1}, '.format(k, ss[k]), end='')

