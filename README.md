# Log structured key-value database with hash indexes

## REST API ##

### Add or update key-value ###
```bash
curl -H "Content-Type: application/json" -X POST http://localhost:8080/db --data '{ "key":"TEST_KEY", "value":"TEST_VALUE" }'
```
Result:
```json
{  
   "key":"TEST_KEY",
   "value":"TEST_VALUE"
}
```

### Get value by key ###
```bash
curl http://localhost:8080/db/TEST_KEY
```
Result:
```json
{  
   "key":"TEST_KEY",
   "value":"TEST_VALUE"
}
```

### Delete key/value ###
```bash
curl -X DELETE http://localhost:8080/db/TEST_KEY
```
Result:
```json
{}
```