//    encoder.c - uses interupts to count the encoders value
//    Copyright (C) 2011 Illinois Institute of Technology Robotics
//	  <robotics@iit.edu>
//
//    This program is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License along
//    with this program; if not, write to the Free Software Foundation, Inc.,
//    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

#define ENCOD1_PINA 6
#define ENCOD1_PINB 7
#define ENCOD2_PINA 8
#define ENCOD2_PINB 9

struct encoder {
  long count;
  int prev;
  int prevA;
};

volatile struct encoder encod1;
volatile struct encoder encod2;

void encoder_setup(){
  
  pinMode(ENCOD1_PINA, INPUT);
  pinMode(ENCOD1_PINB, INPUT);
  pinMode(ENCOD2_PINA, INPUT);
  pinMode(ENCOD2_PINB, INPUT);
  
  attachInterrupt(0, encoder1, CHANGE);
  attachInterrupt(1, encoder2, CHANGE);
  
  encod1.count = 0;
  encod2.count = 0;
  
  encod1.prev = digitalRead(ENCOD1_PINA) << 1;
  encod1.prev |= digitalRead(ENCOD1_PINB);
  encod1.prevA = digitalRead(ENCOD1_PINA);
  
  encod2.prev = digitalRead(ENCOD2_PINA) << 1;
  encod2.prev |= digitalRead(ENCOD2_PINB);
  encod2.prevA = digitalRead(ENCOD2_PINA);
}

void encoder1(){
  int val;
  int temp;
  
  //XOR prevA phase with current phase will tell the direction the encoder moved 0 clockwise 1 counterclockwise 
  val = encod1.prevA ^ digitalRead(ENCOD1_PINB);
  encod1.prevA = digitalRead(ENCOD1_PINA);
  
  //checks to make sure we don't get false reads 
  //the grayhill can trigger the interupt twice between clicks
  //makes sure that the encoder moved from last positions
  //souldn't be a problem with higher quality encoders 
  temp = encod1.prevA << 1;
  temp |= digitalRead(ENCOD1_PINB);
  
  if(temp == encod1.prev) 
    return;
  encod1.prev = temp;
  
  if(val)
    encod1.count--;
  else
    encod1.count++;  
  
//  Serial.print("A ");
//  Serial.println(encod1.count);
}

void encoder2(){
  int val;
  int temp;
  
  val = encod2.prevA ^ digitalRead(ENCOD2_PINB);
  encod2.prevA = digitalRead(ENCOD2_PINA);
  
  temp = encod2.prevA << 1;
  temp |= digitalRead(ENCOD2_PINB);
  
  if(temp == encod2.prev) //checks to make sure we don't get false reads 
    return;
  encod2.prev = temp;
  
  if(val)
    encod2.count--;
  else
    encod2.count++;  
  
//  Serial.print("B ");
//  Serial.println(encod2.count); 
}
