#!/usr/bin/python3
'''
Author: Josue Caraballo
Description: Assumes words are in '/data/', populates MySQL table of availible words with schema:
    |sample_no|word|file_location|
    sample_no - Int - Compound Primary Key
    word - varchar(255) - Compound Primary Key
    file_location - varchar(9999) - Not Null

Assumes folder structure:
    '/data/<word #>-<word>/<sample 1-10>'
'''
import os
import re
from sql_classes import Sample, Base
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

_MYSQL_URL_ = 'mysql://root:senior-design-2@localhost:3306/teamc'
_DATA_DIR_ = '/data'
p = re.compile('^word.*#[0-9]+.*- *([A-Za-z]+)$')
p_ = re.compile('^[a-zA-Z]*([0-9]+)[a-zA-Z]*\.[a-zA-Z0-9]{3}$')

def get_word_locations():
    '''
    Function to enumerate all the folders that are in the /data/ location
    '''
    return [x for x in os.listdir(_DATA_DIR_)
            if os.path.isdir(os.path.join(_DATA_DIR_,x))]

def enumerate_samples(folders):
    '''
    Function to return json object that contains sample info. Structure below:
        {
            <word>: {
                <sample_no>:<file_location>
            }
        }
    '''
    samples = {}
    for word_folder in folders:
        word_path = os.path.join(_DATA_DIR_, word_folder)
        word = p.search(word_folder).group(1)
        word_samples = os.listdir(word_path)
        samples[word] = {}
        for sample in word_samples:
            if '_DS_Store' in sample:
                continue
            word_sample_no = int(p_.search(sample).group(1))
            samples[word][word_sample_no] = os.path.join(word_path, sample)
    return samples

def insert_into_db(samples):
    '''
    Function to insert data into MySQL as in description, assumes JSON object format shown in 'enumerate_samples(folders)'
#TODO
    '''
    engine = create_engine(_MYSQL_URL_)
    Base.metadata.create_all(engine)
    Base.metadata.bind = engine
    DBSession = sessionmaker(bind=engine)
    session = DBSession()

    for word, sample_pairs in samples.items():
        for sample_no, sample_path in sample_pairs.items():
            new_sample = Sample(\
                                sample_no=sample_no,\
                                word=word,\
                                file_path=sample_path)
            session.add(new_sample)

    session.commit()

if __name__=="__main__":
    folders = get_word_locations()
    samples = enumerate_samples(folders)
    insert_into_db(samples)
