package ko.FJEY.Bot;

import java.nio.ByteBuffer;
import net.dv8tion.jda.api.audio.AudioSendHandler;


import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

public class AudioHandler implements AudioSendHandler{
	
	TTSServiceConnector t;
	AudioFrame lastFrame;

	public AudioHandler(TTSServiceConnector t) {
		this.t = t;
	}
	
	@Override
	public boolean canProvide() {
		lastFrame = t.audioPlayer.provide();
		if(lastFrame == null && t.hasNext) {
			t.hasNext = false;	
			t.autoPlayCallback();
		}
		return lastFrame != null;
	}

	@Override
	public ByteBuffer provide20MsAudio() {
		return ByteBuffer.wrap(lastFrame.getData());
	}
	
	@Override
	public boolean isOpus() {
		return true;
	}

}
