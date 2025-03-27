import psycopg2
import pandas as pd
from sqlalchemy import create_engine
import os
import glob
import math

##Dump
host = "host"
port = "port"
user = "username"
password = "password"
tenant_id = 'tenant id for which you want to take dump'


connection = psycopg2.connect(database="db_name",
                    host=host,
                    user=user,
                    password=password,
                    port=port)

tables_not_required = ['node_config','task_info_impl','task_impl_archive','purge_config','flyway_schema_history','application_meta_data','config_metadata','cost_factor_audit_log','cost_attribute_details','attribute_value','attribute_name','task_impl','job_records', 'jobs','feed_records','feeds', 'transit_buffer_data', 'file_metadata', 'transit_buffer_req_jobs_reference', 'user_exit_metadata','transit_buffer_config_request','user_exit_config']

cursor = connection.cursor()
tables = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' ORDER BY table_name"

cursor.execute(tables)
tables = cursor.fetchall()

tables_list = [x[0] for x in tables]
tables_to_dump = [x for x in tables_list if x not in tables_not_required]

print(tables_to_dump)

for table in tables_to_dump:
    if table == 'attribute_value':
        query = "COPY (SELECT AV.* FROM " + table + " AS AV INNER JOIN sourcing_attribute AS SA ON AV.name_id = SA.id AND SA.org_id = "+tenant_id+")" + " TO STDOUT WITH CSV HEADER DELIMITER ';'"
    elif table == 'flexi_handlers':
        query = "COPY (SELECT * FROM " + table + " WHERE tenant_id = '"+tenant_id+"') TO STDOUT WITH CSV HEADER DELIMITER ';'"
    elif table == 'cost_attribute_details':
        query = "COPY (SELECT * FROM " + table + ") TO STDOUT WITH CSV HEADER DELIMITER ';'"
    else:
        query = "COPY (SELECT * FROM " + table + " WHERE org_id = '" + tenant_id + "') TO STDOUT WITH CSV HEADER DELIMITER ';'"
    with open("backups/"+table+".csv", "w") as file:
        cursor.copy_expert(query, file)
print('Dump Completed')