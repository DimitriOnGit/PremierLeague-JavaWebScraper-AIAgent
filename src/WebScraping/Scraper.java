package WebScraping;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scraper {
    private final WebDriver driver;

    public Scraper(WebDriver d){
        this.driver = d;
    }

    public StatTable scrapeForTeamStats(String url) {
        //StringBuilder sb = new StringBuilder();
        //WebDriver driver = new SafariDriver();
        StatTable stats = new StatTable();
        try {
            if (!url.endsWith("stats")) {
                Pattern p = Pattern.compile("(.*)/(.*)");
                Matcher m = p.matcher(url);
                if (m.find()) {
                    //System.out.println("1: " + m.group(1) + "2: " + m.group(2));
                    url = m.group(1) + "/stats";
                }
                //System.out.println(url);
                //return stats;
            }

            driver.get(url);

            Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(d -> driver.findElement(By.cssSelector("#main-content > div.page-wrapper > div:nth-child(2) > div.col-8.col-8-wide.col-6-tab > section > section")).isDisplayed()
                    && driver.findElement(By.cssSelector("#main-content > div.page-wrapper > div:nth-child(2) > div.col-4.col-4-wide.col-6-tab > section > div")).isDisplayed()
                    && driver.findElement(By.cssSelector("#main-content > div.page-wrapper > div:nth-child(2) > div.col-4.col-4-wide.col-6-tab > section > div > div > header > div.club-profile-header__bottom > h1")).isDisplayed()
                    && driver.findElement(By.cssSelector("#main-content > div.page-wrapper > div:nth-child(2) > div.col-8.col-8-wide.col-6-tab > section > section > div > div > div > div.club-profile__panel.club-profile__panel--stats > div.club-profile__stats > section.profile-stat-cards-container")).isDisplayed()
                    && driver.findElement(By.cssSelector("#main-content > div.page-wrapper > div:nth-child(2) > div.col-8.col-8-wide.col-6-tab > section > section > div > div > div > div.club-profile__panel.club-profile__panel--stats > div.club-profile__stats > section.profile-stat-lists-container")).isDisplayed());

            //stats.setName(driver.findElement(By.cssSelector("#main-content > div.page-wrapper > div:nth-child(2) > div.col-4.col-4-wide.col-6-tab > section > div > div > header > div.club-profile-header__bottom > h1")).getText());

            List<WebElement> cards = driver.findElements(By.cssSelector("#main-content > div.page-wrapper > div:nth-child(2) > div.col-8.col-8-wide.col-6-tab > section > section > div > div > div > div.club-profile__panel.club-profile__panel--stats > div.club-profile__stats > section.profile-stat-cards-container > div"));

            for (WebElement e : cards) {
                String field = e.findElement(By.cssSelector("h4")).getText();
                String val = e.findElement(By.cssSelector("p")).getText();
                //System.out.println(field + ": " + val);
                if (field.toLowerCase().replace(" ", "").equals("gamesplayed")) {
                    stats.setGamesPlayed(val);
                }
                if (field.toLowerCase().replace(" ", "").equals("goalsconceded")) {
                    stats.addStat("Defence", field, val);
                }
            }

            List<WebElement> lists = driver.findElements(By.cssSelector("#main-content > div.page-wrapper > div:nth-child(2) > div.col-8.col-8-wide.col-6-tab > section > section > div > div > div > div.club-profile__panel.club-profile__panel--stats > div.club-profile__stats > section.profile-stat-lists-container > div"));

            for (WebElement l : lists) {
                String label = l.findElement(By.cssSelector("h4")).getText();
                List<WebElement> subLists = l.findElements(By.cssSelector("ul > li"));
                for (WebElement s : subLists) {
                    stats.addStat(label, s.findElement(By.cssSelector("p.profiles-stats-list__stat-label")).getText(), s.findElement(By.cssSelector("p.profiles-stats-list__stat-value")).getText());
                }
            }

            //return stats;
        } catch (Exception e){
            System.out.println(e);
        }
        return stats;
//        } finally {
//            driver.close();
//        }
    }

    public HashMap<String, Club> scrapeForTeams() {
        //StringBuilder sb = new StringBuilder();
        //WebDriver driver = new SafariDriver();
        HashMap<String, Club> clubs = new HashMap<>();
        try {
            driver.get("https://www.premierleague.com/en/clubs");

            Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(d -> driver.findElement(By.cssSelector("#main-content > div.page-wrapper > div.default-template > div > section.club-listings-grid > ul")).isDisplayed());
            List<WebElement> cards = driver.findElements(By.cssSelector("#main-content > div.page-wrapper > div.default-template > div > section.club-listings-grid > ul > li"));

            for(WebElement e : cards){
                Club club = new Club();
                club.setUrl(e.findElement(By.cssSelector("div.club-listings-card__team > a")).getAttribute("href"));
                club.setImage(e.findElement(By.cssSelector("div.club-listings-card__team > div > div > img")).getAttribute("src"));
                club.setName(e.findElement(By.cssSelector("div.club-listings-card__team")).getText());

                //sb.append(club);
                clubs.put(club.getName(), club);
            }
            //return clubs;
        } catch (Exception e){
            System.out.println(e);
        }//finally {
           // driver.close();
        //}
        return clubs;
    }

    public static class Club {
        private String url;
        private String name;
        private String image;
        private StatTable table;

        public Club(){}
        public Club(String url, String name, String img) {
            this.url = url;
            this.name = name;
            this.image = img;
        }

        public String getUrl() { return url; }
        public void setUrl(String url) {
            this.url = url;
        }
        public String getName() { return name; }
        public void setName(String name){
            this.name = name;
        }
        public String getImage() { return image; }
        public void setImage(String img){
            this.image = img;
        }

        public StatTable getTable() { return this.table; }
        public void setTable(StatTable table){
            this.table = table;
        }

        @Override
        public String toString() {
            return "{ \"name\": \"" + name+ "\", "
                    + " \"image\": \"" + image + "\", "
                    + "\"url\": \"" + url + "\", "
                    + table + "} ";
        }
    }

    public static class StatTable {
        //private String name;
        private String gamesPlayed;
        private HashMap<String, HashMap<String, String>> table;

        public StatTable(){
            this.table = new HashMap<>();
        }
        /*
        public StatTable(String name){
            this.name = name;
            this.table = new HashMap<>();
        }
         */
        public StatTable(String gp){
            //this.name = name;
            this.gamesPlayed = gp;
            this.table = new HashMap<>();
        }
        /*
        public void setName(String name){
            this.name = name;
        }
         */
        public void setGamesPlayed(String gp){
            this.gamesPlayed = gp;
        }

        public void addStat(String label, String statType, String value){
            table.computeIfAbsent(label, _ -> new HashMap<>()).put(statType, value);
        }

        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            //sb.append("{" + name + ": {Games played: " + gamesPlayed + "}, " + System.lineSeparator());
            sb.append("{Statistics: {Games played: " + gamesPlayed + "}, " + System.lineSeparator());
            table.forEach((label,v)-> {
                sb.append(label + ": {");
                v.forEach((type, val) -> {
                    sb.append(type + ": " + val + ", ");
                });
                sb.append("}, " + System.lineSeparator());
            });
            sb.append("}");
            return sb.toString();
        }
    }
}
