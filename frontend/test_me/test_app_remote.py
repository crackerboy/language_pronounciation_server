#!/usr/bin/python3
import requests
import pdb

base_url = 'http://174.138.55.47/app'
r = requests.get(base_url)
print(r.content)

url = 'http://174.138.55.47/app/api'
files = {'audio_file':open('this1.wav','rb')}
data = {'word':'this'}
r = requests.post(url, files=files, data=data)
print(r.content)
