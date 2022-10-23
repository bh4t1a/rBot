package rBot;

import java.util.List;

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
	
	LoadResultHandlerClass(MessageReceivedEvent event, LavaPlayerTrackScheduler trackSchedulerInstance){
		this.event=event;
		this.trackSchedulerInstance = trackSchedulerInstance;	
	}

	@Override
	public void trackLoaded(AudioTrack track) {
		event.getChannel().sendMessageEmbeds(EmbedBuilders.buildMetaData(track, "Adding to queue!")).queue();
		trackSchedulerInstance.addToQueue(track);
	}

	@Override
	public void playlistLoaded(AudioPlaylist playlist) {
		List<AudioTrack> tracks = playlist.getTracks();
		// for loading all the matches
//		for(AudioTrack track : tracks) {
//			event.getChannel().sendMessageEmbeds(EmbedBuilders.buildMetaData(track, "Adding to queue!")).queue();
//			trackSchedulerInstance.addToQueue(track);
//		}
		// load only first result
		event.getChannel().sendMessageEmbeds(EmbedBuilders.buildMetaData(tracks.get(0), "Adding to queue!")).queue();
		trackSchedulerInstance.addToQueue(tracks.get(0));
		
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
