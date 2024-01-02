#!/bin/bash

function heading() {
  tput setaf 2; printf "\n\n$@"; tput sgr 0
  #pause
  tput setaf 2; printf "\n\n"; tput sgr 0
}

function subheading() {
  tput setaf 3; printf "$@\n"; tput sgr 0
}

function error_msg() {
  tput setaf 1; printf "\n$@\n\n"; tput sgr 0
}

if ! [ -x "$(command -v jq)" ]; then
    echo ""
    echo "jq is not found, please install and make it available on your path, https://stedolan.github.io/jq"
    echo ""
    exit
fi

CURL="curl -s -H \"Content-Type: application/json\" http://localhost:18083/connectors"

CONNECTORS=($(${CURL} | jq -r '.[]'))

display_menu() {
    heading "connectors:"
    for ((i=1; i<=${#CONNECTORS[@]}; i++)); do
        subheading "  $i. ${CONNECTORS[$i-1]}"
    done
    echo ""
}

if [ $# -eq 0 ]; then
  display_menu
  tput setaf 3; printf "Enter the number of your choice: "; tput sgr 0
  read -p "" choice
  printf "\n"
  if [[ $choice -ge 1 && $choice -le ${#CONNECTORS[@]} ]]; then
    CONNECTOR=${CONNECTORS[$choice-1]}
  else
    echo "invalid selection"
    exit
  fi
else
  CONNECTOR=$1
  shift
fi

