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

/*
 * This is meant to log important information 
 * This is left to each robot to implement individually 
 * Maybe there will be support for this in the future  
 */

package general;
import java.io.*;

public class Log {
	FileWriter out = null;
	public Log(){
		build(null);
	}
	public Log(String in){
		build(in);
	}

	public void write(RobotEvent envIn){
		try{
			//write the event to the file as a csv string
			out.write(envIn.toString()+"\n");
		}
		catch(IOException broke){
			System.out.println("Error!");
		}
	}
	public void close()
	{
		try{
			out.flush();
			out.close();
		}
		catch(IOException error){
			
		}
	}
	private void build(String in){
		if(in == null){
			in = "log";
		}
		int counter = 0;
		try{
			//try every extension to "log-" sequentially until there is no file
			while(true){
				FileReader check = new FileReader(in+"-"+counter+".txt");
				++counter;
			}
		}
		catch(FileNotFoundException e){
			try{
				//define the output as the counter FileWriter failed at
				out = new FileWriter(in+"-"+counter+".txt");
			}
			catch(IOException ex){
				
			}
		}
	}
}

