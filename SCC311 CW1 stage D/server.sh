cd Server
rmiregistry &
sleep 2
java Replica &
sleep 2
java FrontEnd
