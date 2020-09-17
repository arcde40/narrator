package ko.FJEY.Bot;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class Listener extends ListenerAdapter {	
	
	HashMap<Guild, AudioHandler> handlerMap = new HashMap<Guild, AudioHandler>();
	ArrayList<String> member = new ArrayList<>();
	HashMap<String, PersonalSetting> setting = new HashMap<>();
	HashMap<String, String> mIdTable = new HashMap<>();
	boolean notifyUserInOut = false;

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		User user = event.getAuthor();
		TextChannel channel = event.getTextChannel();
		Message msg = event.getMessage();
		if(event.isFromGuild()) return;
		if(user.isBot()) return;
		if(msg.getContentRaw().equalsIgnoreCase("들어와")) {
			channel.sendMessage("저는 서버에서 부른 것만 가요").queue();
		}
		
		
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		User user = event.getAuthor();
		TextChannel channel = event.getChannel();
		Message msg = event.getMessage();
		Guild guild = event.getGuild();
		
		if(user.isBot()) return;
		if(getSetting(user).CURRENT_STEP > 0) {
			String arg;
			Message m;
			if((m = getSetting(user).pendingMessage) != null) m.delete().queue();
			switch(getSetting(user).CURRENT_STEP) {
			case 1:
				arg = msg.getContentRaw();
				try
				{
					Double d = Double.parseDouble(arg);
					if(d > 20.0) d = 20.0;
					else if(d < -20.0) d = -20.0;
					getSetting(user).CURRENT_STEP = 0;
					getSetting(user).PITCH = d;
					channel.sendMessage("변경 완료!").queue(message -> {
						msg.delete().queue();
						getSetting(user).pendingMessage.delete().queue(a -> {
							getSetting(user).pendingMessage = null;
						});
						message.delete().queueAfter(10, TimeUnit.SECONDS);
					});
				}catch(NumberFormatException e) {
					channel.sendMessage("올바르지 않은 형식입니다.").queue(message->{
						message.delete().queueAfter(10, TimeUnit.SECONDS);
					});
				}
				break;
			case 2:
				arg = msg.getContentRaw();
				try
				{
					Double d = Double.parseDouble(arg);
					if(d > 4.0) d = 4.0;
					else if(d <  0.25) d = 0.25;
					getSetting(user).CURRENT_STEP = 0;
					getSetting(user).SPEED = d;
					channel.sendMessage("변경 완료!").queue(message -> {
						msg.delete().queue();
						getSetting(user).pendingMessage.delete().queue(a -> {
							getSetting(user).pendingMessage = null;
						});
						message.delete().queueAfter(10, TimeUnit.SECONDS);
					});
				}catch(NumberFormatException e) {
					channel.sendMessage("올바르지 않은 형식입니다.").queue(message->{
						message.delete().queueAfter(10, TimeUnit.SECONDS);
					});
				}
				break;
			}
		}
		if(msg.getContentRaw().matches("!(내|나)(래|레)이(터|타)")){
			if(handlerMap.containsKey(guild)) {
				if(member.contains(user.getId())) channel.sendMessage("네~ 듣고 있어요!").queue(message->{
					message.delete().queueAfter(10, TimeUnit.SECONDS);
				});
				else {member.add(user.getId()); channel.sendMessage("네~ 말씀하세요!").queue(message->{
					message.delete().queueAfter(10, TimeUnit.SECONDS);
				});}
				return;
			}
			channel.sendMessage("네! 들어갈게요~").queue(message->{
				message.delete().queueAfter(10, TimeUnit.SECONDS);
			});
			joinVoiceChannel(guild, event.getMember(), channel);
			member.add(user.getId());
		}else if(msg.getContentRaw().contains("!그만")) {
			if(member.contains(user.getId())) {
				member.remove(user.getId());
				channel.sendMessage("알겠습니다!").queue(message->{
					message.delete().queueAfter(10, TimeUnit.SECONDS);
				});
				if(member.isEmpty()) {
					guild.getAudioManager().closeAudioConnection();
					handlerMap.remove(guild);
				}
			}
			
		}else if(msg.getContentRaw().contains("!설정")) {
			if(!msg.getContentRaw().equals("!설정")) {
				String[] args = msg.getContentRaw().split(" ");
				if(args.length < 4) channel.sendMessage("사용법: !설정 (A/B/C/D) (높낮이) (속도)").queue(message -> message.delete().queueAfter(5, TimeUnit.SECONDS));
				else {
					PersonalSetting p = getSetting(user);
					if(!args[1].matches("[a-dA-D]")) channel.sendMessage(args[1] + " : 존재하지 않는 유형입니다.").queue(message -> message.delete().queueAfter(5, TimeUnit.SECONDS));
					p.updateVoiceID((int)(args[1].toLowerCase().charAt(0)-'a'));
					try {
					p.PITCH = Double.parseDouble(args[2]);
					p.SPEED = Double.parseDouble(args[3]);
					channel.sendMessage("변경 완료!").queue(message -> {
						message.delete().queueAfter(10, TimeUnit.SECONDS);
					});
					}catch(NumberFormatException e) {
						channel.sendMessage("올바르지 않은 형식입니다.").queue(message -> message.delete().queueAfter(5, TimeUnit.SECONDS));
					}
				}
				return;
			}
			if(!setting.containsKey(user.getId())) setting.put(user.getId(), new PersonalSetting(null));
			channel.sendMessage(Main.settingMain.build()).queue(message -> {
				message.addReaction("U+1F1E6").queue();
				message.addReaction("U+1F1E7").queue();
				message.addReaction("U+1F1E8").queue();
				message.addReaction("U+1F1E9").queue();	
				message.addReaction("U+1F1F5").queue();
				message.addReaction("U+1F1F8").queue();
				message.addReaction("U+1F1F7").queue();
				if(getSetting(user).pendingMessage != null) {
					mIdTable.remove(getSetting(user).pendingMessage.getId());
					getSetting(user).pendingMessage.delete().queue();
				}
				setting.get(user.getId()).pendingMessage = message;
				mIdTable.put(message.getId(), user.getId());
			});
		}else if(msg.getContentRaw().contains("!들낙알림")) {
			if(event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
				notifyUserInOut = !notifyUserInOut;
				if(notifyUserInOut) channel.sendMessage("음성 채널 접속 알림을 켰습니다.").queue(message->{
					message.delete().queueAfter(10, TimeUnit.SECONDS);
				});
				else channel.sendMessage("음성 채널 접속 알림을 껐습니다.").queue(message->{
					message.delete().queueAfter(10, TimeUnit.SECONDS);
				});
			}else return;
		}else if(msg.getContentRaw().contains("!재생")){
			String dest = msg.getContentRaw().substring(4);
			try {
				
				msg.delete().queue(a -> {
					File f = new File(Utils.mp3Stoarge+dest+".mp3");
					channel.sendMessage(dest + ".mp3를 대기열에 추가했습니다.").queue(message->{
						message.delete().queueAfter(10, TimeUnit.SECONDS);
					});
					try{handlerMap.get(guild).t.play(f);}catch(Exception e) {e.printStackTrace(); System.out.println("Play Error encountered.");}
				});
			}catch(Exception e) {e.printStackTrace();}
		}else if(msg.getContentRaw().contains("!스킵")) {
			if(event.getMember().hasPermission(Permission.VOICE_MUTE_OTHERS)) {
				AudioPlayer ap = handlerMap.get(guild).t.audioPlayer;
				try {
					ap.stopTrack();
					handlerMap.get(guild).t.autoPlayCallback();
					channel.sendMessage("스킵 완료!").queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
				}catch(Exception e) {
					e.printStackTrace();
				}
				
			}else {
				channel.sendMessage("뮤트 권한이 있는 사람만 스킵이 가능합니다!").queue(message ->{
					message.delete().queueAfter(10, TimeUnit.SECONDS);
				});
			}
		}
		else if(!msg.getContentRaw().startsWith("!") && !msg.getContentRaw().contains("https://") && !msg.getContentRaw().contains("http://")){
			if(handlerMap.containsKey(event.getGuild())) {
				if(member.contains(user.getId())) {
					if(msg.getContentRaw().matches("(.|\\n)*([\\u3000-\\u303f\\u3040-\\u309f\\u30a0-\\u30ff\\uff00-\\uffef\\u4e00-\\u9faf])+(.|\\n)*")) {
						handlerMap.get(guild).t.speech(msg.getContentDisplay(), "", "ja-JP", getSetting(user).PITCH, getSetting(user).SPEED);
					}
					else handlerMap.get(guild).t.speech(Utils.processHangul(msg.getContentDisplay()), getSetting(user).VOICE_ID, "ko-KR", getSetting(user).PITCH, getSetting(user).SPEED);
				}
			}
		}
	}
	
	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
		if(notifyUserInOut) handlerMap.get(event.getGuild()).t.speech(event.getMember().getNickname() + "님이 음성 채팅에 접속하셨습니다.");
	}
	
	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
		Member m = event.getMember();
		if(member.contains(m.getId())) member.remove(m.getId());
		if(notifyUserInOut) handlerMap.get(event.getGuild()).t.speech(event.getMember().getNickname() + "님이 음성 채팅에서 퇴장하셨습니다.");

	}
	
	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
		User u = event.getUser();
		if(u.isBot()) return;
		TextChannel c = event.getChannel();
		if(getSetting(u).pendingMessage.getId().equalsIgnoreCase(event.getMessageId())) {
			switch(event.getReactionEmote().getName()) {
			case "🇦":
			case "U+1F1E6": getSetting(u).updateVoiceID(0); break;
			case "🇧":
			case "U+1F1E7": getSetting(u).updateVoiceID(1); break;
			case "🇨":
			case "U+1F1E8": getSetting(u).updateVoiceID(2); break;
			case "🇩":
			case "U+1F1E9": getSetting(u).updateVoiceID(3); break;
			case "🇵": pitchSetting(u, c); return;
			case "🇸": speedSetting(u ,c); return;
			case "🇷": c.sendMessage("리셋 완료!"); setting.put(u.getId(), new PersonalSetting(null)); c.deleteMessageById(event.getMessageId()).queue(a -> {
				getSetting(u).pendingMessage = null;
			});
			default: System.out.println("Locale Change Failed - " + event.getReactionEmote().getName());
			}
		}
		c.sendMessage("변경 완료!").queue();
		c.deleteMessageById(event.getMessageId()).queue(a -> {
			getSetting(u).pendingMessage = null;
		});
		
	}
	
	
	public void joinVoiceChannel(Guild g, Member m, TextChannel tChannel) {
		if(!m.getVoiceState().inVoiceChannel()) {
			tChannel.sendMessage("님은 음성채팅방에 없는 것 같은데요?");
			return;
		}
		VoiceChannel vChannel = m.getVoiceState().getChannel();
		AudioManager manager = g.getAudioManager();
		AudioHandler a;
		if(!handlerMap.containsKey(g)) {
			a = new AudioHandler(new TTSServiceConnector());
			handlerMap.put(g, a);
		}else a = handlerMap.get(g);
		manager.setSendingHandler(a);
		manager.openAudioConnection(vChannel);
	}
	
	public PersonalSetting getSetting(User u) {
		PersonalSetting p;
		if((p=setting.get(u.getId()))==null) setting.put(u.getId(), (p=new PersonalSetting(null)));
		return p;
		
	}
	
	public void pitchSetting(User u, TextChannel channel) {
		if(getSetting(u).CURRENT_STEP == 0) {
			getSetting(u).CURRENT_STEP = 1;
			getSetting(u).pendingMessage.delete().queue();
			channel.sendMessage("목소리의 높낮이를 설정해주세요. 범위는 -20.00~20.00 입니다. (소수점 가능)").queue(message -> {
				getSetting(u).pendingMessage = message;
			});
			return;
		}
	}
	
	public void speedSetting(User u, TextChannel channel) {
		if(getSetting(u).CURRENT_STEP == 0) {
			getSetting(u).CURRENT_STEP = 2;
			getSetting(u).pendingMessage.delete().queue();
			channel.sendMessage("목소리의 빠르기를 설정해주세요. 범위는 0.25~4.00 입니다. (소수점 가능)").queue(message -> {
				getSetting(u).pendingMessage = message;
			});
			return;
		}
	}
}
