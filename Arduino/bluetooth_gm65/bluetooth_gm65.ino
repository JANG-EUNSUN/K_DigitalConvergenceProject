#include <SoftwareSerial.h>

// Bluetooth 통신
#define BT_RX 10
#define BT_TX 9
SoftwareSerial btSerial(BT_TX,BT_RX);

// GM65 바코드 리더기 UART 통신
#define GM65_TX 2
#define GM65_RX 3
SoftwareSerial GM65(GM65_TX,GM65_RX);

void setup() {
  // HC-06 통신속도 9600
  Serial.begin(9600);
  btSerial.begin(9600);
  GM65.begin(9600);
}


void loop() {
  // 바코드 리더기 인식이 된다면
  if (GM65.available()) {
    // 바코드 리더기 번호확인
    String barcode = GM65.readString();
//    Serial.println(barcode);
//    btSerial.write(barcode);
    // 바코드 문자 하나 하나 전송
    for (int i = 0;i < barcode.length(); i++){
      Serial.println(barcode[i]);
      btSerial.write(barcode[i]);
    }
  }
}
