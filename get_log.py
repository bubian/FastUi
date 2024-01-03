#!/usr/bin/python3
from email.policy import default
import os
import shutil
import sys
import time
import subprocess

need_compress = True
default_pkg_number = '2'
default_exclude_audio = True

pkg_list = ['cerence.ark.assistant.test', 'cerence.ark.assistant.sample',
            'com.cerence.cara.launcher',  'cerence.ark.assistant.common.app' , 'com.cerence.arkassistantsample']

def delete_file_dir(file_path):
    if os.path.exists(file_path):
        if os.path.isdir(file_path):
            shutil.rmtree(file_path)
        else:
            os.remove(file_path)

def pull_log(_log_path, _dst_dir, exclude_audio=False):
    print(f'pull_log exclude_audio = {exclude_audio}')
    if _log_path is None:
        return
    if (exclude_audio):
        os.makedirs(_dst_dir)
        find_cmd = f'adb shell find {_log_path} -type d -path "{_log_path}/log/audio" -prune -o -type f -print'
        result = subprocess.run(find_cmd, shell=True, capture_output=True, text=True)
        folder_paths = result.stdout.strip().split('\n')
        print(f'folder_paths {folder_paths}')
        for folder_path in folder_paths:
            print(f'folder_path = {folder_path} , _log_path = {_log_path}')
            if folder_path == _log_path:
                continue
            child_files = folder_path.replace(_log_path, "")
            child_files = os.path.normpath(child_files.strip()).lstrip("\\")
            target_folder = os.path.join(_dst_dir, child_files)
            print(f'target_folder = {target_folder}')
            if os.path.isdir(target_folder):
                if not os.path.exists(target_folder):
                    os.makedirs(target_folder)
                pull_command = f'adb pull "{folder_path}" "{target_folder}"'
            else:
                if not os.path.exists(os.path.dirname(target_folder)):
                    os.makedirs(os.path.dirname(target_folder))
                pull_command = f'adb pull "{folder_path}" "{os.path.dirname(target_folder)}"'
            pull_command = f'adb pull "{folder_path}" "{target_folder}"'
            subprocess.run(pull_command, shell=True)
        return
    cmd = f'adb pull {_log_path} {_dst_dir}'
    os.system(cmd)

if __name__ == '__main__':
    length = len(pkg_list)
    print('Please input the name of the log file, Click Enter to end:')
    file_name = input()

    print(f'Please select the package name of the log to be exported, enter its serial number(default value is {default_pkg_number}):')
    for i in range(0, length):
        print(f'{i}: {pkg_list[i]},                     /sdcard/Android/data/{pkg_list[i]}/files/file')

    packageNum = input()
    if packageNum == "":
        packageNum = default_pkg_number
    if not packageNum.isdigit():
        print('Invalid input!!!')
        sys.exit(0)

    packageNum = int(packageNum)
    if packageNum < 0 or packageNum > length:
        print(f'Invalid input!!!  It must be between 0 and {length}!!!')
        sys.exit(0)

    current_time = time.strftime("%Y-%m-%d_%H-%M-%S_", time.localtime())
    package_name = pkg_list[packageNum]
    print(f'You chose {packageNum}: {package_name}')
    if not default_exclude_audio: 
        default_value = "y" 
    else: 
        default_value = "n"
    print(f'Do you need to pull audio? (y/n), default value is {default_value} ')
    audio_input = input().strip().lower()
    if audio_input == 'y':
        exclude_audio = False
    elif audio_input == 'n':
        exclude_audio = True
    else:
        if audio_input == "":
            exclude_audio = default_exclude_audio
        else:
            print("Invalid input! Only 'y' or 'n' is allowed.")
            exit()
            
    script_path = os.path.dirname(__file__)
    if (file_name == ""):
        dst_dir1 = current_time.rstrip("_")
    else:
        dst_dir1 = current_time + file_name
    current_directory = os.getcwd()
    dst_dir = os.path.join(current_directory, dst_dir1)
    log_path = f'/sdcard/Android/data/{package_name}/files/file'
    pull_log(log_path, dst_dir, exclude_audio)

    if need_compress and os.path.exists(dst_dir):
        delete_file_dir(f'{dst_dir}.zip')
        shutil.make_archive(dst_dir, 'zip', dst_dir)
        delete_file_dir(f'{dst_dir}')
    
    print(f'Please find the log file in {dst_dir}')
