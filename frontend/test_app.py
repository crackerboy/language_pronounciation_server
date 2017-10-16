#!/usr/bin/python3
import requests

url = 'localhost:1337/'
headers = {'Content-type':'multipart/form-data'}
files = {'audio_file':open('us1.mp3','rb')}
data = {'word':'us'}
r = requests.post(url, files=files, data=data, headers=headers)
print(r.content)
