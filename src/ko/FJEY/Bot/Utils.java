package ko.FJEY.Bot;

public class Utils {
	
	static String credentialLocation = "";
	static String ttsVoiceStoarge = "";
	static String mp3Stoarge = "";
	
	public static String processHangul(String msg) {
		return msg.replaceAll("ㅇㅈ", "인정").replaceAll("ㅋ{3,}","ㅋㅋㅋ").replaceAll("ㅅㄱ", "수고")
				.replaceAll("ㄹㅇ", "레알").replaceAll("ㅋㅋㄹㅃㅃ", "크크루삥뽕").replaceAll("ㄱㄷ", "기달")
				.replaceAll("ㄷㄷ", "덜덜").replaceAll("ㅇㄴ", "아놔").replaceAll("ㅅㅂ","시바")
				.replaceAll("(ㄹㄱㄴ|ㄺㄴ)", "레게노").replaceAll("ㅇㅋ","오키").replaceAll("ㄱㅊ","괜찬").replaceAll("ㅁㄹ", "몰라");
	}
	
}
