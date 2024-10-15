# To check the process running on port 8080 on mac.

lsof -n -i4TCP:8080
The PID is the second field in the output.

# To clean up docker 
docker system prune -a

# Please run ngrok as docker container for CI
docker run --net=host -it -e NGROK_AUTHTOKEN=2mW9616b5xPpcagIIBn3a0c4jFZ_2JeyoRDcpBitQCMxoSeWH ngrok/ngrok:latest http 80

# Test the pod is up 
kubectl exec rsvpbackend-665bcd5484-qf6fh -- curl http://localhost:8080/actuator/health

# Test the service once its up 
kubectl run tmp --rm -it --image curlimages/curl -- curl http://rsvpbackend:8090/actuator/health
kubectl run tmp --rm -it --image curlimages/curl -- curl http://rsvpbackend:8090/rsvp/allrsvps

# ########## Install Eureka Server on k8s
helm fetch ygqygq2/eureka --version 2.0.0
helm fetch bitnami/common
tar -xvzf eureka-2.0.0.tgz                                    
tar -xvzf common-2.24.0.tgz

mkdir -p eureka/charts                                        
mv common eureka/charts/

Update values.yaml

- name: REGISTER_WITH_EUREKA
  value: "False"                  # Meaning this Eureka server is not going to be Registered with other Eureka servers.
- name: FETCH_REGISTRY
  value: "False"                  # Meaning this Eureka server is not going to be client to any other Eureka servers.
- name: ENABLE_SELF_PRESERVATION
  value: “False”

helm install my-eureka ./eureka  

# ########## Install Ngnix Ingress Controller  Server on k8s
Install new helm repo
helm repo add nginx-stable https://helm.nginx.com/stable
helm repo update

Install ngnix controller with our reserved External IP as load balancer
helm install nginx-ingress nginx-stable/nginx-ingress   --namespace ingress-nginx   --create-namespace   --set controller.service.loadBalancerIP=34.44.172.58   --set controller.debug.enable=false

helm install nginx-ingress nginx-stable/nginx-ingress \
--namespace ingress-nginx \
--create-namespace \
--set controller.service.loadBalancerIP=34.44.172.58 \
--set controller.admissionWebhooks.enabled=true \
--set controller.admissionWebhooks.failurePolicy=Fail \
--set controller.admissionWebhooks.patch.enabled=true \
--set controller.debug.enable=false

Then create your ingress .

If this creates problem,
kubectl delete -A ValidatingWebhookConfiguration ingress-nginx-admission
helm uninstall nginx-ingress -n ingress-nginx
kubectl delete namespace ingress-nginx
Then install nginx controller again using the command

helm install nginx-ingress nginx-stable/nginx-ingress \
--namespace ingress-nginx \
--create-namespace \
--set controller.service.loadBalancerIP=34.44.172.58 \
--set controller.admissionWebhooks.enabled=true \
--set controller.admissionWebhooks.failurePolicy=Fail \
--set controller.admissionWebhooks.patch.enabled=true \
--set controller.debug.enable=false

# ########## Install ArgoCD Ingress Controller  Server on k8s