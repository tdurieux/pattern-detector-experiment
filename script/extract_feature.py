#!/usr/bin/env python
from __future__ import print_function
from multiprocessing import Pool, TimeoutError
import os
import subprocess
import tempfile
import json
import collections
import sys
from Config import config

root = config.get('path', 'root')

benchmark = sys.argv[1]
path_patch_clustering = os.path.realpath(sys.argv[2])
output_path = os.path.realpath(sys.argv[3])

if not os.path.exists(output_path):
    os.makedirs(output_path)

def get_project_features((bug_path, bug_id)):
    print(bug_id)
    path = os.path.join(bug_path, "buggy-version")
    path_diff = os.path.join(bug_path, "path.diff")
    jar = os.path.join(path_patch_clustering, "target", "automatic-diff-dissection-1.1-SNAPSHOT-jar-with-dependencies.jar")
    cmd = """cd %s;timeout 30s java -jar %s --bugId %s --buggySourceDirectory %s --diff %s -m %s""" % \
          (path_patch_clustering,
           jar,
           bug_id,
           path,
           path_diff,
           "ALL")
    if output_path:
        cmd += """ -o %s""" % output_path
    try:
        return subprocess.check_output(cmd, shell=True).strip()
    except subprocess.CalledProcessError as e:
        "command '{}' return with error (code {}): {}".format(e.cmd, e.returncode, e.output)

tasks = []
path_benchmark = os.path.join(root, "benchmark", benchmark)
for project in sorted(os.listdir(path_benchmark)):
    project_path = os.path.join(path_benchmark, project)
    for bug_id in sorted(os.listdir(project_path)):
        bug_path = os.path.join(project_path, bug_id)
        if "_" not in bug_id:
            bug_id = project + "_" + bug_id
        if os.path.exists(os.path.join(output_path, "%s_all.json" % bug_id)):
            continue
        tasks.append([bug_path, bug_id])

if __name__ == '__main__':
    pool = Pool(processes=3)
    pool.map(get_project_features, tasks)

    
