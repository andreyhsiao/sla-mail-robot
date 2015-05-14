package io.hsiao.sla.mail.helpers;

import io.hsiao.devops.clib.exception.Exception;
import io.hsiao.devops.clib.logging.Logger;
import io.hsiao.devops.clib.logging.Logger.Level;
import io.hsiao.devops.clib.logging.LoggerFactory;
import io.hsiao.devops.clib.utils.IniUtils;
import io.hsiao.devops.clib.utils.IniUtils.Section;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ProjectHelper {
  public ProjectHelper(final String projectPropsFile, final String defaultTracker) throws Exception {
    if (projectPropsFile == null) {
      throw new Exception("argument 'projectPropsFile' is null");
    }

    if (defaultTracker == null) {
      throw new Exception("argument 'defaultTracker' is null");
    }

    this.defaultTracker = defaultTracker;

    try (final InputStream ins = getClass().getResourceAsStream(projectPropsFile)) {
      if (ins == null) {
        logger.log(Level.INFO, "failed to locate file [" + projectPropsFile + "]");
        throw new Exception("failed to locate file [" + projectPropsFile + "]");
      }

      ini.load(ins, false);
    }
    catch (IOException ex) {
      final Exception exception = new Exception("failed to load file [" + projectPropsFile + "]", ex);
      logger.log(Level.INFO, "failed to load file [" + projectPropsFile + "]", exception);
      throw exception;
    }
  }

  private Map<Section, Map<String, String>> getDefaultRules(final List<Section> sections) throws Exception {
    if (sections == null) {
      throw new Exception("argument 'sections' is null");
    }

    final Map<Section, Map<String, String>> defaultRules = new LinkedHashMap<>();
    for (final Section section: sections) {
      final String project = section.getMajorKey();
      final String tracker = section.getMinorKey().isEmpty() ? defaultTracker : section.getMinorKey();

      if (project.startsWith("default")) {
        defaultRules.put(new Section(project, tracker), ini.getProperties(section));
      }
    }

    return defaultRules;
  }

  public Map<Section, Map<String, String>> getProjectRules() throws Exception {
    final List<Section> sections = ini.getSections();

    final Map<Section, Map<String, String>> defaultRulesMap = getDefaultRules(sections);
    final Map<Section, Map<String, String>> projectRulesMap = new LinkedHashMap<>();

    for (final Section section: sections) {
      final String project = section.getMajorKey();
      final String tracker = section.getMinorKey().isEmpty() ? defaultTracker : section.getMinorKey();

      if (project.startsWith("default")) {
        continue;
      }

      final Map<String, String> projectRules = ini.getProperties(section);
      final Map<String, String> effProjectRules = new LinkedHashMap<>();

      final String inherit = projectRules.containsKey("inherit") ? projectRules.get("inherit") : "default";
      if (inherit.equalsIgnoreCase("false")) {
        effProjectRules.putAll(projectRules);;
      }
      else {
        final Map<String, String> defaultRules = defaultRulesMap.get(new Section(inherit, tracker));

        if (defaultRules == null) {
          logger.log(Level.INFO, "failed to get default inherit rules for project tracker [" + project + "] [" + tracker + "]");
          throw new Exception("failed to get default inherit rules for project tracker [" + project + "] [" + tracker + "]");
        }

        effProjectRules.putAll(defaultRules);
        effProjectRules.putAll(projectRules);
      }

      projectRulesMap.put(new Section(project, tracker), effProjectRules);
    }

    return projectRulesMap;
  }

  private static final Logger logger = LoggerFactory.getLogger(ProjectHelper.class);
  private final IniUtils ini = new IniUtils();
  private final String defaultTracker;
}
