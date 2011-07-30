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

//this code is for the 500 step omron E6B2_CWZ3E with z-axis correction
//this includes an interupt implementation for two encoders (max amount of hardware interrupts)
//This link has an implementaion to add more interrupts with some limitations 
//http://forums.trossenrobotics.com/tutorials/how-to-diy-128/an-introduction-to-interrupts-3248/
//for the interupt to work the output of the encoder should be XOR together
//and the output of the XOR be placed in pin 2 (interutt 0) or pin 3 (interupt 1)
//the two pins should be set bellow in the define statements for the code in the encoder_cheepo

//Warnings 
//Don't know the max speed (rpm) for one or two encoders untill there will be skipped pin jumps.
//If one value is skipped an increase or decrease of one will happen depending which value is skipped which 
//could mean a worst case diffrence of 3 from the accutal value of only a difference of one on a best case diffrence
//Don't know how much other code can run or how well incoming serial data will behave
//When the count variable reaches the end it will roll over and start count from the max negative value.
//about 134000000 revolutions (16 ticks per revolution) of the encoder are need to roll over

//To incorprate this code with the standard robot queue code would require polling the count varialbe
//and populating the robot queue then deciding how to go from there.

//Author: Adrian Birylo
//Date:   7/28/11

#define BAUD 57600

void setup(){
  Serial.begin(BAUD);
  encoder_setup();
}

void loop(){
  delay(100);
  //does nothing interupt managed 
}
