#!/bin/bash

cd "$(dirname -- "$0")" || exit

if ! [ -x "$(command -v jq)" ]; then
    echo ""
    echo "jq is not found, please install and make it available on your path, https://stedolan.github.io/jq"
    echo ""
    exit
fi

OPERATION=$1
shift

if [[ $OPERATION != "create" ]] && [[ $OPERATION != "install" ]] && [[ $OPERATION != "validate" ]] && [[ $OPERATION != "update" ]]; then
  . ./select.sh
elif [[ "$1" == "" ]] || ! [[ -f $1 ]]; then
  echo ""
  echo "operation of create, validate, or update requires a valid configuration file."
  echo ""
  exit
else
  if ! jq . $1 > /dev/null 2>&1; then
      echo "not a well formed JSON configuration file."
      exit
  else
    //verify top level name and config
  fi
fi

CONNECT_URL="http://localhost:18083"
ACCEPT="Accept:application/json"
CONTENT_TYPE="Content-Type:application/json"

case "${OPERATION}" in
  get)
    curl -k -s -H "${ACCEPT}" -H "${CONTENT_TYPE}" -X GET "${CONNECT_URL}/connectors/${CONNECTOR}" | jq
    ;;
  create|install)
    curl -k -s -H "${ACCEPT}" -H "${CONTENT_TYPE}" -X POST --data "@$1" "${CONNECT_URL}/connectors" | jq
    ;;
  update)
    CONNECTOR="$(cat $1 | jq -r .name)"
    curl -k -s -H "${ACCEPT}" -H "${CONTENT_TYPE}" -X PUT --data "$(cat < $1 | jq .config)" "${CONNECT_URL}/connectors/${CONNECTOR}/config" | jq
    ;;
  pause)
    curl -k -s -H "${ACCEPT}" -H "${CONTENT_TYPE}" -X PUT "${CONNECT_URL}/connectors/${CONNECTOR}/pause"
    ;;
  resume)
    curl -k -s -H "${ACCEPT}" -H "${CONTENT_TYPE}" -X PUT "${CONNECT_URL}/connectors/${CONNECTOR}/resume"
    ;;
  delete)
    curl -k -s -H "${ACCEPT}" -H "${CONTENT_TYPE}" -X DELETE "${CONNECT_URL}/connectors/${CONNECTOR}" | jq
    ;;
  status)
    curl -k -s -H "${ACCEPT}" -H "${CONTENT_TYPE}" -X GET "${CONNECT_URL}/connectors/${CONNECTOR}/status" | jq
    ;;
  validate)
    CONNECTOR_CLASS=$(cat $1 |  jq -r '.config."connector.class"')
    CONNECTOR=${CONNECTOR_CLASS##*.}
    curl -k -s -H "${ACCEPT}" -H "${CONTENT_TYPE}" -X PUT ${CONNECT_URL}/connector-plugins/${CONNECTOR}/config/validate/ --data "$(cat < $1 | jq .config)" | jq
    ;;
  *)
    echo "Usage: $0 {get|create|update|pause|resume|delete|status|validate} (name|filename)"
esac

echo ""
