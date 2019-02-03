# bottom-modal
![Jitpack](https://jitpack.io/v/gocrafterlp/bottom-modal.svg)

bottom-modal is customizable bottom navigation drawer library

## Installation
Add Jitpack to your repositories
```gradle
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

Add bottom-modal to depedencies
```gradle
dependencies {
  ...
  implementation 'com.github.gocrafterlp:bottom-modal:1.0'
}
```

## Usage

1. Add drawer to your layout
```xml
<sk.gocrafterlp.bottommodal.ModalNavigationDrawer
  android:id="@+id/drawer"
  android:layout_width="match_parent"
  android:layout_height="match_parent"
  app:modal_layout="@layout/content_modal">
  
</sk.gocrafterlp.bottommodal.ModalNavigationDrawer>
```
> Warning:
> Do not add childs to ModalNavigationDrawer xml element. It will be deleted at runtime anyway.

2. Create content_modal.xml in your layout folder (you can customize content however you want
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
              android:layout_width="match_parent"
              android:layout_height="match_parent"
              android:orientation="vertical"
              android:padding="16dp">

    <TextView
        android:id="@+id/textView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Hello world"/>

    <Button
        android:id="@+id/button2"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Button"/>
</LinearLayout>
```
3. Setup drawer in activity code
```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // note: all in-code customizations are optional

        Button button = findViewById(R.id.button);
        final ModalNavigationDrawer drawer = findViewById(R.id.drawer); // find drawer by its id
        
        drawer.setDim(Color.parseColor("#66ff0000")); // set dim color to transparent red, default is rgb(102, 0, 0, 0)

        ModalDrawerParameters parameters = ModalDrawerParameters.create(); // create new parameters
        parameters.setAnimationDurationLong(400); // long animation duration, default is 200
        parameters.setAnimationDurationShort(350); // short animation duration, default is 150
        parameters.setDismissPoint(Fraction.of(4, 10)); // fraction defining point where will be drawer dismissed if released swipe here, default is 1/3
        parameters.setPressDeflectionTolerance(30); // tolerance in pixels defining max. area considered as click in background
        
        drawer.setParams(parameters); // apply parameters on our drawer
        
        drawer.setContentBackgroundColor(Color.BLUE); // set custom backround color (default is drawable)
        drawer.setContentBackground(R.drawable.my_background) // set custom background drawable (default is R.drawable.modal_background)
        drawer.setContent(new Button(this)); // set custom content, this will delete layout defined in xml!!

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.show(); // show drawer
            }
        });
    }
}
```

## Compatibility
You might need to add Java 1.8 compatibity in order to use library
```gradle
android {
    compileSdkVersion ...
    defaultConfig {
        ...
    }
    buildTypes {
        release {
            ...
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```

## License
This project is published under Apache 2.0 license.
[More info about license](https://github.com/gocrafterlp/bottom-modal/blob/master/LICENSE)
