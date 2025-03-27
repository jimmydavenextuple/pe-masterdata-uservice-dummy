"""
Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.

The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
"""

import pandas as pd
from sqlalchemy import create_engine
import os
import glob
import math

## Restore
def is_nan(value):
    return isinstance(value, float) and math.isnan(value)

engine = create_engine('postgresql://postgres:postgres@localhost:5432/pe')
prospectIndex = int(input("Prospect DB Index: "))
prospectName = input("Tenant Id for which data is being migrated : ")
tenant_id = input("Tenant ID from which data is being migrated : ")

count = 0
path = os.getcwd()
csv_files = glob.glob(os.path.join(path+"/backups", "*.csv"))
for file in csv_files:
    table = file.split("/")[-1].split('.')[0]
    print(file)
    df = pd.read_csv(file, delimiter=';')
    df.replace(tenant_id,prospectName, inplace=True)

    if 'id' in df and df.empty is False:
        df['id'] = df['id'] if isinstance(df['id'][0],str) else  df['id'] + prospectIndex
        # df.drop(['id'], axis=1,inplace=True)
        if table == 'sourcing_attributes_definition':
            req_attributes = []
            opt_attributes = []

            # Process req_attributes
            for j in df['req_attributes']:
                req_attributes.append([int(float(i)) + prospectIndex for i in str(j).split(",")])

            # Process opt_attributes safely
            for j in df['opt_attributes']:
                opt_attributes.append([] if is_nan(j) else [int(float(i)) + prospectIndex for i in str(j).split(",")])

            req_attributes_1 = []
            opt_attributes_1 = []

            for j in req_attributes:
                req_attributes_1.append(','.join(str(num) for num in j))

            for j in opt_attributes:
                opt_attributes_1.append(None if ','.join(str(num) for num in j) == '' else ','.join(str(num) for num in j))

            df['req_attributes'] = req_attributes_1
            df['opt_attributes'] = opt_attributes_1
        
        elif table == 'group_definition' or table == 'sourcing_rules_configuration' :
            df['sourcing_attributes_definition_id'] = df['sourcing_attributes_definition_id'] + prospectIndex

        elif table == 'node_priority':
            df['node_group_id'] = df['node_group_id'] + prospectIndex
        
        elif table == 'sourcing_rule_details':
            df['node_groups'] = df['node_groups'] + prospectIndex
            df['sourcing_rule_id'] = df['sourcing_rule_id'] + prospectIndex
        
        elif table == 'sourcing_constraint' or table == 'named_optimization_strategy' :
            df['group_id'] = pd.to_numeric(df['group_id'], errors='coerce').fillna(0).astype(int)
            df['group_id'] = (df['group_id'] + prospectIndex).astype(str)

        elif table == 'attribute_value':
            df['name_id'] = df['name_id'] + prospectIndex

    df.to_sql(table, engine, if_exists='append', index=False )
    count += 1
print("Insertion performed on ",count," tables")
