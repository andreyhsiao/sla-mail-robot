package io.hsiao.sla.mail.helpers;

import io.hsiao.devops.clib.exception.Exception;
import io.hsiao.devops.clib.logging.Logger;
import io.hsiao.devops.clib.logging.LoggerFactory;
import io.hsiao.devops.clib.teamforge.ArtifactDetailElement;
import io.hsiao.devops.clib.teamforge.FilterBuilder;
import io.hsiao.devops.clib.teamforge.FilterElement;
import io.hsiao.devops.clib.teamforge.SortKeyElement;
import io.hsiao.devops.clib.teamforge.Teamforge;
import io.hsiao.devops.clib.teamforge.TrackerFieldData;
import io.hsiao.devops.clib.utils.CommonUtils;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public final class TeamforgeHelper {
  public TeamforgeHelper(final String teamforgePropsFile) throws Exception {
    if (teamforgePropsFile == null) {
      throw new Exception("argument 'teamforgePropsFile' is null");
    }

    teamforgeProps = CommonUtils.loadProperties(getClass(), teamforgePropsFile);

    final String serverUrl = CommonUtils.getProperty(teamforgeProps, "url", false);
    teamforge = new Teamforge(serverUrl, 30 * 1000);
  }

  public void login() throws Exception {
    final String username = CommonUtils.getProperty(teamforgeProps, "username", false);
    final String password = CommonUtils.getProperty(teamforgeProps, "password", false);

    teamforge.login(username, password);
  }

  public void logoff() throws Exception {
    teamforge.logoff();
  }

  public String getProjectId(final String project) throws Exception {
    return teamforge.getProjectId(project);
  }

  public String getTrackerId(final String projectId, final String tracker) throws Exception {
    return teamforge.getTrackerId(projectId, tracker);
  }

  public List<ArtifactDetailElement> getArtifactDetailList(final String projectId, final String trackerId, final Map<String, String> rules) throws Exception {
    if (rules == null) {
      throw new Exception("argument 'rules' is null");
    }

    final Map<String, String> filterMap = new LinkedHashMap<>();
    for (final Map.Entry<String, String> rule: rules.entrySet()) {
      final String ruleName = rule.getKey();
      final String ruleValue = rule.getValue();

      if (ruleName.equalsIgnoreCase("inherit") || ruleName.equalsIgnoreCase("mailto")
          || ruleName.equalsIgnoreCase("timeframe") || ruleName.equalsIgnoreCase("headlines") || ruleName.equalsIgnoreCase("more")) {
        continue;
      }

      filterMap.put(ruleName, ruleValue);
    }

    final List<FilterElement> filters = FilterBuilder.build(projectId, trackerId, teamforge, filterMap, ",", true);

    final List<SortKeyElement> sortKeys = new LinkedList<>();
    sortKeys.add(new SortKeyElement("Priority", true));
    sortKeys.add(new SortKeyElement("Category", true));
    sortKeys.add(new SortKeyElement("Status", true));

    return teamforge.getArtifactDetailList(trackerId, filters, sortKeys);
  }

  public Map<String, TrackerFieldData> getTrackerFieldDataMap(final String trackerId) throws Exception {
    return TrackerFieldData.toMap(teamforge.getTrackerFieldDataList(trackerId));
  }

  public String getArtifactFieldType(final Map<String, TrackerFieldData> map, final String name) throws Exception {
    return TrackerFieldData.getFieldType(map, name);
  }

  public String getArtifactFieldValue(final ArtifactDetailElement artifactDetailElement, final String name, final String type) throws Exception {
    final Object object;

    if (name.equalsIgnoreCase("assignedto") || name.equalsIgnoreCase("assigned to") || name.equalsIgnoreCase("assignee")) {
      object = artifactDetailElement.getAssignedToFullname();
    }
    else if (name.equalsIgnoreCase("priority")) {
      object = artifactDetailElement.getPriorityText();
    }
    else if (name.equalsIgnoreCase("submittedby") || name.equalsIgnoreCase("submitted by") || name.equalsIgnoreCase("submitter")) {
      object = artifactDetailElement.getAssignedToFullname();
    }
    else {
      object = artifactDetailElement.getFieldValue(name, type);
    }

    if (object instanceof List<?>) {
      final StringBuilder sb = new StringBuilder();
      @SuppressWarnings("unchecked")
      final List<Object> values = (List<Object>) object;
      for (final Object value: values) {
        sb.append(value);
        sb.append("\n");
      }
      return sb.toString();
    }
    else {
      return object.toString();
    }
  }

  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(TeamforgeHelper.class);
  private final Teamforge teamforge;
  private final Properties teamforgeProps;
}
