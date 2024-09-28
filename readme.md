# To check the process running on port 8080 on mac.

lsof -n -i4TCP:8080
The PID is the second field in the output.

# To clean up docker 
docker system prune -a

# Please run ngrok as docker container for CI
docker run --net=host -it -e NGROK_AUTHTOKEN=2mW9616b5xPpcagIIBn3a0c4jFZ_2JeyoRDcpBitQCMxoSeWH ngrok/ngrok:latest http 80

Testing with Mahaan 