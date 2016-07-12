void setup() {
  pinMode(13, OUTPUT);
  pinMode(9, OUTPUT);
  int first = 0;
}

void loop() {
  if(first == 0) {
    for(int i = 0; i < 10; i++) {
      lyingStartUp();
    }
    first++;
  }
  
  digitalWrite(13, HIGH);
  delay(1000);
  digitalWrite(13, LOW);
  delay(1000);
}

void lyingStartUp() {
  digitalWrite(9, HIGH);
  delay(100);
  digitalWrite(9, LOW);
  delay(100);
}

