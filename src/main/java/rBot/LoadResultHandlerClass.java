package rBot;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class LoadResultHandlerClass implements AudioLoadResultHandler {
	
	private MessageReceivedEvent event;
	private LavaPlayerTrackScheduler trackSchedulerInstance;
	private static final String thumbUrl = "https://img.youtube.com/vi/";
	
	LoadResultHandlerClass(MessageReceivedEvent event, LavaPlayerTrackScheduler trackSchedulerInstance){
		this.event=event;
		this.trackSchedulerInstance = trackSchedulerInstance;	
	}
	
	public static MessageEmbed buildMetaData(AudioTrack track) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle(track.getInfo().title);
		embed.addField("Uploader", track.getInfo().author, false);
		embed.setThumbnail(thumbUrl+track.getInfo().identifier+"/0.jpg");
		embed.setFooter("Added to queue!");
		
		return embed.build();
	}

	@Override
	public void trackLoaded(AudioTrack track) {
		trackSchedulerInstance.addToQueue(track);
		event.getChannel().sendMessageEmbeds(buildMetaData(track)).queue();
	}

	@Override
	public void playlistLoaded(AudioPlaylist playlist) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void noMatches() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadFailed(FriendlyException exception) {
		// TODO Auto-generated method stub
		
	}

}
