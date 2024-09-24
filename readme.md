# To check the process running on port 8080 on mac.

lsof -n -i4TCP:8080
The PID is the second field in the output.

# Please run ngrok as docker container for CI
docker run -d -p 4040:4040 -p 8080:80 --name ngrok-container -e NGROK_AUTHTOKEN=2mW9616b5xPpcagIIBn3a0c4jFZ_2JeyoRDcpBitQCMxoSeWH ngrok/ngrok http 80
