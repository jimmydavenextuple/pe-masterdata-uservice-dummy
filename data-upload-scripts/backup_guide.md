# Data migration guide
## Taking dump of data from the database
### Step 1: Update the db credentials and mention the db name in the script
```python
host = "host" # update the host
port = "port" # update the port
user = "username" # update the username
password = "password"   # update the password
tenant_id = 'tenant id for which you want to take dump' # update the tenant id


connection = psycopg2.connect(database="db_name", # Mention the db name
                    host=host,
                    user=user,
                    password=password,
                    port=port)
```

### Step 2: Run the script
1. Cd into the directory where the script is present and run the script using the below command
2. Create a backups directory in the same directory where the script is present
```sh
python3 data_dump.py
```
3. Multiple csv files will be created in the backups directory with the name of the tables


## Restoring the data to the database
### Step 1: Update the db credentials and mention the db name in the script
```python
engine = create_engine('postgresql://postgres:postgres@localhost:5432/pe') # update the db string with valid credentials
```

### Step 2: Run the script
1. Cd into the directory where the script is present and run the script using the below command
```shell
python3 pg-restore.py
```
2. The data will be restored to the database
