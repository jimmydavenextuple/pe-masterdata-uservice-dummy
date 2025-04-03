/*
Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.

The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
*/


DELETE FROM attribute_value where id > startID and id < endID;
DELETE FROM "node" WHERE ORG_ID = 'ORGID';
DELETE FROM "sourcing_constraint" WHERE ORG_ID = 'ORGID';
DELETE FROM "node_carrier_service_calendars" WHERE ORG_ID = 'ORGID';
DELETE FROM "weightage_configuration" WHERE ORG_ID = 'ORGID';
DELETE FROM "node_carrier" WHERE ORG_ID = 'ORGID';
DELETE FROM "group_definition" WHERE ORG_ID = 'ORGID';
DELETE FROM "sourcing_attribute" WHERE ORG_ID = 'ORGID';
DELETE FROM "calendars" WHERE ORG_ID = 'ORGID';
DELETE FROM "sourcing_rule_details" WHERE ORG_ID = 'ORGID';
DELETE FROM "node_carrier_selection" WHERE ORG_ID = 'ORGID';
DELETE FROM "node_calendars" WHERE ORG_ID = 'ORGID';
DELETE FROM "custom_region" WHERE ORG_ID = 'ORGID';
DELETE FROM "item" WHERE ORG_ID = 'ORGID';
DELETE FROM "postal_code" WHERE ORG_ID = 'ORGID';
DELETE FROM "promise_sourcing_rule" WHERE ORG_ID = 'ORGID';
DELETE FROM "named_optimization_strategy" WHERE ORG_ID = 'ORGID';
DELETE FROM "postal_code_timezone" WHERE ORG_ID = 'ORGID';
DELETE FROM "carrier_service_calendars" WHERE ORG_ID = 'ORGID';
DELETE FROM "transit_data" WHERE ORG_ID = 'ORGID';
DELETE FROM "sourcing_attributes_definition" WHERE ORG_ID = 'ORGID';
DELETE FROM "node_priority" WHERE ORG_ID = 'ORGID';
DELETE FROM "node_group" WHERE ORG_ID = 'ORGID';
DELETE FROM "carrier_service" WHERE ORG_ID = 'ORGID';
DELETE FROM "sourcing_rules_configuration" WHERE ORG_ID = 'ORGID';
DELETE FROM "transfer_schedules" WHERE ORG_ID = 'ORGID';