package ko.FJEY.Bot;

public class Utils {
	
	static String credentialLocation = "";
	static String ttsVoiceStoarge = "";
	static String mp3Stoarge = "";
	
	public static String processHangul(String msg) {
		return msg.replaceAll("¤·¤¸¤©", "ÀÌÁö¶ö").replaceAll("¤·¤¸", "ÀÎÁ¤").replaceAll("¤»{3,}","¤»¤»¤»").replaceAll("¤µ¤¡", "¼ö°í")
				.replaceAll("¤©¤·", "·¹¾Ë").replaceAll("¤»¤»¤©¤³¤³", "Å©Å©·ç»æ»Í").replaceAll("¤¡¤§", "±â´Þ")
				.replaceAll("¤§¤§", "´ú´ú").replaceAll("¤·¤¤", "¾Æ³ö").replaceAll("¤µ¤²","½Ã¹Ù")
				.replaceAll("(¤©¤¡¤¤|¤ª¤¤)", "·¹°Ô³ë").replaceAll("¤·¤»","¿ÀÅ°").replaceAll("¤¡¤º","±¦Âù").replaceAll("¤±¤©", "¸ô¶ó")
				.replaceAll("(\\(|\\)|;|'|¡¾){3,}", "");
	}
	
}
