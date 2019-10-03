# -*- coding: utf-8 -*-
'''
Module responsible for NLP tasks
'''
import spacy


'''
nlp object is the statistcal model trained on english web dataset in spacy
'''
nlp = spacy.load('en_core_web_lg')

'''
matchers object holds all RuleMatcher objects created
'''
matchers = {}

def process(documents: list):
    return [nlp(each) for each in documents]

def create_matchers():
    '''
    Register patterns in matcher 
    '''
    pattern1 = [
        {"POS": "INTJ", "OP": "+"},
        {"POS": "PROPN", "OP": "?"},
        {"POS": "DET", "OP": "?"},
        {"POS": "PRON", "OP": "?"},
        {"POS": "VERB", "OP": "+"},
        {"POS": "PROPN", "OP": "+"},
        {"POS": "PROPN", "OP": "?"},
    ]
    
    matchers['MeetingGreeting'] = spacy.matcher.Matcher(nlp.vocab)
    matchers['MeetingGreeting'].add("MeetingGreeting1", None, pattern1)

## IIFE
create_matchers()

def filter_matched(documents: list, matcher_id: str):
    '''
    Return documents matched in matcher
    '''
    matcher = matchers[matcher_id]
    
    processed_documents = process(documents)
    
    list_of_matches = [matcher(each) for each in processed_documents]
    matched = []
    
    for sentence_no, matches in enumerate(list_of_matches):
        for id, start, end in matches:
            matched.append(documents[sentence_no])
    return matched
    
def filter_entities(documents: list, label = 'PERSON'):
    
    processed_documents = process(documents)
    entities = []
    
    for doc in processed_documents:
        persons = [token for token in doc.ents if token.label_ == label]
        entities.append( (persons, doc))
        
    return entities