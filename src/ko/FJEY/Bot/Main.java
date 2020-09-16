package ko.FJEY.Bot;

import java.awt.Color;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

public class Main {
	
	static EmbedBuilder settingMain = new EmbedBuilder();
	
	public static void main(String args[]) throws LoginException {
		settingMain.setTitle("����");
		settingMain.setColor(Color.RED);
		settingMain.addField("��Ҹ� ����", "���ϴ� ��Ҹ��� ���� ��ư�� ���� �����ϼ���.", false);
		settingMain.addField("��Ҹ� ���", "A(����), B(����), C(����), D(����)", false);
		settingMain.addBlankField(false);
		settingMain.addField("������ / �ӵ� ����", "�����̸� �����Ϸ��� P, �ӵ��� �����Ϸ��� S�� ��������.", false);
		settingMain.addField("","�����Ϸ��� R�� ��������.",false);
		Utils.credentialLocation = args[1];
		Utils.ttsVoiceStoarge = args[2];
		Utils.mp3Stoarge = args[3];
		
		
		JDABuilder builder = new JDABuilder(AccountType.BOT);
		builder.setToken(args[0]);
		builder.setAutoReconnect(true);
		builder.setStatus(OnlineStatus.ONLINE);
		builder.setActivity(Activity.playing("!��������"));
		builder.addEventListeners(new Listener());
		
		JDA jda = builder.build();
	}
}
