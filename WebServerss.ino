#include <SPI.h>
#include <Ethernet.h>

// MAC address from Ethernet shield sticker under board
byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
IPAddress ip(192, 168, 0, 177); // IP address, may need to change depending on network
EthernetServer server(80);  // create a server at port 80

char cTemp;
String sCommand = "";
boolean incoming = 0;

void setup()
{
  Serial.begin(9600);       // for diagnostics
  Ethernet.begin(mac, ip);  // initialize Ethernet device
  server.begin();           // start to listen for clients

  Serial.print("server is at ");
  Serial.println(Ethernet.localIP());
}

void loop()
{
  EthernetClient client = server.available();  // try to get client
  if (client) {  // got client?
    Serial.println("new client");
    boolean currentLineIsBlank = true;
    sCommand = "";
    while (client.connected()) {
      if (client.available()) {   // client data available to read
        char c = client.read(); // read 1 byte (character) from client
        if(incoming && c == ' '){
          incoming = 0; 
        }
        if(c == '$'){
          incoming = 1;
        }

        if(incoming == 1){
          Serial.println(c);

          if(c == '1'){
            Serial.println("ON");
            digitalWrite(8, HIGH);
          }
          if(c == '2'){
            Serial.println("OFF");
            digitalWrite(8, LOW);  
          }
        }
        
        Serial.write(c);
        // last line of client request is blank and ends with \n
        // respond to client only after last line received
        if (c == '\n' && currentLineIsBlank) {
          // send a standard http response header
          client.println("HTTP/1.1 200 OK");
          client.println("Content-Type: text/html");
          client.println("Connection: close");  // the connection will be closed after completion of the response
          client.println("Refresh: 1");  // refresh the page automatically every 5 sec
          client.println();
          client.println("<!DOCTYPE HTML>");
          client.println("<html>");
          while(Serial.available()){
            cTemp = Serial.read();
            sCommand.concat(cTemp);
          }
          Serial.println(sCommand);
          client.print("<p>");
          client.print(sCommand);
          client.println(sCommand);
          client.println("</html>");
          break;
        }
        // every line of text received from the client ends with \r\n
        if (c == '\n') {
          // last character on line of received text
          // starting new line with next character read
          currentLineIsBlank = true;
        }
        else if (c != '\r') {
          // a text character was received from client
          currentLineIsBlank = false;
        }
      } // end if (client.available())
    } // end while (client.connected())
    delay(1);      // give the web browser time to receive the data
    client.stop(); // close the connection
  } // end if (client)
  delay(1000);
}

