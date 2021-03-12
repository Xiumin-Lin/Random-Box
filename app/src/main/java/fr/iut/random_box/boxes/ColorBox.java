package fr.iut.random_box.boxes;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.Random;

import fr.iut.random_box.R;
import fr.iut.random_box.RandomBox;

public class ColorBox extends RandomBox {
    public ColorBox() {
        super("color");
    }

    /**
     * @return a random color-int from red, green, blue components, alpha = 255
     */
    public static int getRandomColor(){
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    @Override
    public void setPopupView(View popupView) {
        super.setPopupView(popupView);
        ImageView imgRandColor = popupView.findViewById(R.id.imgPopContent);

        //setup imageView & convert px to dp
        int _200dpInpx = (int) (200 * popupView.getResources().getDisplayMetrics().density);
        Log.d("rb", "200 dp = " + _200dpInpx + " px");
        imgRandColor.getLayoutParams().height = _200dpInpx; //height receive px
        imgRandColor.getLayoutParams().width = _200dpInpx;

        int randColor = getRandomColor();
        imgRandColor.setBackgroundColor(randColor);
        imgRandColor.setVisibility(View.VISIBLE);

        //convert int color to hex format string
        String hex = "HEX : " + String.format("#%06X", (0xFFFFFF & randColor));
        setTextViewContent(popupView.findViewById(R.id.txtPopContentTitle), hex);
    }
}
