//
//	This sample program is provided AS IS and may be used, executed, copied and
//	modified without royalty payment by customer (a) for its own instruction and 
//	study, (b) in order to develop applications designed to run with an IBM 
//	WebSphere product, either for customer's own internal use or for redistribution 
//	by customer, as part of such an application, in customer's own products. "
//
//	(C) COPYRIGHT International Business Machines Corp. 2017
//	All Rights Reserved * Licensed Materials - Property of IBM
//

package com.ibm.sdwet.pricer.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Vector;

import com.ibm.sdwet.wxs.client.Grid;

/**
 * This command handling class will display the list of cached entries in one of the
 * maps of the grid.
 * It will invoke the eXtremeScale <code>xscmd</code> application to gather the list
 * and will massage its console output into a more compact form for our console.
 * 
 * @author mplindsay
 * @version 1.0
 * 
 * @see <code>com.ibm.sdwet.wxs.client.Grid</code>
 * @see <a
 * 			href="http://www.ibm.com/support/knowledgecenter/SSTVLU_8.6.0/com.ibm.websphere.extremescale.doc/cmd_findbykey.html?lang=en">
 * 			<code>xscmd findbykey</code>
 * 			</a>
 */

public class DoList
{
	/**
	 * This method will invoke the eXtremeScale <code>xscmd -c findbykey</code>
	 * to fetch the list key/value pairs from the map named in theStatement.
	 * <P>
	 * The method will
	 * construct a Windows or Unix command line to invoke the<code>xscmd</code> application,
	 * start a Process to run the command and
	 * will extract the useful part of the applications console display for our console.
	 * 
	 * @param theStatement	names the map to be listed
	 */
	public static void please (
		Statement						theStatement
		)
	{
		try
		{		
			String endpoint = Grid.catalogEndpoints;
			String gridName = Grid.gridName;
			String suffix = "-cep " + endpoint +
								" -g " + gridName +
								" -m " + theStatement.map;
			
			String command = PricerClient.xscmd + " -c findbykey -rv -fs \".*\" " + suffix;
			
			System.out.println (command);
			
			String[] split = command.split(" ");
			Vector<String> list = new Vector<String> ();
			for (int s = 0; s < split.length; s++) list.add(split[s]);
			
			ProcessBuilder builder = new ProcessBuilder (list);
			builder.redirectErrorStream(true);
			
			Process xscmd = builder.start();
			
			// xscmd.waitFor ();
			
			BufferedReader				reader = new BufferedReader(
														new InputStreamReader(
																xscmd.getInputStream()
																)
														);
			boolean printFinds = false;
			boolean printBadMap = false;
			String thisLine = reader.readLine ();
			while (thisLine != null)
			{
				printFinds = ((printFinds || thisLine.contains("matching keys"))
									&& !thisLine.contains("completed successfully"));
				
				printBadMap = thisLine.contains ("specified map name");
				
				if (printFinds || printBadMap) System.out.println (thisLine);
				thisLine = reader.readLine();
			}

			xscmd.waitFor();
			if (xscmd.exitValue() != 0)
				System.out.println ("Could not list entries for " + theStatement.map);
			
		}
		catch (Exception e)
		{
			System.err.println (
					"Could not list entries of " + theStatement.map + " because: " + e
					);
				e.printStackTrace(System.err);
		}
	}
}
