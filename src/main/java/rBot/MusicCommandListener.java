package rBot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;

import org.apache.commons.lang3.StringUtils;

import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class MusicCommandListener extends ListenerAdapter {
	
	public static final String YT_REGEX = "((?:https?:)?\\/\\/)?((?:www|m)\\.)?((?:youtube(-nocookie)?\\.com|youtu.be))(\\/(?:[\\w\\-]+\\?v=|embed\\/|v\\/)?)([\\w\\-]+)(\\S+)?";
	
	public static final String PLAY_PREFIX = "g!play";
	public static final String VIEW_QUEUE = "viewqueue";
	public static final String STOP_PLAYING = "stop";
	public static final String LEAVE_CHANNEL = "disconnect";
	
	public static AudioPlayerManager playerManager;
	public static AudioPlayer player;
	public static AudioSendHandler audioSendHandler;
	public static LavaPlayerTrackScheduler trackScheduler;
	
	MusicCommandListener(){
		// Creates AudioPlayer instances and translates URLs to AudioTrack instances
		playerManager = new DefaultAudioPlayerManager();
		// This is an optimization strategy that Discord4J can utilize. It is not important to understand
		playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
		// Allow playerManager to parse remote sources like YouTube links
		AudioSourceManagers.registerRemoteSources(playerManager);
		// Create an AudioPlayer so Discord4J can receive audio data
		player = playerManager.createPlayer();
		// We will be creating LavaPlayerAudioProvider in the next step
		audioSendHandler = new LavaPlayerAudioProvider(player);
		trackScheduler = new LavaPlayerTrackScheduler(player);
		player.addListener(trackScheduler);
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if(event.getAuthor().isBot())
			return;
		
		if(event.getMessage().getContentRaw().toLowerCase().startsWith(PLAY_PREFIX))
			playCommand(event);
		if(event.getMessage().getContentRaw().toLowerCase().startsWith(VIEW_QUEUE))
			viewQueue(event);
		if(event.getMessage().getContentRaw().toLowerCase().startsWith(STOP_PLAYING))
			stopPlaying(event);
		if(event.getMessage().getContentRaw().toLowerCase().startsWith(LEAVE_CHANNEL))
			leaveChannel(event);
		
	}
	
	public void leaveChannel(MessageReceivedEvent event) {
		stopPlaying(event);
	
	}
	
	public void stopPlaying(MessageReceivedEvent event) {
		player.stopTrack();
	}
	
	public void viewQueue(MessageReceivedEvent event) {
		String queue = trackScheduler.getStringQueue();
		if(StringUtils.isBlank(queue))
			queue = "EMPTY";
		event.getChannel().sendMessage("In queue : "+"\n```"+queue+"\n```").queue();
	}
	
	public void playCommand(MessageReceivedEvent event) {
		MessageChannel authorTextChannel = event.getChannel();
		Member authorAsMember = event.getMember();
		AudioChannel authorVoiceChannel;
		String messageContent = event.getMessage().getContentRaw();
		String trackUrl = RegexEngine.getContentData(YT_REGEX, messageContent);
		
		if(StringUtils.isBlank(trackUrl)) {
			System.out.println("EMPTY TRACK URL");
			authorTextChannel.sendMessage("Please provide a YouTube URL.").queue();
			return;
		}
		
		if(!authorAsMember.getVoiceState().inAudioChannel()) {
			System.out.println("USER NOT IN VC");
			authorTextChannel.sendMessage("Please join a voice channel first.").queue();
			return;
		}
		
		authorVoiceChannel = authorAsMember.getVoiceState().getChannel();
		playTrack(authorVoiceChannel, trackUrl, event);
	}
	
	public void playTrack(AudioChannel channel, String URL, MessageReceivedEvent event) {
		AudioManager audioManager = event.getGuild().getAudioManager();
		audioManager.setSendingHandler(audioSendHandler);
		audioManager.openAudioConnection(channel);
		LoadResultHandlerClass audioLoadResultHandler = new LoadResultHandlerClass(event, trackScheduler);
		playerManager.loadItem(URL, audioLoadResultHandler);
	}
	

}
