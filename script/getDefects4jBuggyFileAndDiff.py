#!/usr/bin/env python
import os
import subprocess
import shutil
import re
import operator
from Config import config

root = config.get('path', 'root')
defects4j_path = config.get('path', 'defects4j')
defects4j_bin_path = os.path.join(defects4j_path, 'framework', 'bin')
defects4j_projects_path = os.path.join(defects4j_path, 'framework', 'projects')
defects4j_checkout_path = config.get('path', 'defects4j_buggy_projects')
defects4j_fix_checkout_path = config.get('path', 'defects4j_projects')


def str_intersection(s1, s2):
    out = ""
    for c in s1.split("/"):
        if c in s2 and c not in out:
            out += c + "/"
    return out


def parse_project_info(value):
    project_info = {}
    for line in value.splitlines():
        split = line.split(':', 1)
        if len(split) == 2:
            key = split[0].strip().replace(' ', '_').lower()
            value = split[1].strip()
            project_info[key] = value
    return project_info


def get_project_info(project):
    cmd = """export PATH="%s:$PATH"
defects4j info -p %s
""" % (defects4j_bin_path, project)
    output = subprocess.check_output(cmd, shell=True)
    return parse_project_info(output)


def get_project_source(project, bug_id):
    wd = os.path.join(defects4j_fix_checkout_path, project.lower(), "%s_%s" % (project.lower(), bug_id))

    cmd = """export PATH="%s:$PATH"
defects4j export -p dir.src.classes	-w %s 2> /dev/null
""" % (defects4j_bin_path, wd)

    output = subprocess.check_output(cmd, shell=True)
    return os.path.join(project.lower(), "%s_%s" % (project.lower(), bug_id), output)


def get_project_changed_classes(project, bug_id):
    wd = os.path.join(defects4j_fix_checkout_path, project.lower(), "%s_%s" % (project.lower(), bug_id))

    cmd = """export PATH="%s:$PATH"
defects4j export -p classes.modified -w %s 2> /dev/null
""" % (defects4j_bin_path, wd)

    output = subprocess.check_output(cmd, shell=True)
    return output.splitlines()


def get_diff(project, bug_id, source):
    cmd = 'git diff ' + os.path.join(defects4j_checkout_path, source) + ' ' + os.path.join(defects4j_fix_checkout_path, source);

    try:
        output = subprocess.check_output(cmd, shell=True)
    except Exception, e:
        output = str(e.output)
    output = output[output.index("---"):]
    project_path = os.path.join(project.lower(), "%s_%s" % (project.lower(), bug_id))
    return output.replace(os.path.join(defects4j_fix_checkout_path, project_path), "").replace(os.path.join(defects4j_checkout_path, project_path), "");

def main():
    commit_id = {}
    for project in os.listdir(defects4j_projects_path):
        if project == 'lib':
            continue
        if os.path.isfile(os.path.join(defects4j_projects_path, project)):
            continue
        info = get_project_info(project)

        for bug_id in xrange(1, int(info['number_of_bugs']) + 1):
            source = get_project_source(project, bug_id)
            changed_classes = get_project_changed_classes(project.lower(), bug_id)
            bug_output_path = os.path.join(root, 'benchmark', 'defects4j', project.lower() +"_" + str(bug_id))
            
            intersection_folder = None
            for changed_class in changed_classes:
                class_name = changed_class.replace(".", "/") + ".java"
                if intersection_folder is None:
                    intersection_folder = class_name
                else:
                    intersection_folder = str_intersection(intersection_folder, class_name)
                class_path = os.path.join(source, class_name)
                file_output_path = os.path.join(bug_output_path, "buggy-version", class_name)
                if not os.path.exists(os.path.dirname(file_output_path)):
                    os.makedirs(os.path.dirname(file_output_path))
                shutil.copy(os.path.join(defects4j_checkout_path, class_path), file_output_path)
            with open(os.path.join(bug_output_path, "path.diff"), 'w+') as fd:
                fd.write(get_diff(project, bug_id, source))
if __name__ == '__main__':
    main()
