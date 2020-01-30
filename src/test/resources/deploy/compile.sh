#!/bin/sh
REPO=${OPERATOR_REPO:-https://github.com/kiegroup/kie-cloud-operator}
BRANCH=${OPERATOR_BRANCH:-master}

echo "Cloning: $REPO"
git clone $REPO

echo "Selected branch: $BRANCH"
cd kie-cloud-operator
git checkout $BRANCH
git pull

echo "Compile sources"
make