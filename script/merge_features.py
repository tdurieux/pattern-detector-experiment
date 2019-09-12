#!/usr/bin/env python

from __future__ import print_function
import os
import subprocess
import tempfile
import json
import collections
import sys
import codecs

from Config import config

root = config.get('path', 'root')

path_benchmark = os.path.realpath(sys.argv[1])
benchmark = sys.argv[2]
path_json_file = os.path.realpath(sys.argv[3])

output = []
count = 0
for file_name in sorted(os.listdir(path_json_file)):
    file_path = os.path.join(path_json_file, file_name)
    if not os.path.isfile(file_path):
        continue
    with open(file_path) as fd:
        data = json.load(fd)
        data['benchmark'] = benchmark
        project=file_name.split('_')[0]
        for category in data:
            if category == 'metrics':
                continue
            for key in sorted(data[category]):
                if type(data[category]) is dict:
                    if data[category][key] == 0:
                        del data[category][key]
        output.append(data)

with codecs.open(os.path.join("dissection.json"), "w",encoding='utf8') as fd:
    json.dump(output, fd)
