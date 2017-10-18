#!/usr/bin/python3
import requests
import pdb

url = 'http://174.138.55.47/app'
files = {'audio_file':open('this1.wav','rb')}
data = {'word':'this'}
r = requests.post(url, files=files, data=data)
print(r.content)
