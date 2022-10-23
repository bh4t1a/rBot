package rBot;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;

import net.dv8tion.jda.api.audio.AudioSendHandler;

public class LavaPlayerAudioProvider implements AudioSendHandler {
	private final AudioPlayer player;
	private final ByteBuffer byteBuffer;
	private final MutableAudioFrame frame;

	public LavaPlayerAudioProvider(AudioPlayer player) {
		// TODO Auto-generated constructor stub
		this.player=player;
		this.byteBuffer=ByteBuffer.allocate(StandardAudioDataFormats.DISCORD_OPUS.maximumChunkSize());
		this.frame = new MutableAudioFrame();
		this.frame.setBuffer(byteBuffer);
	}

	@Override
	public boolean canProvide() {
		// TODO Auto-generated method stub
		return this.player.provide(this.frame);
	}

	@Override
	public ByteBuffer provide20MsAudio() {
		final Buffer buffer = ((Buffer) this.byteBuffer).flip();
		return (ByteBuffer) buffer;
	}
	
	@Override
	public boolean isOpus() {
		return true;
	}

}
