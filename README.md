# HTTP Bolt

## Usage

### Input

```
["url" "opts"]
```

The input tuple consists of the URL to be requested, and an options
map to provide queruy parameters, headers, and any other option
clj-http allows. Certain options will be ignored, like the `:url`,
`:socket-timeout`, `:conn-timeout`, etc. The HTTP Bolt defaults to
performaing a GET request. If you wish to do something else, you may
provide `:method` in the options map.

### Output

```
["state" "response"]
```

The output tuple has two fields. The first field represents the state
of the HTTP request. If a response returned, the value of will be
`"response"` and the second field will contain a response map. The
other two possible states are `"socket_timeout"` and
`"conn_timeout"`. The second field is nil for both of these states.

## Configuration

* `HTTP_BOLT_SOCKET_TIMEOUT`: In milliseconds, defaults to 10000.
* `HTTP_BOLT_CONN_TIMEOUT`: In milliseconds, defaults to 10000.