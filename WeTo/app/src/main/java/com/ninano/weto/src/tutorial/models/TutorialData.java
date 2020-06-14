package com.ninano.weto.src.tutorial.models;

public class TutorialData {

    private String title;
    private String subTitle;
    private int GuideImage;

    public TutorialData(int guideImage) {
        GuideImage = guideImage;
    }

    public TutorialData(String title, String subTitle, int guideImage) {
        this.title = title;
        this.subTitle = subTitle;
        GuideImage = guideImage;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public int getGuideImage() {
        return GuideImage;
    }
}
