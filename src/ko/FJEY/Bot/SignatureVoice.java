package ko.FJEY.Bot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignatureVoice {
	
	private static HashMap<String, String> SignatureVoiceMap = new HashMap<String, String>();
	private static Pattern pattern;
	private static Matcher matcher;
	
	public static void ready() {
		//TODO DB Connection
		SignatureVoiceMap.put("¸ø¸ÂÃèÁö·Õ", "¸ø¸ÂÃèÁö·Õ.mp3");
		pattern = Pattern.compile("(¸ø¸ÂÃèÁö·Õ)");
		matcher = pattern.matcher("");
	}
	
	public static Set<String> getVoiceList() {
		return SignatureVoiceMap.keySet();
	}
	
	public static String getVoiceURL(String name){
		return SignatureVoiceMap.get(name);
	}
	
	
	public static LinkedList<SignatureString> getSignatureSound(String msg) {
		LinkedList<SignatureString> queue = new LinkedList<>();
		matcher.reset(msg);
		if(matcher.find()) {
			String tMsg = msg;
			String[] t;
			for(int i = 0; i < matcher.groupCount(); i++) {
				String voiceName = matcher.group(i);
				t = tMsg.split(voiceName, 2);
				queue.add(new SignatureString(t[0]));
				queue.add(new SignatureString(voiceName, getVoiceURL(voiceName)));
				if(t[1] == null || t[1].equals("")) break;
				queue.addAll(getSignatureSound(t[1]));
			}
		}else queue.add(new SignatureString(msg));
		
		return queue;
	}
	
	
}

class SignatureString{
	
	String content = null;
	String name = null;
	String path = null;
	
	public SignatureString(String name, String path) {
		this.name = name;
		this.path = path;
	}
	
	public SignatureString(String content) {
		this.content = content;
	}
	
	public boolean isSignatureString() {
		return path == null;
	}
}