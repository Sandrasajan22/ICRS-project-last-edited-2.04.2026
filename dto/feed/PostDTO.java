package com.main.icrsbackend.dto.feed;



import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private Long id;
    private String mediaUrl;
    private String mediaType;
    private String caption;
}