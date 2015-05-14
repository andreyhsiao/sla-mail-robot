package io.hsiao.sla.mail.helpers;

import io.hsiao.devops.clib.exception.Exception;
import io.hsiao.devops.clib.exception.RuntimeException;
import io.hsiao.devops.clib.logging.Logger;
import io.hsiao.devops.clib.logging.LoggerFactory;
import io.hsiao.devops.clib.teamforge.ArtifactDetailElement;
import io.hsiao.devops.clib.utils.CommonUtils;

import java.util.List;
import java.util.Properties;

public final class MailHelper {
  public MailHelper(final String mailPropsFile) throws Exception {
    if (mailPropsFile == null) {
      throw new RuntimeException("argument 'mailPropsFile' is null");
    }

    mailProps = CommonUtils.loadProperties(getClass(), mailPropsFile);
  }

  public boolean send(final List<ArtifactDetailElement> artifactDetailList, final String mailToList,
      final String timeFrame, final String headlines) {
    if (artifactDetailList == null) {
      throw new RuntimeException("argument 'artifactDetailList' is null");
    }

    if (mailToList == null) {
      throw new RuntimeException("argument 'mailToList' is null");
    }

    if (timeFrame == null) {
      throw new RuntimeException("argument 'timeFrame' is null");
    }

    if (headlines == null) {
      throw new RuntimeException("argument 'headlines' is null");
    }

    return true;
  }

  private static final Logger logger = LoggerFactory.getLogger(MailHelper.class);
  private final Properties mailProps;
}
