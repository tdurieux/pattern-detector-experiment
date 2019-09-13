#!/usr/bin/env python
from __future__ import print_function
from multiprocessing import Pool, TimeoutError
import os
import subprocess
import tempfile
import json
import collections
import sys
import requests
import time
import signal
from Config import config

root = config.get('path', 'root')

benchmark = sys.argv[1]
path_jar = os.path.realpath(sys.argv[2])
output_path = os.path.realpath(sys.argv[3])

if not os.path.exists(output_path):
    os.makedirs(output_path)

def eprint(*args, **kwargs):
    """Error print"""
    print(*args, file=sys.stderr, **kwargs)

def start_detector_service():
    cmd = "java -cp %s add.main.Server" % (path_jar)
    # , stdout=subprocess.PIPE
    pro = subprocess.Popen(cmd, shell=True, preexec_fn=os.setsid)
    time.sleep(2)
    return pro

def stop_detector_service(pro):
    os.killpg(os.getpgid(pro.pid), signal.SIGTERM)
    time.sleep(5)

def get_project_features((bug_path, bug_id)):
    eprint("%s" % bug_id)
    path = os.path.join(bug_path, "buggy-version")
    path_diff = os.path.join(bug_path, "path.diff")
    data = {
        "bugId": bug_id,
        "buggySourceDirectory": path,
        "diffPath": path_diff
    }
    try:
        response = requests.post("http://localhost:9888", json=data, allow_redirects=False, timeout=30)
        with open(os.path.join(output_path, "%s_all.json" % bug_id), "w+") as fd:
            fd.write(response.content)
    except Exception:
        eprint("Timeout %s" % bug_id)
    

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
        tasks.append((bug_path, bug_id))

if __name__ == '__main__':
    pool = Pool(processes=3)
    serviceId = None
    try:
        serviceId = start_detector_service()
        pool.map(get_project_features, tasks)
        # pool.close()
        # pool.join()
    finally:
        stop_detector_service(serviceId)

    