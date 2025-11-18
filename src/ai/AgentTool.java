package ai;

public interface AgentTool {
    /** @return a short name for the tool */
    String getName();

    /** @return a one-line description of what the tool does */
    String getDescription();
}
