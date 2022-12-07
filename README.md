# <u>Chat Cloak</u>

Een poging om een chat applicatie voor android te maken die gebruik maakt van geëncrypteerde communicatie. Ik heb gebruik gemaakt van de Android Jetpack(androidx) libraries en enkele andere zoals BouncyCastle, QRGen, ML Kit's barcode scanner, accompanist's permissions en google's json parser. 

# 1. Demos

Ik heb eerst de features/libraries/apis die ik dacht te gebruiken in aparte demos proberen te gebruiken, ik heb ze hier opgelijst.

## 1.1 [<u>Android Basic Demo](https://github.com/SenneS/android_basic_demo)</u>
Een demo waar ik gebruik maak van androidx compose om knoppen op het scherm te tonen die meldingen tonen op touch.  

## 1.2 <u>[Broadcast Demo](https://github.com/SenneS/broadcast_demo)</u>
Een demo waar ik gebruik maak van udp sockets om mijn apparaat zichtbaar te maken op het netwerk. Ik dacht dit te gebruiken om nearby devices te kunnen vinden maar ik heb deze functionaliteit laten vallen in de main app.  

## 1.3 <u>[Cryptography](https://github.com/SenneS/cryptography_demo)</u>
Een demo waar ik gebruik maak van BouncyCastle functies voor encryptie/decryptie en ECDH sleutels genereer.  

## 1.3 <u>[Communication Demo](https://github.com/SenneS/communication_demo)</u>
Een demo waar ik gebruik maak van tcp sockets EN de functionaliteit van de cryptografie demo om berichten geëncrypteerd te versturen en ontvangen.  


## 1.5 <u>[Navigation Demo](https://github.com/SenneS/navigation_demo)</u>
Een demo waar ik gebruik maak van androidx compose's TabRow element om te navigeren tussen verschillende screens.  

## 1.6 <u>[QR Demo](https://github.com/SenneS/qr_demo)</u>
Een demo waar ik gebruik maak van QRGen en ML Kit's barcode scanner, de helft van het scherm is een qr code en de andere helft is een scanner.  

## 1.7 <u>[Room Demo](https://github.com/SenneS/room_demo)</u>

Een demo waar ik gebruik maak van de androidx room api, je kan het gebruiken voor database management (entities, daos, databases). Ik dacht deze library te gebruiken om contacten en chat sessies op te slagen maar ik heb deze functionaliteit laten vallen in de main app.