package io.hsiao.sla.mail;

import io.hsiao.devops.clib.exception.Exception;
import io.hsiao.devops.clib.logging.Logger;
import io.hsiao.devops.clib.logging.Logger.Level;
import io.hsiao.devops.clib.logging.LoggerFactory;
import io.hsiao.devops.clib.logging.impl.SimpleConsoleLoggerFactory;
import io.hsiao.sla.mail.helpers.RobotHelper;

public final class Robot {
  public void hello() {
    logger.log(Level.INFO, "SLA Mailing Robot Wakes Up ...");
  }

  public void doWork() throws Exception {
    new RobotHelper(robotPropsFile).doWork();
  }

  public void bye() {
    logger.log(Level.INFO, "SLA Mailing Robot Falls Asleep ...");
  }

  public static void main(String[] args) throws Exception {
    final Robot robot = new Robot();
    robot.hello();
    robot.doWork();
    robot.bye();
  }

  static {
    LoggerFactory.setLoggerFactory(new SimpleConsoleLoggerFactory(Level.INFO));
  }

  private static final Logger logger = LoggerFactory.getLogger(Robot.class);
  private static final String robotPropsFile = "robot.properties";
}
