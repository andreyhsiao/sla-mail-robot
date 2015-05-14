package io.hsiao.sla.mail.helpers;

import io.hsiao.devops.clib.exception.Exception;
import io.hsiao.devops.clib.exception.RuntimeException;
import io.hsiao.devops.clib.logging.Logger;
import io.hsiao.devops.clib.logging.LoggerFactory;
import io.hsiao.devops.clib.teamforge.ArtifactDetailElement;
import io.hsiao.devops.clib.utils.CommonUtils;
import io.hsiao.devops.clib.utils.IniUtils.Section;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public final class RobotHelper {
  public RobotHelper(final String robotPropsFile) throws Exception {
    if (robotPropsFile == null) {
      throw new RuntimeException("argument 'robotPropsFile' is null");
    }

    robotProps = CommonUtils.loadProperties(getClass(), robotPropsFile);

    mailHelper = new MailHelper(mailPropsFile);
    projectHelper = new ProjectHelper(projectPropsFile, getDefaultTracker());
    teamforgeHelper = new TeamforgeHelper(teamforgePropsFile);
  }

  public String getDefaultTracker() throws Exception {
    return CommonUtils.getProperty(robotProps, "tracker", false);
  }

  public void doWork() throws Exception {
    final Map<Section, Map<String, String>> projectRulesMap = projectHelper.getProjectRules();

    teamforgeHelper.login();

    final List<ArtifactDetailElement> artifactDetailList = new LinkedList<>();
    for (final Map.Entry<Section, Map<String, String>> projectRulesEntry: projectRulesMap.entrySet()) {
      final String project = projectRulesEntry.getKey().getMajorKey();
      final String tracker = projectRulesEntry.getKey().getMinorKey();

      final Map<String, String> projectRules = projectRulesEntry.getValue();

      final String more = CommonUtils.getMapValue(projectRules, "more");

      artifactDetailList.addAll(teamforgeHelper.getArtifactDetailList(project, tracker, projectRules));

      if (more.equalsIgnoreCase("true")) {
        continue;
      }

      // send mail
      final List<String> headlineList = new ArrayList<>();
      for (String headline: CommonUtils.getMapValue(projectRules, "headlines").split(",")) {
        headline = headline.trim();
        if (!headline.isEmpty()) {
          headlineList.add(headline);
        }
      }
      final String[] headlines = new String[headlineList.size()];
      headlineList.toArray(headlines);

      final String timeframe = CommonUtils.getMapValue(projectRules, "timeframe");

      final List<List<String>> artifactDetailRows = new LinkedList<>();
      for (final ArtifactDetailElement artifactDetailElement: artifactDetailList) {
        final List<String> artifactDetailRow = new LinkedList<>();
        for (final String headline: headlines) {

        }
      }

      artifactDetailList.clear();
    }

    teamforgeHelper.logoff();
  }

  private static final Logger logger = LoggerFactory.getLogger(RobotHelper.class);

  private static final String mailPropsFile = "mail.properties";
  private static final String projectPropsFile = "projects.properties";
  private static final String teamforgePropsFile = "teamforge.properties";

  private final Properties robotProps;

  private final MailHelper mailHelper;
  private final ProjectHelper projectHelper;
  private final TeamforgeHelper teamforgeHelper;
}
