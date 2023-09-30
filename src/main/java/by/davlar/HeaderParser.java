package by.davlar;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HeaderParser {
    private String source;
    private Map<String, String> headers = new HashMap<>();

    public HeaderParser(String source) {
        this.source = source;
        parse();
    }

    private void parse() {
        List<String> lines = source.lines().toList();
        if (lines.size() == 0) {
            return;
        }
        String firstLine = lines.get(0);
        String[] strings = firstLine.split("\\s+(/\\s*)?");
        headers.put("web-type", strings[0]);
        headers.put("http-type", strings[1]);
        for (int i = 1; i < lines.size(); i++) {
            strings = lines.get(i).split("\\s+");
            headers.put(
                    strings[0].substring(0, strings[0].length() - 1).toLowerCase(),
                    strings[1]
            );
        }
    }

    public String findValue(String key) {
        return headers.get(key);
    }
}
