#include <DHT11.h>
#include <SPI.h>
#include <Ethernet.h>

// MAC address from Ethernet shield sticker under board
byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
IPAddress ip(192, 168, 0, 177); // IP address, may need to change depending on network
EthernetServer server(80);  // create a server at port 80
DHT11 dht11(9);

boolean incoming = 0;

String SensorValue[3];
String list[]={"temp","humi","window"};
String HTTP_req;            // stores the HTTP request

int acdsValue;
float ahumi, atemp;

void setup()
{
    Ethernet.begin(mac, ip);  // initialize Ethernet device
    server.begin();           // start to listen for clients
    Serial.begin(9600);       // for diagnostics
    pinMode(3, OUTPUT);
    digitalWrite(3, HIGH);
    sensing();
}

void loop()
{
    EthernetClient client = server.available();  // try to get client
    
    if (client) {  // got client?
        boolean currentLineIsBlank = true;
        while (client.connected()) {
            if (client.available()) {   // client data available to read
                char c = client.read(); // read 1 byte (character) from client
                
                if(incoming && c == ' '){
                  incoming = 0;  
                }
                if(c == '$'){
                  incoming = 1;
                }

                if(incoming == 1){ // LED all down
                  if(c == '1'){
                    digitalWrite(3, LOW);  
                  }
                }
                HTTP_req += c;  // save the HTTP request 1 char at a time
                // last line of client request is blank and ends with \n
                // respond to client only after last line received
                if (c == '\n' && currentLineIsBlank) {
                    // send a standard http response header
                    client.println("HTTP/1.1 200 OK");
                    client.println("Content-Type: text/html;charset=utf-8");
                    client.println("Connection: keep-alive");
                    client.println();
                    // AJAX request for switch state
                    if (HTTP_req.indexOf("ajax_switch") > -1) {
                        // read switch state and send appropriate paragraph text
                        GetSwitchState(client);
                    }
                    else {  // HTTP request for web page
                        // send web page - contains JavaScript with AJAX calls
                        client.println("<!DOCTYPE html>");
                        client.println("<html>");
                        client.println("<head>");
                        client.println(F("<title>Arduino Web Page</title>"));
                        client.println(F("<style>"));
                        client.println(F("div.content {"));
                        client.println(F("border:3px solid lightslategray;"));
                        client.println(F("text-align:center;"));
                        client.println(F("background-color: lawngreen;"));
                        client.println(F("display:inline-block;"));
                        client.println(F("margin:20px"));
                        client.println("}");
                        client.println("h1.explain{");
                        client.println("color: yellow;");
                        client.println("background-color:black;"); 
                        client.println("}");
                        client.println("h3.info{");
                        client.println(F("text-decoration:overline;"));
                        client.println("}");
                        client.println("</style>");
                        client.println("<script>");
                        client.println(F("function GetSwitchState() {"));
                        client.println(F("nocache = \"&nocache=\"+ Math.random() * 1000000;"));
                        client.println(F("var request = new XMLHttpRequest();"));
                        client.println(F("request.onreadystatechange = function() {"));
                        client.println(F("if (this.readyState == 4) {"));
                        client.println(F("if (this.status == 200) {"));
                        client.println(F("if (this.responseText != null) {"));
                        client.println(F("var sensorDataArray = this.responseText.split(\" \")"));
                        client.println(F("document.getElementById(\"ondo\").innerHTML = sensorDataArray[0];"));
                        client.println(F("document.getElementById(\"seupdo\").innerHTML = sensorDataArray[1];"));
                        client.println(F("document.getElementById(\"window\").innerHTML = sensorDataArray[2];"));
                        client.println("}}}}");
                        client.println(F("request.open(\"GET\", \"ajax_switch\" + nocache, true);"));
                        client.println(F("request.send(null);"));
                        client.println(F("setTimeout('GetSwitchState()', 1000);"));
                        client.println("}");
                        client.println("</script>");
                        client.println("</head>");
                        client.println(F("<body onload=\"GetSwitchState()\">"));
                        client.println(F("<div style=\"margin:0 auto\">"));
                        client.println(F("<div class=\"content\">"));
                        client.println(F("<h1 class=\"explain\">현재 온도</h1>"));
                        client.println(F("<h3 class=\"info\" id=\"ondo\">"));
                        client.println((int)atemp);
                        client.println(F("</h3>"));
                        client.println(F("</div>"));
                        client.println(F("<div class=\"content\">"));
                        client.println(F("<h1 class=\"explain\">현재 습도</h1>"));
                        client.println(F("<h3 class=\"info\" id=\"seupdo\">"));
                        client.println((int)ahumi);
                        client.println(F("</h3>"));
                        client.println(F("</div>"));
                        client.println(F("<div class=\"content\">"));
                        client.println(F("<h1 class=\"explain\">창문 상태</h1>"));
                        client.println(F("<h3 class=\"info\" id=\"window\">"));
                        client.println(acdsValue);
                        client.println(F("</h3>"));
                        client.println(F("</div>"));
                        client.println(F("<input type=\"hidden\" value=\"32\"/>"));
                        client.println("</body>");
                        client.println("</html>");
                    }
                    // display received HTTP request on serial port
                    Serial.print(HTTP_req);
                    HTTP_req = "";            // finished with request, empty string
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
}

void sensing(){
  int err, cdsValue;
  float humi, temp;
  if((err = dht11.read(humi, temp)) == 0 && cdsValue != -1){
    acdsValue = cdsValue;
    ahumi = humi;
    atemp = temp;
  }
  else {
    return;  
  }
}

// send the state of the switch to the web browser
void GetSwitchState(EthernetClient cl)
{   
  sensing();
    cl.println(String(atemp) + " " + String(ahumi) + " " + String(acdsValue));
}
