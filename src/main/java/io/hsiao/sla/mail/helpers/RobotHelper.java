package io.hsiao.sla.mail.helpers;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import io.hsiao.devops.clib.exception.Exception;
import io.hsiao.devops.clib.exception.RuntimeException;
import io.hsiao.devops.clib.logging.Logger;
import io.hsiao.devops.clib.logging.Logger.Level;
import io.hsiao.devops.clib.logging.LoggerFactory;
import io.hsiao.devops.clib.teamforge.ArtifactDetailElement;
import io.hsiao.devops.clib.teamforge.TrackerFieldData;
import io.hsiao.devops.clib.utils.CommonUtils;
import io.hsiao.devops.clib.utils.IniUtils.Section;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
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

  private Template getFtlTemplate() throws Exception {
    final Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
    cfg.setClassForTemplateLoading(getClass(), "templates");
    cfg.setDefaultEncoding("UTF-8");
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

    try {
      return cfg.getTemplate(ftlTemplateFile);
    }
    catch (java.lang.Exception ex) {
      final Exception exception = new Exception("failed to get ftl template [" + ftlTemplateFile + "]", ex);
      logger.log(Level.INFO, "failed to get ftl template [" + ftlTemplateFile + "]");
      throw exception;
    }
  }

  private Map<String, Object> getFtlDataModel(final String trackerId, final Map<String, String> projectRules,
      final List<ArtifactDetailElement> artifactDetailList) throws Exception {
    final Map<String, Object> root = new HashMap<>();

    final List<String> headlineList = new ArrayList<>();

    for (String headline: CommonUtils.getMapValue(projectRules, "headlines").split(",")) {
      headline = headline.trim();
      if (!headline.isEmpty()) {
        headlineList.add(headline);
      }
    }

    final String[] headlines = new String[headlineList.size()];
    headlineList.toArray(headlines);

    root.put("headlines", headlines);

    final String timeframe = CommonUtils.getMapValue(projectRules, "timeframe");

    final List<List<String>> artifactDetailRows = new LinkedList<>();
    final Map<String, TrackerFieldData> trackerFieldNameDataMap = teamforgeHelper.getTrackerFieldDataMap(trackerId);

    for (final ArtifactDetailElement artifactDetailElement: artifactDetailList) {
      final List<String> artifactDetailRow = new LinkedList<>();

      for (final String headline: headlines) {
        if (headline.equalsIgnoreCase("timeframe") || headline.equalsIgnoreCase("time frame")) {
          artifactDetailRow.add(timeframe);
        }
        else if (headline.equalsIgnoreCase("expire date")) {
          artifactDetailRow.add("today");
        }
        else if (headline.equalsIgnoreCase("remaining days")) {
          artifactDetailRow.add("0 fuck days");
        }
        else {
          final String fieldType = teamforgeHelper.getArtifactFieldType(trackerFieldNameDataMap, headline);
          final String fieldValue = teamforgeHelper.getArtifactFieldValue(artifactDetailElement, headline, fieldType);
          artifactDetailRow.add(fieldValue);
        }
      }

      artifactDetailRows.add(artifactDetailRow);
    }

    root.put("artifacts", artifactDetailRows);

    root.put("cntTotalDefects", 0);
    root.put("cntOverdueDefects", 0);
    root.put("cnt24HExpireDefects", 0);
    root.put("cntP1Defects", 0);
    root.put("cntP2Defects", 0);
    root.put("cntP3Defects", 0);
    root.put("cntP4Defects", 0);
    root.put("artifactBaseURL", "fuck");

    return root;
  }

  public void doWork() throws Exception {
    final Map<Section, Map<String, String>> projectRulesMap = projectHelper.getProjectRules();

    teamforgeHelper.login();

    final List<ArtifactDetailElement> artifactDetailList = new LinkedList<>();
    for (final Map.Entry<Section, Map<String, String>> projectRulesEntry: projectRulesMap.entrySet()) {
      final String project = projectRulesEntry.getKey().getMajorKey();
      final String tracker = projectRulesEntry.getKey().getMinorKey();

      final String projectId = teamforgeHelper.getProjectId(project);
      final String trackerId = teamforgeHelper.getTrackerId(projectId, tracker);

      final Map<String, String> projectRules = projectRulesEntry.getValue();

      final String more = CommonUtils.getMapValue(projectRules, "more");

      artifactDetailList.addAll(teamforgeHelper.getArtifactDetailList(projectId, trackerId, projectRules));

      if (more.equalsIgnoreCase("true") || more.equalsIgnoreCase("yes")) {
        continue;
      }

      // template + data-model = output
      final Template template = getFtlTemplate();
      final Map<String, Object> dataModel = getFtlDataModel(trackerId, projectRules, artifactDetailList);
      final Writer out = new StringWriter();
      try {
        template.process(dataModel, out);
      }
      catch (java.lang.Exception ex) {
        final Exception exception = new Exception("failed to process template with data-model", ex);
        logger.log(Level.INFO, "failed to process template with data-model", exception);
        throw exception;
      }

      final String subject = String.format("[%s] SLA Reminder for Teamforge Remaining Artifacts", project);
      final String mailtos = CommonUtils.getMapValue(projectRules, "mailto");

      logger.log(Level.INFO, "Sending SLA reminder mail for project [" + project + "]");
      mailHelper.send(subject, out.toString(), mailtos);

      artifactDetailList.clear();
    }

    teamforgeHelper.logoff();
  }

  private static final Logger logger = LoggerFactory.getLogger(RobotHelper.class);

  private static final String mailPropsFile = "mail.properties";
  private static final String projectPropsFile = "projects.properties";
  private static final String teamforgePropsFile = "teamforge.properties";

  private static final String ftlTemplateFile = "email.ftl";

  private final Properties robotProps;

  private final MailHelper mailHelper;
  private final ProjectHelper projectHelper;
  private final TeamforgeHelper teamforgeHelper;
}
