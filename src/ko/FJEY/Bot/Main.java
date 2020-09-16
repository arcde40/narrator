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
		settingMain.setTitle("설정");
		settingMain.setColor(Color.RED);
		settingMain.addField("목소리 설정", "원하는 목소리를 밑의 버튼을 눌러 설정하세요.", false);
		settingMain.addField("목소리 목록", "A(여성), B(여성), C(남성), D(남성)", false);
		settingMain.addBlankField(false);
		settingMain.addField("높낮이 / 속도 설정", "높낮이를 조정하려면 P, 속도를 조정하려면 S를 누르세요.", false);
		settingMain.addField("","리셋하려면 R을 누르세요.",false);
		Utils.credentialLocation = args[1];
		Utils.ttsVoiceStoarge = args[2];
		Utils.mp3Stoarge = args[3];
		
		
		JDABuilder builder = new JDABuilder(AccountType.BOT);
		builder.setToken(args[0]);
		builder.setAutoReconnect(true);
		builder.setStatus(OnlineStatus.ONLINE);
		builder.setActivity(Activity.playing("!내래이터"));
		builder.addEventListeners(new Listener());
		
		JDA jda = builder.build();
	}
}
