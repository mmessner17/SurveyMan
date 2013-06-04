import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Scanner;
import java.io.File;

public class parser
{
    String regex = "\\s*\"([^\"]*)\"|\\s*(\\[\\[[^\\[\\]]*\\]\\])\\s*|\\s*\\[([^\\[\\]]*)\\]|\\s?(?<=, ?|^)([^,]*)(?:, ?|$)";
    private final Pattern csvPattern = Pattern.compile(regex);
    private ArrayList<String> allMatches = null;
    private Matcher matcher = null;
    private String match = null;
    private int size;

    public parser()
    {
        allMatches = new ArrayList<String>();
        matcher = null;
        match = null;
    }
    
    public String[] parse(String csv)
    {
        matcher = csvPattern.matcher(csv);
        allMatches.clear();
        String match;
        while (matcher.find()) {
            match = matcher.group(1);
            if (matcher.group(1)!=null)
            {
                allMatches.add(match);
            }
            else if (matcher.group(2)!=null)
            {
                allMatches.add(matcher.group(2));
            }
            
            else if (matcher.group(3)!=null)
            {
                allMatches.add(matcher.group(3));
            }
            else
            {
                allMatches.add(matcher.group(4));
            }
        }
        size = allMatches.size();
        if (size > 0)
        {
            return allMatches.toArray(new String[size]);
        }
        else
        {
            return new String[0];
        }
    }

    static String readFile(String path)
        throws java.io.FileNotFoundException
    {
        return new Scanner(new File(path)).useDelimiter("\\A").next();
    }

    static String[] split(String file)
    {
        String[] lines = file.split("\\r?\\n");
        return lines;
    }
    
    public static void main(String[] args)
    {
        webGenerator generator = new webGenerator();
        String fileContents = "";
        String[] lines;
        List<String> toQuestion = new ArrayList<String>();
        ArrayList<String[]> allQuestions = new ArrayList<String[]>();
        String[] parsedData;
        if (args.length == 0)
        {
            System.out.println("Usage: java parser.java [file path]");
            System.exit(0);
        }
        
        parser csvParser = new parser();
        try
        {
            fileContents = csvParser.readFile(args[0]);
        }
        catch (java.io.FileNotFoundException e)
        {
            System.out.println("Invalid filename");
        }
        lines = csvParser.split(fileContents);
        for (int i = 0; i < lines.length; i++)
        {
                parsedData = csvParser.parse(lines[i]);
            for (int j = 0; j < parsedData.length; j++)
            {
                System.out.print(parsedData[j]+", ");
            }
            System.out.println();
            // If the line that was just parsed is for a new question, we are done with the last question. Add the last question to allQuestions
            if (toQuestion.size() == 0 || (parsedData.length > 0 && !parsedData[1].equals(toQuestion.get(1))))
            {
                System.out.println("I am adding this question to the array: " + toQuestion);
                String[] q = toQuestion.toArray(new String[toQuestion.size()]);
                if (i != 0 && i != 1)
                {
                    allQuestions.add(q);
                }
                toQuestion = Arrays.asList(parsedData);
            }
            // If we're not done with the last question, add the option to toQuestion.
            else
            {
                if (toQuestion.size() > 3 && parsedData.length > 3)
                {
                    toQuestion.set(3, toQuestion.get(3) + "," + parsedData[3]);
                }
                else
                {
                    System.out.println("Data in wrong format. Check line " + (i + 1) + " .");
                    System.exit(0);
                }
            }
        }
        // Add last question to allQuestions
        String[] q = toQuestion.toArray(new String[toQuestion.size()]);
        allQuestions.add(q);
        generator.generateSurvey(allQuestions);
    }
}