package rBot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;

import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;

import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class MusicCommandListener extends ListenerAdapter {
	
	public static final String YT_REGEX = "((?:https?:)?\\/\\/)?((?:www|m)\\.)?((?:youtube(-nocookie)?\\.com|youtu.be))(\\/(?:[\\w\\-]+\\?v=|embed\\/|v\\/)?)([\\w\\-]+)(\\S+)?";
	public static final String URLRegex = "[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*)";
	
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
		//implement pause and next
		
	}
	
	public void leaveChannel(MessageReceivedEvent event) {
		stopPlaying(event);
		AudioManager audioManager = event.getGuild().getAudioManager();
		audioManager.closeAudioConnection();
	}
	
	public void stopPlaying(MessageReceivedEvent event) {
		if(!isPlaying())
			return;
		player.stopTrack();
	}
	
	public boolean isPlaying() {
		if(player.getPlayingTrack()==null) {
			System.out.println("NO TRACK PLAYING ");
			return false;
		}
		return true;
	}
	
	public void viewQueue(MessageReceivedEvent event) {
		if(!isPlaying())
			return;
		else {
			if(trackScheduler.isQueueEmpty()) {
				event.getChannel().sendMessageEmbeds(EmbedBuilders.buildMetaData(player.getPlayingTrack(), "Now playing..")).queue();
				return;
			}
			
		}
		String songList = trackScheduler.getQueueAsString();
		event.getChannel().sendMessageEmbeds(EmbedBuilders.build_viewQueueMetaData(player.getPlayingTrack(), songList)).queue();
	}
	
	public void playCommand(MessageReceivedEvent event) {
		MessageChannel authorTextChannel = event.getChannel();
		Member authorAsMember = event.getMember();
		AudioChannel authorVoiceChannel;
		String messageContent = event.getMessage().getContentRaw();
		String trackUrl = getUrl(messageContent);
		
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
	
	public static boolean isUrl(String URL) {
		Matcher matches = RegexEngine.getMatches(URLRegex, URL);
		if(matches.find()) {
			return true;
		}
		return false;
	}
	
	public static String getUrl(String messageContent) {
		String identifier = Main.EMPTY_STRING;
		if(isUrl(messageContent)) {
			if(RegexEngine.getMatches(YT_REGEX, messageContent).find()) {
				identifier = RegexEngine.getContentData(YT_REGEX, messageContent);
			}
		}else {
			String searchTerm = RegexEngine.getGroupData("^(?:g!play)(.*)", messageContent, 1);
			identifier = "ytsearch:" + searchTerm;
		}
		return identifier;
	}
	

}
