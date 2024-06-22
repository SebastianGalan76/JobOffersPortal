package com.coresaken.jobportal.database.model;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "link")
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(length = 20)
    String type;

    String url;

    public LinkTypeData getType(){
        try{
            LinkTypeEnum linkTypeEnum = LinkTypeEnum.valueOf(type.toUpperCase());

            return new LinkTypeData(false, linkTypeEnum.friendlyName, linkTypeEnum.iconUrl);
        }catch (IllegalArgumentException e){
            return new LinkTypeData(true, type, LinkTypeEnum.CUSTOM.iconUrl);
        }
    }

    @Data
    @AllArgsConstructor
    public static class LinkTypeData{
        boolean custom;

        String name;
        String iconUrl;
    }

    public enum LinkTypeEnum{
        CUSTOM("CUSTOM","https://cdn-icons-png.freepik.com/256/6994/6994770.png"),
        FACEBOOK("Facebook", "https://static-00.iconduck.com/assets.00/facebook-icon-512x512-seb542ju.png"),
        INSTAGRAM("Instagram", "https://cdn-icons-png.flaticon.com/512/2111/2111463.png"),
        X("X", "https://cdn0.iconfinder.com/data/icons/social-network-flat-4/512/x_icon-512.png"),
        TWITTER("Twitter", "https://www.iconpacks.net/icons/2/free-twitter-logo-icon-2429-thumb.png"),
        YOUTUBE("YouTube", "https://www.freepnglogos.com/uploads/youtube-vector-logo-png-9.png"),
        ;

        final String friendlyName;
        final String iconUrl;

        LinkTypeEnum(String friendlyName, String iconUrl){
            this.friendlyName = friendlyName;
            this.iconUrl = iconUrl;
        }

        @JsonValue
        public LinkTypeInfo convert() {
            return new LinkTypeInfo(friendlyName, iconUrl);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LinkTypeInfo {
        String friendlyName;
        String iconUrl;
    }
}
