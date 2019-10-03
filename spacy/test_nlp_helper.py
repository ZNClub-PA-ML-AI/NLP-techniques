# -*- coding: utf-8 -*-
'''
Module to test helper methods of convoTransformer.py
'''

import nlp_helper as nlp



def test_matcher():
    
    #GIVEN
    name = 'Nevil'
    name = 'James Anderson'
    test_docs = [
        f'Hey Jonathan this is {name}',
        
        f'Hi I am {name} from Mumbai',
        
        
        f'Hey this is {name}',
        f'Hi I am {name} from Mumbai',
        f'Hi this is {name} joining from Mumbai'
    ]
    
    
    
    #WHEN
    filtered = nlp.filter_matched(test_docs, 'MeetingGreeting')
    names = nlp.filter_entities(filtered)
    
    #THEN
    print(filtered, names)
    

if __name__ == "__main__": 
    print('starting ' + __name__)
    
    test_matcher()