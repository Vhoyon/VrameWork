package io.github.vhoyon.vramework.utilities;

import io.github.vhoyon.vramework.objects.Dictionary;
import io.github.vhoyon.vramework.objects.Request;

import java.util.HashMap;

public class MessageManager {
	
	private HashMap<Integer, Message> messages;
	private HashMap<String, Object> messageReplacements;
	
	protected class Message {
		
		String langKey;
		String[] replacementsKeys;
		
		protected Message(String langKey, String... replacementsKeys){
			this.langKey = langKey;
			this.replacementsKeys = replacementsKeys;
		}
		
	}
	
	public MessageManager(){}
	
	public MessageManager addMessage(int indice, String langKey,
			String... replacementsKeys){
		
		if(this.messages == null)
			this.messages = new HashMap<>();
		
		this.messages.put(indice, new Message(langKey, replacementsKeys));
		
		return this;
		
	}
	
	public MessageManager addReplacement(String key, Object value){
		
		if(this.messageReplacements == null)
			this.messageReplacements = new HashMap<>();
		
		this.messageReplacements.put(key, value);
		
		return this;
		
	}
	
	private Object getReplacement(String key){
		
		if(this.messageReplacements == null)
			return null;
		
		return this.messageReplacements.get(key);
		
	}
	
	public Message getMessageRaw(int indice){
		return this.messages.get(indice);
	}
	
	public String getMessage(int indice, Dictionary dictionary){
		return this.getMessage(indice, dictionary, (String)null);
	}
	
	public String getMessage(int indice, Dictionary dictionary,
			Object possiblePrefixObject){
		return this.getMessage(indice, dictionary,
				possiblePrefixObject.getClass());
	}
	
	public String getMessage(int indice, Dictionary dictionary,
			Class<?> possiblePrefixClass){
		return this.getMessage(indice, dictionary,
				possiblePrefixClass.getSimpleName());
	}
	
	public String getMessage(int indice, Dictionary dictionary,
			String possiblePrefix){
		
		Message message = this.getMessageRaw(indice);
		
		Object[] replacements = new Object[message.replacementsKeys.length];
		
		for(int i = 0; i < replacements.length; i++){
			replacements[i] = this.getReplacement(message.replacementsKeys[i]);
		}
		
		return dictionary.getString(message.langKey, possiblePrefix,
				replacements);
		
	}
	
}
