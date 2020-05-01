# AgentskeTehnologije

Dovoljno je importovati ChatEar i pokrenuti na wildfly11 serveru.
Frontend je builodvan i postavljen unutar war projekta.
Može mu se pristupiti na http://{ip}:{port}/ChatWar/
  
Master node je hardkodovan u klasi NodeManager i pri pokretanju se mora podesiti na ip adresu kompjutera koji zelite da bude host.
Pre pokretanja potrebno je rucno podesiti interfejs wildfly servera u standalone.xml ili pokrenuti server pomocu komande:
./standalone.sh -c standalone.xml -b={ip adresa}
