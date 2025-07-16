Create: curl --location 'http://localhost:9090/rules/create' \
--header 'Content-Type: application/json' \
--data '{
"agentRuleId": 101,
"agentType": "BOT",
"agentId": 55,
"deviceName": "Sensor-X",
"deviceId": 9001,
"deviceAddress": "192.168.0.101"
}'

Delete: curl --location --request DELETE 'http://localhost:9090/rules/delete/{id}'

Pacth: curl --location --request PATCH 'http://localhost:9090/rules/{id}/toggle-active'

GET: curl --location 'http://localhost:9090/rules/get/{id}'
