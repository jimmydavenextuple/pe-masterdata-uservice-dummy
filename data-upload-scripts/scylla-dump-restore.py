"""
Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.

The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
"""

from cassandra.cluster import Cluster
from cassandra.auth import PlainTextAuthProvider
from cassandracsv import CassandraCsv
import pandas as pd
import numpy as np


ap = PlainTextAuthProvider(username="user", password="password")

cluster = Cluster(['host'],auth_provider=ap)
session = cluster.connect('pe_inventory_sandbox')

result = session.execute("""select * from inventory_atp where org_id = 'NEXTUPLE_GR' ALLOW FILTERING;""")

CassandraCsv.export(
    result,
    filename="iv_dump.csv",
    output_dir="./backups/"
)

df = pd.read_csv('backups/iv_dump.csv')
df.replace("NEXTUPLE_GR",input("Prospect ID: "),inplace=True)
df.replace(np.nan,0,inplace=True)

prepared = session.prepare("""
        INSERT INTO inventory_atp (Org_Id, Item_Id, Uom, Node_Id, Bopus_Atp, Earliest_Future_Bopus_Date, Earliest_Future_Ship_Date, Future_Bopus_Atp, Future_Ship_Atp, Last_Bopus_Update_Time, Last_Ship_Update_Time, Ship_Atp, Updated_Time)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """)

# print(df)
count = 0
for ind in df.index:
    count += 1
    session.execute(prepared, [ df['Org_Id'][ind], str(df['Item_Id'][ind]), df['Uom'][ind], str(df['Node_Id'][ind]), df['Bopus_Atp'][ind], df['Earliest_Future_Bopus_Date'][ind], df['Earliest_Future_Ship_Date'][ind], df['Future_Bopus_Atp'][ind], df['Future_Ship_Atp'][ind], df['Last_Bopus_Update_Time'][ind], df['Last_Ship_Update_Time'][ind], df['Ship_Atp'][ind], df['Updated_Time'][ind] ])
print(count, " iv records inserted successfully")