package org.etri.ado.appls;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.etri.ado.AgentSystem;
import org.etri.ado.appls.MoveDeltaXYCommander.MoveDeltaXY;
import org.etri.ado.appls.MoveToXYCommander.MoveToXY;
import org.etri.ado.config.Configuration;
import org.etri.ado.device.Device;
import org.etri.ado.device.emulator.EmulatorDevice;

import ros.msgs.geometry_msgs.Vector3;

public class Predator2AgentMain {	

	public static void main(String[] args) throws Exception {

		Options options = new Options();
		options.addOption("c", "config", true, "the name of the agent's configuration file");
		
		CommandLineParser parser = new DefaultParser();
		HelpFormatter help = new HelpFormatter();
		CommandLine line = null;
		try {
			line = parser.parse(options, args);
		}
		catch ( ParseException e ) {
			help.printHelp("ado", options);
			return;
		}
		
		String fileName = null;
		if ( line.hasOption("config") ) {
			fileName = line.getOptionValue("config");
		}
		else {
			help.printHelp("ado", options);
			return;
		}
						
		AgentSystem system = AgentSystem.create(fileName);
		system.setAgent(new Predator2Impl());
		
		Configuration config = system.getConfiguration();	
		String deviceUri = "ws://" + config.DEVICE_HOST + ":" + config.DEVICE_PORT;
				
		Device device = system.createDevice(deviceUri);
		device.setDeviceBinding(new EmulatorDevice(-1f, -1f));

		device.addContextUpdater("/waffle/current_loc", "geometry_msgs/Vector3", Vector3.class, new LocationUpdater());
		device.addContextUpdater("/waffle/velocity", "geometry_msgs/Vector3", Vector3.class, new VelocityUpdater());
		device.addActionCommander(MoveDeltaXY.class, new MoveDeltaXYCommander());
		device.addActionCommander(MoveToXY.class, new MoveToXYCommander());
		device.connect();
	}
}
