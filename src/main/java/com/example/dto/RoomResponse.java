package com.example.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomResponse {

	private Long id;
	private String name;
	private String location;
	private Integer capacity;
    private List<ImageInfo> images;
    
    @Data 
    @Builder
    @NoArgsConstructor 
    @AllArgsConstructor
    public static class ImageInfo {
        private Long id;
        private String url;
    }
    
}