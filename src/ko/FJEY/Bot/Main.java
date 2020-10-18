package ko.FJEY.Bot;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.InputMismatchException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Main {
	
	static EmbedBuilder settingMain = new EmbedBuilder();
	static final String fnameQuota = ".quota";
	static int quota = 0;
	static int maxQuota;
	static int prevQuota = 0;
	static boolean overloaded = false;
	static ListenerAdapter l;
	static File q = new File(fnameQuota);
	static File log = new File("log.log");
	static BufferedWriter bw;
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

		ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
		exec.scheduleAtFixedRate(new QuotaIOManager(), 1, 1, TimeUnit.MINUTES);
		
		SignatureVoice.ready();
	
		JDABuilder builder = new JDABuilder(AccountType.BOT);
		builder.setToken(args[0]);
		builder.setAutoReconnect(true);
		builder.setStatus(OnlineStatus.ONLINE);
		builder.setActivity(Activity.playing("!내래이터"));
		builder.addEventListeners(l=new Listener());
		
		maxQuota = args.length > 4 ? Integer.parseInt(args[4]) : 100000;
		
		JDA jda = builder.build();
	}
	
	public static int addQuota(int count) {
		System.out.printf("Quota - %d/%d\n", quota+=count, maxQuota);
		return quota;
	}
}

class QuotaIOManager implements Runnable{
	
	@Override
	public void run() {
		try {
			File f = new File(Main.fnameQuota);
			int t;
			if(f.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(f));
				try {
				t = Integer.parseInt(br.readLine());
				}catch(InputMismatchException e) {t = 0;}
				if(t != Main.prevQuota) {
					if(Main.prevQuota > 0) Main.quota -= Main.prevQuota - t;
					System.out.println("Quota Changed; Did it happened manually? If not, it is bug.");
					System.out.println("prevQuota = " + Main.prevQuota + " t = " + t);
				}
				f.delete();
			}else {
				t = 0;
			}
			f.createNewFile();
			BufferedWriter bw = new BufferedWriter(new PrintWriter(f));
			bw.write(String.valueOf(Main.quota));
			bw.flush();
			bw.close();
			System.out.println("Quota Saved.");
			Main.prevQuota = Main.quota;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
