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
	static int quota = 0;
	static int maxQuota;
	static boolean overloaded = false;
	static ListenerAdapter l;
	static File q = new File(".quota");
	static File log = new File("log.log");
	static BufferedWriter bw;
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
		
		try {
			if(!q.exists()) q.createNewFile();
			BufferedReader bf = new BufferedReader(new FileReader(q));
			quota = Integer.parseInt(bf.readLine());
			bw = new BufferedWriter(new PrintWriter(q));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} catch (InputMismatchException e) {
			e.printStackTrace();
			return;
		}
		
		ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
		exec.scheduleAtFixedRate(new QuotaIOManager(bw, q), 0, 1, TimeUnit.MINUTES);
	
		JDABuilder builder = new JDABuilder(AccountType.BOT);
		builder.setToken(args[0]);
		builder.setAutoReconnect(true);
		builder.setStatus(OnlineStatus.ONLINE);
		builder.setActivity(Activity.playing("!��������"));
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

	BufferedWriter bw;
	File q;
	
	public QuotaIOManager(BufferedWriter bw, File q) {
		this.bw = bw;
		this.q = q;
	}
	
	@Override
	public void run() {
		try {
			bw.write(Main.quota);
			System.out.println("Quota Saved.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
