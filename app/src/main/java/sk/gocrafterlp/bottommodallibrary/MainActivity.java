package sk.gocrafterlp.bottommodallibrary;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import sk.gocrafterlp.bottommodal.Fraction;
import sk.gocrafterlp.bottommodal.ModalDrawerParameters;
import sk.gocrafterlp.bottommodal.ModalNavigationDrawer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        final ModalNavigationDrawer drawer = findViewById(R.id.drawer);

        drawer.setDim(Color.parseColor("#66ff0000"));

        ModalDrawerParameters parameters = ModalDrawerParameters.create();
        parameters.setAnimationDurationLong(400);
        parameters.setAnimationDurationShort(350);
        parameters.setDismissPoint(Fraction.of(4, 10));
        parameters.setPressDeflectionTolerance(30);

        drawer.setParams(parameters);

        drawer.setContentBackgroundColor(Color.BLUE);
        drawer.setContent(new Button(this));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.show();
            }
        });
    }
}
