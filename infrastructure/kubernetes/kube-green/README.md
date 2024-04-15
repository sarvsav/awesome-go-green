# kube-green

go install sigs.k8s.io/kind@v0.22.0 && kind create cluster --name learngo

kubectl config get-contexts 
CURRENT   NAME                                      CLUSTER                                   AUTHINFO                                                                  NAMESPACE
          aks-test-eu-west-1-medium-blog            aks-test-eu-west-1-medium-blog            clusterUser_kubernetes-codingtherightway_aks-test-eu-west-1-medium-blog   
          docker-desktop                            docker-desktop                            docker-desktop                                                            
*         kind-learngo                              kind-learngo                              kind-learngo                                                              
          kind-local-test-eu-west-1-tictactoe-app   kind-local-test-eu-west-1-tictactoe-app   kind-local-test-eu-west-1-tictactoe-app 


After reboot,
$ kind get clusters
learngo
local-test-eu-west-1-tictactoe-app

$ kind delete cluster --name=learngo
Deleting cluster "learngo" ...
Deleted nodes: ["learngo-control-plane"]


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

https://cert-manager.io/docs/installation/kubectl/

kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.14.4/cert-manager.yaml

kubectl apply -f green-pods/test-resources.yaml

kubectl get pods --namespace cert-manager
NAME                                       READY   STATUS    RESTARTS   AGE
cert-manager-67c98b89c8-ct56n              1/1     Running   0          40s
cert-manager-cainjector-5c5695d979-np99w   1/1     Running   0          40s
cert-manager-webhook-7f9f8648b9-lbdcb      1/1     Running   0          40s

Events:
  Type    Reason     Age   From                                       Message
  ----    ------     ----  ----                                       -------
  Normal  Issuing    29m   cert-manager-certificates-trigger          Issuing certificate as Secret does not exist
  Normal  Generated  29m   cert-manager-certificates-key-manager      Stored new private key in temporary Secret resource "selfsigned-cert-x7vcx"
  Normal  Requested  29m   cert-manager-certificates-request-manager  Created new CertificateRequest resource "selfsigned-cert-1"
  Normal  Issuing    29m   cert-manager-certificates-issuing          The certificate has been successfully issued

kubectl delete -f https://github.com/cert-manager/cert-manager/releases/download/v1.14.4/cert-manager.yaml

kubectl apply -f https://github.com/kube-green/kube-green/releases/download/v0.5.2/kube-green.yaml

testfile

kubectl create ns sleepme
kubectl -n sleepme create deploy echo-service-replica-1 --image=davidebianchi/echo-service
kubectl -n sleepme create deploy do-not-sleep --image=davidebianchi/echo-service
kubectl -n sleepme create deploy echo-service-replica-4 --image=davidebianchi/echo-service --replicas 4

kubectl -n sleepme get pods

To setup kube-green, the SleepInfo resource must be created in sleepme namespace.

kubectl -n sleepme apply -f kube-green-test.yaml

use k9s
https://kube-green.dev/ 