import psycopg2
import pandas as pd
from sqlalchemy import create_engine

##Dump

host = "host"
port = "5432"
user = "pe_user"
password = "password"
db_name = "pe_sandbox"

tenantId = input("TenantId of the tenant to take dump of: ")


connection = psycopg2.connect(database=db_name,
                    host=host,
                    user=user,
                    password=password,
                    port=port)

tables_not_required = ['attribute_name','job_records', 'jobs', 'transit_buffer_data', 'file_metadata'
                       , 'transit_buffer_req_jobs_reference', 'user_exit_metadata','transit_buffer_config_request'
                       ,'user_exit_config','config_metadata','cost_attribute_details','flyway_schema_history'
                       ,'purge_config','task_impl','task_impl_archive','task_info_impl']

cursor = connection.cursor()
tables = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' ORDER BY table_name"

cursor.execute(tables)
tables = cursor.fetchall()

tables_list = [x[0] for x in tables]
tables_to_dump = [x for x in tables_list if x not in tables_not_required]

print(tables_to_dump)



for table in tables_to_dump:
    if table == 'attribute_value':
        query = "COPY (SELECT AV.* FROM " + table + " AS AV INNER JOIN sourcing_attribute AS SA ON AV.name_id = SA.id AND SA.org_id = '"+tenantId+"')" + " TO STDOUT WITH CSV HEADER DELIMITER ';'"
    else:
        query = "COPY (SELECT * FROM " + table + " WHERE org_id = '"+tenantId+"') TO STDOUT WITH CSV HEADER DELIMITER ';'"
    with open("backups/"+table+".csv", "w") as file:
        cursor.copy_expert(query, file)
