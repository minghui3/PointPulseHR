package hooks;

import utils.EmailUtil;

import io.cucumber.java.AfterAll;


public class GlobalHooks {
    @AfterAll
    public static void sendEmailAfterTests() {
        try {
            // Send an email once all tests are done
            String subject = "Cucumber Test Run Results";
            String body = "All Cucumber tests have finished running.";
            EmailUtil.sendEmail(subject, body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
