package ko.FJEY.Bot;

public class Utils {
	
	static String credentialLocation = "";
	static String ttsVoiceStoarge = "";
	static String mp3Stoarge = "";
	
	public static String processHangul(String msg) {
		return msg.replaceAll("������", "������").replaceAll("����", "����").replaceAll("��{3,}","������").replaceAll("����", "����")
				.replaceAll("����", "����").replaceAll("����������", "ũũ����").replaceAll("����", "���")
				.replaceAll("����", "����").replaceAll("����", "�Ƴ�").replaceAll("����","�ù�")
				.replaceAll("(������|����)", "���Գ�").replaceAll("����","��Ű").replaceAll("����","����").replaceAll("����", "����")
				.replaceAll("(\\(|\\)|;|'|��){3,}", "");
	}
	
}
