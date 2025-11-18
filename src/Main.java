import WebScraping.Scraper;
import ai.Agent;
import ai.AnalysisTool;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.util.HashMap;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void runAgent(HashMap<String, Scraper.Club> clubs) {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName("gpt-4o-mini")
                .temperature(0.2)
                .build();

        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(20);

        Agent agent = AiServices.builder(Agent.class)
                .chatLanguageModel(model)
                .tools(new AnalysisTool(clubs, model))
                .chatMemory(chatMemory)
                .build();

        System.out.print("Dialog open\n");
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.print("> ");
                String input = sc.nextLine();
                if ("exit".equalsIgnoreCase(input.trim())) break;
                String reply = agent.chat(input);
                System.out.println(reply);
            }
        }
    }

    public static HashMap<String, Scraper.Club> cleanClubMap(HashMap<String, Scraper.Club> clubMap){
        HashMap<String, Scraper.Club> newClubMap = new HashMap<>();
        clubMap.forEach((name, club) -> {
            newClubMap.computeIfAbsent(name.toLowerCase().replaceAll(" ", ""), p -> club);
        });
        return newClubMap;
    }

    public static void main(String[] args) {
        WebDriver driver = new SafariDriver();
        Scraper scraper = new Scraper(driver);

        HashMap<String, Scraper.Club> clubs = scraper.scrapeForTeams();
        //System.out.println(clubs);
        clubs.forEach((name, club) -> club.setTable(scraper.scrapeForTeamStats(club.getUrl())));

        driver.close();

        clubs = cleanClubMap(clubs);
        //System.out.println(clubs);
        runAgent(clubs);
    }
}