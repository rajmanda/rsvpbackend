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