package io.github.vhoyon.vramework.abstracts;

import java.util.HashMap;
import java.util.TreeMap;

import io.github.vhoyon.vramework.interfaces.Translatable;
import io.github.vhoyon.vramework.interfaces.Hidden;
import io.github.vhoyon.vramework.interfaces.LinkableCommand;
import io.github.vhoyon.vramework.objects.CommandLinksContainer;
import io.github.vhoyon.vramework.objects.Link;

public abstract class CommandsLinker implements Translatable {
	
	private CommandLinksContainer container;
	
	private Dictionary dict;
	
	public abstract CommandLinksContainer createLinksContainer();
	
	public CommandLinksContainer getContainer(){
		if(container != null)
			return container;
		
		return container = createLinksContainer();
	}
	
	@Override
	public void setDictionary(Dictionary dict){
		this.dict = dict;
	}
	
	@Override
	public Dictionary getDictionary(){
		return this.dict;
	}
	
	public String getFullHelpString(String textHeader){
		return getFullHelpString(textHeader, false);
	}
	
	public String getFullHelpString(String textHeader, boolean showDescriptions){
		return getFullHelpString(textHeader, showDescriptions, true);
	}
	
	public String getFullHelpString(String textHeader,
			boolean showDescriptions, boolean shouldSummarize){
		
		CommandLinksContainer container = getContainer();
		
		StringBuilder builder = new StringBuilder();
		
		if(textHeader != null){
			builder.append(textHeader).append("\n\n");
		}
		
		HashMap<String, Link> linksMap = container.getLinkMap();
		
		TreeMap<String, LinkableCommand> defaultCommands = new TreeMap<>();
		
		linksMap.forEach((key, link) -> {
			
			LinkableCommand command = link.getInstance();
			
			boolean isSubstitute = defaultCommands.containsKey(link.getCall());
			
			boolean isHidden = (command instanceof Hidden)
					&& ((Hidden)command).hiddenCondition();
			
			if(!isSubstitute && !isHidden){
				defaultCommands.put(link.getCall(), command);
			}
			
		});
		
		String prependChars = getPrependChars();
		String prependCharsVariants = getPrependCharsForVariants();
		
		defaultCommands.forEach((key, command) -> {
			
			String wholeCommandString = formatWholeCommand(prependChars, key);
			
			if(!showDescriptions){
				builder.append(wholeCommandString);
			}
			else{
				
				String wholeHelpString = null;
				
				// Try to get the help string of a link
				try{
					
					String helpString = command.getCommandDescription();
					
					wholeHelpString = formatHelpString(helpString);
					
				}
				catch(Exception e){}
				
				if(wholeHelpString == null){
					builder.append(wholeCommandString);
				}
				else{
					builder.append(formatWholeHelpLine(wholeCommandString,
							wholeHelpString, shouldSummarize));
				}
				
			}
			
			builder.append("\n");
			
			String[] aliases = command.getAliases();
			
			if(aliases.length > 0){
				
				for(String alias : aliases){
					
					builder.append(formatAlias(formatWholeCommand(
							prependCharsVariants, alias)));
					
					builder.append("\n");
					
				}
				
			}
			
		});
		
		return builder.toString().trim();
		
	}
	
	private String formatWholeCommand(String prependChars, String command){
		if(prependChars == null || prependChars.length() == 0)
			return formatCommand(command);
		
		return prependChars + formatCommand(command);
	}
	
	public String formatWholeHelpLine(String wholeCommandString,
			String wholeHelpString, boolean shouldSummarize){
		
		String helpString;
		int returnIndex;
		
		if(shouldSummarize
				&& (returnIndex = wholeHelpString.indexOf("\n")) != -1){
			helpString = wholeHelpString.substring(0, returnIndex);
		}
		else{
			helpString = wholeHelpString;
		}
		
		return wholeCommandString + " : " + helpString;
		
	}
	
	public String formatCommand(String command){
		return command;
	}
	
	public String formatHelpString(String helpString){
		return helpString;
	}
	
	public String formatAlias(String alias){
		return "\t" + alias;
	}
	
	public String getPrependChars(){
		return "~ ";
	}
	
	public String getPrependCharsForVariants(){
		return getPrependChars();
	}
	
}
