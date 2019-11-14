package com.ndfitnessplus.Model;

public class AdSliderList {
    String adTitle,adDisc,bannerImage,Url;
    int image;
    int id;

    public AdSliderList() {
    }

    public AdSliderList(String adTitle, String adDisc, int image,int id) {
        this.adTitle = adTitle;
        this.adDisc = adDisc;
        this.image = image;
        this.id=id;
    }

    public String getAdTitle() {
        return adTitle;
    }

    public void setAdTitle(String adTitle) {
        this.adTitle = adTitle;
    }

    public String getAdDisc() {
        return adDisc;
    }

    public void setAdDisc(String adDisc) {
        this.adDisc = adDisc;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
