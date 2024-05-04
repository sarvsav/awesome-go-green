# kube-green

- [kube-green](#kube-green)
  - [Overview](#overview)
  - [How it works?](#how-it-works)
  - [Demo on local machine](#demo-on-local-machine)
    - [Installation and creation of cluster using kind](#installation-and-creation-of-cluster-using-kind)
    - [Installing cert manager](#installing-cert-manager)
      - [Set up cert manager resources](#set-up-cert-manager-resources)
    - [Set up kube-green](#set-up-kube-green)
  - [Verification](#verification)
  - [Uninstallation of cert manager](#uninstallation-of-cert-manager)
  - [Thank you](#thank-you)

[**kube green**](https://kube-green.dev/) - An operator to reduce CO2 footprint of your clusters

## Overview

Shutdown your pods during non working hours to optimize resources.

## How it works?

It is custom resource definition a.k.a. CRD of k8s, that monitors the pods. It will bring up and down the pods based on the spec details.

An example confiuration. Source: [kube-green](https://kube-green.dev/docs/lifecycle/) website.

```yaml
apiVersion: kube-green.com/v1alpha1
kind: SleepInfo
metadata:
  name: working-hours
spec:
  weekdays: "1-5"
  sleepAt: "20:00"
  wakeUpAt: "08:00"
  timeZone: "Europe/Rome"
  suspendCronJobs: true
  excludeRef:
    - apiVersion: "apps/v1"
      kind:       Deployment
      name:       my-deployment
```

## Demo on local machine

### Installation and creation of cluster using kind

- Start by installing and creating the [kind](https://kind.sigs.k8s.io/) cluster on your local machine. The below command will create a cluster named `learngo` on your machine.

```bash
go install sigs.k8s.io/kind@v0.22.0 && kind create cluster --name learngo
```

- `kind` automatically switches to context and can be verified by running below command.

```bash
$ kubectl config get-contexts | grep -i ^\* | awk '{print $1, $2}'
* kind-learngo
```

**NOTE:** After system reboot, we cann't bring the cluster back. Hence, we need to delete and create it again.

- list the clusters (after system reboot).

```bash
$ kind get clusters
learngo
```

- Delete the cluster.

```bash
$ kind delete cluster --name=learngo
Deleting cluster "learngo" ...
Deleted nodes: ["learngo-control-plane"]
```

- Creating the cluster again.

```bash
$ kind create cluster --name learngo
Creating cluster "learngo" ...
 ✓ Ensuring node image (kindest/node:v1.29.2) 🖼
 ✓ Preparing nodes 📦
 ✓ Writing configuration 📜
 ✓ Starting control-plane 🕹️
 ✓ Installing CNI 🔌
 ✓ Installing StorageClass 💾
Set kubectl context to "kind-learngo"
You can now use your cluster with:
kubectl cluster-info --context kind-learngo
Thanks for using kind! 😊
```

### Installing cert manager

Once we have the running cluster, we need to install [certmanager](https://cert-manager.io/docs/installation/kubectl/) for authentication purpose.

The cert manager can be installed by running below command.

```bash
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.14.4/cert-manager.yaml
```

#### Set up cert manager resources

There is a file with resources to create issuer and certificates is present in file named [green-pods/test-resources.yaml](./green-pods/test-resources.yaml). Use the yaml file for deployment.

```bash
kubectl apply -f green-pods/test-resources.yaml
```

Verify the running pods.

```bash
$ kubectl get pods --namespace cert-manager
NAME                                       READY   STATUS    RESTARTS   AGE
cert-manager-67c98b89c8-ct56n              1/1     Running   0          40s
cert-manager-cainjector-5c5695d979-np99w   1/1     Running   0          40s
cert-manager-webhook-7f9f8648b9-lbdcb      1/1     Running   0          40s
```

You can also see the issuing of certificates once you describe the pod.

```bash
Events:
  Type    Reason     Age   From                                       Message
  ----    ------     ----  ----                                       -------
  Normal  Issuing    29m   cert-manager-certificates-trigger          Issuing certificate as Secret does not exist
  Normal  Generated  29m   cert-manager-certificates-key-manager      Stored new private key in temporary Secret resource "selfsigned-cert-x7vcx"
  Normal  Requested  29m   cert-manager-certificates-request-manager  Created new CertificateRequest resource "selfsigned-cert-1"
  Normal  Issuing    29m   cert-manager-certificates-issuing          The certificate has been successfully issued
```

### Set up kube-green

- Install the kube-green using below command.

```bash
kubectl apply -f https://github.com/kube-green/kube-green/releases/download/v0.5.2/kube-green.yaml
```

- Create some test resources under namespace `sleepme` to configure with **kube-green**.

```bash
kubectl create ns sleepme
kubectl -n sleepme create deploy echo-service-replica-1 --image=davidebianchi/echo-service
kubectl -n sleepme create deploy do-not-sleep --image=davidebianchi/echo-service
kubectl -n sleepme create deploy echo-service-replica-4 --image=davidebianchi/echo-service --replicas 4
```

- Verify the created resources by running below command.

```bash
kubectl -n sleepme get pods
```

As a final step, configure the kube green for above resources. To setup kube-green, the SleepInfo resource must be created in sleepme namespace. Update the [green-pods/kube-green-test.yaml](./green-pods/kube-green-test.yaml) based on your requirements for testing purpose. The time and timezone needs to be modified.

```bash
kubectl -n sleepme apply -f kube-green-test.yaml
```

## Verification

Verify the pods getting down and brought up by kube-green using [k9s](https://k9scli.io/) in real time environment.

## Uninstallation of cert manager

- Delete the resources first.

```bash
kubectl delete -f green-pods/test-resources.yaml
```

- Delete the cert manager.

```bash
kubectl delete -f https://github.com/cert-manager/cert-manager/releases/download/v1.14.4/cert-manager.yaml
```

## Thank you

Thank you for contributing towards the green development 🌍.
