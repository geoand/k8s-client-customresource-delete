# Purpose

Demonstrate the deleting Custom resources via the Kubernetes Client does not work correctly for 
on Openshift 3.10 (but works file on Openshift 3.9)

# Run

## Prerequisites

Login into a cluster that has Istio (version 0.8 or higher) installed using the standard `oc login` command

## Run tests

`mvn test`

The above command will work correctly for the Openshift 3.9 cluster but will fail with an Openshift 3.10 cluster   