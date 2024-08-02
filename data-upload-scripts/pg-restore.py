import pandas as pd
from sqlalchemy import create_engine
import os
import glob
import math

host = "host"
port = "5432"
user = "postgres"
password = "postgres"
db_name = "postgres"

## Restore
def is_nan(variable):
    return isinstance(variable, (int, float)) and math.isnan(variable) 

engine = create_engine(f'postgresql://{user}:{password}@{host}:{port}/{db_name}')
prospectIndex = int(input("Prospect DB Index: "))
prospectName = input("Prospect Name: ")
tenantId = input("Tenant Id : ")

count = 0
path = os.getcwd()
csv_files = glob.glob(os.path.join(path+"/backups", "*.csv"))
for file in csv_files:
    table = file.split("/")[-1].split('.')[0]
    print(file)
    df = pd.read_csv(file, delimiter=';')
    df.replace(tenantId,prospectName, inplace=True)

    if 'id' in df and df.empty is False:
        df['id'] = df['id'] if isinstance(df['id'][0],str) else  df['id'] + prospectIndex
        # df.drop(['id'], axis=1,inplace=True)
        if table == 'sourcing_attributes_definition':
            req_attributes = []
            opt_attributes = [] 
            for j in df['req_attributes']:
                if(type(j) == float):
                    req_attributes.append([int(float(i)) + prospectIndex for i in str(j).split(",")])
                else:
                    req_attributes.append([int(i) + prospectIndex for i in str(j).split(",")])
            for j in df['opt_attributes']:
                if (type(j) == float):
                    opt_attributes.append([] if is_nan(j) else [int(float(i)) + prospectIndex for i in str(j).split(",")])
                else:
                    opt_attributes.append([] if is_nan(j) else [int(i) + prospectIndex for i in str(j).split(",")])
            
            req_attributes_1 = []
            opt_attributes_1 = []
            for j in req_attributes:
                req_attributes_1.append(','.join(str(num) for num in j))
            for j in opt_attributes:
                opt_attributes_1.append(None if ','.join(str(num) for num in j) == '' else ','.join(str(num) for num in j))    
            df['req_attributes'] = req_attributes_1
            df['opt_attributes'] =  opt_attributes_1
        
        elif table == 'group_definition' or table == 'sourcing_rules_configuration' :
            df['sourcing_attributes_definition_id'] = df['sourcing_attributes_definition_id'] + prospectIndex

        elif table == 'node_priority':
            df['node_group_id'] = df['node_group_id'] + prospectIndex
        
        elif table == 'sourcing_rule_details':
            df['node_groups'] = df['node_groups'] + prospectIndex
            df['sourcing_rule_id'] = df['sourcing_rule_id'] + prospectIndex
        
        elif table == 'sourcing_constraints' or table == 'named_optimization_strategy' :
            if(df['group_id'] == "DEFAULT").any(): 
                continue
            else:
                df['group_id'] = str(df['group_id'].astype(int) + prospectIndex)

        elif table == 'attribute_value':
            df['name_id'] = df['name_id'] + prospectIndex

        elif table == 'rules_configuration':
            df['attribute_definition_id'] + prospectIndex    

    elif table == 'holiday_cutoff':
        df['sourcing_attributes_definition_id'] = df['sourcing_attributes_definition_id'] + prospectIndex        

    df.to_sql(table, engine, if_exists='append', index=False )
    count += 1
print("Insertion performed on ",count," tables")
