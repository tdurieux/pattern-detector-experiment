#!/usr/bin/env python
# usage 
# python merge_features.py <benchmark_path> <benchmark_name> <folder_json>
# python merge_features.py ../../benchmarks/ defects4j /tmp/out
#
# folder_json is the output of extract_feature.py


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
        with codecs.open(os.path.join(path_benchmark, project, file_name.replace("_all.json", ""), "path.diff"), "r", encoding='utf8', errors='ignore') as fd:
            data['diff'] = fd.read()
        output.append(data)

with codecs.open(os.path.join("%s/dissection.json" % benchmark), "w",encoding='utf8') as fd:
    json.dump(output, fd)
