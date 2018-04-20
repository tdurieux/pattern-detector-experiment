#!/usr/bin/env python
import os
import subprocess
import shutil
import re
import operator
import json
from Config import config

root = config.get('path', 'root')
bugsjar_bugs = config.get('path', 'bugsjar_bugs')
bugsjar_repositories = config.get('path', 'bugsjar_repositories')

def get_changed_file(diff):
    return re.findall(r'\+\+\+ b(.+)', diff)

def copy_buggy_files(project, bug_id, changed_files):
    bug_output_path = os.path.join(root, 'benchmark', 'bugsjar', project.lower(), bug_id)

    repo = os.path.join(bugsjar_repositories, project)
    cmd = "cd %s; git checkout -- .; git checkout %s;" % (repo, bug_id)
    subprocess.call(cmd, shell=True)
    for changed_file in changed_files:
        file_path = os.path.join(repo, changed_file[1:])

        file_output_path = os.path.join(bug_output_path, "buggy-version", changed_file[1:])
        if not os.path.exists(os.path.dirname(file_output_path)):
            os.makedirs(os.path.dirname(file_output_path))
        shutil.copy(file_path, file_output_path)
        pass

def main():
    for project in os.listdir(bugsjar_bugs):
        project_path = os.path.join(bugsjar_bugs, project)
        if os.path.isfile(project_path):
            continue

        for bug_file in os.listdir(project_path):
            bug_id = bug_file.replace(".json", "")
            with open(os.path.join(project_path, bug_file)) as fd:
                data = json.load(fd)
                if len(data['patch']) == 0:
                    continue
                changed_files = get_changed_file(data['patch'])
                bug_output_path = os.path.join(root, 'benchmark', 'bugsjar', project.lower(), bug_id)
                
                copy_buggy_files(project, bug_id, changed_files)
                with open(os.path.join(bug_output_path, "path.diff"), 'w+') as fd:
                    fd.write(data['patch'].encode('utf-8'))
if __name__ == '__main__':
    main()
