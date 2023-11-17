## call ./publish_sample.sh <topic> <file>
## client id is test_client
## port is 1884
## host is assumed to be localhost

mosquitto_pub -i test_client -t "$1"/test_client -p 1884 -m "$(cat "$2")"