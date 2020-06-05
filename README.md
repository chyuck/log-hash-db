# Log structured key-value database with hash indexes 

Key/value database with the following properties:
- Keeps data in files. Files are updated by only appending new values to the end of file.
- Has hash indexes with (key -> file value position) for each file.
- Files has a size threshold, when limit exceeded, it creates a new file.
- Runs background process to optimize files, Because files can contain a lot of garbage for updated values.
- Rebuilds hash indexes from files on database start.

## Running ##
Start database by running the following command:
```bash
./mvnw spring-boot:run
```

## Testing ##

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
{  
   "key":"TEST_KEY",
   "value":null
}
```

## Use cases ##

### Get value by key ###
- Perform GET API request with key
- Iterate through hash indexes (key, position) associated to files, find key and retrieve file position
- Read file associated to hash index at position and get value

### Update key/value ###
- Perform POST API request with key/value
- Append key/value to the end of current writable file
- Save file position to hash index (key, position)

### Delete key/value ###
- Perform POST API request with key
- Iterate through hash indexes (key, position) associated to files and check hash index for key existence
- Append key/value with tombstone value to the end of file associated to hash index

### Database file exceeds max size on update ###
- Perform POST API request with key
- Append key/value to the end of current writable file
- Check writable file size - file size exceeds max size
- Create new writable file with hash index
- Append new writable file to collection of read hash indexes
- Set new writable file as current writable file

### Database restarts and rebuilding indexes ###
- Database starts
- Finds all files
- Builds hash indexes from all files and creates collection of read hash indexes
- Identifies writable file and make it current writable file

### Database optimizes files and merges files ###
- Periodic background thread started
- Collects keys/values for all readonly files
- Creates new files, populates them with data and builds hash indexes
- Adds new hash indexes to collection of read hash indexes
- Removes old hash indexes from collection of read hash indexes
- Deletes old files