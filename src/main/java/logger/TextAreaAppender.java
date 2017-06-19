package logger;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Created by Ihar_Chekan on 5/19/2017.
 * Taken here: http://www.rshingleton.com/javafx-log4j-textarea-log-appender/
 */
public class TextAreaAppender extends WriterAppender {

  private static volatile TextArea textArea = null;

  /**
   * Set the target TextArea for the logging information to appear.
   *
   * @param textArea
   */
  public static void setTextArea(final TextArea textArea) {
    TextAreaAppender.textArea = textArea;
  }

  /**
   * Format and then append the loggingEvent to the stored TextArea.
   *
   * @param loggingEvent
   */
  @Override
  public void append(final LoggingEvent loggingEvent) {
    final String message = this.layout.format(loggingEvent);

    // Append formatted message to text area using the Thread.
    try {
      Platform.runLater(new Runnable() {
        @Override
        public void run() {
          try {
            if (textArea != null) {
              if (textArea.getText().length() == 0) {
                textArea.setText(message);
              } else {
                textArea.selectEnd();
                textArea.insertText(textArea.getText().length(),
                  message);
              }
            }
          } catch (final Throwable t) {
            System.out.println("Unable to append log to text area: "
              + t.getMessage());
          }
        }
      });
    } catch (final IllegalStateException e) {
      // ignore case when the platform hasn't yet been initialized
    }
  }


}
