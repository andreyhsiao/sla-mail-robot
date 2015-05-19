package io.hsiao.sla.mail.helpers;

import io.hsiao.devops.clib.exception.Exception;
import io.hsiao.devops.clib.exception.RuntimeException;
import io.hsiao.devops.clib.logging.Logger;
import io.hsiao.devops.clib.logging.LoggerFactory;
import io.hsiao.devops.clib.mail.Mail;
import io.hsiao.devops.clib.utils.CommonUtils;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public final class MailHelper {
  public MailHelper(final String mailPropsFile) throws Exception {
    if (mailPropsFile == null) {
      throw new RuntimeException("argument 'mailPropsFile' is null");
    }

    mailProps = CommonUtils.loadProperties(getClass(), mailPropsFile);
  }

  public void send(final String subject, final String content, final String mailtos) throws Exception {
    if (subject == null) {
      throw new RuntimeException("argument 'subject' is null");
    }

    if (content == null) {
      throw new RuntimeException("argument 'content' is null");
    }

    if (mailtos == null) {
      throw new RuntimeException("argument 'mailtos' is null");
    }

    final String smtpHost = CommonUtils.getProperty(mailProps, "smtp.host", false);
    final String smtpPort = CommonUtils.getProperty(mailProps, "smtp.port", false);

    final String username = CommonUtils.getProperty(mailProps, "username", false);
    final String password = CommonUtils.getProperty(mailProps, "password", false);

    final String domain = CommonUtils.getProperty(mailProps, "default.domain", false);

    final Properties props = Mail.getProperties(smtpHost, smtpPort);
    final Mail mail = new Mail(props);

    final List<String> mailToList = new LinkedList<>();
    for (String mailto: mailtos.split(",")) {
      mailto = mailto.trim();

      if (!mailto.isEmpty()) {
        mailToList.add(mailto);
      }
    }

    mail.setFrom(username + "@" + domain);
    mail.setSubject(subject, "UTF-8");
    mail.setContent(content, "text/html; charset=UTF-8");
    mail.setRecipients(Mail.RecipientTypeTO, mailToList, domain);
    mail.setSentDate(new Date());

    mail.send(username, password);
  }

  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(MailHelper.class);
  private final Properties mailProps;
}
