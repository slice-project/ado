package org.etri.ado;

import java.util.List;
import java.util.function.Function;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.etri.ado.gateway.openai.OpenAI.Action;

import akka.camel.CamelMessage;

@SuppressWarnings("deprecation")
public class CLIBuilder implements Function<CamelMessage, Action> {
	
	public static final String START = "Start";
	public static final String SUSPEND = "Suspend";
	public static final String RESUME = "Resume";
	public static final String STOP = "Stop";

	private CommandLineParser m_parser = new DefaultParser();
	private Options m_options = new Options();
	private HelpFormatter m_help = new HelpFormatter();	
	
	public CLIBuilder() {
		m_options.addOption(Option.builder("c").argName("string").desc("agent -c ( start | suspend | resume | stop )").hasArg().build());
	}
	
	@Override
	public Action apply(CamelMessage msg) {
		
		String cmdline = (String) msg.body();
		String[] splited = StringUtils.split(cmdline);
		
		CommandLine line = null;
		try {
			line = m_parser.parse(m_options, splited);
		} 
		catch ( ParseException e ) {
			m_help.printHelp("agent", m_options);
			return null;
		}
		
		List<String> argList = line.getArgList();
		if ( argList.isEmpty() ) {
			m_help.printHelp("agent", m_options);
			return null;
		}
		
		String cmd = line.getArgList().get(0);		
		if ( !line.hasOption("c") ) {
			m_help.printHelp("agent", m_options);
			return null;
		}
		
		String target = line.getOptionValue("c");
		if ( target.equals("start") ) {
			return Action.newBuilder().setCapability(START).build();
		}
		else if ( target.equals("suspend") ) {
			return Action.newBuilder().setCapability(SUSPEND).build();
		}
		else if ( target.equals("resume") ) {
			return Action.newBuilder().setCapability(RESUME).build();
		}
		else if ( target.equals("stop") ) {
			return Action.newBuilder().setCapability(STOP).build();
		}
		else {
			return null;
		}
	}

}
