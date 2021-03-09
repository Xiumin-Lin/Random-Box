package fr.iut.random_box.boxes;

import android.view.View;

import java.util.Random;

import fr.iut.random_box.R;
import fr.iut.random_box.RandomBox;

public class NumberBox extends RandomBox {
    public NumberBox(){
        super("number");
    }

    /**
     * @return a random number between 0 & 100
     */
    public static int getRandomNumber(){ return (new Random()).nextInt(101); }

    @Override
    public void setPopupView(View popupView) {
        super.setPopupView(popupView);
        setTextViewContent(popupView.findViewById(R.id.txtPopBigNumber), "" + getRandomNumber());
    }
}
