package io.github.vhoyon.vramework.objects;

import java.util.LinkedHashMap;
import java.util.List;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.github.vhoyon.vramework.exceptions.CommandNotFoundException;
import io.github.vhoyon.vramework.interfaces.LinkableCommand;
import io.github.vhoyon.vramework.modules.Logger;

public abstract class CommandLinksContainer {
	
	private LinkedHashMap<String, Link> linkMap;
	
	/**
	 * The latest links commands will always replace the first command call.
	 */
	public CommandLinksContainer(Link... links){
		initializeContainer(links);
	}
	
	/**
	 * The latest links commands will always replace the first command call.
	 */
	@SafeVarargs
	public CommandLinksContainer(Class<? extends LinkableCommand>... commands){
		Link[] links = new Link[commands.length];
		
		for(int i = 0; i < commands.length; i++){
			links[i] = new Link(commands[i]);
		}
		
		initializeContainer(links);
	}
	
	public CommandLinksContainer(String linksPackage){
		
		try(ScanResult results = new ClassGraph().whitelistPackages(
				linksPackage).scan()){
			
			List<Class<LinkableCommand>> linkableCommands = results
					.getClassesImplementing(
							LinkableCommand.class.getCanonicalName())
					.loadClasses(LinkableCommand.class);
			
			Link[] links = new Link[linkableCommands.size()];
			
			for(int i = 0; i < linkableCommands.size(); i++){
				links[i] = new Link(linkableCommands.get(i));
			}
			
			initializeContainer(links);
			
		}
		
	}
	
	private void initializeContainer(Link[] links){
		
		linkMap = new LinkedHashMap<>();
		
		for(Link link : links){
			
			Object calls = link.getCalls();
			
			if(calls != null){
				
				if(calls instanceof String[]){
					
					String[] callsArray = (String[])calls;
					
					for(String call : callsArray)
						linkMap.put(call, link);
					
				}
				else{
					
					linkMap.put(calls.toString(), link);
					
				}
				
			}
			
		}
		
	}
	
	public LinkableCommand initiateLink(String commandName){
		try{
			
			Link link = findLink(commandName);
			
			if(link != null){
				return link.getInstance();
			}
			
		}
		catch(Exception e){
			Logger.log(e);
		}
		
		return whenCommandNotFound(commandName);
	}
	
	public abstract LinkableCommand whenCommandNotFound(String commandName);
	
	public LinkedHashMap<String, Link> getLinkMap(){
		return this.linkMap;
	}
	
	public Link findLink(String commandName){
		return getLinkMap().get(commandName);
	}
	
	public LinkableCommand findCommand(String commandName) throws Exception{
		
		Link link = findLink(commandName);
		
		if(link == null){
			throw new CommandNotFoundException(commandName);
		}
		else{
			
			return link.getInstance();
			
		}
		
	}
	
}
