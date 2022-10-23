package rBot;

import java.util.List;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class EmbedBuilders {
	
	private static final String thumbUrl = "https://img.youtube.com/vi/";
	
	public static MessageEmbed buildMetaData(AudioTrack track, String footer) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle(track.getInfo().title);
		embed.addField("Uploader", track.getInfo().author, false);
		embed.setThumbnail(thumbUrl+track.getInfo().identifier+"/0.jpg");
		embed.setFooter(footer);
		
		return embed.build();
	}
	
	public static MessageEmbed build_viewQueueMetaData(AudioTrack nowPlaying, String songList) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("NOW PLAYING");
		embed.setDescription(nowPlaying.getInfo().title);
		embed.addField("Next in line..", songList, false);
		embed.setThumbnail(thumbUrl+nowPlaying.getInfo().identifier+"/0.jpg");
		return embed.build();
	}
	
	
}
