package rBot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class Main {
	
	private static final String TOKEN_FILE="C:\\Users\\Rishabh Suman\\Documents\\jbot";
	public static final String EMPTY_STRING = "";
	public static JDA bot;
	
	public static void main(String[] args) {
		
		String token = getToken();
		try {
			bot = JDABuilder.createDefault(token)
					.enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_VOICE_STATES)
					.addEventListeners(new GaeListener(), new MusicCommandListener())
					.setActivity(Activity.watching("gaE PoRN"))
					.setMemberCachePolicy(MemberCachePolicy.VOICE)
					.build()
					.awaitReady();
			
			System.out.println("BOT STARTED");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static List<String> messageDbBuilder(String dbChannelId) {
		List<String> strMessageList = new ArrayList<String>();
		MessageChannel dbChannel = bot.getTextChannelById(dbChannelId);
		MessageHistory dbChannelHistory = MessageHistory.getHistoryFromBeginning(dbChannel).complete();
		List<Message> messageList = dbChannelHistory.getRetrievedHistory();
		for(Message message : messageList) {
			strMessageList.add(message.getContentRaw());
		}
		
		return strMessageList;
	}
	
	private static String getToken() {
		String encodedString=EMPTY_STRING;
		try {
			encodedString = new String(Files.readAllBytes(Paths.get(TOKEN_FILE+".conf")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
		return new String(decodedBytes);
	}

}
