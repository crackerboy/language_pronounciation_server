#!/usr/bin/python3
import requests
import pdb

url = 'http://localhost:1337/'
files = {'audio_file':open('this1.wav','rb')}
data = {'word':'this'}
r = requests.post(url, files=files, data=data)
print(r.content)
