package io.hsiao.sla.mail.helpers;

import io.hsiao.devops.clib.exception.Exception;
import io.hsiao.devops.clib.utils.IniUtils.Section;

import java.util.Map;

import org.junit.Test;

public final class ProjectHelperTest {
  @Test
  public void getProjectRulesTest() throws Exception {
    final ProjectHelper helper = new ProjectHelper(projectPropsFile, defaultTracker);
    final Map<Section, Map<String, String>> projectRules = helper.getProjectRules();

    for (final Map.Entry<Section, Map<String, String>> projectRule: projectRules.entrySet()) {
      final String project = projectRule.getKey().getMajorKey();
      final String tracker = projectRule.getKey().getMinorKey();
      final Map<String, String> rules = projectRule.getValue();

      System.out.println("=====================================");
      System.out.println("[" + project + " \"" + tracker + "\"]");
      for (final Map.Entry<String, String> rule: rules.entrySet()) {
        System.out.println(rule.getKey() + " = " + rule.getValue());
      }
    }

    System.out.println("=====================================");
  }

  public static final String defaultTracker = "External Issue";
  public static final String projectPropsFile = "projects-test.properties";
}
