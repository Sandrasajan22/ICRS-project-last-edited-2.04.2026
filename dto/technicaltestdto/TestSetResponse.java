package com.main.icrsbackend.dto.technicaltestdto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestSetResponse {
    private String stream;
    private String skill;
    private String level;
    private Integer set_number;
    private List<QuestionResponse> questions;
}