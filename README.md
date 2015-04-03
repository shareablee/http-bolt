# HTTP Bolt

[![Build Status](https://magnum.travis-ci.com/shareablee/http-bolt.svg?token=K6s6pdvGP253fz9WbBKT)](https://magnum.travis-ci.com/shareablee/http-bolt)

## Usage

Include the following in `project.clj`:

```
[http-bolt "0.1.4"]
```

### Input

```
["meta" "url" "opts"]
```

The input tuple consists an meta map with arbitrary values, the URL to
be requested, and an options map to provide queruy parameters,
headers, and any other option clj-http allows. Certain options will be
ignored, like the `:url`, `:socket-timeout`, `:conn-timeout`, etc.

The HTTP Bolt defaults to performaing a GET request. If you wish to do
something else, you may provide `:method` in the `opts` map.

```clojure
[{} "http://example.com" {:query-params {"foo" "bar"}}]
```

In order to do something other than a `GET`, supply `:method` option.

```clojure
[{} "http://example.com" {:method :post}]
```

### Output

```
["meta" "state" "response"]
```

The output tuple has three fields. The first field is the meta map,
which may or may not be changed, but is taken from the input
tuple. The second field represents the state of the HTTP request. If a
response returned, the value of will be `"response"` and the second
field will contain a response map. The other two possible states are
`"socket_timeout"` and `"conn_timeout"`. The second field is nil for
both of these states.

```clojure
[{} "response" {:status 418}]
[{} "socket_timeout" nil]
[{} "socket_error" nil]
```

## Configuration

* `HTTP_BOLT_SOCKET_TIMEOUT`: In milliseconds, defaults to 10000.
* `HTTP_BOLT_CONN_TIMEOUT`: In milliseconds, defaults to 10000.

## Running tests

```
lein test
```
