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

import com.ibm.sdwet.wxs.client.*;

/**
 * This class is the validation program for the PricerGrid.
 * It will connect to the grid and then parse and execute statements
 * until the user enters "done".
 * <P>
 * The class requires the environment variables:
 * <UL>
 * <LI>JAVA_HOME - documenting where the java runtime is located</LI>
 * <LI>WXS_BIN - documenting the location of the WXS ObjectGrid bin directory</LI>
 * </UL>
 * 
 * @author mplindsay
 * @version 1.0
 * 
 * @see Statement
 * @see Statements
 * @see DoLoad
 * @see DoList
 * @see DoPrice
 * @see DoClear
 * @see com.ibm.sdwet.wxs.client.Grid
 */

public class PricerClient
{

	/*
	 * The system specific, <code>xscdm</code> command line, for the
	 * benefit of <code>doList</code> and <code>doClear</code>
	 */
	protected static String				xscmd = null;

			
	/**
	 * The PricerClient's <code>main (String[])</code> method will
	 * construct the system specific <code>xscmd</code> command line,
	 * connect to the Grid and then conduct a dialog with the client.
	 * 
	 * @param theArgs Provides the catalog endpoints (<code>cep</code>) and grid name.
	 */
	public static void main(
		String[]						theArgs
		)
	{
		String wxs_bin = System.getenv("WXS_BIN");
		if (wxs_bin == null)
		{
			System.out.println ("could not find required environment value 'WXS_BIN'");
			return;
		}
		
		xscmd = wxs_bin + "/xscmd.sh";
		if (System.getProperty("os.name").toLowerCase().startsWith("win"))
			xscmd = wxs_bin + "\\xscmd.bat";
		
		// this is not correct. i should test for all the legal unix implementations
		// but who has the time
		
		try
		{
			new Grid (theArgs);
			Grid.getObjectGrid ();
		}
		catch (GridException ge)
		{
			return;
		}
		
		dialog ();

	}
	
	/**
	 * This method will, while there are statements to be executed at the console,
	 * execute the statements.
	 * 
	 * The method will instance a Statements iterator and, while statments.hasNext()
	 * can parse and return a Statement from the console, will map the statement to a
	 * Command and will ask the command to please() execute the statement.
	 */
	private static void dialog ()
	{
		Statements						statements = new Statements ();
		
		while (statements.hasNext ())
		{
			Statement statement = statements.next();
			
			switch (statement.type)
			{
				case help:
						// the Statements iterator answered this
						break;
				
				case load:
						DoLoad.please (statement);
						break;
						
				case sizes:
						DoSizes.please(statement);
						break;
					
				case clear:
						DoClear.please (statement);
						break;
					
				case price:
						DoPrice.please (statement);
						break;
						
				case list:
						DoList.please (statement);
						break;
				
				case error:
						// no action required
						break;
						
				default:
						System.out.println (
							"Unexpected Statement.Type " + statement.type
							);
						break;
			}
		}
		
		System.out.println ("You're Welcome");
		
	}
	
}
