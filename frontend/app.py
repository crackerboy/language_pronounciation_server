#!/usr/bin/python3
from flask import Flask, request, jsonify
from flask_restful import Resource, Api
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
import os,sys,inspect,json,datetime

currentdir = os.path.dirname(os.path.abspath(inspect.getfile(inspect.currentframe())))
parentdir = os.path.dirname(currentdir)
sys.path.insert(0,os.path.join(parentdir,'backend'))

from sql_classes import Base, Sample
from audio_comparison import pipeline

_MYSQL_URL_ = 'mysql://root:senior-design-2@localhost:3306/teamc'
_TMP_FOLDER_ = '/temp_audio'

app = Flask(__name__)
app.config['JSONIFY_PRETTYPRINT_REGULAR'] = False
api = Api(app)

engine = create_engine(_MYSQL_URL_)
Base.metadata.create_all(engine)
Base.metadata.bind = engine
DBSession = sessionmaker(bind=engine)
session = DBSession()

class CompareAudio(Resource):
    def post(self):
        # Extract info from request
        time_recieved = datetime.datetime.now()
        time_string = time_recieved.strftime("%b%d%Y_%I:%M:%S.%f%p")
        word, input_audio = (request.form['word'], request.files['audio_file'])

        # Save a temporary copy of the file
        audio_format = input_audio.filename.split('.')[-1]
        temp_file_name = "{0}.{1}".format(time_string,audio_format)
        temp_file_path = os.path.join(_TMP_FOLDER_,temp_file_name)
        input_audio.save(temp_file_path)

        file_paths = [x[0] for x in session.query(Sample.file_path).filter(Sample.word == word).all()]

        score = pipeline(temp_file_path, file_paths) # magic

        # Delete the temporary copy of the file
        os.remove(temp_file_path)

        obj = {'word':word, 'score':score}
        return jsonify(obj)

api.add_resource(CompareAudio, '/api')

# DEBUGGING INFO
#@app.before_request
#def before():
#    if [k for k in request.files]:
#        sys.stderr.write('Request info:\n')
#        sys.stderr.write('Request Headers:\n')
#        sys.stderr.write(str(request.headers))
#        sys.stderr.write('\n')
#        sys.stderr.write('Request Form Keys + Values:\n')
#        sys.stderr.write(str([(k,v) for (k,v) in request.form.items()]))
#        sys.stderr.write('\n')
#        sys.stderr.write('Request Files:\n')
#        keys = [k for k in request.files]
#        sys.stderr.write(str(keys))
#        sys.stderr.write('\n')
#        sys.stderr.write('Request File Info:\n')
#        sys.stderr.write(str(request.files[keys[0]].headers)+'\n'+str(request.files[keys[0]].mimetype))
#        sys.stderr.write('\n')
#        pass

@app.route('/')
def test_connection():
    return '<html><body><h1>You can connect to the Flask app...</h1></body></html>'

if __name__=='__main__':
    app.run(port=1337,debug=True)
