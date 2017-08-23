MODE 1000,1000

start "security-server-1" java -jar @executable.server.jar.name@.jar --server.port=1111 --spring.application.name=security-server --spring.profiles.active=qa
