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
    for changed_file in changed_files:
        file_path = os.path.join(repo, changed_file[1:])

        file_output_path = os.path.join(bug_output_path, "buggy-version", changed_file[1:])
        if not os.path.exists(os.path.dirname(file_output_path)):
            os.makedirs(os.path.dirname(file_output_path))
        shutil.copy(file_path, file_output_path)
        pass

def get_bug_branches(project):
    branches = []
    repo = os.path.join(bugsjar_repositories, project)
    cmd = "cd %s; git branch -a" % repo
    output = subprocess.check_output(cmd, shell=True)
    for line in output.split():
        if "bugs-dot-jar_" in line and "remotes/origin/" in line:
            line = line.replace(")", "").replace("remotes/origin/", "")
            info = extract_bug_id(line)
            info.insert(0, line)
            branches.append(info)
    return branches

def extract_bug_id(branch):
    tmp = branch[branch.index("_") + 1::]
    tmp = tmp[tmp.index("-") + 1::]
    return tmp.split("_")

def get_diff(project, branch):

    repo = os.path.join(bugsjar_repositories, project)
    cmd = "cd %s; git checkout %s; cat .bugs-dot-jar/developer-patch.diff" % (repo, branch)
    output = subprocess.check_output(cmd, shell=True)
    return output[output.index("---"):]


def main():
    for project in os.listdir(bugsjar_bugs):
        project_path = os.path.join(bugsjar_bugs, project)
        if os.path.isfile(project_path):
            continue

        branches = get_bug_branches(project)

        for (branch, jira_id, bug_id) in branches:
            patch = get_diff(project, branch)
            changed_files = get_changed_file(patch)
            bug_output_path = os.path.join(root, 'benchmark', 'bugsjar', project.lower(), bug_id)
            
            copy_buggy_files(project, bug_id, changed_files)
            with open(os.path.join(bug_output_path, "path.diff"), 'w+') as fd:
                fd.write(patch)
if __name__ == '__main__':
    main()
