from flask import Flask
from flask_restful import Resource, Api, reqparse

app = Flask(__name__)
api = Api(app)
parser = reqparse.RequestParser()
parser.add_argument("word")
parser.add_argument("audio_file")

class CompareAudio(Resource):
    def post(self):
        args = parser.parse_args()
        #todo
