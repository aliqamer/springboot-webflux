package com.example.demo.common.logging;

import com.fasterxml.jackson.core.JsonStreamContext;
import net.logstash.logback.mask.ValueMasker;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@Component
public class MaskingPatternLayout implements ValueMasker {

    private static final String CONFIG_FILE = "/maskPatternLine.txt";

    private Pattern regexPattern;

    public MaskingPatternLayout(){
        try {
            InputStream inputStream = getClass().getResourceAsStream(CONFIG_FILE);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String eachStringLine;

            while((eachStringLine = bufferedReader.readLine()) != null) {
                sb.append(eachStringLine).append("|");
            }
            String textDataFromFile = sb.toString();
            this.regexPattern = Pattern.compile(textDataFromFile, Pattern.MULTILINE);

        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    protected String maskMessage(String msg) {
        if(regexPattern == null) {
            return msg;
        }
        StringBuilder sb = new StringBuilder(msg);
        Matcher matcher = regexPattern.matcher(sb);

        while (matcher.find()) {
            IntStream.rangeClosed(1, matcher.groupCount()).forEach(group -> {
                if(matcher.group(group) != null) {
                    IntStream.range(matcher.start(group), matcher.end(group))
                            .forEach(i -> sb.setCharAt(i, '*'));
                }
            });
        }
        return sb.toString();
    }

    @Override
    public Object mask(JsonStreamContext context, Object value) {
        if(value instanceof CharSequence) {
            return maskMessage((String)value);
        }
        return value;
    }
}
