//    Copyright (C) 2011  Illinois Institute of Technology Robotics
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

//source: http://www.arduino.cc/playground/Interfacing/Java
package general;

import java.io.*;
import java.util.*;
import gnu.io.*;

public class Communication implements SerialPortEventListener {
	
	private SerialPort serialPort = null;
	private InputStream input = null;
	private OutputStream output = null;
	private RobotQueue recv = null;
	
	public Communication(RobotQueue r){
		this.recv = r;
	}
	
	/**
	 * Opens the Serial port with the baud and port_name given
	 */
	public boolean OpenSerial(int baud, String port){
		CommPortIdentifier portId = null;
		@SuppressWarnings("rawtypes")
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
		
		while (portEnum.hasMoreElements()){
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			if(currPortId.getName().equals(port)){
				portId = currPortId;		
				break;
			}
		}
		
		if(portId == null){
			System.err.println("Can not open serial port");
			return false;
		}
		
		try{
			serialPort = (SerialPort) portId.open(this.getClass().getName(),2000);
			
			serialPort.setSerialPortParams(baud, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		
			input = serialPort.getInputStream();
			output = serialPort.getOutputStream();
			
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
			Thread.sleep(1500);
		}
		catch(Exception e){
			return false;
		}
		
		return true;
	}
	
	/**
	 * returns an array of all the serial ports available 
	 */
	public static CommPortIdentifier[] getSerialPorts(){
		@SuppressWarnings("rawtypes")
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
		int i=0;
		while(portEnum.hasMoreElements()){
			i++;
			portEnum.nextElement();
		}
		
		CommPortIdentifier[] portId = new CommPortIdentifier[i];
		@SuppressWarnings("rawtypes")
		Enumeration portEnum2 = CommPortIdentifier.getPortIdentifiers();
		
		i=0;
		while (portEnum2.hasMoreElements()){
			portId[i] = (CommPortIdentifier) portEnum2.nextElement();
		}
		return portId;
	}
		
	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 */
	public synchronized void closeSerial() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	/**
	 * Handle an event on the serial port. 
	 * Read the data and parse it into a Robot_Event and add to queue
	 */
	private final int BUF_SIZE = 256;
	private byte[] buf = new byte[BUF_SIZE+1];
	private int count = 0;
	private int start = 0;
	private int newline = 0;
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				int available = input.available();
				if(count < BUF_SIZE){
		            count = count + input.read(buf, count, (available > BUF_SIZE-count) ? BUF_SIZE-count : available);
		        }

		        start = newline;
		        for(int i=newline; i<=count; i++){
		            if(buf[i] == 'U'){ //start byte
		                start = i;
		                break;
		            }
		        }
		        for(int i=start; i<=count; i++){
		            if(buf[i] == '\n'){ //end byte
		                newline = i;
		                break;
		            }
		        }
		        if(newline <= start){
		            if(count == BUF_SIZE){ //buf full an no valid datagram found, clear the buffer
		                //need to copy the stuff after the start byte to the beginning
		                if((count - start) <= 16){
		                    System.arraycopy(buf, 0, buf, start, count-start);
		                    count = count-start;
		                    start = 0;
		                    newline = 0;
		                }
		                else{  //buff full and last start byte is more than max packet size from end
		                    count = 0; //no need to copy since the buf is grabage
		                    start = 0;
		                    newline = 0;
		                }
		            }
		            return;
		        }

		        //found valid datagram
		        //parse datagram comma delimited
		        buf[newline] = ',';
		        String newbuf = new String(buf, start, newline-start);
		        StringTokenizer st = new StringTokenizer(newbuf, ",");
		        int[] data = new int[5];

		        int i=0;
		        while(st.hasMoreElements() && i<5){
		        	String temp = st.nextToken();
		        	try{
		        		data[i] = Integer.parseInt(temp,16);
		        	}
		        	catch(NumberFormatException e){
		        	}
		        	i++;
		        }

		        RobotEvent ev = new RobotEvent();
		        int checksum;

		        ev.setCommand(EventEnum.getEvent(data[1]));
		        ev.setIndex((short)data[2]);
		        ev.setValue((int)data[3]);
		        checksum = data[4];

		        int checksum2 = (((byte)(ev.getCommand().getValue()) + (byte)ev.getIndex() +(byte)ev.getValue() + (byte)(ev.getValue() >>8)) % 255);

		        if(checksum2 == checksum){
		            //log event received
		            recv.put(ev);
		        }
			} 
			catch (Exception e) {
			}
		}
	}
	
	
	public void sendEvent(RobotEvent ev){
		try{
			output.write(ev.toStringSend().getBytes());   //write needs a byte array instead of a string
		}
		catch(Exception e){
		}
		
	}
}
