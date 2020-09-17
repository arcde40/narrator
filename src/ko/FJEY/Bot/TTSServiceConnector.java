package ko.FJEY.Bot;

import java.awt.geom.CubicCurve2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.api.entities.Message;

public class TTSServiceConnector extends AudioEventAdapter implements AudioEventListener{
	final String clientId = "";
	final String clientSecret = "";
	
	DefaultAudioPlayerManager playerManager;
	AudioPlayer audioPlayer;
	LinkedList<AudioFile> queue = new LinkedList<>();
	TTSServiceConnector audioListener;
	boolean hasNext = false;
	
	public TTSServiceConnector() {
		setAudioPlayer();
	}
	
	public void speech(String s) {
		speech(s, "ko-KR-Standard-A", "ko-KR", 0, 1.15);
	}
	
	public void speech(String s, String voice_id, String lang, double pitch, double speed) {
		File t = getSpeech(s, voice_id, lang, speed, pitch);
		if(t == null) {
			System.out.println("Error - getSpeech() returned a null file.");
			return;
		}
		play(t);
	}
	
	public void queue(AudioFile track) {
		queue.add(track);
	}

	public void play(File f) {
		playerManager.loadItem(f.getAbsolutePath(), new AudioLoadResultHandler() {
				@Override
				public void trackLoaded(AudioTrack track) {
					
					audioListener.queue(new AudioFile(f, track));
					if(audioPlayer.getPlayingTrack() == null) {
						audioPlayer.playTrack(queue.pop().getAudioTrack());
					}
					hasNext = true;
					System.gc();
				}
				
				@Override
				public void playlistLoaded(AudioPlaylist playlist) {
					// TODO Auto-generated method stub
					// Nope, this can't happen XD
					
				}
				
				@Override
				public void noMatches() {
					System.out.println("No matches.");
				}
				
				@Override
				public void loadFailed(FriendlyException exception) {
					exception.printStackTrace();
				}
			});
	}
	
	public File getSpeech(String s, String voice_id, String lang, double speed, double pitch) {
		GoogleCredentials credentials;
		try {
			credentials = GoogleCredentials.fromStream(new FileInputStream(Utils.credentialLocation))
			        .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
			Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		  

		try(TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            SynthesisInput input = SynthesisInput.newBuilder().setText(s).build();
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder().setLanguageCode(lang).setSsmlGender(SsmlVoiceGender.NEUTRAL).setName(voice_id).build();
            AudioConfig audioConfig = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.MP3).setPitch(pitch).setSpeakingRate(speed).build();
            SynthesizeSpeechResponse responce = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);
            ByteString audioContents = responce.getAudioContent();
            File f = new File(Utils.ttsVoiceStoarge+audioContents.hashCode()+".mp3");
            f.createNewFile();	
            try (OutputStream out = new FileOutputStream(f)) {
                out.write(audioContents.toByteArray());
                System.out.println("Audio content written to file " + f.getName());
                return f;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		return null;
	}
	
	public void setAudioPlayer() {
		DefaultAudioPlayerManager playerManager = new DefaultAudioPlayerManager();
		this.playerManager = playerManager;
		AudioSourceManagers.registerLocalSource(playerManager);
		AudioPlayer player = playerManager.createPlayer();
		this.audioPlayer = player;
		TTSServiceConnector audioListener = this;
		this.audioListener = audioListener;
		player.addListener(audioListener);
		
	}
	
	public void autoPlayCallback() {
		System.out.println("Track Ended.");
	    if(!queue.isEmpty()) {
	    	AudioFile af = queue.pop();
	    	if(af.getFile().getParent().contains("tts") && !af.getFile().delete()) System.out.println(af.getFile().getName() + "- Failed to delete");
	    	audioPlayer.playTrack(queue.peek().getAudioTrack());
	    }
	}
	
}

class PersonalSetting{
	
	int CURRENT_STEP;
	String VOICE_ID;
	double PITCH, SPEED;
	Message pendingMessage;
	
	public PersonalSetting(Message m) {
		this.pendingMessage = m;
		CURRENT_STEP = 0;
		VOICE_ID = "ko-KR-Standard-A";
		PITCH = 1.0;
		SPEED = 1.0;
	}
	
	public void updateVoiceID(int i) {
		switch(i) {
		case 0: VOICE_ID = "ko-KR-Standard-A"; break;
		case 1: VOICE_ID = "ko-KR-Standard-B"; break;
		case 2: VOICE_ID = "ko-KR-Standard-C"; break;
		case 3: VOICE_ID = "ko-KR-Standard-D"; break;
		}
	}
}

class AudioFile{
	File f;
	AudioTrack t;

	public AudioFile(File f, AudioTrack t) {
		this.f = f;
		this.t = t;
	}
	
	public File getFile() {
		return f;
	}
	
	public AudioTrack getAudioTrack() {
		return t;
	}
}
