package ai;

import WebScraping.Scraper;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.chat.ChatLanguageModel;

import java.util.HashMap;

public class AnalysisTool implements AgentTool {
    private final ChatLanguageModel model;
    private final HashMap<String, Scraper.Club> clubMap;

    public AnalysisTool(HashMap<String, Scraper.Club> clubMap, ChatLanguageModel model) {
        this.clubMap = clubMap;
        this.model = model;
    }

    @Tool("Predict which team would win in a Premier League football match")
    public String compareTeams(String team1, String team2) {
        System.out.println("[Tool] compareTeams(" + team1 + ", " + team2 + ")");
        StringBuilder sb = new StringBuilder();
        if(team1 != null && team2 != null) {
            String clean1 = team1.toLowerCase().replaceAll("[^a-z]", "");
            String clean2 = team2.toLowerCase().replaceAll("[^a-z]", "");
            System.out.println("Cleans: " + clean1 + ", " + clean2);
            sb.append("The statistics of " + team1 + ": ");
            sb.append(clubMap.get(clean1).getTable());
            sb.append("The statistics of " + team2 + ": ");
            sb.append(clubMap.get(clean2).getTable());
            sb.append(" Compare the two statistics tables and give a brief summary. Give your confidence in your prediction as a percentage.");
        }
        else
            return "Cannot find club";
        return model.generate(sb.toString());
    }

    @Tool("Generate a report on the statistical performance of the given football club")
    public String generateReport(String team) {
        System.out.println("[Tool] generateReport(" + team + ")");
        StringBuilder sb = new StringBuilder();
        if(team != null) {
            String clean = team.toLowerCase().replaceAll("[^a-z]", "");
            sb.append(" Highlight the strengths and shortcomings of the team as well as what one can expect of their performance. ");
            sb.append(clubMap.get(clean).getTable());
        }
        else
            return "Cannot find club";
        return model.generate(sb.toString());
    }

    @Override
    public String getName() {
        return "Analysis and Prediction Tool";
    }

    @Override
    public String getDescription() {
        return "Analyze the statistics of one or two Premier League football clubs (create report, predict outcome) and predict an outcome";
    }
}
