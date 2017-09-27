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

package com.ibm.sdwet.wxs.client;

import com.ibm.websphere.objectgrid.*;
import com.ibm.websphere.objectgrid.datagrid.AgentManager;

/**
 * The class will connect and keep a static pointer to the <code>ObjcectGrid</code>
 * connection specified in the applications command line arguments.
 * <P>
 * Keeping the pointer statically means that the methods of this class can be called
 * anywhere in the application form the static Grid object, as
 * <code>Grid.getMap(</code>&lt;map name&gt;<code>)</code> sparing us the trouble
 * of passing the grid from place to place.
 * 
 * @author mplindsay
 * @version 1.0
 */
 
public class Grid
{
	private static ObjectGrid			objectGrid = null;
	
	public static String				catalogEndpoints = null;
	public static String				gridName = null;

	@SuppressWarnings("unused")
	private Grid ()
	{
		// don't let anyone do this
	}
	
	/**
	 * The constructor will read through to strings of <code>theArgs[]</code>
	 * for the required arguments <code>-cep</code> &lt;endpoint list&gt; and
	 * <code>-grid</code> &lt;grid name&gt; and will connect to the specified
	 * grid.
	 * 
	 * @param theArgs are the application's command line arguments
	 * @throws com.ibm.sdwet.wxs.client.GridException for any WXS or configuration issue that prevents us instancing an ObjectGrid
	 */
	
	public Grid (
		String[]						theArgs
		) throws GridException
	{
		boolean cepArg = false;	
		for (String arg: theArgs)
		{
			if (cepArg) catalogEndpoints = arg;
			cepArg = arg.equalsIgnoreCase("-cep");
		}
		
		if (catalogEndpoints == null)
		{
			throw new GridException ("Required -cep argument not found");
		}
		
		boolean gridArg = false;	
		for (String arg: theArgs)
		{
			if (gridArg) gridName = arg;
			gridArg = arg.equalsIgnoreCase("-grid");
		}
		
		if (gridName == null)
		{
			throw new GridException ("Required -grid argument not found");
		}
	}
	
	
	/**
	 * Construct a grid connection from the catalog endpoints and grid name arguments.
	 * 
	 * @param theCatalogEndpoints. a WXS cep string
	 * @param theGridName of what may be one of several ObjectGrids defined on the WXS service
	 */
	
	public Grid (
		String							theCatalogEndpoints,
		String							theGridName
		)
	{
		catalogEndpoints = theCatalogEndpoints;
		gridName = theGridName;
	}
	
	
	/**
     *  This method will get the <code>ObjectGrid</code> for this server, from
     *  which we will extract sessions from which we will extract object maps.
	 *
	 * 	This method is synchronized because we only want to get one object grid for
	 *  this server. it is protected because we want to use this for doing the just
	 * in time connection to the grid. the publicly defined connect() method can do
	 *  the explicit connection for applications needing the explicit connections.
	 *
	 *  The method returns the existing, connected ObjectGrid instance or a newly
	 *  connected ObjectGrid image or null if a new instance ObjectGrid could not be
	 *  connected. In this latter case, the Configuration will be reset so that
	 *  configuration values can be re-read
	 *
	 *  the caller will not need to depend on a instance/null test for correctness
	 *  however. we will throw a proper Exception
	 *  @return the static instance objectgrid we have opened
	 *  @throws com.ibm.sdwet.wxs.client.GridException for any error opening the WXS or other error preventing us returning a working grid
     */
    
	public synchronized static ObjectGrid getObjectGrid ()
		throws GridException
	{
		if (objectGrid == null)
		{
			ObjectGridManager ogm = ObjectGridManagerFactory.getObjectGridManager();
			
			try
            {
            	ClientClusterContext ccc = ogm.connect(catalogEndpoints, null, null);
            	if (ccc != null) objectGrid = ogm.getObjectGrid(ccc, gridName);
            }
            catch (Throwable t)
            {
            	throw new GridException ("could not create grid connection", t);
			}          
		}

		return objectGrid;
	}
	
	/**
	 * This method will test that the connection exists or that a new connection
	 * can be constructed.
	 * 
	 * @return <code>true</code> if the connection is ready to handled requests.
	 * @throws com.ibm.sdwet.wxs.client.GridException for any error opening the WXS or other error preventing us returning a working grid
	 */
	
	public static boolean isReady () throws GridException
	{
		return getObjectGrid () != null;
	}

	
	/**
	 * This method will return the ObjectMap of this Grid which is named by theMapName.
	 * 
	 * @param theMapName of the Map we need
	 * @return the named ObjectMap.
	 * @throws com.ibm.sdwet.wxs.client.GridException for any error opening the WXS or other error preventing us returning the named Map
	 */	
	
	public static ObjectMap getMap (
		String							theMapName
		) throws GridException
	{
		try
		{
			return getObjectGrid ().getSession().getMap(theMapName);
		}
		catch (GridException ge)
		{
			throw ge;
		}
		catch (Throwable t)
		{
			throw new GridException ("Could not get map " + theMapName, t);
		}
	}

	/**
	 * This method will return the AgentManager for the named ObjectMap
	 * of this Grid.
	 * 
	 * @param theMapName of the Map we need an AgentManager for
	 * @return the AgentManager for the named map of the Grid.
	 * @throws com.ibm.sdwet.wxs.client.GridException for any error opening the WXS or other error preventing us returning this Map's AgentManager
	 */
	
	public static AgentManager getAgentManager (
		String							theMapName
		) throws GridException
	{
		try
		{
			return getMap(theMapName).getAgentManager();
		}
		catch (GridException ge)
		{
			throw ge;
		}
		catch (Throwable t)
		{
			throw new GridException (
				"Could not get " + theMapName + " AgentManager", t
				);
		}
	}

	/**
	 * This method will return the PartitionManager of the named ObjectMap
	 * of the Grid
	 * 
	 * @param theMapName for the Map we need a PartitionManager for
	 * @return the PartitionManager for the named map of the Grid.
	 * @throws com.ibm.sdwet.wxs.client.GridException for any error opening the WXS or other error preventing us returning this Map's PartitionManager
	 */

	
	public static PartitionManager getPartitionManager (
		String							theMapName
		) throws GridException
	{
		return getObjectGrid().getMap(theMapName).getPartitionManager();
	}	


	/**
	 * This method will return the number of the local partition of the
	 * named map
	 * 
	 * @param theMapName for which we need to know a server side Map slice we need a partition number for
	 * @return the number of the local partition of the named map.
	 * @throws com.ibm.sdwet.wxs.client.GridException for any error opening the WXS or other error
	 * preventing us getting the server side partition id
	 * for the slice of the Map on that server
	 */

	
	public static Integer getPartitionId (
		String							theMapName
		) throws GridException
	{
		return new Integer (getObjectGrid().getMap(theMapName).getPartitionId());
	}	


}
