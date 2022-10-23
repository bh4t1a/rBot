package rBot;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexEngine {
	public static Matcher getMatches(String regex, String content) {
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		return pattern.matcher(content);
	}
	
	public static String getContentData(String regex, String content) {
		return getGroupData(regex,content,0);
	}
	
	public static String getGroupData(String regex, String content, int grpNum) {
		String data = Main.EMPTY_STRING;
		Matcher dataMatcher = getMatches(regex, content);
		if(dataMatcher.find()) {
			data = dataMatcher.group(grpNum);
		}
		
		return data;
	}
}
