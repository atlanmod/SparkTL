"""
To use this current module:
    python3 -m pip install GitPython
"""

import git
import os

def git_update(sparktl_path: str, branch: str = "master"):
    repo = git.Repo(sparktl_path)
    past_branch = repo.create_head(branch, 'HEAD')
    repo.head.reference = past_branch
    repo.remotes.origin.pull()

def build_experiments_jar(sparktl_path):
    os.system("gradle -b " + sparktl_path + "experiments/build.gradle jar")

def move_jar(sparktl_path: str, directory: str, jar_name: str):
    actual_jar = jar_name if jar_name[-4:] == ".jar" else jar_name + ".jar" 
    path_to_jar = sparktl_path + directory + "/build/libs/" + actual_jar
    os.system("cp " + path_to_jar + " ~/jars")

def full_update(sparktl_path: str = "~/Project/SparkTL/", branch: str = "master", jar_name: str = "SparkTE-1.1-SNAPSHOT.jar"):
    git_update(sparktl_path, branch)
    build_experiments_jar(sparktl_path)
    move_jar(sparktl_path, "experiments", jar_name)

def get_git_commit(sparktl_path: str = "~/Project/SparkTL/", branch: str = "master"):
    repo = git.Repo(sparktl_path)
    past_branch = repo.create_head(branch, 'HEAD')
    repo.head.reference = past_branch
    return repo.head.object.hexsha