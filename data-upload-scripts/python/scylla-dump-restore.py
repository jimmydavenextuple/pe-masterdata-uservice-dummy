from cassandra.cluster import Cluster
from cassandra.auth import PlainTextAuthProvider
from cassandracsv import CassandraCsv
import pandas as pd
import numpy as np

user = "user"
password = "password"
host = "host"
port = "port"
db_name = "pe_inventory_sandbox"

ap = PlainTextAuthProvider(username=user, password=password)

cluster = Cluster([host], port=9042, auth_provider=ap)
session = cluster.connect(db_name)

tenant = input("TenantId of the tenant to take dump of: ")
result = session.execute("""select * from inventory_atp where org_id = '{tenant}' ALLOW FILTERING;""")

CassandraCsv.export(
    result,
    filename="iv_dump.csv",
    output_dir="./backups/"
)

df = pd.read_csv('backups/iv_dump.csv')
df.replace(tenant,input("Prospect ID: "),inplace=True)
df.replace(np.nan,0,inplace=True)

prepared = session.prepare("""
        INSERT INTO inventory_atp (Org_Id, Item_Id, Uom, Node_Id, Bopus_Atp, Earliest_Future_Bopus_Date, Earliest_Future_Ship_Date, Future_Bopus_Atp, Future_Ship_Atp, Last_Bopus_Update_Time, Last_Ship_Update_Time, Ship_Atp, Updated_Time)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """)

# print(df)
count = 0
for ind in df.index:
    count += 1
    print("earliest bopus date : " + str(df['Earliest_Future_Ship_Date'][ind]))
    session.execute(prepared, [ df['Org_Id'][ind], str(df['Item_Id'][ind]), df['Uom'][ind], str(df['Node_Id'][ind]), df['Bopus_Atp'][ind], int(df['Earliest_Future_Bopus_Date'][ind]), df['Earliest_Future_Ship_Date'][ind], df['Future_Bopus_Atp'][ind], df['Future_Ship_Atp'][ind], df['Last_Bopus_Update_Time'][ind], df['Last_Ship_Update_Time'][ind], df['Ship_Atp'][ind], df['Updated_Time'][ind] ])
print(count, " iv records inserted successfully")

