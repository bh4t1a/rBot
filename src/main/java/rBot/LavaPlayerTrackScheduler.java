package rBot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public class LavaPlayerTrackScheduler extends AudioEventAdapter {

	private final BlockingQueue<AudioTrack> queue;
	private final AudioPlayer player;
	
	LavaPlayerTrackScheduler(AudioPlayer player){
		this.queue = new LinkedBlockingQueue<AudioTrack>();
		this.player = player;
	}
	
	public void nextTrack() {
		this.player.startTrack(queue.poll(), false);
	}
	
	public void addToQueue(AudioTrack track) {
		System.out.println("IN ADD TO QUEUE");
		boolean isAnotherTrackPlaying = !this.player.startTrack(track, true);
		if(isAnotherTrackPlaying){
			queue.offer(track);
		}else {
			this.player.playTrack(track);
		}
	}
	
	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if(endReason.mayStartNext) {
			nextTrack();
		}
	}
	
	public boolean isQueueEmpty() {
		return queue.isEmpty();
	}
	
	public String getQueueAsString() {
		String strQueue = Main.EMPTY_STRING;
		int index = 1;
		for(AudioTrack track : queue) {
			strQueue = strQueue + "\n" + index + ". " + track.getInfo().title;
			index++;
		}
		return strQueue;
	}
}
