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

benchmark = sys.argv[1]
output_path = os.path.realpath(sys.argv[2])

output = []
count = 0
path_benchmark = os.path.join(output_path, benchmark)
for file_name in sorted(os.listdir(path_benchmark)):
    file_path = os.path.join(path_benchmark, file_name)
    if not os.path.isfile(file_path):
        continue
    with open(file_path) as fd:
        data = json.load(fd)
        data['benchmark'] = benchmark
        with codecs.open(os.path.join(root, "benchmark", benchmark, file_name.replace("_all.json", "").replace("_", "/"), "path.diff"), "r", encoding='utf8', errors='ignore') as fd:
            data['diff'] = fd.read()
        output.append(data)

with codecs.open(os.path.join(output_path, "%s.json" % benchmark), "w",encoding='utf8') as fd:
    json.dump(output, fd)
