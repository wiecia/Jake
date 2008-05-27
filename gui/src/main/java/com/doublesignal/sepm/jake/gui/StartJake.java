package com.doublesignal.sepm.jake.gui;

import com.doublesignal.sepm.jake.core.services.JakeGuiAccess;
import org.apache.log4j.Logger;

/**
 * @author domdorn
 */
public class StartJake
{
	private static Logger log = Logger.getLogger(StartJake.class); 
	public static void main(String[] args)
	{
		log.info("===================================================");
		log.info("Welcome to Jake - a collaborative Environment");

		log.info("We're happy you're using our software.");
		log.info("For any acknowledgments, gifts or what else you ");
		log.info("want to give us, feel free to send us an email!");

		log.info("===================================================");
		
		log.debug("starting frontend....");
		new JakeGui(new JakeGuiAccess());
		log.debug("loading done.");
	}
}
