#!/usr/bin/python3
from flask import Flask
from flask_restful import Resource, Api, reqparse
from sql_classes import Base, Sample
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
import werkzeug
import pdb

_MYSQL_URL_ = 'mysql://root:senior-design-2@localhost:3306/teamc'

app = Flask(__name__)
api = Api(app)
parser = reqparse.RequestParser()
parser.add_argument('word', required=True, help="Need a word to compare against...",\
                    type=str, location='data')
parser.add_argument('audio_file', required=True, help="Need audio input...",\
                    type=werkzeug.datastructures.FileStorage, location='files')

engine = create_engine(_MYSQL_URL_)
Base.metadata.create_all(engine)
Base.metadata.bind = engine
DBSession = sessionmaker(bind=engine)
session = DBSession()

class CompareAudio(Resource):
    def post(self):
        # Load samples and input sample into memory
        pdb.set_trace()
        args = parser.parse_args()
        file_paths = session.query(Sample.file_path).filter(Sample.word == args['word']).all()
        files = {file_path: open(file_path,'r') for file_path in file_paths} #be sure to close files

        # Magic
        score = 0
        for path, file_obj in files.items():
            file_obj.close()
        return "{word:{},score:{}}".format(args['word'],score)

api.add_resource(CompareAudio, '/')
if __name__=='__main__':
    app.run(port=1337,debug=True)
