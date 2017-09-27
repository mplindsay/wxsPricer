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
import java.util.Iterator;

/**
 * This class will fetch lines of text from the console, parse them and
 * construct <code>Statement</code> objects to be passed to the command handlers.
 * 
 * @author mplindsay
 * @version 1.0
 * 
 * @see Statement
 */
public class Statements
	implements Iterator<Statement>
{

	private BufferedReader				reader;
	
	/**
	 * The constructor will create a reader for the console.
	 */
	public Statements ()
	{
		reader = new BufferedReader(new InputStreamReader(System.in));
	}
	
	private String						statementText = null;
	
	/**
	 * The process will return <code>true</code> if a command text other then "done"
	 * is available.
	 * <P>
	 * If a command text has not already been read, the next line will be read from
	 * the console. This bit of logic is required as the method may be called several
	 * times before anyone gets around to seeing what the next statement is.
	 * <P>
	 * The method will return <code>false</code> if there is no new text or the next
	 * available text is "done". There is no more work for the application to do.
	 * 
	 * @return <code>true</code> while there is a statement to be parsed and executed.
	 */
	public boolean hasNext ()
	{
		try
		{
			if (statementText == null)
			{
				System.out.print ("> ");
				statementText = reader.readLine();
			}
			return (statementText != null && !statementText.equalsIgnoreCase("done"));
		}
		catch (Exception e) { return false; }
	}

	
	/**
	 * This method will parse the next available line found by <code>next()</code>,
	 * will parse the statement and construct a <code>Statement</code> object.
	 * <P>
	 * The recognized statements are:
	 * <ul>
	 * <li><code>load</code></li>
	 * <li><code>list</code> &lt;map name&gt;</li>
	 * <li><code>sizes</code></li>
	 * <li><code>price</code> &lt;customerId&gt; &lt;materialId&gt; [ &lt;quantity&gt; ]</li>
	 * <li><code>clear</code></li>
	 * <li><code>done</code></li>
	 * <li><code>help</code></li>
	 * <li><code>error</code></li>
	 * </ul>
	 * 
	 * @return	when <code>hasNext()</code> is true, a <code>Statement</code> containing
	 * 			the parsed result of the current statement text or <code>error</code>
	 * 			if that statement text could not parse,
	 * 			otherwise <code>null</code>.
	 */
	public Statement next ()
	{
		if (hasNext ())
		{
			String[] words = statementText.split (" |,");
			statementText = null;
			
			if (words.length == 1 && words[0].equalsIgnoreCase("help"))
			{
				usage ();
				return new Statement (Statement.Type.help);
			}
			
			else
			if (words.length == 1 && words[0].equalsIgnoreCase("load"))
				return new Statement (Statement.Type.load);
			
			else
			if (words.length == 1 && words[0].equalsIgnoreCase("sizes"))
				return new Statement (Statement.Type.sizes);

			else
			if (words.length == 1 && words[0].equalsIgnoreCase("clear"))
				return new Statement (Statement.Type.clear);
			
			else
			if ((words.length == 3 || words.length == 4)
											&& words[0].equalsIgnoreCase("price"))
			try
			{
				int quantity = 1;
				if (words.length == 4) quantity = Integer.parseInt(words[3]);
				return new Statement (
								Statement.Type.price,
									words[1], words[2], quantity
								);	
			}
			catch (Exception e)
			{
				System.out.println ("quantity " + words[3] + " is not an integer. \"help\" for details");
				return new Statement(Statement.Type.error);
			}

			else
			if (words.length == 2 && words[0].equalsIgnoreCase("list"))
				return new Statement (Statement.Type.list, words[1], null);
				
			System.out.println ("request not recognized. help for details");
			return new Statement (Statement.Type.error);
		}
		
		else return null;
	}

	/**
	 * This method will display to the application console the user statements that
	 * can be parsed by this parser. Where is <code>error</code>? Why would anyone
	 * want to application to error?
	 */
	private static void usage ()
	{
		System.out.println ("load");
		System.out.println ("price <customerId> <materialId> [ <quantity> ]");
		System.out.println ("sizes");
		System.out.println ("list <map>");
		System.out.println ("clear");
		System.out.println ("help");
		System.out.println ("done");
	}

	/**
	 * This method will remove the next Statement
	 */
	@Override
	public void remove()
	{
		if (hasNext()) next ();
	}


}
