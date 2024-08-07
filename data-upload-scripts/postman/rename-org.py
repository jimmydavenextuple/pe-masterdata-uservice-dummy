import os
import shutil
import csv

def copy_directory(src, dest):
    """Copy a directory from src to dest."""
    if os.path.exists(dest):
        shutil.rmtree(dest)
    shutil.copytree(src, dest)
    print(f"Copied directory from {src} to {dest}")

def replace_string_in_csv(file_path, old_string, new_string):
    """Replace a string in a CSV file."""
    temp_file = file_path + '.tmp'
    with open(file_path, 'r', newline='') as infile, open(temp_file, 'w', newline='') as outfile:
        reader = csv.reader(infile)
        writer = csv.writer(outfile)
        for row in reader:
            new_row = [item.replace(old_string, new_string) for item in row]
            writer.writerow(new_row)
    os.replace(temp_file, file_path)
    print(f"Replaced '{old_string}' with '{new_string}' in {file_path}")

def process_directory(directory, old_string, new_string):
    """Process each CSV file in the directory, including nested directories."""
    for root, _, files in os.walk(directory):  # os.walk handles recursion internally
        for file in files:
            if file.endswith('.csv'):
                file_path = os.path.join(root, file)
                replace_string_in_csv(file_path, old_string, new_string)

if __name__ == "__main__":  
    string_to_replace = 'CTPPT'
    new_string = str(input("Organisation Id of new tenant: "))
    src_directory = './Sandbox-Setup-Files-CTPPT/DEMO-FILES'
    dest_directory = './Sandbox-Setup-Files-'+new_string+'/DEMO-FILES'

    # Copy the directory
    copy_directory(src_directory, dest_directory)

    # Process the copied directory
    process_directory(dest_directory, string_to_replace, new_string)
