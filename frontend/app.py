#!/usr/bin/python3
from flask import Flask, request
from flask_restful import Resource, Api
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
import os,sys,inspect,json

currentdir = os.path.dirname(os.path.abspath(inspect.getfile(inspect.currentframe())))
parentdir = os.path.dirname(currentdir)
sys.path.insert(0,os.path.join(parentdir,'backend'))

from sql_classes import Base, Sample

_MYSQL_URL_ = 'mysql://root:senior-design-2@localhost:3306/teamc'

app = Flask(__name__)
api = Api(app)

engine = create_engine(_MYSQL_URL_)
Base.metadata.create_all(engine)
Base.metadata.bind = engine
DBSession = sessionmaker(bind=engine)
session = DBSession()

class CompareAudio(Resource):
    def post(self):
        # Load samples and input sample into memory
        word, input_audio = (request.form['word'], request.files['audio_file'])
        file_paths = [x[0] for x in session.query(Sample.file_path).filter(Sample.word == word).all()]

        # Magic
        score = 0

        obj = {'word':word, 'score':score}
        return json.dumps(obj)

api.add_resource(CompareAudio, '/')
if __name__=='__main__':
    app.run(port=1337,debug=True)
