#Dining Hall
The following project is the first half of the laboratory number 1 for Network Programming Course. 
The objective of the laboratory work is to simulate a restaurant environment within Docker Containers using Threads.

To run this project it is recommended to rebuild the FatJar using the command. 
```
 ./gradlew :buildFatJar      
```
And then use the commands below to create the image and run the docker container. Beware that before running the dining-container,
the docker network and the kitchen-container must be created first.
```
docker build -t dining .     
docker run --name dining-container --network lina-restaurant-network  -p 8080:8080 dining

```

