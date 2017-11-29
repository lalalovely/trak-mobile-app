package subai.trak2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    private ImageView iv;

    UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new UserSessionManager(this);

        setContentView(R.layout.activity_welcome);
        iv = (ImageView) findViewById(R.id.welcomeImage);
        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.mytransition);

        iv.setAnimation(myanim);

        final Intent intent;
        if (sessionManager.isUserLoggedIn()) {
            intent = new Intent(this, LoginActivity.class);
        } else {
            intent = new Intent(this, IntroActivity.class);
        }

        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(intent);
                    finish();
                }
            }
        };
        timer.start();
    }
}
