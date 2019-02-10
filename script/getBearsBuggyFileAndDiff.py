#!/usr/bin/env python
import os
import subprocess
import shutil
import re
import operator
import json

from Config import config

root = config.get('path', 'root')
bears_repo = config.get('path', 'bears_repo')


def get_changed_file(diff):
    return re.findall(r'\+\+\+ b(.+)', diff)


def copy_buggy_files(project, bug_id, changed_files):
    cmd = "cd %s; git log --format=format:%%H --grep='End';" % bears_repo
    PATCHED_COMMIT = subprocess.check_output(cmd, shell=True)

    cmd = "cd %s; git log --format=format:%%H --grep='Changes in the tests';" % bears_repo
    BUGGY_COMMIT = subprocess.check_output(cmd, shell=True)
    if len(BUGGY_COMMIT) == 0:
        cmd = "cd %s; git log --format=format:%%H --grep='Bug commit';" % bears_repo
        BUGGY_COMMIT = subprocess.check_output(cmd, shell=True)
    
    cmd = "cd %s; git checkout %s;" % (bears_repo, BUGGY_COMMIT)
    subprocess.call(cmd, shell=True)

    bug_output_path = os.path.join(root, 'benchmark', 'bears', project, bug_id)

    new_files = []
    for changed_file in changed_files:
        file_path = os.path.join(bears_repo, changed_file[1:])

        file_output_path = os.path.join(bug_output_path, "buggy-version", changed_file[1:])
        if not os.path.exists(os.path.dirname(file_output_path)):
            os.makedirs(os.path.dirname(file_output_path))
	try:        
	    shutil.copy(file_path, file_output_path)
	except:
	    new_files.append(changed_file)
        pass

    cmd = "cd %s; git checkout %s;" % (bears_repo, PATCHED_COMMIT)
    subprocess.call(cmd, shell=True)
    for new_file in new_files:
	file_path = os.path.join(bears_repo, new_file[1:])
        file_output_path = os.path.join(bug_output_path, "buggy-version", new_file[1:])
	shutil.copy(file_path, file_output_path)


def get_bug_branches():
    branches = []
    cmd = "cd %s; git branch -a" % bears_repo
    output = subprocess.check_output(cmd, shell=True)
    for line in output.split():
        if "HEAD" not in line and "master" not in line and "pr-add-bug" not in line and "remotes/origin/" in line:
            line = line.replace(")", "").replace("remotes/origin/", "")
            branches.append(line)
    return branches


def get_diff():
    cmd = "cd %s; git rev-parse HEAD~2;" % bears_repo
    buggy_commit = subprocess.check_output(cmd, shell=True).replace("\n", "")

    cmd = "cd %s; git rev-parse HEAD~1;" % bears_repo
    fixed_commit = subprocess.check_output(cmd, shell=True).replace("\n", "")

    cmd = "cd %s; git diff %s %s -- '*.java';" % (bears_repo, buggy_commit, fixed_commit)
    human_patch = subprocess.check_output(cmd, shell=True)
    return human_patch[human_patch.index("---"):]

def checkout_branch(branch):
    cmd = "cd %s; git reset .; git checkout -- .; git clean -f; git checkout %s;" % (bears_repo, branch)
    subprocess.call(cmd, shell=True)

def main():
    branches = get_bug_branches()
    print "Found %s branches" % len(branches)

    for branch in branches:
	print "Branch %s" % branch

        checkout_branch(branch)

        with open(os.path.join(bears_repo, 'bears.json')) as original_json_file:
            bug = json.load(original_json_file)

        project = bug['repository']['name'].replace("/","-").lower()
        bug_id = bug['bugId'].replace("-","_").lower()
        patch = get_diff()

        changed_files = get_changed_file(patch)

        copy_buggy_files(project, bug_id, changed_files)

	bug_output_path = os.path.join(root, 'benchmark', 'bears', project, bug_id)
        with open(os.path.join(bug_output_path, "path.diff"), 'w+') as fd:
            fd.write(patch)


if __name__ == '__main__':
    main()

